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

package net.daporkchop.lib.binary.stream.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.AbstractDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from a {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class ByteBufIn extends AbstractDataIn {
    @NonNull
    protected ByteBuf delegate;

    @Override
    protected int read0() throws IOException {
        if (this.delegate.isReadable()) {
            return this.delegate.readByte() & 0xFF;
        } else {
            return -1;
        }
    }

    @Override
    protected int readSome0(@NonNull byte[] dst, int start, int length, boolean blocking) throws IOException {
        int count = min(this.delegate.readableBytes(), length);
        if (count <= 0) {
            return RESULT_EOF;
        } else {
            this.delegate.readBytes(dst, start, count);
            return count;
        }
    }

    @Override
    protected long readSome0(long addr, long length, boolean blocking) throws IOException {
        int count = toInt(min(this.delegate.readableBytes(), length));
        int readerIndex = this.delegate.readerIndex();
        if (count <= 0) {
            return RESULT_EOF;
        } else if (this.delegate.hasMemoryAddress()) {
            PUnsafe.copyMemory(this.delegate.memoryAddress() + readerIndex, addr, count);
        } else if (this.delegate.hasArray()) {
            PUnsafe.copyMemory(this.delegate.array(), PUnsafe.ARRAY_BYTE_BASE_OFFSET + this.delegate.arrayOffset() + readerIndex, null, addr, count);
        } else {
            this.delegate.getBytes(readerIndex, Unpooled.wrappedBuffer(addr, count, false));
        }
        this.delegate.skipBytes(count);
        return count;
    }

    @Override
    protected void readAll0(@NonNull byte[] dst, int start, int length) throws EOFException, IOException {
        int readableBytes = this.delegate.readableBytes();
        if (length <= readableBytes)    {
            this.delegate.readBytes(dst, start, length);
        } else {
            //emulate having read all the data in the buffer
            this.delegate.skipBytes(readableBytes);
            throw new EOFException();
        }
    }

    @Override
    protected void readAll0(long addr, long length) throws EOFException, IOException {
        int readableBytes = this.delegate.readableBytes();
        if (length <= readableBytes)    {
            int readerIndex = this.delegate.readerIndex();
            if (this.delegate.hasMemoryAddress()) {
                PUnsafe.copyMemory(this.delegate.memoryAddress() + readerIndex, addr, length);
            } else if (this.delegate.hasArray()) {
                PUnsafe.copyMemory(this.delegate.array(), PUnsafe.ARRAY_BYTE_BASE_OFFSET + this.delegate.arrayOffset() + readerIndex, null, addr, length);
            } else {
                this.delegate.getBytes(readerIndex, Unpooled.wrappedBuffer(addr, toInt(length), false));
            }
            this.delegate.skipBytes(toInt(length));
        } else {
            //emulate having read all the data in the buffer
            this.delegate.skipBytes(readableBytes);
            throw new EOFException();
        }
    }

    @Override
    protected long skip0(long count) throws IOException {
        int countI = (int) min(this.delegate.readableBytes(), count);
        this.delegate.skipBytes(countI);
        return countI;
    }

    @Override
    protected long remaining0() throws IOException {
        return this.delegate.readableBytes();
    }

    @Override
    protected void close0() throws IOException {
        this.delegate.release();
        this.delegate = null;
    }

    @Override
    protected InputStream asStream0() {
        return new ByteBufInputStream(this.delegate);
    }

    @Override
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        return this.delegate.readCharSequence(toInt(size, "size"), charset);
    }
}
