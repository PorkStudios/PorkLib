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

package net.daporkchop.lib.binary.stream;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.data.BufferOut;
import net.daporkchop.lib.binary.stream.data.SlashDevSlashNull;
import net.daporkchop.lib.binary.stream.data.StreamOut;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Provides simple methods for encoding data to a binary form
 *
 * @author DaPorkchop_
 * @see DataIn
 */
public abstract class DataOut extends OutputStream {
    /**
     * Wraps an {@link OutputStream} to make it a {@link DataOut}
     *
     * @param out the {@link OutputStream} to wrap
     * @return the wrapped stream, or the original stream if it was already an instance of {@link DataOut}
     */
    public static DataOut wrap(@NonNull OutputStream out) {
        return out instanceof DataOut ? (DataOut) out : new StreamOut(out, true);
    }

    /**
     * Wraps an {@link OutputStream} to make it a {@link DataOut}.
     * <p>
     * Calling {@link #close()} on the returned {@link DataOut} will not cause the wrapped stream to be closed.
     *
     * @param out the {@link OutputStream} to wrap
     * @return the wrapped stream, or the original stream if it was already an instance of {@link DataOut}
     */
    public static DataOut wrapNonClosing(@NonNull OutputStream out) {
        return out instanceof StreamOut ? ((StreamOut) out).close(false) : new StreamOut(out, false);
    }

    /**
     * Wraps a {@link ByteBuffer} to make it a {@link DataOut}
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer
     */
    public static DataOut wrap(@NonNull ByteBuffer buffer) {
        return new BufferOut(buffer);
    }

    /**
     * @see #wrapBuffered(File)
     */
    public static DataOut wrap(@NonNull File file) throws IOException {
        return wrapBuffered(file);
    }

    /**
     * Gets a {@link DataOut} for writing to a {@link File}.
     * <p>
     * This stream will additionally be buffered for faster write access, using the default buffer size of 8192 bytes.
     *
     * @param file the file to write to
     * @return a buffered {@link DataOut} that will write to the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataOut wrapBuffered(@NonNull File file) throws IOException {
        return wrap(new BufferedOutputStream(new FileOutputStream(file)));
    }

    /**
     * Gets a {@link DataOut} for writing to a {@link File}.
     * <p>
     * This stream will additionally be buffered for faster write access, using the given buffer size.
     *
     * @param file       the file to write to
     * @param bufferSize the size of the buffer to use
     * @return a buffered {@link DataOut} that will write to the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataOut wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrap(new BufferedOutputStream(new FileOutputStream(file), bufferSize));
    }

    /**
     * Gets a {@link DataOut} for writing to a {@link File}.
     * <p>
     * {@link DataOut} instances returned by this method will NOT be buffered.
     *
     * @param file the file to write to
     * @return a direct {@link DataOut} that will write to the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataOut wrapNonBuffered(@NonNull File file) throws IOException {
        return wrap(new FileOutputStream(file));
    }

    /**
     * /dev/null
     *
     * @return an instance of {@link DataOut} that will discard any data written to it
     * @see SlashDevSlashNull
     */
    public static DataOut slashDevSlashNull() {
        return new SlashDevSlashNull();
    }

    /**
     * Writes a boolean
     *
     * @param b the boolean to write
     */
    public DataOut writeBoolean(boolean b) throws IOException {
        this.write(b ? 1 : 0);
        return this;
    }

    /**
     * Writes a byte (8-bit) value
     *
     * @param b the byte to write
     */
    public DataOut writeByte(byte b) throws IOException {
        this.write(b & 0xFF);
        return this;
    }

    /**
     * Writes a byte (8-bit) value
     *
     * @param b the byte to write
     */
    public DataOut writeUByte(int b) throws IOException {
        this.write(b & 0xFF);
        return this;
    }

    /**
     * Writes a short (16-bit) value
     *
     * @param s the short to write
     */
    public DataOut writeShort(short s) throws IOException {
        this.write((s >>> 8) & 0xFF);
        this.write(s & 0xFF);
        return this;
    }

    /**
     * Writes a short (16-bit) value
     *
     * @param s the short to write
     */
    public DataOut writeUShort(int s) throws IOException {
        return this.writeShort((short) (s & 0xFFFF));
    }

    /**
     * Writes a char (16-bit) value
     *
     * @param c the char to write
     */
    public DataOut writeChar(char c) throws IOException {
        this.write((c >>> 8) & 0xFF);
        this.write(c & 0xFF);
        return this;
    }

    /**
     * Writes an medium (24-bit) value
     *
     * @param m the medium to write
     */
    public DataOut writeMedium(int m) throws IOException {
        if ((m & 0xFF000000) != 0)  {
            m |= 0x800000;
        }
        return this.writeUMedium(m);
    }

    /**
     * Writes an medium (24-bit) value
     *
     * @param m the medium to write
     */
    public DataOut writeUMedium(int m) throws IOException {
        this.write((m >>> 16) & 0xFF);
        this.write((m >>> 8) & 0xFF);
        this.write(m & 0xFF);
        return this;
    }

    /**
     * Writes an int (32-bit) value
     *
     * @param i the int to write
     */
    public DataOut writeInt(int i) throws IOException {
        this.write((i >>> 24) & 0xFF);
        this.write((i >>> 16) & 0xFF);
        this.write((i >>> 8) & 0xFF);
        this.write(i & 0xFF);
        return this;
    }

    /**
     * Writes an int (32-bit) value
     *
     * @param i the int to write
     */
    public DataOut writeUInt(long i) throws IOException {
        return this.writeInt((int) (i & 0xFFFFFFFFL));
    }

    /**
     * Writes a long (64-bit) value
     *
     * @param l the long to write
     */
    public DataOut writeLong(long l) throws IOException {
        this.write((int) (l >>> 56) & 0xFF);
        this.write((int) (l >>> 48) & 0xFF);
        this.write((int) (l >>> 40) & 0xFF);
        this.write((int) (l >>> 32) & 0xFF);
        this.write((int) (l >>> 24) & 0xFF);
        this.write((int) (l >>> 16) & 0xFF);
        this.write((int) (l >>> 8) & 0xFF);
        this.write((int) l & 0xFF);
        return this;
    }

    /**
     * Writes a float (32-bit floating point) value
     *
     * @param f the float to write
     */
    public DataOut writeFloat(float f) throws IOException {
        return this.writeInt(Float.floatToIntBits(f));
    }

    /**
     * Writes a double (64-bit floating point) value
     *
     * @param d the double to write
     */
    public DataOut writeDouble(double d) throws IOException {
        return this.writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Writes a UTF-8 encoded string, including a null header and the length in bytes encoded as a varInt
     *
     * @param s the string to write
     */
    public DataOut writeUTF(@NonNull String s) throws IOException {
        return this.writeByteArray(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes a plain byte array with a length prefix encoded as a varInt
     *
     * @param b the bytes to write
     */
    public DataOut writeByteArray(@NonNull byte[] b) throws IOException {
        return this.writeVarInt(b.length).writeBytes(b);
    }

    /**
     * Writes an enum value
     *
     * @param e   the value to write
     * @param <E> the type of the enum
     */
    public <E extends Enum<E>> DataOut writeEnum(@NonNull E e) throws IOException {
        return this.writeUTF(e.name());
    }

    /**
     * Writes a Mojang-style VarInt.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @param value the value to write
     */
    public DataOut writeVarInt(int value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            this.write(temp);
        } while (value != 0);
        return this;
    }

    /**
     * Writes a Mojang-style VarLong.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @param value the value to write
     */
    public DataOut writeVarLong(long value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7L;
            if (value != 0) {
                temp |= 0b10000000;
            }
            this.write(temp);
        } while (value != 0L);
        return this;
    }

    public DataOut writeBytes(@NonNull byte[] b) throws IOException {
        this.write(b);
        return this;
    }

    public DataOut writeBytes(@NonNull byte[] b, int off, int len) throws IOException {
        this.write(b, off, len);
        return this;
    }

    @Override
    public abstract void close() throws IOException;
}
