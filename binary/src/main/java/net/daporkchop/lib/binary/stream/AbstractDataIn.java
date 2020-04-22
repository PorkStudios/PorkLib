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

package net.daporkchop.lib.binary.stream;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Base implementation of {@link DataIn}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDataIn implements DataIn {
    protected static final long CLOSED_OFFSET = PUnsafe.pork_getOffset(AbstractDataIn.class, "closed");

    protected volatile int closed = 0;

    @Override
    public abstract int read() throws IOException;

    @Override
    public int readUnsignedByte() throws IOException {
        int b = this.read();
        if (b >= 0) {
            return b;
        } else {
            throw new EOFException();
        }
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws IOException {
        PValidation.checkRangeLen(dst.length, start, length);
        return this.readSome0(dst, start, length);
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) throws IOException {
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws IOException {
        return 0;
    }

    @Override
    public int read(@NonNull ByteBuf dst, int count) throws IOException {
        return 0;
    }

    @Override
    public int read(@NonNull ByteBuf dst, int start, int length) throws IOException {
        return 0;
    }

    @Override
    public int readFully(@NonNull ByteBuffer dst) throws IOException {
        return 0;
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int count) throws IOException {
        return 0;
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int start, int length) throws IOException {
        return 0;
    }

    protected abstract int readSome0(@NonNull byte[] dst, int start, int length) throws IOException;

    protected abstract long readSome0(long addr, long length) throws IOException;

    protected abstract void readAll0(@NonNull byte[] dst, int start, int length) throws IOException;

    protected abstract void readAll0(long addr, long length) throws IOException;

    /**
     * Actually closes this {@link DataIn}.
     * <p>
     * This method is guaranteed to only be called once, regardless of outcome.
     *
     * @throws IOException if an IO exception occurs you dummy
     */
    protected abstract void doClose() throws IOException;

    @Override
    public InputStream asStream() {
        return this;
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return PValidation.toInt(this.skip((long) n));
    }

    @Override
    public final long skipBytes(long n) throws IOException {
        return this.skip(n);
    }

    @Override
    public void close() throws IOException {
        if (PUnsafe.compareAndSwapInt(this, CLOSED_OFFSET, 0, 1)) {
            this.doClose();
        }
    }

    @Override
    public boolean isOpen() {
        return this.closed == 0;
    }
}
