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
import net.daporkchop.lib.binary.stream.AbstractHeapDataOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.binary.util.PNioBuffers;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataOut} that can read from any direct {@link ByteBuffer}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericDirectBufferOut extends AbstractHeapDataOut {
    static {
        //we store primitives directly to a byte[]
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    protected ByteBuffer delegate;

    public GenericDirectBufferOut(@NonNull ByteBuffer delegate) {
        checkArg(delegate.isDirect(), "delegate must be direct!");
        this.delegate = delegate;
    }

    @Override
    protected void write0(int b) throws NoMoreSpaceException, IOException {
        try {
            this.delegate.put((byte) b);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, @Positive int length) throws NoMoreSpaceException, IOException {
        try {
            this.delegate.put(src, start, length);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    protected void write0(long addr, @Positive long length) throws NoMoreSpaceException, IOException {
        try {
            int position = PNioBuffers.skipForWrite(this.delegate, toInt(length, "length"));
            PUnsafe.copyMemory(addr, PUnsafe.pork_directBufferAddress(this.delegate) + position, length);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    protected void flush0() throws IOException {
        //no-op
    }

    @Override
    protected void close0() throws IOException {
        this.delegate = null;
    }

    //
    // primitives
    //

    @Override
    public void writeByte(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            this.delegate.put((byte) b);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Short.BYTES);
            PUnsafe.putUnalignedShortBE(PUnsafe.pork_directBufferAddress(this.delegate) + position, (short) v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Short.BYTES);
            PUnsafe.putUnalignedShortLE(PUnsafe.pork_directBufferAddress(this.delegate) + position, (short) v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Character.BYTES);
            PUnsafe.putUnalignedCharBE(PUnsafe.pork_directBufferAddress(this.delegate) + position, (char) v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Character.BYTES);
            PUnsafe.putUnalignedCharLE(PUnsafe.pork_directBufferAddress(this.delegate) + position, (char) v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Integer.BYTES);
            PUnsafe.putUnalignedIntBE(PUnsafe.pork_directBufferAddress(this.delegate) + position, v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Integer.BYTES);
            PUnsafe.putUnalignedIntLE(PUnsafe.pork_directBufferAddress(this.delegate) + position, v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Long.BYTES);
            PUnsafe.putUnalignedLongBE(PUnsafe.pork_directBufferAddress(this.delegate) + position, v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        try {
            int position = PNioBuffers.skipForWrite(this.delegate, Long.BYTES);
            PUnsafe.putUnalignedLongLE(PUnsafe.pork_directBufferAddress(this.delegate) + position, v);
        } catch (BufferOverflowException e) {
            throw new NoMoreSpaceException(e);
        }
    }
}
