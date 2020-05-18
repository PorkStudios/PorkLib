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

package net.daporkchop.lib.binary.stream;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.wrapper.DataInAsInputStream;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link DataIn}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDataIn implements DataIn {
    protected static final long CLOSED_OFFSET = PUnsafe.pork_getOffset(AbstractDataIn.class, "closed");

    protected static final int RESULT_EOF = -1;

    protected InputStream inputStream;

    protected volatile int closed = 0;

    @Override
    public int read() throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            return this.read0();
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            int b = this.read0();
            if (b >= 0) {
                return b;
            } else {
                throw new EOFException();
            }
        }
    }

    /**
     * Reads exactly one unsigned byte.
     *
     * @return an unsigned byte, or {@code -1} if EOF is reached
     */
    protected abstract int read0() throws IOException;

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            PValidation.checkRangeLen(dst.length, start, length);
            if (length == 0) {
                return 0;
            }
            return this.read0(dst, start, length);
        }
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            int remaining = dst.remaining();
            if (remaining <= 0) {
                return 0;
            }

            int position = dst.position();
            int read = dst.isDirect()
                       ? toInt(this.read0(PUnsafe.pork_directBufferAddress(dst) + position, remaining))
                       : this.read0(dst.array(), dst.arrayOffset() + position, remaining);
            if (read > 0) {
                dst.position(position + read);
            }
            return read;
        }
    }

    @Override
    public int read(@NonNull ByteBuf dst, int start, int length) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            checkRangeLen(dst.maxCapacity(), start, length);
            if (length == 0) {
                return 0;
            }
            dst.ensureWritable(start + length - dst.writerIndex());

            int read;
            if (dst.hasMemoryAddress()) {
                read = toInt(this.read0(dst.memoryAddress() + start, length));
            } else if (dst.hasArray()) {
                read = this.read0(dst.array(), dst.arrayOffset() + start, length);
            } else {
                read = dst.setBytes(start, this, length);
            }
            return read;
        }
    }

    /**
     * Reads between {@code 0} and {@code length} bytes into the given {@code byte[]}.
     * <p>
     * This will continue to read until the requested number of bytes have been read or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@link #RESULT_EOF}.
     *
     * @param dst    the {@code byte[]} to read data into
     * @param start  the first index to start reading data into
     * @param length the number of bytes to read. Will always be at least {@code 1}
     * @return the actual number of bytes read or {@link #RESULT_EOF}
     */
    protected abstract int read0(@NonNull byte[] dst, int start, int length) throws IOException;

    /**
     * Reads between {@code 0L} and {@code length} bytes into the given memory address.
     * <p>
     * This will continue to read until the requested number of bytes have been read or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@link #RESULT_EOF}.
     *
     * @param addr   the base memory address to read data into
     * @param length the number of bytes to read. Will always be at least {@code 1L}
     * @return the actual number of bytes read or {@link #RESULT_EOF}
     */
    protected abstract long read0(long addr, long length) throws IOException;

    /**
     * Makes a best-effort attempt to skip the requested number of bytes.
     * <p>
     * This will continue to read until the requested number of bytes have been skipped or EOF is reached.
     * <p>
     * If EOF was already reached, this method will return {@code 0}.
     *
     * @param count the number of bytes to skip. Will always be at least {@code 1L}
     * @return the number of bytes actually skipped, possibly {@code 0}
     */
    protected abstract long skip0(long count) throws IOException;

    /**
     * Transfers between {@code 0} and {@code count} bytes to the given {@link DataOut}.
     * <p>
     * This will continue to read until the requested number of bytes have been transferred or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@link #RESULT_EOF}.
     *
     * @param dst   the {@link DataOut} to write data to
     * @param count the maximum number of bytes to transfer. If negative, data should be transferred until EOF is reached. Will never be {@code 0}
     * @return the actual number of bytes transferred or {@link #RESULT_EOF}
     */
    protected abstract long transfer0(@NonNull DataOut dst, long count) throws IOException;

    /**
     * Gets an estimate of the number of bytes that may be read.
     * <p>
     * If EOF has been reached, this method may return either {@code 0} or {@code -1}.
     *
     * @return an estimate of the number of bytes that may be read
     */
    protected abstract long remaining0() throws IOException;

    /**
     * Actually closes this {@link DataIn}.
     * <p>
     * This method is guaranteed to only be called once, regardless of outcome.
     *
     * @throws IOException if an IO exception occurs you dummy
     */
    protected abstract void close0() throws IOException;

    protected Object mutex() {
        return this;
    }

    protected InputStream asStream0() {
        return new DataInAsInputStream(this);
    }

    @Override
    public long transferTo(@NonNull DataOut dst) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            return this.transfer0(dst, -1L);
        }
    }

    @Override
    public long transferTo(@NonNull DataOut dst, long count) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            if (positive(count, "count") == 0L)    {
                return 0L;
            }
            return this.transfer0(dst, count);
        }
    }

    @Override
    public InputStream asInputStream() {
        InputStream inputStream = this.inputStream;
        if (inputStream == null) {
            synchronized (this.mutex()) {
                if ((inputStream = this.inputStream) == null) {
                    this.inputStream = inputStream = this.asStream0();
                }
            }
        }
        return inputStream;
    }

    @Override
    public long remaining() throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            return this.remaining0();
        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            if (positive(n, "n") == 0)  {
                return 0;
            }
            return toInt(this.skip0(n));
        }
    }

    @Override
    public long skipBytes(long n) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            if (positive(n, "n") == 0L)  {
                return 0L;
            }
            return toInt(this.skip0(n));
        }
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public boolean isHeap() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return this.closed == 0;
    }

    @Override
    public void close() throws IOException {
        synchronized (this.mutex()) {
            if (PUnsafe.compareAndSwapInt(this, CLOSED_OFFSET, 0, 1)) {
                this.close0();
            }
        }
    }

    protected void ensureOpen() throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
}
