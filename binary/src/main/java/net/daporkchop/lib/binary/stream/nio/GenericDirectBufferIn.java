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
import net.daporkchop.lib.binary.stream.AbstractDirectDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
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
 * An implementation of {@link DataIn} that can read from any direct {@link ByteBuffer}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericDirectBufferIn extends AbstractDirectDataIn {
    static {
        //we copy data between byte[]s and direct buffers
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    protected ByteBuffer delegate;

    public GenericDirectBufferIn(@NonNull ByteBuffer delegate) {
        checkArg(delegate.isDirect(), "delegate must be direct!");
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

        int position = PNioBuffers.skipForRead(this.delegate, count);
        PUnsafe.copyMemory(PUnsafe.pork_directBufferAddress(this.delegate) + position, addr, count);
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
    protected long transfer0(@NonNull DataOut dst, @Positive long count) throws IOException {
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

    @Override
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Short.BYTES);
            return PUnsafe.getUnalignedShortBE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Short.BYTES);
            return PUnsafe.getUnalignedShortLE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Character.BYTES);
            return PUnsafe.getUnalignedCharBE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Character.BYTES);
            return PUnsafe.getUnalignedCharLE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Integer.BYTES);
            return PUnsafe.getUnalignedIntBE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Integer.BYTES);
            return PUnsafe.getUnalignedIntLE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Long.BYTES);
            return PUnsafe.getUnalignedLongBE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Long.BYTES);
            return PUnsafe.getUnalignedLongLE(PUnsafe.pork_directBufferAddress(this.delegate) + position);
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }
}
