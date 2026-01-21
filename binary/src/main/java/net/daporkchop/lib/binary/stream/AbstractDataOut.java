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
import net.daporkchop.lib.binary.stream.wrapper.DataOutAsOutputStream;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;
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
    protected OutputStream outputStream;

    protected boolean closed = false;

    @Override
    public void write(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        this.write0(b);
    }

    @Override
    public void write(@NonNull byte[] src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        checkRangeLen(src.length, offset, length);

        switch (length) {
            case 0:
                return;
            case 1:
                this.write0(src[offset]);
                return;
            default:
                this.write0(src, offset, length);
        }
    }

    @Override
    public int write(@NonNull ByteBuffer src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        int remaining = src.remaining();
        if (remaining <= 0) {
            return 0;
        }

        int position = src.position();
        if (src.isDirect()) {
            this.write0(PUnsafe.pork_directBufferAddress(src) + position, remaining);
        } else {
            this.write0(src.array(), src.arrayOffset() + position, remaining);
        }
        src.position(position + remaining);
        return remaining;
    }

    @Override
    public int write(@NonNull ByteBuf src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        checkRangeLen(src.capacity(), offset, length);
        if (length == 0) {
            return 0;
        }

        if (src.hasMemoryAddress()) {
            this.write0(src.memoryAddress() + offset, length);
        } else if (src.hasArray()) {
            this.write0(src.array(), src.arrayOffset() + offset, length);
        } else {
            src.getBytes(offset, this, length);
        }
        return length;
    }

    /**
     * Writes exactly one unsigned byte.
     */
    protected abstract void write0(int b) throws NoMoreSpaceException, IOException;

    /**
     * Writes exactly {@code length} bytes from the given {@code byte[]}.
     * <p>
     * This will continue to write, possibly blocking, until the requested number of bytes have been written.
     *
     * @param src    the {@code byte[]} to write data from
     * @param start  the first index to start writing data from
     * @param length the number of bytes to write. Will always be at least {@code 1}
     */
    protected abstract void write0(@NonNull byte[] src, int start, @Positive int length) throws NoMoreSpaceException, IOException;

    /**
     * Writes exactly {@code length} bytes from the given memory address.
     * <p>
     * This will continue to write, possibly blocking, until the requested number of bytes have been written.
     *
     * @param addr   the base memory address to write data from
     * @param length the number of bytes to write. Will always be at least {@code 1L}
     */
    protected abstract void write0(long addr, @Positive long length) throws NoMoreSpaceException, IOException;

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

    protected OutputStream asStream0() {
        return new DataOutAsOutputStream(this);
    }

    @Override
    public long transferFrom(@NonNull DataIn src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        return src.transferTo(this);
    }

    @Override
    public long transferFrom(@NonNull DataIn src, long count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();
        return src.transferTo(this, notNegative(count, "count"));
    }

    @Override
    public final OutputStream asOutputStream() throws ClosedChannelException, IOException {
        this.ensureOpen();
        OutputStream outputStream = this.outputStream;
        if (outputStream == null) {
            this.outputStream = outputStream = this.asStream0();
        }
        return outputStream;
    }

    @Override
    public final void flush() throws ClosedChannelException, IOException {
        this.ensureOpen();
        this.flush0();
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
    public final void close() throws IOException {
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
