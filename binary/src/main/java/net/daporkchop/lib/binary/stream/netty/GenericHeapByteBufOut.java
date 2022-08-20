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
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.AbstractHeapDataOut;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataOut} that can write to any heap-based {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericHeapByteBufOut extends AbstractHeapDataOut {
    protected ByteBuf delegate;
    protected final boolean autoRelease;

    public GenericHeapByteBufOut(@NonNull ByteBuf delegate, boolean autoRelease) {
        checkArg(!delegate.isDirect(), "delegate may not be direct!");
        this.delegate = delegate;
        this.autoRelease = autoRelease;
    }

    @Override
    protected void write0(int b) throws IOException {
        this.delegate.writeByte(b);
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        int count = min(this.delegate.maxWritableBytes(), length);
        checkIndex(count == length);
        this.delegate.writeBytes(src, start, length);
    }

    @Override
    protected void write0(long addr, long length) throws IOException {
        int writerIndex = this.delegate.writerIndex();
        int count = toInt(min(this.delegate.maxCapacity() - writerIndex, length));
        checkIndex(count == length);
        this.delegate.writeBytes(PlatformDependent.directBuffer(addr, count));
    }

    @Override
    protected void flush0() throws IOException {
        //no-op
    }

    @Override
    protected void close0() throws IOException {
        if (this.autoRelease) {
            this.delegate.release();
        }
        this.delegate = null;
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return this.delegate.writeCharSequence(text, charset);
    }

    //
    // primitives
    //

    @Override
    public void writeByte(int b) throws IOException {
        this.delegate.writeByte(b);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.delegate.writeShort(v);
    }

    @Override
    public void writeShortLE(int v) throws IOException {
        this.delegate.writeShortLE(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.delegate.writeShort(v);
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        this.delegate.writeShortLE(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.delegate.writeInt(v);
    }

    @Override
    public void writeIntLE(int v) throws IOException {
        this.delegate.writeIntLE(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.delegate.writeLong(v);
    }

    @Override
    public void writeLongLE(long v) throws IOException {
        this.delegate.writeLongLE(v);
    }
}
