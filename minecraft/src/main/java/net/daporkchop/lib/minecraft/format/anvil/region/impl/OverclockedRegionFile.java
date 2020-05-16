/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.format.anvil.region.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.minecraft.format.anvil.region.AbstractRegionFile;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.util.BitSet;

import static net.daporkchop.lib.minecraft.format.anvil.region.RegionConstants.*;

/**
 * A highly optimized rewrite of Mojang's original RegionFile class, designed with backwards-compatibility in mind.
 *
 * @author DaPorkchop_
 */
public final class OverclockedRegionFile extends AbstractRegionFile {
    protected final ByteBufAllocator alloc;
    protected final MappedByteBuffer headers;
    protected final ByteBuf nettyHeadersBuf;
    protected final BitSet occupiedSectors = new BitSet();

    public OverclockedRegionFile(@NonNull File file, @NonNull ByteBufAllocator alloc, boolean readOnly) throws IOException {
        super(file, readOnly);
        this.alloc = alloc;

        long fileSize = this.channel.size();
        if (fileSize < HEADER_BYTES) {
            //region headers are incomplete, write empty headers
            try {
                //write the offset and timestamp tables
                this.channel.transferFrom(SlashDevSlashNull.INSTANCE, 0L, HEADER_BYTES);
            } catch (NonWritableChannelException e) {
                throw new IOException(String.format("Cannot open read-only region \"%s\" as the headers are not complete", file.getAbsolutePath()));
            }
        } else if (!this.readOnly && (fileSize & 0xFFFL) != 0) {
            //the file size is not a multiple of 4KB, grow it
            this.channel.transferFrom(SlashDevSlashNull.INSTANCE, fileSize, fileSize & 0xFFFL);
        }

        this.headers = this.channel.map(this.readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, 0, HEADER_BYTES);
        this.nettyHeadersBuf = Unpooled.wrappedBuffer(this.headers);

        this.occupiedSectors.set(0, 2);
        //init occupied sectors bitset
        try {
            for (int i = 0; i < SECTOR_BYTES; i += 4) {
                int offset = this.headers.getInt(i);
                if (offset != 0) {
                    this.occupiedSectors.set(offset >> 8, (offset >> 8) + (offset & 0xFF));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IOException(String.format("Corrupt region headers in \"%s\"", file.getAbsolutePath()));
        }
    }

    @Override
    protected ByteBuf headersBuf() {
        return this.nettyHeadersBuf;
    }

    @Override
    protected ByteBuf doRead(int x, int z, int offsetIndex, int offset) throws IOException {
        int bytesToRead = (offset & 0xFF) * SECTOR_BYTES;
        ByteBuf buf = this.alloc.ioBuffer(bytesToRead);
        int read = buf.writeBytes(this.channel, (offset >>> 8) * SECTOR_BYTES, bytesToRead);
        if (read != bytesToRead) {
            throw new IOException(String.format("Read %d/%d bytes!", read, bytesToRead));
        }
        return buf.writerIndex(buf.readInt() + 4);
    }

    @Override
    protected void doWrite(int x, int z, @NonNull ByteBuf chunk, long timestamp, int requiredSectors) throws IOException {
        int size = chunk.readableBytes();

        int offset = this.headers.getInt(getOffsetIndex(x, z));
        int sectors = offset & 0xFF;
        offset >>>= 8;
        if (offset != 0) {
            if (sectors == requiredSectors) {
                //re-use old sectors
                if (chunk.readBytes(this.channel, offset * SECTOR_BYTES, size) != size) {
                    throw new IllegalStateException("Unable to write all bytes to disk!");
                }
                this.channel.transferFrom(SlashDevSlashNull.INSTANCE, offset * SECTOR_BYTES + size, ((size - 1 >> 12) + 1 << 12) - size);
                return;
            } else {
                //clear old sectors to search for new ones
                //this makes it faster and less prone to bugs than shrinking existing allocations
                this.occupiedSectors.clear(offset, offset + sectors);
            }
        }
        offset = 0;
        SEARCH:
        for (int i = this.occupiedSectors.nextClearBit(0); offset == 0; ) {
            int j = 0;
            while (j < requiredSectors) {
                if (this.occupiedSectors.get(i + j++)) {
                    i = this.occupiedSectors.nextClearBit(i + j);
                    continue SEARCH;
                }
            }
            //if we get this far, we've found a sufficiently long clear stretch
            offset = i;
        }
        if (chunk.readBytes(this.channel, offset * SECTOR_BYTES, size) != size) {
            throw new IllegalStateException("Unable to write all bytes to disk!");
        }
        this.channel.transferFrom(SlashDevSlashNull.INSTANCE, offset * SECTOR_BYTES + size, ((size - 1 >> 12) + 1 << 12) - size);

        this.occupiedSectors.set(offset, offset + requiredSectors);
        this.headers.putInt(getOffsetIndex(x, z), (offset << 8) | requiredSectors);
        this.headers.putInt(getTimestampIndex(x, z), (int) (timestamp / 1000L));
    }

    @Override
    protected void doDelete(int x, int z, int startIndex, int length) throws IOException {
        this.occupiedSectors.clear(startIndex, startIndex + length);

        this.channel.transferFrom(SlashDevSlashNull.INSTANCE, startIndex * SECTOR_BYTES, length * SECTOR_BYTES);
    }

    @Override
    protected void doDefrag() throws IOException {
        //need to implement this at some point
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFlush() throws IOException {
        this.headers.force();
    }

    @Override
    protected void doClose() throws IOException {
        if (!this.readOnly()) {
            this.doFlush();
        }
        PUnsafe.pork_releaseBuffer(this.headers);
    }
}
