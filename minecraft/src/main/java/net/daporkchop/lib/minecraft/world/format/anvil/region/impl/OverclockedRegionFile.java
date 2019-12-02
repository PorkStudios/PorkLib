/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.world.format.anvil.region.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.minecraft.world.format.anvil.region.AbstractRegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionConstants;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionOpenOptions;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.CorruptedRegionException;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.ReadOnlyRegionException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.util.BitSet;

/**
 * A highly optimized rewrite of Mojang's original RegionFile class, designed with backwards-compatibility in mind.
 * <p>
 * Of course, compression modes other than 1 and 2 (GZip and DEFLATE respectively) won't work with a vanilla Minecraft implementation, so
 * they should be avoided.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class OverclockedRegionFile extends AbstractRegionFile {
    protected final ByteBuf headers;
    protected final BitSet occupiedSectors = new BitSet();

    public OverclockedRegionFile(@NonNull File file, @NonNull RegionOpenOptions options) throws IOException {
        super(file, options);

        long fileSize = this.channel.size();
        if (fileSize < RegionConstants.HEADER_BYTES) {
            //region headers are incomplete, write empty headers
            try {
                //write the chunk offset table
                this.channel.write(ByteBuffer.wrap(RegionConstants.EMPTY_SECTOR), 0L);
                //write another sector for the timestamp info
                this.channel.write(ByteBuffer.wrap(RegionConstants.EMPTY_SECTOR), RegionConstants.SECTOR_BYTES);
            } catch (NonWritableChannelException e) {
                throw new CorruptedRegionException(String.format("Cannot open read-only region \"%s\" as the headers are not complete", file.getAbsolutePath()));
            }
        } else if (!this.readOnly && (fileSize & 0xFFFL) != 0) {
            //the file size is not a multiple of 4KB, grow it
            this.channel.write(ByteBuffer.wrap(RegionConstants.EMPTY_SECTOR, 0, (int) (fileSize & 0xFFFL)), fileSize);
        }

        this.headers = Unpooled.wrappedBuffer(this.channel.map(this.readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, 0, RegionConstants.HEADER_BYTES));

        this.occupiedSectors.set(0, 2);
        //init occupied sectors bitset
        try {
            for (int i = RegionConstants.SECTOR_INTS - 1; i >= 0; i--) {
                int offset = this.headers.getInt(i << 2);
                if (offset != 0) {
                    this.occupiedSectors.set(offset >> 8, (offset >> 8) + (offset & 0xFF));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new CorruptedRegionException(String.format("Corrupt region headers in \"%s\"", file.getAbsolutePath()));
        }
    }

    @Override
    protected boolean optionsSupported(@NonNull RegionOpenOptions options) {
        return options.mode() == Mode.STANDARD;
    }

    @Override
    protected ByteBuf headersBuf() {
        return this.headers;
    }

    @Override
    protected ByteBuf doRead(int x, int z, int offsetIndex) throws IOException {
        int offset = this.headers.getInt(offsetIndex);
        if (offset == 0) {
            return null;
        }

        int bytesToRead = (offset & 0xFF) * RegionConstants.SECTOR_BYTES;
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.ioBuffer(bytesToRead);
        int read = buf.writeBytes(this.channel, (offset >>> 8) * RegionConstants.SECTOR_BYTES, bytesToRead);
        if (read != bytesToRead) {
            throw new IOException(String.format("Read %d/%d bytes!", read, bytesToRead));
        }
        return buf.slice(RegionConstants.LENGTH_HEADER_SIZE, buf.getInt(0));
    }

    @Override
    protected void doWrite(int x, int z, int offsetIndex, @NonNull ByteBuf chunk, int requiredSectors) throws IOException {
        int size = chunk.readableBytes();
        if (size < RegionConstants.CHUNK_HEADER_SIZE || chunk.readableBytes() > (1 << 20)) {
            throw new IllegalArgumentException("Invalid input length!");
        } else if (chunk.getInt(0) + RegionConstants.LENGTH_HEADER_SIZE != size) {
            throw new IllegalArgumentException("Invalid chunk data: length header doesn't correspond to input length!");
        }

        int offset = this.headers.getInt(offsetIndex);
        int sectors = offset & 0xFF;
        offset >>>= 8;
        if (offset != 0) {
            if (sectors == requiredSectors) {
                //re-use old sectors
                offset *= RegionConstants.SECTOR_BYTES;
                int readable = chunk.readableBytes();
                chunk.readBytes(this.channel, offset, readable);
                this.channel.write(ByteBuffer.wrap(RegionConstants.EMPTY_SECTOR, 0, ((readable - 1 >> 12) + 1 << 12) - readable), offset + readable);
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
        if (chunk.readBytes(this.channel, offset * RegionConstants.SECTOR_BYTES, size) != size) {
            throw new IllegalStateException("Unable to write all bytes to disk!");
        }
        this.occupiedSectors.set(offset, offset + requiredSectors);
        this.headers.setInt(offsetIndex, (offset << 8) | requiredSectors);
        this.headers.setInt(offsetIndex + RegionConstants.SECTOR_BYTES, (int) (System.currentTimeMillis() / 1000L));
    }

    @Override
    protected void doDelete(int x, int z, int startIndex, int length, boolean erase) throws IOException {
        this.occupiedSectors.clear(startIndex, startIndex + length);

        if (erase) {
            ByteBuffer empty = ByteBuffer.wrap(RegionConstants.EMPTY_SECTOR);
            for (int i = 0; i < length; i++)    {
                empty.clear();
                int written = this.channel.write(empty, (startIndex + i) * (long) RegionConstants.SECTOR_BYTES);
                if (written != RegionConstants.SECTOR_BYTES) {
                    throw new IllegalStateException(String.format("Only wrote %d/%d bytes!", written, RegionConstants.SECTOR_BYTES));
                }
            }
        }
    }

    @Override
    protected void doFlush() throws IOException {
        ((MappedByteBuffer) this.headers.nioBuffer()).force();
    }

    @Override
    protected void doClose() throws IOException {
        if (!this.readOnly())   {
            this.doFlush();
        }
        PorkUtil.release(this.headers.nioBuffer());
    }
}
