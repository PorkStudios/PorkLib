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

package net.daporkchop.lib.binary.stream.nio;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An implementation of {@link DataOut} that can write to a {@link ByteBuffer}
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class BufferOut extends DataOut {
    @NonNull
    private final ByteBuffer buffer;

    @Override
    public void write(int b) throws IOException {
        this.buffer.put((byte) b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        this.buffer.put(b, off, len);
    }

    @Override
    public DataOut writeByte(byte b) throws IOException {
        this.buffer.put(b);
        return this;
    }

    @Override
    public DataOut writeShort(short s) throws IOException {
        this.buffer.order(ByteOrder.BIG_ENDIAN).putShort(s);
        return this;
    }

    @Override
    public DataOut writeShortLE(short s) throws IOException {
        this.buffer.order(ByteOrder.LITTLE_ENDIAN).putShort(s);
        return this;
    }

    @Override
    public DataOut writeChar(char c) throws IOException {
        this.buffer.order(ByteOrder.BIG_ENDIAN).putChar(c);
        return this;
    }

    @Override
    public DataOut writeCharLE(char c) throws IOException {
        this.buffer.order(ByteOrder.LITTLE_ENDIAN).putChar(c);
        return this;
    }

    @Override
    public DataOut writeInt(int i) throws IOException {
        this.buffer.order(ByteOrder.BIG_ENDIAN).putInt(i);
        return this;
    }

    @Override
    public DataOut writeIntLE(int i) throws IOException {
        this.buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(i);
        return this;
    }

    @Override
    public DataOut writeLong(long l) throws IOException {
        this.buffer.order(ByteOrder.BIG_ENDIAN).putLong(l);
        return this;
    }

    @Override
    public DataOut writeLongLE(long l) throws IOException {
        this.buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(l);
        return this;
    }

    @Override
    public DataOut writeFloat(float f) throws IOException {
        this.buffer.order(ByteOrder.BIG_ENDIAN).putFloat(f);
        return this;
    }

    @Override
    public DataOut writeFloatLE(float f) throws IOException {
        this.buffer.order(ByteOrder.LITTLE_ENDIAN).putFloat(f);
        return this;
    }

    @Override
    public DataOut writeDouble(double d) throws IOException {
        this.buffer.order(ByteOrder.BIG_ENDIAN).putDouble(d);
        return this;
    }

    @Override
    public DataOut writeDoubleLE(double d) throws IOException {
        this.buffer.order(ByteOrder.LITTLE_ENDIAN).putDouble(d);
        return this;
    }

    @Override
    public void close() throws IOException {
    }
}
