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

package net.daporkchop.lib.binary.stream.wrapper;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Implementation of {@link DataOut} that forwards all method calls along to another {@link DataOut}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class ForwardingDataOut implements DataOut {
    @NonNull
    protected final DataOut delegate;

    @Override
    public void write(int b) throws IOException {
        this.delegate.write(b);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        this.delegate.writeBoolean(b);
    }

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
        this.delegate.writeChar(v);
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        this.delegate.writeCharLE(v);
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

    @Override
    public void writeFloat(float f) throws IOException {
        this.delegate.writeFloat(f);
    }

    @Override
    public void writeFloatLE(float f) throws IOException {
        this.delegate.writeFloatLE(f);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        this.delegate.writeDouble(d);
    }

    @Override
    public void writeDoubleLE(double d) throws IOException {
        this.delegate.writeDoubleLE(d);
    }

    @Override
    public void writeBytes(@NonNull String text) throws IOException {
        this.delegate.writeBytes(text);
    }

    @Override
    public long writeBytes(@NonNull CharSequence text) throws IOException {
        return this.delegate.writeBytes(text);
    }

    @Override
    public long writeBytes(@NonNull CharSequence text, int start, int length) throws IOException {
        return this.delegate.writeBytes(text, start, length);
    }

    @Override
    public void writeChars(@NonNull String text) throws IOException {
        this.delegate.writeChars(text);
    }

    @Override
    public long writeChars(@NonNull CharSequence text) throws IOException {
        return this.delegate.writeChars(text);
    }

    @Override
    public long writeChars(@NonNull CharSequence text, int start, int length) throws IOException {
        return this.delegate.writeChars(text, start, length);
    }

    @Override
    public void writeUTF(@NonNull String text) throws IOException {
        this.delegate.writeUTF(text);
    }

    @Override
    public void writeUTF(@NonNull CharSequence text) throws IOException {
        this.delegate.writeUTF(text);
    }

    @Override
    public void writeVarUTF(@NonNull CharSequence text) throws IOException {
        this.delegate.writeVarUTF(text);
    }

    @Override
    public void writeString(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        this.delegate.writeString(text, charset);
    }

    @Override
    public void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        this.delegate.writeVarString(text, charset);
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return this.delegate.writeText(text, charset);
    }

    @Override
    public long writeText(@NonNull CharSequence text, int start, int length, @NonNull Charset charset) throws IOException {
        return this.delegate.writeText(text, start, length, charset);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(@NonNull E e) throws IOException {
        this.delegate.writeEnum(e);
    }

    @Override
    public void writeVarInt(int value) throws IOException {
        this.delegate.writeVarInt(value);
    }

    @Override
    public void writeVarIntZigZag(int value) throws IOException {
        this.delegate.writeVarIntZigZag(value);
    }

    @Override
    public void writeVarLong(long value) throws IOException {
        this.delegate.writeVarLong(value);
    }

    @Override
    public void writeVarLongZigZag(long value) throws IOException {
        this.delegate.writeVarLongZigZag(value);
    }

    @Override
    public void write(@NonNull byte[] src) throws IOException {
        this.delegate.write(src);
    }

    @Override
    public void write(@NonNull byte[] src, int start, int length) throws IOException {
        this.delegate.write(src, start, length);
    }

    @Override
    public int write(@NonNull ByteBuffer src) throws IOException {
        return this.delegate.write(src);
    }

    @Override
    public long write(@NonNull ByteBuffer[] srcs) throws IOException {
        return this.delegate.write(srcs);
    }

    @Override
    public long write(@NonNull ByteBuffer[] srcs, int offset, int length) throws IOException {
        return this.delegate.write(srcs, offset, length);
    }

    @Override
    public int write(@NonNull ByteBuf src) throws IOException {
        return this.delegate.write(src);
    }

    @Override
    public int write(@NonNull ByteBuf src, int count) throws IOException {
        return this.delegate.write(src, count);
    }

    @Override
    public int write(@NonNull ByteBuf src, int start, int length) throws IOException {
        return this.delegate.write(src, start, length);
    }

    @Override
    public OutputStream asOutputStream() throws IOException {
        return this.delegate.asOutputStream();
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public abstract void close() throws IOException;
}
