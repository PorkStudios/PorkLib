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

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
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
 * An implementation of {@link DataIn} that can read from a heap-based {@link ByteBuffer} which {@link ByteBuffer#hasArray() has an array}.
 *
 * @author DaPorkchop_
 */
public class ArrayHeapBufferIn extends GenericHeapBufferIn {
    static {
        //we copy data between byte[]s and direct buffers, and load primitives directly from a byte[]
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    protected byte[] array;
    protected final int arrayOffset;

    public ArrayHeapBufferIn(@NonNull ByteBuffer delegate) {
        super(delegate);
        checkArg(delegate.hasArray(), "delegate must have an array!");

        this.array = delegate.array();
        this.arrayOffset = delegate.arrayOffset();
    }

    @Override
    protected long read0(long addr, @Positive long length) throws IOException {
        //BufferUnderflowException can't be thrown because we never read more than remaining()
        int count = toInt(min(this.delegate.remaining(), length));
        if (count <= 0) {
            return RESULT_EOF;
        }

        //copy directly from the array to off-heap memory
        int position = PNioBuffers.skipForRead(this.delegate, count);
        PUnsafe.copyMemory(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), null, addr, count);
        return count;
    }

    @Override
    protected void close0() throws IOException {
        super.close0();
        this.array = null;
    }

    //
    // primitives
    //

    @Override
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Short.BYTES);
            return PUnsafe.getUnalignedShortBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Short.BYTES);
            return PUnsafe.getUnalignedShortLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Character.BYTES);
            return PUnsafe.getUnalignedCharBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Character.BYTES);
            return PUnsafe.getUnalignedCharLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Integer.BYTES);
            return PUnsafe.getUnalignedIntBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Integer.BYTES);
            return PUnsafe.getUnalignedIntLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Long.BYTES);
            return PUnsafe.getUnalignedLongBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForRead(this.delegate, Long.BYTES);
            return PUnsafe.getUnalignedLongLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position));
        } catch (BufferUnderflowException e) {
            throw new EOFException();
        }
    }
}
