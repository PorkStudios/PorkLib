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

package net.daporkchop.lib.binary.stream.netty;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Variant of {@link GenericHeapByteBufOut} which doesn't allow the destination buffer to be grown.
 *
 * @author DaPorkchop_
 */
public class NonGrowingGenericHeapByteBufOut extends GenericHeapByteBufOut {
    public NonGrowingGenericHeapByteBufOut(@NonNull ByteBuf delegate, boolean autoRelease) {
        super(delegate, autoRelease);
    }

    @Override
    protected void write0(int b) throws IOException {
        checkIndex(this.delegate.isWritable());
        super.write0(b);
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        checkIndex(this.delegate.isWritable(length));
        super.write0(src, start, length);
    }

    @Override
    protected void write0(long addr, long length) throws IOException {
        checkIndex(this.delegate.isWritable(toInt(length, "length")));
        super.write0(addr, length);
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return this.writeText(text, 0, text.length(), charset); //delegate to non-accelerated version
    }

    @Override
    public void writeByte(int b) throws IOException {
        checkIndex(this.delegate.isWritable(Byte.BYTES));
        super.writeByte(b);
    }

    @Override
    public void writeShort(int v) throws IOException {
        checkIndex(this.delegate.isWritable(Short.BYTES));
        super.writeShort(v);
    }

    @Override
    public void writeShortLE(int v) throws IOException {
        checkIndex(this.delegate.isWritable(Short.BYTES));
        super.writeShortLE(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        checkIndex(this.delegate.isWritable(Character.BYTES));
        super.writeChar(v);
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        checkIndex(this.delegate.isWritable(Character.BYTES));
        super.writeCharLE(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        checkIndex(this.delegate.isWritable(Integer.BYTES));
        super.writeInt(v);
    }

    @Override
    public void writeIntLE(int v) throws IOException {
        checkIndex(this.delegate.isWritable(Integer.BYTES));
        super.writeIntLE(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        checkIndex(this.delegate.isWritable(Long.BYTES));
        super.writeLong(v);
    }

    @Override
    public void writeLongLE(long v) throws IOException {
        checkIndex(this.delegate.isWritable(Long.BYTES));
        super.writeLongLE(v);
    }
}
