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
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * An implementation of {@link DataIn} that can read from a {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class NettyByteBufIn extends DataIn {
    @NonNull
    protected ByteBuf buf;

    @Override
    public int read() throws IOException {
        return this.buf.isReadable() ? this.buf.readByte() & 0xFF : -1;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        if (this.buf.isReadable()) {
            len = Math.min(len, this.buf.readableBytes());
            this.buf.readBytes(b, off, len);
            return len;
        } else {
            return -1;
        }
    }

    @Override
    public int available() throws IOException {
        return this.buf.readableBytes();
    }

    @Override
    public long skip(long n) throws IOException {
        n = Math.max(Math.min(n, this.buf.readableBytes()), 0L);
        this.buf.skipBytes((int) n);
        return n;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.buf.markReaderIndex();
    }

    @Override
    public synchronized void reset() throws IOException {
        this.buf.resetReaderIndex();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.buf.readByte();
    }

    @Override
    public int readUByte() throws IOException {
        return this.buf.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return this.buf.readShort();
    }

    @Override
    public int readUShort() throws IOException {
        return this.buf.readUnsignedShort();
    }

    @Override
    public short readShortLE() throws IOException {
        return this.buf.readShortLE();
    }

    @Override
    public int readUShortLE() throws IOException {
        return this.buf.readUnsignedShortLE();
    }

    @Override
    public char readChar() throws IOException {
        return this.buf.readChar();
    }

    @Override
    public char readCharLE() throws IOException {
        return Character.reverseBytes(this.buf.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return this.buf.readInt();
    }

    @Override
    public long readUInt() throws IOException {
        return this.buf.readUnsignedInt();
    }

    @Override
    public int readIntLE() throws IOException {
        return this.buf.readIntLE();
    }

    @Override
    public long readUIntLE() throws IOException {
        return this.buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() throws IOException {
        return this.buf.readLong();
    }

    @Override
    public long readLongLE() throws IOException {
        return this.buf.readLongLE();
    }

    @Override
    public float readFloat() throws IOException {
        return this.buf.readFloat();
    }

    @Override
    public float readFloatLE() throws IOException {
        return this.buf.readFloatLE();
    }

    @Override
    public double readDouble() throws IOException {
        return this.buf.readDouble();
    }

    @Override
    public double readDoubleLE() throws IOException {
        return this.buf.readDoubleLE();
    }

    @Override
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("size parameter too large!");
        }
        return this.buf.readCharSequence((int) size, charset);
    }

    @Override
    public byte[] readFully(@NonNull byte[] dst) throws IOException {
        this.buf.readBytes(dst);
        return dst;
    }

    @Override
    public byte[] readFully(@NonNull byte[] dst, int start, int length) throws EOFException, IOException {
        PorkUtil.assertInRangeLen(dst.length, start, length);
        if (this.buf.isReadable(length)) {
            this.buf.readBytes(dst, start, length);
            return dst;
        } else {
            throw new EOFException();
        }
    }

    @Override
    public byte[] toByteArray() throws IOException {
        byte[] arr = new byte[this.buf.readableBytes()];
        this.buf.readBytes(arr);
        return arr;
    }

    @Override
    public void close() throws IOException {
    }

    /**
     * A variant of {@link NettyByteBufIn} that invokes {@link ByteBuf#release()} on the buffer when it is closed.
     *
     * @author DaPorkchop_
     */
    public static final class Releasing extends NettyByteBufIn {
        public Releasing(ByteBuf buf) {
            super(buf);
        }

        @Override
        public void close() throws IOException {
            this.buf.release();
        }
    }
}
