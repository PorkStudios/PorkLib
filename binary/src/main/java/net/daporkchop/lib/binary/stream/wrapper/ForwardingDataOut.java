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

package net.daporkchop.lib.binary.stream.wrapper;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
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
    public void write(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.write(b);
    }

    @Override
    public void writeBoolean(boolean b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeBoolean(b);
    }

    @Override
    public void writeByte(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeByte(b);
    }

    @Override
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeShort(v);
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeShortLE(v);
    }

    @Override
    public void writeShort(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeShort(v, order);
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeChar(v);
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeCharLE(v);
    }

    @Override
    public void writeChar(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeChar(v, order);
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeInt(v);
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeIntLE(v);
    }

    @Override
    public void writeInt(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeInt(v, order);
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeLong(v);
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeLongLE(v);
    }

    @Override
    public void writeLong(long v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeLong(v, order);
    }

    @Override
    public void writeFloat(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeFloat(v);
    }

    @Override
    public void writeFloatLE(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeFloatLE(v);
    }

    @Override
    public void writeFloat(float v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeFloat(v, order);
    }

    @Override
    public void writeDouble(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeDouble(v);
    }

    @Override
    public void writeDoubleLE(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeDoubleLE(v);
    }

    @Override
    public void writeDouble(double v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeDouble(v, order);
    }

    @Override
    public void writeBytes(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeBytes(text);
    }

    @Override
    public long writeBytes(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeBytes(text);
    }

    @Override
    public long writeBytes(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeBytes(text, offset, length);
    }

    @Override
    public void writeChars(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeChars(text);
    }

    @Override
    public long writeChars(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeChars(text);
    }

    @Override
    public long writeChars(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeChars(text, offset, length);
    }

    @Override
    public void writeCharsLE(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeCharsLE(text);
    }

    @Override
    public long writeCharsLE(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeCharsLE(text);
    }

    @Override
    public long writeCharsLE(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeCharsLE(text, offset, length);
    }

    @Override
    public void writeUTF(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeUTF(text);
    }

    @Override
    public void writeUTF(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeUTF(text);
    }

    @Override
    public void writeVarUTF(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeVarUTF(text);
    }

    @Override
    public void writeString(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeString(text, charset);
    }

    @Override
    public void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeVarString(text, charset);
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeText(text, charset);
    }

    @Override
    public long writeText(@NonNull CharSequence text, int offset, int length, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeText(text, offset, length, charset);
    }

    @Override
    public <E extends Enum<E>> void writeEnum(@NonNull E e) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeEnum(e);
    }

    @Override
    public void writeVarInt(int value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeVarInt(value);
    }

    @Override
    public void writeVarIntZigZag(int value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeVarIntZigZag(value);
    }

    @Override
    public void writeVarLong(long value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeVarLong(value);
    }

    @Override
    public void writeVarLongZigZag(long value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeVarLongZigZag(value);
    }

    @Override
    public void write(@NonNull byte[] src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.write(src);
    }

    @Override
    public void write(@NonNull byte[] src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.write(src, offset, length);
    }

    @Override
    public int write(@NonNull ByteBuffer src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.write(src);
    }

    @Override
    public long write(@NonNull ByteBuffer[] srcs) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.write(srcs);
    }

    @Override
    public long write(@NonNull ByteBuffer[] srcs, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.write(srcs, offset, length);
    }

    @Override
    public int write(@NonNull ByteBuf src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.write(src);
    }

    @Override
    public int write(@NonNull ByteBuf src, int count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.write(src, count);
    }

    @Override
    public int write(@NonNull ByteBuf src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.write(src, offset, length);
    }

    @Override
    public long transferFrom(@NonNull DataIn src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.transferFrom(src);
    }

    @Override
    public long transferFrom(@NonNull DataIn src, long count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.transferFrom(src, count);
    }

    @Override
    public long transferFromFully(@NonNull DataIn src, long count) throws ClosedChannelException, EOFException, NoMoreSpaceException, IOException {
        return this.delegate.transferFromFully(src, count);
    }

    @Override
    public OutputStream asOutputStream() throws ClosedChannelException, IOException {
        return this.delegate.asOutputStream();
    }

    @Override
    public void flush() throws ClosedChannelException, IOException {
        this.delegate.flush();
    }

    @Override
    public boolean isDirect() {
        return this.delegate.isDirect();
    }

    @Override
    public boolean isHeap() {
        return this.delegate.isHeap();
    }

    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public abstract void close() throws IOException;
}
