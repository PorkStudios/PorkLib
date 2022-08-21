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
import net.daporkchop.lib.binary.stream.AbstractHeapDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from any heap-based {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericHeapByteBufIn extends AbstractHeapDataIn {
    protected ByteBuf delegate;
    protected final boolean autoRelease;

    public GenericHeapByteBufIn(@NonNull ByteBuf delegate, boolean autoRelease) {
        checkArg(!delegate.isDirect(), "delegate may not be direct!");
        this.delegate = delegate;
        this.autoRelease = autoRelease;
    }

    @Override
    protected int read0() throws IOException {
        if (this.delegate.isReadable()) {
            return this.delegate.readByte() & 0xFF;
        } else {
            return RESULT_EOF;
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
        if (count <= 0) {
            return RESULT_EOF;
        }
        this.delegate.readBytes(PlatformDependent.directBuffer(addr, count));
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
        if (this.autoRelease) {
            this.delegate.release();
        }
        this.delegate = null;
    }

    //we catch IndexOutOfBoundsException rather than checking readableBytes because AbstractByteBuf#checkReadableBytes0() invokes AbstractByteBuf#ensureAccessible(),
    //  and access checks (which are enabled by default) involve a volatile load. as far as i am aware, which would prevent the automatic bounds checks in
    //  AbstractByteBuf#checkReadableBytes0() from being optimized away once inlined, so if we did 'if (readableBytes() < blah) throw new EOFException();', it'd
    //  essentially result in bounds being checked twice for every read. since exceeding a buffer's bounds will be a rare occurrence, and exception handlers incur
    //  zero performance penalties when not used (see https://shipilev.net/blog/2014/exceptional-performance/), catching IndexOutOfBoundsException and throwing an
    //  EOFException should be fastest.

    @Override
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        try {
            return this.delegate.readCharSequence(toInt(size, "size"), charset);
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    //
    // primitives
    //

    @Override
    public byte readByte() throws IOException {
        try {
            return this.delegate.readByte();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        try {
            return this.delegate.readUnsignedByte();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public short readShort() throws IOException {
        try {
            return this.delegate.readShort();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public short readShortLE() throws IOException {
        try {
            return this.delegate.readShortLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        try {
            return this.delegate.readUnsignedShort();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedShortLE() throws IOException {
        try {
            return this.delegate.readUnsignedShortLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readChar() throws IOException {
        try {
            return (char) this.delegate.readShort();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readCharLE() throws IOException {
        try {
            return (char) this.delegate.readShortLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return this.delegate.readInt();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readIntLE() throws IOException {
        try {
            return this.delegate.readIntLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return this.delegate.readLong();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLongLE() throws IOException {
        try {
            return this.delegate.readLongLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }
}
