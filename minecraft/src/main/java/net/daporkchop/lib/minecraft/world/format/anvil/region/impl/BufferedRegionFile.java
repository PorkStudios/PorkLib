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

package net.daporkchop.lib.minecraft.world.format.anvil.region.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.daporkchop.lib.math.primitive.PMath;
import net.daporkchop.lib.minecraft.world.format.anvil.region.AbstractRegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionConstants;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionOpenOptions;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.CorruptedRegionException;

import java.io.File;
import java.io.IOException;

/**
 * An implementation of {@link net.daporkchop.lib.minecraft.world.format.anvil.region.RegionFile} which loads the entire region file into memory and performs
 * operations on it there.
 *
 * @author DaPorkchop_
 */
public final class BufferedRegionFile extends AbstractRegionFile {
    protected ByteBuf buf;

    protected boolean dirty;

    public BufferedRegionFile(@NonNull File file, @NonNull RegionOpenOptions options) throws IOException {
        super(file, options);

        int size = (int) this.channel.size();
        if (size < RegionConstants.HEADER_BYTES)    {
            this.buf = Unpooled.directBuffer(RegionConstants.HEADER_BYTES);
            //initialize with empty headers
            this.buf.writeBytes(RegionConstants.EMPTY_SECTOR).writeBytes(RegionConstants.EMPTY_SECTOR);
            this.dirty = true;
        } else {
            (this.buf = Unpooled.directBuffer(PMath.roundUp(size, RegionConstants.SECTOR_BYTES)).clear()).writeBytes(this.channel, 0L, size);
            if (size != this.buf.readableBytes())   {
                this.buf.release();
                this.channel.close();
                throw new IllegalStateException(String.format("Only read %d bytes!", this.buf.readableBytes()));
            }
            this.buf.writeBytes(RegionConstants.EMPTY_SECTOR, 0, this.buf.writableBytes()); //pad to 4KiB boundary
        }
    }

    @Override
    protected boolean optionsSupported(@NonNull RegionOpenOptions options) {
        return options.mode() == Mode.BUFFER_FULL;
    }

    @Override
    protected ByteBuf headersBuf() {
        return this.buf;
    }

    @Override
    protected ByteBuf doRead(int x, int z, int offsetIndex, int offset) throws IOException {
        int pos = (offset >>> 8) * RegionConstants.SECTOR_BYTES;
        int length = this.buf.getInt(pos);
        int maxLength = ((offset & 0xFF) * RegionConstants.SECTOR_BYTES) - RegionConstants.LENGTH_HEADER_SIZE;
        if (length < 0 || length > maxLength) {
            throw new CorruptedRegionException(String.format("Length at sector %d (offset %d) is %d! (should be max. %d)", offset >>> 8, pos, length, maxLength));
        }
        //return Unpooled.wrappedBuffer(this.buf.memoryAddress() + pos + RegionConstants.LENGTH_HEADER_SIZE, length, false);
        return this.buf.retainedSlice(pos + RegionConstants.LENGTH_HEADER_SIZE, length);
    }

    @Override
    protected void doWrite(int x, int z, long time, int offsetIndex, ByteBuf chunk, int requiredSectors) throws IOException {
        int oldSize = this.buf.getInt(offsetIndex) & 0xFF;

        //allocate new buffer which will contain the region data
        ByteBuf next = Unpooled.directBuffer(this.buf.capacity() - oldSize * RegionConstants.SECTOR_BYTES + requiredSectors * RegionConstants.SECTOR_BYTES);
        next.writeBytes(RegionConstants.EMPTY_SECTOR).writeBytes(RegionConstants.EMPTY_SECTOR);

        //copy every region into the new buffer
        int sector = 2;
        for (int index = 0; index < RegionConstants.SECTOR_BYTES; index += 4)   {
            ByteBuf buf;
            int timestamp;
            if (index != offsetIndex)   {
                int chunkOffset = this.buf.getInt(index);
                if (chunkOffset == 0)   {
                    //don't copy this chunk if it doesn't exist
                    continue;
                }
                buf = this.buf.slice((chunkOffset >>> 8) * RegionConstants.SECTOR_BYTES, (chunkOffset & 0xFF) * RegionConstants.SECTOR_BYTES);
                timestamp = this.buf.getInt(index + RegionConstants.SECTOR_BYTES); //copy old timestamp
            } else if (chunk == null) {
                //if the chunk is null then it's being deleted, so we can safely do nothing
                continue;
            } else {
                //if the current header index is the headers of the chunk, use the actual chunk
                buf = chunk;
                timestamp = (int) (time / 1000L); //compute new timestamp
            }
            next.writeBytes(buf);

            int writerIndex = next.writerIndex();
            //pad with zeroes
            next.writeBytes(RegionConstants.EMPTY_SECTOR, 0, ((writerIndex - 1 >> 12) + 1 << 12) - writerIndex);

            int nextSector = (buf.writerIndex() - 1 >> 12) + 1; //compute next chunk sector
            next.setInt(index, (sector << 8) | (nextSector - sector));
            next.setInt(index + RegionConstants.SECTOR_BYTES, timestamp);
            sector = nextSector;
        }

        this.buf.release();
        this.buf = next;
        this.dirty = true;
    }

    @Override
    protected void doDelete(int x, int z, int startIndex, int length, boolean erase) throws IOException {
        this.doWrite(x, z, System.currentTimeMillis(), RegionConstants.getOffsetIndex(x, z), null, 0);
    }

    @Override
    protected void doFlush() throws IOException {
        //only write if dirty
        if (this.dirty && this.buf.getBytes(0, this.channel, 0L, this.buf.readableBytes()) != this.buf.readableBytes()) {
            throw new IllegalStateException("Unable to write entire region!");
        }
        this.dirty = false;
    }

    @Override
    protected void doClose() throws IOException {
        if (!this.readOnly) {
            //save if needed
            this.doFlush();
        }
        this.buf.release();
        this.buf = null;
    }
}
