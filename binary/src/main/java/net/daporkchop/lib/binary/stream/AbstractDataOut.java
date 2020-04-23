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
import net.daporkchop.lib.binary.stream.wrapper.DataOutAsOutputStream;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link DataOut}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDataOut implements DataOut {
    protected static final long CLOSED_OFFSET = PUnsafe.pork_getOffset(AbstractDataIn.class, "closed");

    protected static final int RESULT_BLOCKING = -1;

    protected OutputStream outputStream;

    protected volatile int closed = 0;

    @Override
    public void write(int b) throws IOException {
        this.ensureOpen();
        this.write0(b);
    }

    /**
     * Writes exactly one unsigned byte.
     */
    protected abstract void write0(int b) throws IOException;

    @Override
    public void write(@NonNull byte[] src, int start, int length) throws IOException {
        this.ensureOpen();
        checkRangeLen(src.length, start, length);
        if (length == 0) {
            return;
        } else if (length == 1) {
            this.write0(src[start]);
        } else {
            this.writeAll0(src, start, length);
        }
    }

    @Override
    public int write(@NonNull ByteBuffer src) throws IOException {
        this.ensureOpen();
        int remaining = src.remaining();
        if (remaining <= 0) {
            return 0;
        }

        int position = src.position();
        int written = src.isDirect()
                      ? toInt(this.writeSome0(PUnsafe.pork_directBufferAddress(src) + position, remaining))
                      : this.writeSome0(src.array(), src.arrayOffset() + position, remaining);

        if (written > 0) {
            src.position(position + written);
        }
        return written == RESULT_BLOCKING ? 0 : written;
    }

    @Override
    public int write(@NonNull ByteBuf src, int count) throws IOException {
        int readerIndex = src.readerIndex();
        int written = this.write(src, readerIndex, count);
        src.readerIndex(readerIndex + written);
        return written;
    }

    @Override
    public int write(@NonNull ByteBuf src, int start, int length) throws IOException {
        this.ensureOpen();
        checkRangeLen(src.capacity(), start, length);
        if (length == 0) {
            return 0;
        }

        int written;
        if (src.hasMemoryAddress()) {
            written = toInt(this.writeSome0(src.memoryAddress() + start, length));
        } else if (src.hasArray()) {
            written = this.writeSome0(src.array(), src.arrayOffset() + start, length);
        } else {
            written = src.getBytes(start, this, length);
        }
        return written == RESULT_BLOCKING ? 0 : written;
    }

    @Override
    public int writeFully(@NonNull ByteBuffer src) throws IOException {
        this.ensureOpen();
        int remaining = src.remaining();
        if (remaining <= 0) {
            return 0;
        }

        int position = src.position();
        if (src.isDirect()) {
            this.writeAll0(PUnsafe.pork_directBufferAddress(src) + position, remaining);
        } else {
            this.writeAll0(src.array(), src.arrayOffset() + position, remaining);
        }
        src.position(position + remaining);
        return remaining;
    }

    @Override
    public int writeFully(@NonNull ByteBuf src, int count) throws IOException {
        int readerIndex = src.readerIndex();
        int written = this.writeFully(src, readerIndex, count);
        src.readerIndex(readerIndex + written);
        return written;
    }

    @Override
    public int writeFully(@NonNull ByteBuf src, int start, int length) throws IOException {
        this.ensureOpen();
        checkRangeLen(src.capacity(), start, length);
        if (length == 0) {
            return 0;
        }

        if (src.hasMemoryAddress()) {
            this.writeAll0(src.memoryAddress() + start, length);
        } else if (src.hasArray()) {
            this.writeAll0(src.array(), src.arrayOffset() + start, length);
        } else {
            for (ByteBuffer buffer : src.nioBuffers(start, length)) {
                this.writeFully(buffer);
            }
        }
        return length;
    }

    /**
     * Writes between {@code 0} and {@code length} bytes from the given {@code byte[]}.
     * <p>
     * This will continue to write until the requested number of bytes have been written or no further data can be written without blocking.
     * <p>
     * If at least 1 byte could be written, this method will return the number of bytes written.
     * <p>
     * If no bytes could be written because it would have blocked, this method will return {@link #RESULT_BLOCKING}.
     *
     * @param src    the {@code byte[]} to write data from
     * @param start  the first index to start writing data from
     * @param length the number of bytes to write. Will always be at least {@code 1}
     * @return the actual number of bytes read, or {@link #RESULT_BLOCKING}
     */
    protected abstract int writeSome0(@NonNull byte[] src, int start, int length) throws IOException;

    /**
     * Writes between {@code 0} and {@code length} bytes from the given memory address.
     * <p>
     * This will continue to write until the requested number of bytes have been written or no further data can be written without blocking.
     * <p>
     * If at least 1 byte could be written, this method will return the number of bytes written.
     * <p>
     * If no bytes could be written because it would have blocked, this method will return {@link #RESULT_BLOCKING}.
     *
     * @param addr   the base memory address to write data from
     * @param length the number of bytes to write. Will always be at least {@code 1L}
     * @return the actual number of bytes read, or {@link #RESULT_BLOCKING}
     */
    protected abstract long writeSome0(long addr, long length) throws IOException;

    /**
     * Writes exactly {@code length} bytes from the given {@code byte[]}.
     * <p>
     * This will continue to write, possibly blocking, until the requested number of bytes have been written.
     *
     * @param src    the {@code byte[]} to write data from
     * @param start  the first index to start writing data from
     * @param length the number of bytes to write. Will always be at least {@code 1}
     */
    protected abstract void writeAll0(@NonNull byte[] src, int start, int length) throws IOException;

    /**
     * Writes exactly {@code length} bytes from the given memory address.
     * <p>
     * This will continue to write, possibly blocking, until the requested number of bytes have been written.
     *
     * @param addr   the base memory address to write data from
     * @param length the number of bytes to write. Will always be at least {@code 1L}
     */
    protected abstract void writeAll0(long addr, long length) throws IOException;

    /**
     * Flushes any data buffered by this {@link DataOut}.
     */
    protected abstract void flush0() throws IOException;

    /**
     * Actually closes this {@link DataOut}.
     * <p>
     * This method is guaranteed to only be called once, regardless of outcome.
     *
     * @throws IOException if an IO exception occurs you dummy
     */
    protected abstract void close0() throws IOException;

    protected Object mutex()    {
        return this;
    }

    protected OutputStream asStream0() {
        return new DataOutAsOutputStream(this);
    }

    @Override
    public final OutputStream asOutputStream() {
        OutputStream outputStream = this.outputStream;
        if (outputStream == null) {
            synchronized (this.mutex()) {
                if ((outputStream = this.outputStream) == null) {
                    this.outputStream = outputStream = this.asStream0();
                }
            }
        }
        return outputStream;
    }

    @Override
    public final void flush() throws IOException {
        this.ensureOpen();
        this.flush0();
    }

    @Override
    public boolean isOpen() {
        return this.closed == 0;
    }

    @Override
    public final void close() throws IOException {
        if (PUnsafe.compareAndSwapInt(this, CLOSED_OFFSET, 0, 1)) {
            this.close0();
        }
    }

    protected final void ensureOpen() throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
}
