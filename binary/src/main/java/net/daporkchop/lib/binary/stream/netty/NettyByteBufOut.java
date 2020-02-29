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
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * An implementation of {@link DataOut} that can write to a {@link ByteBuf}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class NettyByteBufOut extends DataOut {
    @NonNull
    protected ByteBuf buf;

    @Override
    public void write(int b) throws IOException {
        this.buf.writeByte(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        this.buf.writeBytes(b, off, len);
    }

    @Override
    public DataOut writeBoolean(boolean b) throws IOException {
        this.buf.writeBoolean(b);
        return this;
    }

    @Override
    public DataOut writeByte(byte b) throws IOException {
        this.buf.writeByte(b & 0xFF);
        return this;
    }

    @Override
    public DataOut writeShort(short s) throws IOException {
        this.buf.writeShort(s & 0xFFFF);
        return this;
    }

    @Override
    public DataOut writeUShort(int s) throws IOException {
        this.buf.writeShort(s);
        return this;
    }

    @Override
    public DataOut writeShortLE(short s) throws IOException {
        this.buf.writeShortLE(s & 0xFFFF);
        return this;
    }

    @Override
    public DataOut writeUShortLE(int s) throws IOException {
        this.buf.writeShortLE(s);
        return this;
    }

    @Override
    public DataOut writeChar(char c) throws IOException {
        this.buf.writeChar(c);
        return this;
    }

    @Override
    public DataOut writeCharLE(char c) throws IOException {
        this.buf.writeChar(Character.reverseBytes(c));
        return this;
    }

    @Override
    public DataOut writeInt(int i) throws IOException {
        this.buf.writeInt(i);
        return this;
    }

    @Override
    public DataOut writeIntLE(int i) throws IOException {
        this.buf.writeIntLE(i);
        return this;
    }

    @Override
    public DataOut writeLong(long l) throws IOException {
        this.buf.writeLong(l);
        return this;
    }

    @Override
    public DataOut writeLongLE(long l) throws IOException {
        this.buf.writeLongLE(l);
        return this;
    }

    @Override
    public DataOut writeFloat(float f) throws IOException {
        this.buf.writeFloat(f);
        return this;
    }

    @Override
    public DataOut writeFloatLE(float f) throws IOException {
        this.buf.writeFloatLE(f);
        return this;
    }

    @Override
    public DataOut writeDouble(double d) throws IOException {
        this.buf.writeDouble(d);
        return this;
    }

    @Override
    public DataOut writeDoubleLE(double d) throws IOException {
        this.buf.writeDoubleLE(d);
        return this;
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return this.buf.writeCharSequence(text, charset);
    }

    @Override
    public final void close() throws IOException {
        if (this.buf == null) {
            throw new IllegalStateException("Already closed!");
        }
        try {
            if (this.handleClose(this.buf)) {
                this.buf.release();
            }
        } finally {
            this.buf = null;
        }
    }

    /**
     * Called when this stream is closed.
     *
     * @param buf the buffer that this stream was writing to
     * @return whether or not the buffer should be released
     * @throws IOException if an IO exception occurs you dummy
     */
    protected abstract boolean handleClose(@NonNull ByteBuf buf) throws IOException;

    /**
     * A basic implementation of {@link NettyByteBufOut} that simply does nothing when closed.
     */
    public static class Default extends NettyByteBufOut {
        public Default(ByteBuf buf) {
            super(buf);
        }

        @Override
        protected boolean handleClose(@NonNull ByteBuf buf) throws IOException {
            return false;
        }
    }
}
