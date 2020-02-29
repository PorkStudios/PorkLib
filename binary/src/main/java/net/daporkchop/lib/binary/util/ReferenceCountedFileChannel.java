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

package net.daporkchop.lib.binary.util;

import io.netty.util.ReferenceCounted;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A {@link FileChannel} that is reference-counted.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class ReferenceCountedFileChannel extends FileChannel implements ReferenceCounted {
    protected static final long CLOSELOCK_OFFSET = PUnsafe.pork_getOffset(AbstractInterruptibleChannel.class, "closeLock");
    protected static final long OPEN_OFFSET      = PUnsafe.pork_getOffset(AbstractInterruptibleChannel.class, "open");

    /**
     * Wraps an existing {@link FileChannel} into a {@link ReferenceCountedFileChannel}.
     * <p>
     * This can be unsafe if used incorrectly, you are advised to use {@link #open(Path, OpenOption...)} instead!
     *
     * @param channel the {@link FileChannel} to wrap
     * @return the wrapped {@link FileChannel} as a {@link ReferenceCountedFileChannel}
     */
    public static ReferenceCountedFileChannel wrapUnsafe(@NonNull FileChannel channel) {
        return new ReferenceCountedFileChannel(channel);
    }

    /**
     * Opens a file as a {@link ReferenceCountedFileChannel}.
     *
     * @param path        the {@link Path} of the file to open
     * @param openOptions the {@link OpenOption}s to open the file with
     * @return the newly opened {@link ReferenceCountedFileChannel}
     * @throws IOException if an IO exception occurs you dummy
     * @see FileChannel#open(Path, OpenOption...)
     */
    public static ReferenceCountedFileChannel open(@NonNull Path path, OpenOption... openOptions) throws IOException {
        return new ReferenceCountedFileChannel(FileChannel.open(path, openOptions));
    }

    protected final FileChannel delegate;
    protected final Lock        readLock;
    protected final Lock        writeLock;

    @Getter
    protected volatile int refCnt = 1;

    protected ReferenceCountedFileChannel(@NonNull FileChannel delegate) {
        this.delegate = delegate;

        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    protected void assertOpen() {
        if (this.refCnt == 0) {
            throw new AlreadyReleasedException();
        }
    }

    //
    //
    // ReferenceCounted implementations
    //
    //

    @Override
    public ReferenceCountedFileChannel retain() {
        return this.retain(1);
    }

    @Override
    public ReferenceCountedFileChannel retain(int increment) {
        this.writeLock.lock();
        try {
            int refCnt = this.refCnt;
            if (refCnt == 0) {
                throw new AlreadyReleasedException();
            } else if (++refCnt < 0)     {
                //integer overflow
                throw new IllegalStateException();
            }
            this.refCnt = refCnt;
            return this;
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public ReferenceCountedFileChannel touch() {
        return this;
    }

    @Override
    public ReferenceCountedFileChannel touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        this.writeLock.lock();
        try {
            int refCnt = this.refCnt;
            if (refCnt == 0) {
                throw new AlreadyReleasedException();
            }
            if ((this.refCnt = --refCnt) > 0) {
                return false;
            }
        } finally {
            this.writeLock.unlock();
        }

        //close outside write lock to prevent race condition if close() is called at the same time
        synchronized (PUnsafe.getObject(this, CLOSELOCK_OFFSET)) {
            if (!PUnsafe.getBooleanVolatile(this, OPEN_OFFSET)) {
                throw new IllegalStateException("Not yet closed, but open flag is already set?!?");
            }
            PUnsafe.putBooleanVolatile(this, OPEN_OFFSET, false);
            try {
                this.delegate.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean release(int decrement) {
        if (decrement < 0) {
            throw new IllegalStateException(String.valueOf(decrement));
        } else if (decrement == 0) {
            return false;
        }
        this.writeLock.lock();
        try {
            boolean released = false;
            while (!released && decrement-- > 0) {
                released = this.release();
            }
            if (released && decrement > 0) {
                throw new AlreadyReleasedException();
            }
            return released;
        } finally {
            this.writeLock.unlock();
        }
    }

    //
    //
    // FileChannel implementations
    //
    //

    @Override
    public int read(ByteBuffer dst) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.read(dst);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.read(dsts, offset, length);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.write(src);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.write(srcs, offset, length);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public long position() throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.position();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public ReferenceCountedFileChannel position(long newPosition) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            this.delegate.position(newPosition);
            return this;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public long size() throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.size();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public ReferenceCountedFileChannel truncate(long size) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            this.delegate.truncate(size);
            return this;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void force(boolean metaData) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            this.delegate.force(metaData);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.transferTo(position, count, target);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.transferFrom(src, position, count);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.read(dst, position);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.write(src, position);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.map(mode, position, size);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.lock(position, size, shared);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        this.readLock.lock();
        try {
            this.assertOpen();
            return this.delegate.tryLock(position, size, shared);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    protected void implCloseChannel() throws IOException {
        //reset open flag
        PUnsafe.putBooleanVolatile(this, OPEN_OFFSET, true);

        //this will close the channel if it needs to
        this.release(this.refCnt());
    }
}
