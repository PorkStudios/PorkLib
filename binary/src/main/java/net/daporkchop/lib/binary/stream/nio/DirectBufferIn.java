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
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from a direct {@link ByteBuffer}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class DirectBufferIn extends AbstractDirectDataIn {
    protected ByteBuffer delegate;

    public DirectBufferIn(@NonNull ByteBuffer delegate) {
        checkArg(delegate.isDirect(), "delegate must be direct!");
        this.delegate = delegate;
    }

    @Override
    protected int read0() throws IOException {
        if (this.delegate.hasRemaining()) {
            return this.delegate.get() & 0xFF;
        } else {
            return RESULT_EOF;
        }
    }

    @Override
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        int count = min(this.delegate.remaining(), length);
        if (count <= 0) {
            return RESULT_EOF;
        } else {
            this.delegate.get(dst, start, count);
            return count;
        }
    }

    @Override
    protected long read0(long addr, long length) throws IOException {
        int count = toInt(min(this.delegate.remaining(), length));
        int position = this.delegate.position();
        if (count <= 0) {
            return RESULT_EOF;
        }
        PUnsafe.copyMemory(PUnsafe.pork_directBufferAddress(this.delegate) + position, addr, count);
        this.delegate.position(position + count);
        return count;
    }

    @Override
    protected long skip0(long count) throws IOException {
        int countI = (int) min(count, this.delegate.remaining());
        this.delegate.position(this.delegate.position() + countI);
        return countI;
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, long count) throws IOException {
        if (count < 0L || count > this.delegate.remaining()) {
            count = this.delegate.remaining();
        }
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
}
