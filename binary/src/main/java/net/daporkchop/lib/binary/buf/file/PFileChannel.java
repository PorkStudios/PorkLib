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

package net.daporkchop.lib.binary.buf.file;

import lombok.NonNull;
import net.daporkchop.lib.binary.buf.AbstractCloseablePorkBuf;
import net.daporkchop.lib.binary.buf.AbstractPorkBuf;
import net.daporkchop.lib.binary.buf.PorkBuf;
import net.daporkchop.lib.binary.buf.exception.PorkBufIOException;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
//TODO: file locking
public class PFileChannel extends AbstractCloseablePorkBuf {
    protected final FileDescriptor descriptor;
    protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();

    public PFileChannel(@NonNull File file) throws IOException  {
        this(new RandomAccessFile(file, "rw"));
    }

    public PFileChannel(@NonNull RandomAccessFile raf) throws IOException  {
        this(raf.getFD());
    }

    public PFileChannel(@NonNull FileDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public PFileChannel(@NonNull FileDescriptor descriptor, long maxCapacity) {
        super(maxCapacity);

        this.descriptor = descriptor;
    }

    @Override
    public PorkBuf putByte(byte b) {
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            Long pos = PorkUtil.SMALL_MALLOC_POOL.get();
            PUnsafe.putByte(pos, b);
            PFileDispatcherImpl.WRITE0.write(this.descriptor, pos, 1);
            PorkUtil.SMALL_MALLOC_POOL.release(pos);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public PorkBuf putByte(long index, byte b) {
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            Long pos = PorkUtil.SMALL_MALLOC_POOL.get();
            PUnsafe.putByte(pos, b);
            PFileDispatcherImpl.PWRITE0.pwrite(this.descriptor, pos, 1, index);
            PorkUtil.SMALL_MALLOC_POOL.release(pos);
            return this;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public byte getByte() {
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            Long pos = PorkUtil.SMALL_MALLOC_POOL.get();
            PFileDispatcherImpl.READ0.read(this.descriptor, pos, 1);
            byte b = PUnsafe.getByte(pos);
            PorkUtil.SMALL_MALLOC_POOL.release(pos);
            return b;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public byte getByte(long index) {
        this.closeLock.readLock().lock();
        try {
            this.ensureOpen();
            Long pos = PorkUtil.SMALL_MALLOC_POOL.get();
            PFileDispatcherImpl.PREAD0.pread(this.descriptor, pos, 1, index);
            byte b = PUnsafe.getByte(pos);
            PorkUtil.SMALL_MALLOC_POOL.release(pos);
            return b;
        } catch (IOException e) {
            throw new PorkBufIOException(e);
        } finally {
            this.closeLock.readLock().unlock();
        }
    }

    @Override
    public boolean isClosed() {
        return this.descriptor.valid();
    }

    @Override
    public void close() throws IOException {
        this.closeLock.writeLock().lock();
        try {
            PFileDispatcherImpl.PRECLOSE0.preClose(this.descriptor);
            PFileDispatcherImpl.CLOSE0.close(this.descriptor);
        } finally {
            this.closeLock.writeLock().unlock();
        }
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long memorySize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object refObj() {
        throw new UnsupportedOperationException();
    }
}
