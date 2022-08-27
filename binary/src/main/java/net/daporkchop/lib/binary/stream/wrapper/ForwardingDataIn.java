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
import net.daporkchop.lib.common.annotation.param.NotNegative;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * Implementation of {@link DataIn} that forwards all method calls along to another {@link DataIn}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class ForwardingDataIn implements DataIn {
    @NonNull
    protected final DataIn delegate;

    @Override
    public int read() throws ClosedChannelException, IOException {
        return this.delegate.read();
    }

    @Override
    public boolean readBoolean() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readBoolean();
    }

    @Override
    public byte readByte() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readByte();
    }

    @Override
    public int readUnsignedByte() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUnsignedByte();
    }

    @Override
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readShort();
    }

    @Override
    public int readUnsignedShort() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUnsignedShort();
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readShortLE();
    }

    @Override
    public int readUnsignedShortLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUnsignedShortLE();
    }

    @Override
    public short readShort(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readShort(order);
    }

    @Override
    public int readUnsignedShort(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUnsignedShort(order);
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readChar();
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readCharLE();
    }

    @Override
    public char readChar(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readChar(order);
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readInt();
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readIntLE();
    }

    @Override
    public int readInt(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readInt(order);
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readLong();
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readLongLE();
    }

    @Override
    public long readLong(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readLong(order);
    }

    @Override
    public float readFloat() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFloat();
    }

    @Override
    public float readFloatLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFloatLE();
    }

    @Override
    public float readFloat(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFloat(order);
    }

    @Override
    public double readDouble() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readDouble();
    }

    @Override
    public double readDoubleLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readDoubleLE();
    }

    @Override
    public double readDouble(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readDouble(order);
    }

    @Override
    public String readUTF() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUTF();
    }

    @Override
    public String readVarUTF() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readVarUTF();
    }

    @Override
    public String readString(@NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readString(charset);
    }

    @Override
    public String readVarString(@NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readVarString(charset);
    }

    @Override
    public String readString(@NotNegative long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readString(size, charset);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String readLine() throws IOException {
        return this.delegate.readLine();
    }

    @Override
    public CharSequence readText(long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readText(size, charset);
    }

    @Override
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readEnum(f);
    }

    @Override
    public int readVarInt() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readVarInt();
    }

    @Override
    public int readVarIntZigZag() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readVarIntZigZag();
    }

    @Override
    public long readVarLong() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readVarLong();
    }

    @Override
    public long readVarLongZigZag() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readVarLongZigZag();
    }

    @Override
    public int read(@NonNull byte[] dst) throws ClosedChannelException, IOException {
        return this.delegate.read(dst);
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, IOException {
        return this.delegate.read(dst, start, length);
    }

    @Override
    public void readFully(@NonNull byte[] dst) throws ClosedChannelException, EOFException, IOException {
        this.delegate.readFully(dst);
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        this.delegate.readFully(dst, start, length);
    }

    @Override
    public byte[] fill(@NonNull byte[] dst) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.fill(dst);
    }

    @Override
    public byte[] fill(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.fill(dst, start, length);
    }

    @Override
    public byte[] toByteArray() throws ClosedChannelException, IOException {
        return this.delegate.toByteArray();
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws ClosedChannelException, IOException {
        return this.delegate.read(dst);
    }

    @Override
    public long read(@NonNull ByteBuffer[] dsts) throws ClosedChannelException, IOException {
        return this.delegate.read(dsts);
    }

    @Override
    public long read(@NonNull ByteBuffer[] dsts, int offset, int length) throws ClosedChannelException, IOException {
        return this.delegate.read(dsts, offset, length);
    }

    @Override
    public int read(@NonNull ByteBuf dst) throws ClosedChannelException, IOException {
        return this.delegate.read(dst);
    }

    @Override
    public int read(@NonNull ByteBuf dst, int count) throws ClosedChannelException, IOException {
        return this.delegate.read(dst, count);
    }

    @Override
    public int read(@NonNull ByteBuf dst, int offset, int length) throws ClosedChannelException, IOException {
        return this.delegate.read(dst, offset, length);
    }

    @Override
    public int readFully(@NonNull ByteBuffer dst) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFully(dst);
    }

    @Override
    public long readFully(@NonNull ByteBuffer[] dsts) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFully(dsts);
    }

    @Override
    public long readFully(@NonNull ByteBuffer[] dsts, int offset, int length) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFully(dsts, offset, length);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFully(dst);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int count) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFully(dst, count);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFully(dst, start, length);
    }

    @Override
    public long transferTo(@NonNull DataOut dst) throws ClosedChannelException, IOException {
        return this.delegate.transferTo(dst);
    }

    @Override
    public long transferTo(@NonNull DataOut dst, long count) throws ClosedChannelException, IOException {
        return this.delegate.transferTo(dst, count);
    }

    @Override
    public long transferToFully(@NonNull DataOut dst, long count) throws ClosedChannelException, EOFException, IOException {
        return this.delegate.transferToFully(dst, count);
    }

    @Override
    public InputStream asInputStream() throws ClosedChannelException, IOException {
        return this.delegate.asInputStream();
    }

    @Override
    public long remaining() throws ClosedChannelException, IOException {
        return this.delegate.remaining();
    }

    @Override
    public int skipBytes(int n) throws ClosedChannelException, IOException {
        return this.delegate.skipBytes(n);
    }

    @Override
    public long skipBytes(long n) throws ClosedChannelException, IOException {
        return this.delegate.skipBytes(n);
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
