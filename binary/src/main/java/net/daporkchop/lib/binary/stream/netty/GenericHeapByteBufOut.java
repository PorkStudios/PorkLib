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

    //we catch IndexOutOfBoundsException rather than checking maxWritableBytes() because AbstractByteBuf#ensureWritable0() invokes AbstractByteBuf#ensureAccessible(),
    //  and access checks (which are enabled by default) involve a volatile load. as far as i am aware, which would prevent the automatic bounds checks in
    //  AbstractByteBuf#ensureWritable0() from being optimized away once inlined, so if we did
    //  'if (maxWritableBytes() < blah) throw new IOException(new IndexOutOfBoundsException());', it'd essentially result in bounds being checked twice for every read.
    //  since exceeding a buffer's bounds will be a rare occurrence, and exception handlers incur zero performance penalties when not used
    //  (see https://shipilev.net/blog/2014/exceptional-performance/), catching IndexOutOfBoundsException and throwing an EOFException should be fastest.

    @Override
    protected void write0(int b) throws IOException {
        try {
            this.delegate.writeByte(b);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        try {
            int count = min(this.delegate.maxWritableBytes(), length);
            checkIndex(count == length);
            this.delegate.writeBytes(src, start, length);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    protected void write0(long addr, long length) throws IOException {
        try {
            int writerIndex = this.delegate.writerIndex();
            int count = toInt(min(this.delegate.maxCapacity() - writerIndex, length));
            checkIndex(count == length);
            this.delegate.writeBytes(PlatformDependent.directBuffer(addr, count));
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
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
        try {
            return this.delegate.writeCharSequence(text, charset);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    //
    // primitives
    //

    @Override
    public void writeByte(int b) throws IOException {
        try {
            this.delegate.writeByte(b);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        try {
            this.delegate.writeShort(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeShortLE(int v) throws IOException {
        try {
            this.delegate.writeShortLE(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try {
            this.delegate.writeShort(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        try {
            this.delegate.writeShortLE(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try {
            this.delegate.writeInt(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeIntLE(int v) throws IOException {
        try {
            this.delegate.writeIntLE(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try {
            this.delegate.writeLong(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }

    @Override
    public void writeLongLE(long v) throws IOException {
        try {
            this.delegate.writeLongLE(v);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("buffer maxCapacity() exceeded", e);
        }
    }
}
