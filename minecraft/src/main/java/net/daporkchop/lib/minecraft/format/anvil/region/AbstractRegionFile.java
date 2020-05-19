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

package net.daporkchop.lib.minecraft.format.anvil.region;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.exception.ReadOnlyException;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.minecraft.format.anvil.region.RegionConstants.*;

/**
 * Base implementation of {@link RegionFile}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractRegionFile implements RegionFile {
    protected static final OpenOption[] RO_OPEN_OPTIONS = {StandardOpenOption.READ};
    protected static final OpenOption[] RWC_OPEN_OPTIONS = {
            StandardOpenOption.CREATE,
            StandardOpenOption.READ,
            StandardOpenOption.WRITE
    };

    protected final File file;
    protected final Lock readLock;
    protected final Lock writeLock;
    @Getter(AccessLevel.NONE)
    protected final FileChannel channel;

    protected final boolean readOnly;

    public AbstractRegionFile(@NonNull File file, boolean readOnly) throws IOException {
        Path path = (this.file = file.getAbsoluteFile()).toPath();
        FileChannel channel = FileChannel.open(path, readOnly ? RO_OPEN_OPTIONS : RWC_OPEN_OPTIONS);
        this.channel = channel;

        if (!(this.readOnly = readOnly) && channel.tryLock() == null) {
            this.channel.close();
            throw new IOException(String.format("Unable to lock file: \"%s\"", file.getAbsolutePath()));
        }

        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    /**
     * @return a {@link ByteBuf} of at least {@link RegionConstants#HEADER_BYTES} for accessing this region's headers
     */
    protected abstract ByteBuf headersBuf();

    @Override
    public RawChunk read(int x, int z) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();

            ByteBuf headers = this.headersBuf();
            int offset = headers.getInt(getOffsetIndex(x, z));
            if (offset != 0) {
                ByteBuf data = this.doRead(x, z, getOffsetIndex(x, z), offset);
                return new RawChunk(headers.getInt(getTimestampIndex(x, z)) * 1000L, data);
            } else {
                return null;
            }
        } finally {
            this.readLock.unlock();
        }
    }

    protected abstract ByteBuf doRead(int x, int z, int offsetIndex, int offset) throws IOException;

    @Override
    public boolean write(int x, int z, @NonNull ByteBuf data, int version, long timestamp, boolean forceOverwrite) throws ReadOnlyException, IOException {
        this.assertWritable();
        this.writeLock.lock();
        try {
            this.assertOpen();

            int requiredSectors = ((data.readableBytes() + 5 - 1) >> 12) + 1;
            checkArg(requiredSectors < 256, "input data too large!");

            ByteBuf headers = this.headersBuf();
            if (forceOverwrite || headers.getInt(getOffsetIndex(x, z)) == 0 || Integer.toUnsignedLong(headers.getInt(getTimestampIndex(x, z))) * 1000L < timestamp) {
                ByteBuf composite = Unpooled.wrappedBuffer(
                        Unpooled.directBuffer(5, 5)
                                .writeInt(data.readableBytes() + 1)
                                .writeByte(version),
                        data);
                try {
                    this.doWrite(x, z, composite, timestamp, requiredSectors);
                    return true;
                } finally {
                    composite.release();
                }
            } else {
                return false;
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    protected abstract void doWrite(int x, int z, @NonNull ByteBuf chunk, long timestamp, int requiredSectors) throws IOException;

    @Override
    public boolean delete(int x, int z) throws ReadOnlyException, IOException {
        this.assertWritable();
        this.writeLock.lock();
        try {
            this.assertOpen();

            ByteBuf headers = this.headersBuf();
            int offset = headers.getInt(getOffsetIndex(x, z));
            if (offset != 0) {
                this.doDelete(x, z, offset >>> 8, offset & 0xFF);
                return true;
            } else {
                return false;
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    protected abstract void doDelete(int x, int z, int startIndex, int length) throws IOException;

    @Override
    public boolean contains(int x, int z) throws IOException {
        return false;
    }

    @Override
    public long timestamp(int x, int z) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();

            ByteBuf headers = this.headersBuf();
            return headers.getInt(getOffsetIndex(x, z)) != 0
                   ? Integer.toUnsignedLong(headers.getInt(RegionConstants.getTimestampIndex(x, z))) * 1000L
                   : -1L;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void defrag() throws ReadOnlyException, IOException {
        this.assertWritable();
        this.writeLock.lock();
        try {
            this.assertOpen();

            this.doDefrag();
        } finally {
            this.writeLock.unlock();
        }
    }

    protected abstract void doDefrag() throws IOException;

    @Override
    public void flush() throws IOException {
        if (!this.readOnly) {
            this.writeLock.lock();
            try {
                this.assertOpen();

                this.doFlush();
            } finally {
                this.writeLock.unlock();
            }
        }
    }

    protected abstract void doFlush() throws IOException;

    @Override
    public void close() {
        this.writeLock.lock();
        try {
            this.assertOpen();

            this.doClose();
            this.channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.writeLock.unlock();
        }
    }

    protected abstract void doClose() throws IOException;

    protected void assertOpen() throws IOException {
        if (!this.channel.isOpen()) {
            throw new ClosedChannelException();
        }
    }
}
