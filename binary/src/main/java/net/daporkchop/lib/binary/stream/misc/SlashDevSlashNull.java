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

package net.daporkchop.lib.binary.stream.misc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * /dev/null
 * <p>
 * A {@link DataOut} implementation that simply discards all data written to it.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlashDevSlashNull extends DataOut {
    public static final SlashDevSlashNull INSTANCE = new SlashDevSlashNull();

    @Override
    public void close() throws IOException {
    }

    @Override
    public void write(int b) throws IOException {
    }

    @Override
    public DataOut writeBoolean(boolean b) throws IOException {
        return this;
    }

    @Override
    public DataOut writeByte(byte b) throws IOException {
        return this;
    }

    @Override
    public DataOut writeUByte(int b) throws IOException {
        return this;
    }

    @Override
    public DataOut writeShort(short s) throws IOException {
        return this;
    }

    @Override
    public DataOut writeUShort(int s) throws IOException {
        return this;
    }

    @Override
    public DataOut writeShortLE(short s) throws IOException {
        return this;
    }

    @Override
    public DataOut writeUShortLE(int s) throws IOException {
        return this;
    }

    @Override
    public DataOut writeChar(char c) throws IOException {
        return this;
    }

    @Override
    public DataOut writeCharLE(char c) throws IOException {
        return this;
    }

    @Override
    public DataOut writeInt(int i) throws IOException {
        return this;
    }

    @Override
    public DataOut writeUInt(long i) throws IOException {
        return this;
    }

    @Override
    public DataOut writeIntLE(int i) throws IOException {
        return this;
    }

    @Override
    public DataOut writeUIntLE(long i) throws IOException {
        return this;
    }

    @Override
    public DataOut writeLong(long l) throws IOException {
        return this;
    }

    @Override
    public DataOut writeLongLE(long l) throws IOException {
        return this;
    }

    @Override
    public DataOut writeFloat(float f) throws IOException {
        return this;
    }

    @Override
    public DataOut writeFloatLE(float f) throws IOException {
        return this;
    }

    @Override
    public DataOut writeDouble(double d) throws IOException {
        return this;
    }

    @Override
    public DataOut writeDoubleLE(double d) throws IOException {
        return this;
    }

    @Override
    public DataOut writeUTF(String s) throws IOException {
        return this;
    }

    @Override
    public DataOut writeByteArray(byte[] b) throws IOException {
        return this;
    }

    @Override
    public <E extends Enum<E>> DataOut writeEnum(@NonNull E e) throws IOException {
        return this;
    }

    @Override
    public DataOut writeVarInt(int value) throws IOException {
        return this;
    }

    @Override
    public DataOut writeVarLong(long value) throws IOException {
        return this;
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return 0L;
    }

    @Override
    public DataOut writeBytes(byte[] b) throws IOException {
        return this;
    }

    @Override
    public DataOut writeBytes(byte[] b, int off, int len) throws IOException {
        return this;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
    }
}
