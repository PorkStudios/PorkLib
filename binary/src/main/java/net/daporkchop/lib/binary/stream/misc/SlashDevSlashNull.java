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
