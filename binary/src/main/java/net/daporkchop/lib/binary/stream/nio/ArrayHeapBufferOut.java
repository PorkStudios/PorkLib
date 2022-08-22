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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.PNioBuffers;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataOut} that can write to a heap-based {@link ByteBuffer} which {@link ByteBuffer#hasArray() has an array}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class ArrayHeapBufferOut extends GenericHeapBufferOut {
    static {
        //we copy data between byte[]s and direct buffers, and store primitives directly to a byte[]
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    protected byte[] array;
    protected final int arrayOffset;

    public ArrayHeapBufferOut(@NonNull ByteBuffer delegate) {
        super(delegate);
        checkArg(delegate.hasArray(), "delegate must have an array!");

        this.array = delegate.array();
        this.arrayOffset = delegate.arrayOffset();
    }

    @Override
    protected void write0(long addr, long length) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, toInt(length, "length"));

            //copy directly from off-heap memory to the array
            PUnsafe.copyMemory(null, addr, this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), length);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
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
    public void writeShort(int v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Short.BYTES);
            PUnsafe.putUnalignedShortBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), (short) v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeShortLE(int v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Short.BYTES);
            PUnsafe.putUnalignedShortLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), (short) v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Character.BYTES);
            PUnsafe.putUnalignedCharBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), (char) v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Character.BYTES);
            PUnsafe.putUnalignedCharLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), (char) v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Integer.BYTES);
            PUnsafe.putUnalignedIntBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeIntLE(int v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Integer.BYTES);
            PUnsafe.putUnalignedIntLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Long.BYTES);
            PUnsafe.putUnalignedLongBE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeLongLE(long v) throws IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Long.BYTES);
            PUnsafe.putUnalignedLongLE(this.array, PUnsafe.arrayByteElementOffset(this.arrayOffset + position), v);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }
}
