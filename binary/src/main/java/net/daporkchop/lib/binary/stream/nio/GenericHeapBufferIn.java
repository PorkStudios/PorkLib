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

package net.daporkchop.lib.binary.stream.nio;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.AbstractHeapDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.binary.util.PNioBuffers;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from any heap-based {@link ByteBuffer}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericHeapBufferIn extends AbstractHeapDataIn {
    protected ByteBuffer delegate;

    public GenericHeapBufferIn(@NonNull ByteBuffer delegate) {
        checkArg(!delegate.isDirect(), "delegate may not be direct!");
        this.delegate = delegate;
    }

    @Override
    protected int read0() throws IOException {
        //BufferUnderflowException can't be thrown because we never call get() unless hasRemaining() is true
        if (this.delegate.hasRemaining()) {
            return this.delegate.get() & 0xFF;
        } else {
            return RESULT_EOF;
        }
    }

    @Override
    protected int read0(@NonNull byte[] dst, int start, @Positive int length) throws IOException {
        //BufferUnderflowException can't be thrown because we never read more than remaining()
        int count = min(this.delegate.remaining(), length);
        if (count <= 0) {
            return RESULT_EOF;
        } else {
            this.delegate.get(dst, start, count);
            return count;
        }
    }

    @Override
    protected long read0(long addr, @Positive long length) throws IOException {
        //BufferUnderflowException can't be thrown because we never read more than remaining()
        int count = toInt(min(this.delegate.remaining(), length));
        if (count <= 0) {
            return RESULT_EOF;
        }

        //we don't have direct access to the array, so we'll have to fall back to copying one byte at a time
        //  (JIT might be smart enough to optimize this into a sequential memory copy, but don't bank on it - that's why we have ArrayHeapBufferIn)
        int position = PNioBuffers.skipForRead(this.delegate, count);
        for (int i = 0; i < count; i++) {
            PUnsafe.putByte(addr + i, this.delegate.get(position + i));
        }

        return count;
    }

    @Override
    protected long skip0(@Positive long count) throws IOException {
        //BufferUnderflowException can't be thrown because we never skip more than remaining()
        int countI = (int) min(count, this.delegate.remaining());
        PNioBuffers.skipForRead(this.delegate, countI); //count is always at least 1L, so it'll always be valid
        return countI;
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, @Positive long count) throws NoMoreSpaceException, IOException {
        //BufferUnderflowException can't be thrown because we never transfer more than remaining()
        count = min(count, this.delegate.remaining());
        int oldLimit = this.delegate.limit();
        this.delegate.limit(this.delegate.position() + (int) count);
        int read = dst.write(this.delegate);
        checkState(read == count, "only transferred %s/%s bytes?!?", read, count);
        this.delegate.limit(oldLimit);
        return count;
    }

    @Override
    protected long remaining0() throws IOException {
        return this.delegate.remaining();
    }

    @Override
    protected void close0() throws IOException {
        this.delegate = null;
    }

    //
    // primitives
    //

    @Override
    public byte readByte() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.get();
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }
}
