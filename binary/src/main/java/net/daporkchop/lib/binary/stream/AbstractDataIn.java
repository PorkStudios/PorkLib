/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;
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
    protected static final int RESULT_EOF = -1;

    protected InputStream inputStream;

    protected boolean closed = false;

    @Override
    public int read() throws ClosedChannelException, IOException {
        this.ensureOpen();
        return this.read0();
    }

    @Override
    public int readUnsignedByte() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();
        int b = this.read0();
        if (b >= 0) {
            return b;
        } else {
            throw new EOFException();
        }
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, IOException {
        this.ensureOpen();
        checkRangeLen(dst.length, start, length);
        if (length == 0) {
            return 0;
        }
        return this.read0(dst, start, length);
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws ClosedChannelException, IOException {
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

    @Override
    public int read(@NonNull ByteBuf dst, int offset, int length) throws ClosedChannelException, IOException {
        this.ensureOpen();
        checkRangeLen(dst.capacity(), offset, length);

        int read;
        if (dst.hasMemoryAddress()) {
            read = toInt(this.read0(dst.memoryAddress() + offset, length));
        } else if (dst.hasArray()) {
            read = this.read0(dst.array(), dst.arrayOffset() + offset, length);
        } else {
            read = dst.setBytes(offset, this, length);
        }
        return read;
    }

    /**
     * Reads exactly one unsigned byte.
     *
     * @return an unsigned byte, or {@link #RESULT_EOF} if EOF is reached
     */
    protected abstract int read0() throws IOException;

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
    protected abstract int read0(@NonNull byte[] dst, int start, @Positive int length) throws IOException;

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
    protected abstract long read0(long addr, @Positive long length) throws IOException;

    /**
     * Makes a best-effort attempt to skip the requested number of bytes.
     * <p>
     * This will continue to read until the requested number of bytes have been skipped or EOF is reached.
     * <p>
     * If EOF was already reached, this method will return {@code 0}.
     *
     * @param count the number of bytes to skip. Will always be at least {@code 1L}
     * @return the number of bytes actually skipped, possibly {@code 0L}
     */
    protected abstract long skip0(@Positive long count) throws IOException;

    /**
     * Transfers between {@code 0L} and {@code count} bytes to the given {@link DataOut}.
     * <p>
     * This will continue to read until the requested number of bytes have been transferred or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@link #RESULT_EOF}.
     *
     * @param dst   the {@link DataOut} to write data to
     * @param count the maximum number of bytes to transfer
     * @return the actual number of bytes transferred or {@link #RESULT_EOF}
     */
    protected abstract long transfer0(@NonNull DataOut dst, @Positive long count) throws NoMoreSpaceException, IOException;

    /**
     * Gets an estimate of the number of bytes that may be read.
     * <p>
     * If EOF has been reached, this method will always return {@code 0L}.
     *
     * @return an estimate of the number of bytes that may be read
     */
    protected abstract long remaining0() throws IOException;

    /**
     * Actually closes this {@link DataIn}.
     * <p>
     * This method is guaranteed to only be called once, regardless of outcome.
     */
    protected abstract void close0() throws IOException;

    protected InputStream asStream0() {
        return new DataInAsInputStream(this);
    }

    @Override
    public long transferTo(@NonNull DataOut dst) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        long transferred = this.transfer0(dst, Long.MAX_VALUE);
        checkState(transferred != Long.MAX_VALUE, "somehow, we've transferred 2^63-1 bytes and there's still more left...");
        return transferred;
    }

    @Override
    public long transferTo(@NonNull DataOut dst, long count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        if (notNegative(count, "count") == 0L) {
            return 0L;
        }
        return this.transfer0(dst, count);
    }

    @Override
    public InputStream asInputStream() throws ClosedChannelException, IOException {
        this.ensureOpen();
        InputStream inputStream = this.inputStream;
        if (inputStream == null) {
            this.inputStream = inputStream = this.asStream0();
        }
        return inputStream;
    }

    @Override
    public long remaining() throws ClosedChannelException, IOException {
        this.ensureOpen();
        return this.remaining0();
    }

    @Override
    public int skipBytes(int n) throws ClosedChannelException, IOException {
        this.ensureOpen();
        if (n <= 0) {
            return 0;
        }
        return toInt(this.skip0(n));
    }

    @Override
    public long skipBytes(long n) throws ClosedChannelException, IOException {
        this.ensureOpen();
        if (n <= 0L) {
            return 0L;
        }
        return this.skip0(n);
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
        return !this.closed;
    }

    @Override
    public void close() throws IOException {
        if (this.isOpen()) {
            this.closed = true;
            this.close0();
        }
    }

    protected void ensureOpen() throws ClosedChannelException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
}
