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
    protected static final int RESULT_BLOCKING = -2;

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
            int b = this.read0();
            if (b >= 0) {
                dst[start++] = (byte) b;

                if (length == 1) {
                    //special case for single byte
                    return 1;
                }
            } else {
                return -1;
            }

            int read = this.readSome0(dst, start, length - 1, false);
            return read < 0 ? 1 : read + 1;
        }
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            PValidation.checkRangeLen(dst.length, start, length);
            if (length >= 1)    {
                this.readAll0(dst, start, length);
            }
        }
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws IOException {
        return this.doRead(dst, false);
    }

    @Override
    public int read(@NonNull ByteBuf dst, int start, int length) throws IOException {
        return this.doRead(dst, start, length, false);
    }

    @Override
    public int readBlocking(@NonNull ByteBuffer dst) throws IOException {
        return this.doRead(dst, true);
    }

    @Override
    public int readBlocking(@NonNull ByteBuf dst, int start, int length) throws IOException {
        return this.doRead(dst, start, length, true);
    }

    protected int doRead(@NonNull ByteBuffer dst, boolean blocking) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            int remaining = dst.remaining();
            if (remaining <= 0) {
                return 0;
            }

            int position = dst.position();
            int read = dst.isDirect()
                       ? toInt(this.readSome0(PUnsafe.pork_directBufferAddress(dst) + position, remaining, blocking))
                       : this.readSome0(dst.array(), dst.arrayOffset() + position, remaining, blocking);
            if (read > 0) {
                dst.position(position + read);
            } else if (read == RESULT_BLOCKING) {
                checkState(!blocking, "RESULT_BLOCKING was returned when blocking was expected");
                return 0;
            }
            return read;
        }
    }

    protected int doRead(@NonNull ByteBuf dst, int start, int length, boolean blocking) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            checkRangeLen(dst.maxCapacity(), start, length);
            if (length == 0) {
                return 0;
            }
            dst.ensureWritable(start + length - dst.writerIndex());

            int read;
            if (dst.hasMemoryAddress()) {
                read = toInt(this.readSome0(dst.memoryAddress() + start, length, blocking));
            } else if (dst.hasArray()) {
                read = this.readSome0(dst.array(), dst.arrayOffset() + start, length, blocking);
            } else {
                if (blocking) {
                    read = toInt(this.readBlocking(dst.nioBuffers(start, length)));
                } else {
                    read = dst.setBytes(start, this, length);
                }
            }
            return read == RESULT_BLOCKING ? 0 : read;
        }
    }

    @Override
    public int readFully(@NonNull ByteBuffer dst) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            int remaining = dst.remaining();
            if (remaining <= 0) {
                return 0;
            }

            int position = dst.position();
            if (dst.isDirect()) {
                this.readAll0(PUnsafe.pork_directBufferAddress(dst) + position, remaining);
            } else {
                this.readAll0(dst.array(), dst.arrayOffset() + position, remaining);
            }
            dst.position(position + remaining);
            return remaining;
        }
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int start, int length) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            checkRangeLen(dst.maxCapacity(), start, length);
            if (length == 0) {
                return 0;
            }
            int writerIndex = dst.writerIndex();
            dst.ensureWritable(start + length - writerIndex);

            if (dst.hasMemoryAddress()) {
                this.readAll0(dst.memoryAddress() + start, length);
            } else if (dst.hasArray()) {
                this.readAll0(dst.array(), dst.arrayOffset() + start, length);
            } else {
                for (ByteBuffer buffer : dst.nioBuffers(start, length)) {
                    this.readFully(buffer);
                }
            }
            return length;
        }
    }

    /**
     * Reads between {@code 0} and {@code length} bytes into the given {@code byte[]}.
     * <p>
     * This will continue to read until the requested number of bytes have been read, EOF is reached, or no further data can be read without blocking.
     * <p>
     * If at least 1 byte could be read, this method will return the number of bytes read.
     * <p>
     * If no bytes could be read due to EOF being reached, this method will return {@link #RESULT_EOF}.
     * <p>
     * If no bytes could be read because it would have blocked, this method will return {@link #RESULT_BLOCKING}.
     *
     * @param dst      the {@code byte[]} to read data into
     * @param start    the first index to start reading data into
     * @param length   the number of bytes to read. Will always be at least {@code 1}
     * @param blocking whether or not the read is allowed to block
     * @return the actual number of bytes read, or one of {@link #RESULT_EOF} or {@link #RESULT_BLOCKING}
     */
    protected abstract int readSome0(@NonNull byte[] dst, int start, int length, boolean blocking) throws IOException;

    /**
     * Reads between {@code 0L} and {@code length} bytes into the given memory address.
     * <p>
     * This will continue to read until the requested number of bytes have been read, EOF is reached, or no further data can be read without blocking.
     * <p>
     * If at least 1 byte could be read, this method will return the number of bytes read.
     * <p>
     * If no bytes could be read due to EOF being reached, this method will return {@link #RESULT_EOF}.
     * <p>
     * If no bytes could be read because it would have blocked, this method will return {@link #RESULT_BLOCKING}.
     *
     * @param addr     the base memory address to read data into
     * @param length   the number of bytes to read. Will always be at least {@code 1L}
     * @param blocking whether or not the read is allowed to block
     * @return the actual number of bytes read, or one of {@link #RESULT_EOF} or {@link #RESULT_BLOCKING}
     */
    protected abstract long readSome0(long addr, long length, boolean blocking) throws IOException;

    /**
     * Reads exactly {@code length} bytes into the given {@code byte[]}.
     * <p>
     * This will continue to read, possibly blocking, until the requested number of bytes have been read.
     *
     * @param dst    the {@code byte[]} to read data into
     * @param start  the first index to start reading data into
     * @param length the number of bytes to read. Will always be at least {@code 1}
     * @throws EOFException if EOF is reached at any point while reading
     */
    protected abstract void readAll0(@NonNull byte[] dst, int start, int length) throws EOFException, IOException;

    /**
     * Reads exactly {@code length} bytes into the given memory address.
     * <p>
     * This will continue to read, possibly blocking, until the requested number of bytes have been read.
     *
     * @param addr   the base memory address to read data into
     * @param length the number of bytes to read. Will always be at least {@code 1L}
     * @throws EOFException if EOF is reached at any point while reading
     */
    protected abstract void readAll0(long addr, long length) throws EOFException, IOException;

    /**
     * Makes a best-effort attempt to skip the requested number of bytes.
     * <p>
     * This will continue to read, possibly blocking, until the requested number of bytes have been skipped or EOF is reached.
     * <p>
     * If EOF was already reached, this method will return {@code 0}.
     *
     * @param count the number of bytes to skip
     * @return the number of bytes actually skipped, possibly {@code 0}
     */
    protected abstract long skip0(long count) throws IOException;

    /**
     * Gets an estimate of the number of bytes that may be read without blocking.
     * <p>
     * If EOF has been reached, this method may return either {@code 0} or {@code -1}.
     *
     * @return an estimate of the number of bytes that may be read without blocking
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
            return n != 0 ? toInt(this.skip0((long) positive(n))) : 0;
        }
    }

    @Override
    public long skipBytes(long n) throws IOException {
        synchronized (this.mutex()) {
            this.ensureOpen();
            return n != 0L ? this.skip0(positive(n)) : 0L;
        }
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
