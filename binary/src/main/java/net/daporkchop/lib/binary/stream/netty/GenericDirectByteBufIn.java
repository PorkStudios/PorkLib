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
import net.daporkchop.lib.binary.stream.AbstractDirectDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} that can read from any direct {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class GenericDirectByteBufIn extends AbstractDirectDataIn {
    protected ByteBuf delegate;
    protected final boolean autoRelease;

    public GenericDirectByteBufIn(@NonNull ByteBuf delegate, boolean autoRelease) {
        checkArg(delegate.isDirect(), "delegate must be direct!");
        this.delegate = delegate;
        this.autoRelease = autoRelease;
    }

    @Override
    protected int read0() throws IOException {
        //IndexOutOfBoundsException can't be thrown because we never call readByte() unless isReadable() is true
        if (this.delegate.isReadable()) {
            return this.delegate.readByte() & 0xFF;
        } else {
            return RESULT_EOF;
        }
    }

    @Override
    protected int read0(@NonNull byte[] dst, int start, @Positive int length) throws IOException {
        //IndexOutOfBoundsException can't be thrown because we never read more than readableBytes()
        int count = min(this.delegate.readableBytes(), length);
        if (count <= 0) {
            return RESULT_EOF;
        } else {
            this.delegate.readBytes(dst, start, count);
            return count;
        }
    }

    @Override
    protected long read0(long addr, @Positive long length) throws IOException {
        //IndexOutOfBoundsException can't be thrown because we never read more than readableBytes()
        int count = toInt(min(this.delegate.readableBytes(), length));
        if (count <= 0) {
            return RESULT_EOF;
        }
        this.delegate.readBytes(PlatformDependent.directBuffer(addr, count));
        return count;
    }

    @Override
    protected long skip0(@Positive long count) throws IOException {
        //IndexOutOfBoundsException can't be thrown because we never skip more than readableBytes()
        int countI = (int) min(this.delegate.readableBytes(), count);
        this.delegate.skipBytes(countI);
        return countI;
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, @Positive long count) throws NoMoreSpaceException, IOException {
        //IndexOutOfBoundsException can't be thrown because we never transfer more than readableBytes()
        count = min(count, this.delegate.readableBytes());
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
    public CharSequence readText(long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readCharSequence(toInt(notNegative(size, "size"), "size"), charset);
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    //
    // primitives
    //

    @Override
    public byte readByte() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readByte();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedByte() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readUnsignedByte();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readShort();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readShortLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedShort() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readUnsignedShort();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedShortLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readUnsignedShortLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return (char) this.delegate.readShort();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return (char) this.delegate.readShortLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readInt();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readIntLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readLong();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        try {
            return this.delegate.readLongLE();
        } catch (IndexOutOfBoundsException e) {
            throw new EOFException();
        }
    }
}
