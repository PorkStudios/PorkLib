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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.Endianess;
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.encoding.ToBytes;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.CorruptedRegionException;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.ReadOnlyRegionException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static net.daporkchop.lib.minecraft.world.format.anvil.region.RegionConstants.*;

/**
 * A highly optimized rewrite of Mojang's original RegionFile class, designed with backwards-compatibility in mind.
 * <p>
 * Of course, compression modes other than 1 and 2 (GZip and DEFLATE respectively) won't work with a vanilla Minecraft implementation, so
 * they should be avoided.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class OverclockedRegionFile implements RegionFile {
    protected static final OpenOption[] RO_OPEN_OPTIONS = {StandardOpenOption.READ};
    protected static final OpenOption[] RW_OPEN_OPTIONS = {StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE};

    @Getter
    protected final File             file;
    protected final FileChannel      channel;
    protected final MappedByteBuffer index;
    protected final BitSet        occupiedSectors = new BitSet();
    protected final ReadWriteLock lock            = new ReentrantReadWriteLock();
    protected final AtomicBoolean open            = new AtomicBoolean(true);
    @Getter
    protected final boolean readOnly;

    public OverclockedRegionFile(@NonNull File file) throws IOException {
        this(file, false, false);
    }

    public OverclockedRegionFile(@NonNull File file, boolean readOnly, boolean writeRequired) throws CorruptedRegionException, IOException {
        file = file.getAbsoluteFile();
        PFiles.ensureDirectoryExists(file.getParentFile());

        {
            Path path = file.toPath();
            FileChannel channel;
            try {
                channel = FileChannel.open(path, readOnly ? RO_OPEN_OPTIONS : RW_OPEN_OPTIONS);
            } catch (IOException e) {
                if (readOnly || writeRequired) {
                    throw e;
                } else {
                    //try opening the file read-only
                    //if this fails, then we don't have any filesystem access and won't be able to do anything about it, so the exception is thrown
                    channel = FileChannel.open(path, RO_OPEN_OPTIONS);
                    readOnly = true;
                }
            }

            this.channel = channel;
            this.readOnly = readOnly;
        }

        long fileSize = this.channel.size();
        if (fileSize < SECTOR_BYTES * 2) {
            //region headers are incomplete, write empty headers
            try {
                //write the chunk offset table
                this.channel.write(ByteBuffer.wrap(EMPTY_SECTOR), 0L);
                //write another sector for the timestamp info
                this.channel.write(ByteBuffer.wrap(EMPTY_SECTOR), SECTOR_BYTES);
                //fileSize = SECTOR_BYTES << 1;
            } catch (NonWritableChannelException e) {
                throw new CorruptedRegionException(String.format("Cannot open read-only region \"%s\" as the headers are not complete", file.getAbsolutePath()));
            }
        } else if (!this.readOnly && (fileSize & 0xFFFL) != 0) {
            //the file size is not a multiple of 4KB, grow it
            this.channel.write(ByteBuffer.wrap(EMPTY_SECTOR, 0, (int) (fileSize & 0xFFFL)), fileSize);
        }

        this.index = this.channel.map(this.readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, 0, SECTOR_BYTES * 2);

        this.occupiedSectors.set(0, 2);
        //init occupied sectors bitset
        try {
            for (int i = SECTOR_INTS - 1; i >= 0; i--) {
                int offset = this.index.getInt(i << 2);
                if (offset != 0) {
                    this.occupiedSectors.set(offset >> 8, (offset >> 8) + (offset & 0xFF));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new CorruptedRegionException(String.format("Corrupt region headers in \"%s\"", file.getAbsolutePath()));
        }
        this.file = file;

        if (DEBUG_SECTORS) {
            System.out.println(this.occupiedSectors);
        }
    }

    @Override
    public ByteBuf readDirect(int x, int z) throws IOException {
        assertInBounds(x, z);

        this.lock.readLock().lock();
        try {
            this.assertOpen();

            int offset = this.getOffset(x, z);
            if (offset == 0) {
                return null;
            }

            int bytesToRead = (offset & 0xFF) * SECTOR_BYTES;
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.ioBuffer(bytesToRead);
            int read = buf.writeBytes(this.channel, (offset >> 8) * SECTOR_BYTES, bytesToRead);
            if (read != bytesToRead) {
                throw new IOException(String.format("Read %d/%d bytes!", read, bytesToRead));
            }
            return buf.slice(LENGTH_HEADER_SIZE, buf.getInt(0));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void writeDirect(int x, int z, @NonNull ByteBuf buf) throws ReadOnlyRegionException, IOException {
        assertInBounds(x, z);
        this.assertWritable();
        this.lock.writeLock().lock();
        try {
            this.assertOpen();

            int size = buf.readableBytes();
            if (size < CHUNK_HEADER_SIZE || buf.readableBytes() > (1 << 20)) {
                throw new IllegalArgumentException("Invalid input length!");
            } else if (buf.getInt(0) + LENGTH_HEADER_SIZE != size) {
                throw new IllegalArgumentException("Invalid chunk data: length header doesn't correspond to input length!");
            }

            int requiredSectors = size / SECTOR_BYTES + 1;
            int offset = this.getOffset(x, z);
            if (offset != 0) {
                if ((offset & 0xFF) == requiredSectors) {
                    //re-use old sectors
                    buf.readBytes(this.channel, (offset >> 8) * SECTOR_BYTES);
                    return;
                } else {
                    //clear old sectors to search for new ones
                    //this makes it faster and less prone to bugs than shrinking existing allocations
                    this.occupiedSectors.clear(offset >> 8, (offset >> 8) + (offset & 0xFF));
                }
            }
            offset = 0;
            int i = this.occupiedSectors.nextClearBit(0);
            SEARCH:
            while (offset == 0) {
                int j = 0;
                while (j < requiredSectors) {
                    if (this.occupiedSectors.get(i + j++)) {
                        i = this.occupiedSectors.nextClearBit(i + j);
                        continue SEARCH;
                    }
                }
                //if we get this far, we've found a sufficiently long clear stretch
                this.occupiedSectors.set(i, i + requiredSectors);
                this.setOffset(x, z, offset = requiredSectors | (i << 8));
            }
            if (buf.readBytes(this.channel, (offset >> 8) * SECTOR_BYTES, size) != size) {
                throw new IllegalStateException("Unable to write all bytes to disk!");
            }
            this.internal_updateTimestamp(x, z);
        } finally {
            this.lock.writeLock().unlock();
            if (buf.refCnt() > 0)   {
                buf.release();
            }
        }
    }

    @Override
    public boolean delete(int x, int z, boolean erase) throws ReadOnlyRegionException, IOException {
        assertInBounds(x, z);
        this.lock.writeLock().lock();
        try {
            this.assertOpen();
            this.assertWritable();

            int offset = this.getOffset(x, z);
            if (offset != 0) {
                this.setOffset(x, z, 0);
                this.internal_setTimestamp(x, z, 0);
                int start = offset >> 8;
                int end = start + (offset & 0xFF);
                this.occupiedSectors.clear(start, end);
                if (erase) {
                    ByteBuffer empty = ByteBuffer.wrap(EMPTY_SECTOR);
                    for (; start < end; start++) {
                        empty.clear();
                        int i = this.channel.write(empty, start * (long) SECTOR_BYTES);
                        if (i != SECTOR_BYTES) {
                            throw new IllegalStateException(String.format("Only wrote %d/%d bytes!", i, SECTOR_BYTES));
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public int getOffset(int x, int z) {
        assertInBounds(x, z);
        this.lock.readLock().lock();
        try {
            this.assertOpen();
            return this.index.getInt(getOffsetIndex(x, z));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void setOffset(int x, int z, int offset) {
        this.index.putInt(getOffsetIndex(x, z), offset);
    }

    @Override
    public int getTimestamp(int x, int z) {
        assertInBounds(x, z);
        this.lock.readLock().lock();
        try {
            this.assertOpen();
            return this.index.getInt(getTimestampIndex(x, z));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void updateTimestamp(int x, int z) {
        this.setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
    }

    private void internal_updateTimestamp(int x, int z) {
        this.internal_setTimestamp(x, z, (int) (System.currentTimeMillis() / 1000L));
    }

    public void setTimestamp(int x, int z, int timestamp) {
        assertInBounds(x, z);
        this.lock.writeLock().lock();
        try {
            this.assertOpen();
            this.internal_setTimestamp(x, z, timestamp);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void internal_setTimestamp(int x, int z, int timestamp) {
        this.index.putInt(getTimestampIndex(x, z), timestamp);
    }

    private void assertOpen() {
        if (!this.open.get()) {
            throw new IllegalStateException("Closed!");
        }
    }

    @Override
    public void flush() throws IOException, ReadOnlyRegionException {
        this.assertWritable();
        this.lock.readLock().lock();
        try {
            this.assertOpen();

            this.index.force();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.open.compareAndSet(true, false)) {
                if (!this.readOnly) {
                    this.index.force();
                }
                //i don't think that this actually helps anything
                PorkUtil.release(this.index);
                this.channel.close();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
