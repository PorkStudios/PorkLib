/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.netty;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.math.primitive.PMath;

import java.io.IOException;

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
    public byte[] readFully(@NonNull byte[] b, int off, int len) throws IOException {
        this.buf.readBytes(b, off, len);
        return b;
    }

    @Override
    public int available() throws IOException {
        return this.buf.readableBytes();
    }

    @Override
    public long skip(long n) throws IOException {
        n = PMath.clamp(n, 0L, this.buf.readableBytes());
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
