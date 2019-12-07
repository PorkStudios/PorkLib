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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.binary.stream.netty.NettyByteBufOut;
import net.daporkchop.lib.binary.stream.nio.BufferOut;
import net.daporkchop.lib.binary.stream.stream.StreamOut;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Provides simple methods for encoding data to a binary form
 *
 * @author DaPorkchop_
 * @see DataIn
 */
public abstract class DataOut extends OutputStream {
    /**
     * Wraps an {@link OutputStream} to make it a {@link DataOut}.
     *
     * @param out the {@link OutputStream} to wrap
     * @return the wrapped stream, or the original stream if it was already an instance of {@link DataOut}
     */
    public static DataOut wrap(@NonNull OutputStream out) {
        return out instanceof DataOut ? (DataOut) out : new StreamOut.Closing(out);
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
        return out instanceof StreamOut && !(out instanceof StreamOut.Closing)
                ? (StreamOut) out
                : new StreamOut(out instanceof DataOut ? ((DataOut) out).unwrap() : out);
    }

    /**
     * Wraps a {@link ByteBuffer} to make it a {@link DataOut}.
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
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing.
     * <p>
     * When the {@link DataOut} is closed (using {@link DataOut#close()}), the {@link ByteBuf} will not be released.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     */
    public static DataOut wrap(@NonNull ByteBuf buf) {
        return new NettyByteBufOut.Default(buf);
    }

    /**
     * /dev/null
     *
     * @return an instance of {@link DataOut} that will discard any data written to it
     * @see SlashDevSlashNull
     */
    public static DataOut slashDevSlashNull() {
        return SlashDevSlashNull.INSTANCE;
    }

    /**
     * Writes a boolean.
     *
     * @param b the boolean to write
     */
    public DataOut writeBoolean(boolean b) throws IOException {
        this.write(b ? 1 : 0);
        return this;
    }

    /**
     * Writes a byte (8-bit) value.
     *
     * @param b the byte to write
     */
    public DataOut writeByte(byte b) throws IOException {
        this.write(b & 0xFF);
        return this;
    }

    /**
     * Writes a byte (8-bit) value.
     *
     * @param b the byte to write
     */
    public DataOut writeUByte(int b) throws IOException {
        this.write(b & 0xFF);
        return this;
    }

    /**
     * Writes a big-endian short (16-bit) value.
     *
     * @param s the short to write
     */
    public DataOut writeShort(short s) throws IOException {
        this.write((s >>> 8) & 0xFF);
        this.write(s & 0xFF);
        return this;
    }

    /**
     * Writes a big-endian short (16-bit) value.
     *
     * @param s the short to write
     */
    public DataOut writeUShort(int s) throws IOException {
        return this.writeShort((short) (s & 0xFFFF));
    }

    /**
     * Writes a little-endian short (16-bit) value.
     *
     * @param s the short to write
     */
    public DataOut writeShortLE(short s) throws IOException {
        this.write(s & 0xFF);
        this.write((s >>> 8) & 0xFF);
        return this;
    }

    /**
     * Writes a little-endian short (16-bit) value.
     *
     * @param s the short to write
     */
    public DataOut writeUShortLE(int s) throws IOException {
        return this.writeShortLE((short) (s & 0xFFFF));
    }

    /**
     * Writes a big-endian char (16-bit) value.
     *
     * @param c the char to write
     */
    public DataOut writeChar(char c) throws IOException {
        this.write((c >>> 8) & 0xFF);
        this.write(c & 0xFF);
        return this;
    }

    /**
     * Writes a big-endian char (16-bit) value.
     *
     * @param c the char to write
     */
    public DataOut writeCharLE(char c) throws IOException {
        this.write(c & 0xFF);
        this.write((c >>> 8) & 0xFF);
        return this;
    }

    /**
     * Writes a big-endian int (32-bit) value.
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
     * Writes a big-endian int (32-bit) value.
     *
     * @param i the int to write
     */
    public DataOut writeUInt(long i) throws IOException {
        return this.writeInt((int) (i & 0xFFFFFFFFL));
    }

    /**
     * Writes a little-endian int (32-bit) value.
     *
     * @param i the int to write
     */
    public DataOut writeIntLE(int i) throws IOException {
        this.write(i & 0xFF);
        this.write((i >>> 8) & 0xFF);
        this.write((i >>> 16) & 0xFF);
        this.write((i >>> 24) & 0xFF);
        return this;
    }

    /**
     * Writes a little-endian int (32-bit) value.
     *
     * @param i the int to write
     */
    public DataOut writeUIntLE(long i) throws IOException {
        return this.writeIntLE((int) (i & 0xFFFFFFFFL));
    }

    /**
     * Writes a big-endian long (64-bit) value.
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
     * Writes a little-endian long (64-bit) value.
     *
     * @param l the long to write
     */
    public DataOut writeLongLE(long l) throws IOException {
        this.write((int) l & 0xFF);
        this.write((int) (l >>> 8L) & 0xFF);
        this.write((int) (l >>> 16L) & 0xFF);
        this.write((int) (l >>> 24L) & 0xFF);
        this.write((int) (l >>> 32L) & 0xFF);
        this.write((int) (l >>> 40L) & 0xFF);
        this.write((int) (l >>> 48L) & 0xFF);
        this.write((int) (l >>> 56L) & 0xFF);
        return this;
    }

    /**
     * Writes a big-endian float (32-bit floating point) value.
     *
     * @param f the float to write
     */
    public DataOut writeFloat(float f) throws IOException {
        return this.writeInt(Float.floatToIntBits(f));
    }

    /**
     * Writes a little-endian float (32-bit floating point) value.
     *
     * @param f the float to write
     */
    public DataOut writeFloatLE(float f) throws IOException {
        return this.writeIntLE(Float.floatToIntBits(f));
    }

    /**
     * Writes a big-endian double (64-bit floating point) value.
     *
     * @param d the double to write
     */
    public DataOut writeDouble(double d) throws IOException {
        return this.writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Writes a little-endian double (64-bit floating point) value.
     *
     * @param d the double to write
     */
    public DataOut writeDoubleLE(double d) throws IOException {
        return this.writeLongLE(Double.doubleToLongBits(d));
    }

    /**
     * Writes a UTF-8 encoded string, including a null header and the length in bytes encoded as a varInt.
     *
     * @param s the string to write
     */
    public DataOut writeUTF(@NonNull String s) throws IOException {
        return this.writeByteArray(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes a plain byte array with a length prefix encoded as a varInt.
     *
     * @param b the bytes to write
     */
    public DataOut writeByteArray(@NonNull byte[] b) throws IOException {
        return this.writeVarInt(b.length).writeBytes(b);
    }

    /**
     * Writes an enum value.
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

    /**
     * Writes the given {@link CharSequence} using the given {@link Charset}.
     * <p>
     * It will not be length-prefixed, meaning that it will not be able to be read directly using the corresponding method in {@link DataIn}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param text    the {@link CharSequence} to write
     * @param charset the {@link Charset} to encode the text using
     * @return the number of bytes written
     */
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        byte[] b = text.toString().getBytes(charset);
        this.write(b);
        return b.length;
    }

    public DataOut writeBytes(@NonNull byte[] b) throws IOException {
        this.write(b);
        return this;
    }

    public DataOut writeBytes(@NonNull byte[] b, int off, int len) throws IOException {
        this.write(b, off, len);
        return this;
    }

    /**
     * Gets an {@link OutputStream} that may be used in place of this {@link DataOut} instance.
     * <p>
     * An implementation may choose to return itself.
     * <p>
     * This is intended for use where a {@link DataOut} instance must be passed to external code that only accepts a
     * traditional Java {@link OutputStream}, and performance may benefit from not having all method calls be proxied
     * by a wrapper {@link DataOut} instance.
     *
     * @return an {@link OutputStream} that may be used in place of this {@link DataOut} instance
     */
    public OutputStream unwrap() {
        return this;
    }

    @Override
    public abstract void close() throws IOException;
}
