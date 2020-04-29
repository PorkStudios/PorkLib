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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.AbstractDirectDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from a direct {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class DirectByteBufIn extends AbstractDirectDataIn {
    protected ByteBuf delegate;

    public DirectByteBufIn(@NonNull ByteBuf delegate) {
        checkArg(delegate.hasMemoryAddress(), "delegate must be direct!");
        this.delegate = delegate;
    }

    @Override
    protected int read0() throws IOException {
        if (this.delegate.isReadable()) {
            return this.delegate.readByte() & 0xFF;
        } else {
            return -1;
        }
    }

    @Override
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        int count = min(this.delegate.readableBytes(), length);
        if (count <= 0) {
            return RESULT_EOF;
        } else {
            this.delegate.readBytes(dst, start, count);
            return count;
        }
    }

    @Override
    protected long read0(long addr, long length) throws IOException {
        int count = toInt(min(this.delegate.readableBytes(), length));
        int readerIndex = this.delegate.readerIndex();
        if (count <= 0) {
            return RESULT_EOF;
        }
        PUnsafe.copyMemory(this.delegate.memoryAddress() + readerIndex, addr, count);
        this.delegate.skipBytes(count);
        return count;
    }

    @Override
    protected long skip0(long count) throws IOException {
        int countI = (int) min(this.delegate.readableBytes(), count);
        this.delegate.skipBytes(countI);
        return countI;
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, long count) throws IOException {
        if (count < 0L || count > this.delegate.readableBytes()) {
            count = this.delegate.readableBytes();
        }
        int read = this.delegate.readBytes(dst, (int) count);
        checkState(read == count, "only transferred %s/%s bytes?!?", read, count);
        return count;
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
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        return this.delegate.readCharSequence(toInt(size, "size"), charset);
    }
}
