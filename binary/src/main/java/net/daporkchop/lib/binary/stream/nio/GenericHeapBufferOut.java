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
 * An implementation of {@link DataOut} that can write to any heap-based {@link ByteBuffer}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericHeapBufferOut extends AbstractHeapDataOut {
    protected ByteBuffer delegate;

    public GenericHeapBufferOut(@NonNull ByteBuffer delegate) {
        checkArg(!delegate.isReadOnly(), "delegate may not be read-only!");
        checkArg(!delegate.isDirect(), "delegate may not be direct!");
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
            //we don't have direct access to the array, so we'll have to fall back to copying one byte at a time
            //  (JIT might be smart enough to optimize this into a sequential memory copy, but don't bank on it - that's why we have ArrayHeapBufferOut)
            int position = PNioBuffers.skipForWrite(this.delegate, toInt(length, "length"));
            for (int i = 0; i < length; i++) {
                PUnsafe.putByte(addr + i, this.delegate.get(position + i));
            }
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
}
