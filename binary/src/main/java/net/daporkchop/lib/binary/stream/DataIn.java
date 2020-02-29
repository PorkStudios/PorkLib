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

package net.daporkchop.lib.binary.stream;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.netty.NettyByteBufIn;
import net.daporkchop.lib.binary.stream.nio.BufferIn;
import net.daporkchop.lib.binary.stream.stream.StreamIn;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Provides simple methods for reading data from a binary form
 *
 * @author DaPorkchop_
 * @see DataOut
 */
public abstract class DataIn extends InputStream {
    /**
     * Wraps an {@link InputStream} to make it into a {@link DataIn}.
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link DataIn}
     */
    public static DataIn wrap(@NonNull InputStream in) {
        return in instanceof DataIn ? (DataIn) in : new StreamIn.Closing(in);
    }

    /**
     * Wraps an {@link InputStream} to make it into a {@link DataIn}.
     * <p>
     * Calling {@link #close()} on the returned {@link DataIn} will not cause the wrapped stream to be closed.
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link StreamIn}
     */
    public static DataIn wrapNonClosing(@NonNull InputStream in) {
        return in instanceof StreamIn && !(in instanceof StreamIn.Closing)
                ? (StreamIn) in
                : new StreamIn(in instanceof DataIn ? ((DataIn) in).unwrap() : in);
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into a {@link DataIn}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as a {@link DataIn}
     */
    public static DataIn wrap(@NonNull ByteBuffer buffer) {
        /*if (buffer.hasArray()) {
            return new StreamIn(new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining()));
        } else {*/
            return new BufferIn(buffer);
        //}
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into an {@link InputStream}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as an {@link InputStream}
     */
    public static InputStream wrapAsStream(@NonNull ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining());
        } else {
            return new BufferIn(buffer);
        }
    }

    /**
     * @see #wrapBuffered(File)
     */
    public static DataIn wrap(@NonNull File file) throws IOException {
        return wrapBuffered(file);
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link File}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the default buffer size of {@link BufferedInputStream#DEFAULT_BUFFER_SIZE}.
     *
     * @param file the file to read from
     * @return a buffered {@link DataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataIn wrapBuffered(@NonNull File file) throws IOException {
        return wrap(new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link File}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the given buffer size.
     *
     * @param file       the file to read from
     * @param bufferSize the size of the buffer to use
     * @return a buffered {@link DataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataIn wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrap(new BufferedInputStream(new FileInputStream(file), bufferSize));
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link File}.
     * <p>
     * {@link DataIn} instances returned from this method will NOT be buffered.
     *
     * @param file the file to read from
     * @return a direct {@link DataIn} that will read from the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    public static DataIn wrapNonBuffered(@NonNull File file) throws IOException {
        return wrap(new FileInputStream(file));
    }
    /**
     * Wraps a {@link ByteBuf} into a {@link DataIn} for reading.
     * <p>
     * When the {@link DataIn} is closed (using {@link DataIn#close()}), the {@link ByteBuf} will not be released.
     *
     * @param buf the {@link ByteBuf} to read from
     * @return a {@link DataIn} that can read data from the {@link ByteBuf}
     */
    public static DataIn wrap(@NonNull ByteBuf buf) {
        return wrap(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataIn} for reading.
     * <p>
     * When the {@link DataIn} is closed (using {@link DataIn#close()}), the {@link ByteBuf} may or may not be released, depending on the value of the
     * {@code release} parameter.
     *
     * @param buf     the {@link ByteBuf} to read from
     * @param release whether or not to release the buffer when the {@link DataIn} is closed
     * @return a {@link DataIn} that can read data from the {@link ByteBuf}
     */
    public static DataIn wrap(@NonNull ByteBuf buf, boolean release) {
        return release ? new NettyByteBufIn.Releasing(buf) : new NettyByteBufIn(buf);
    }

    /**
     * Read a boolean.
     *
     * @return a boolean
     */
    public boolean readBoolean() throws IOException {
        return this.read() == 1;
    }

    /**
     * Read a byte (8-bit) value.
     *
     * @return a byte
     */
    public byte readByte() throws IOException {
        return (byte) this.read();
    }

    /**
     * Read a byte (8-bit) value.
     *
     * @return a byte
     */
    public int readUByte() throws IOException {
        return this.read() & 0xFF;
    }

    /**
     * Read a big-endian short (16-bit) value.
     *
     * @return a short
     */
    public short readShort() throws IOException {
        return (short) (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Read a big-endian short (16-bit) value.
     *
     * @return a short
     */
    public int readUShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }

    /**
     * Read a little-endian short (16-bit) value.
     *
     * @return a short
     */
    public short readShortLE() throws IOException {
        return (short) ((this.read() & 0xFF)
                | ((this.read() & 0xFF) << 8));
    }

    /**
     * Read a little-endian short (16-bit) value.
     *
     * @return a short
     */
    public int readUShortLE() throws IOException {
        return this.readShortLE() & 0xFFFF;
    }

    /**
     * Read a big-endian char (16-bit) value.
     *
     * @return a char
     */
    public char readChar() throws IOException {
        return (char) (((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF));
    }

    /**
     * Read a little-endian char (16-bit) value.
     *
     * @return a char
     */
    public char readCharLE() throws IOException {
        return (char) ((this.read() & 0xFF)
                | ((this.read() & 0xFF) << 8));
    }

    /**
     * Read a big-endian int (32-bit) value.
     *
     * @return an int
     */
    public int readInt() throws IOException {
        return ((this.read() & 0xFF) << 24)
                | ((this.read() & 0xFF) << 16)
                | ((this.read() & 0xFF) << 8)
                | (this.read() & 0xFF);
    }

    /**
     * Read a big-endian int (32-bit) value.
     *
     * @return an int
     */
    public long readUInt() throws IOException {
        return this.readInt() & 0xFFFFFFFFL;
    }

    /**
     * Read a little-endian int (32-bit) value.
     *
     * @return an int
     */
    public int readIntLE() throws IOException {
        return (this.read() & 0xFF)
                | ((this.read() & 0xFF) << 8)
                | ((this.read() & 0xFF) << 16)
                | ((this.read() & 0xFF) << 24);
    }

    /**
     * Read a little-endian int (32-bit) value.
     *
     * @return an int
     */
    public long readUIntLE() throws IOException {
        return this.readIntLE() & 0xFFFFFFFFL;
    }

    /**
     * Read a big-endian long (64-bit) value.
     *
     * @return a long
     */
    public long readLong() throws IOException {
        return (((long) this.read() & 0xFF) << 56L)
                | (((long) this.read() & 0xFF) << 48L)
                | (((long) this.read() & 0xFF) << 40L)
                | (((long) this.read() & 0xFF) << 32L)
                | (((long) this.read() & 0xFF) << 24L)
                | (((long) this.read() & 0xFF) << 16L)
                | (((long) this.read() & 0xFF) << 8L)
                | ((long) this.read() & 0xFF);
    }

    /**
     * Read a little-endian long (64-bit) value.
     *
     * @return a long
     */
    public long readLongLE() throws IOException {
        return ((long) this.read() & 0xFF)
                | (((long) this.read() & 0xFF) << 8L)
                | (((long) this.read() & 0xFF) << 16L)
                | (((long) this.read() & 0xFF) << 24L)
                | (((long) this.read() & 0xFF) << 32L)
                | (((long) this.read() & 0xFF) << 40L)
                | (((long) this.read() & 0xFF) << 48L)
                | (((long) this.read() & 0xFF) << 56L);
    }

    /**
     * Read a big-endian float (32-bit floating point) value.
     *
     * @return a float
     */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Read a little-endian float (32-bit floating point) value.
     *
     * @return a float
     */
    public float readFloatLE() throws IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }

    /**
     * Read a big-endian double (64-bit floating point) value.
     *
     * @return a double
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    /**
     * Read a little-endian double (64-bit floating point) value.
     *
     * @return a double
     */
    public double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }

    /**
     * Read a UTF-8 encoded string.
     *
     * @return a string
     */
    public String readUTF() throws IOException {
        return new String(this.readByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a plain byte array with a length prefix encoded as a varInt.
     *
     * @return a byte array
     */
    public byte[] readByteArray() throws IOException {
        byte[] b = new byte[this.readVarInt()];
        this.readFully(b);
        return b;
    }

    /**
     * Reads an enum value.
     *
     * @param f   a function to calculate the enum value from the name (i.e. MyEnum::valueOf)
     * @param <E> the enum type
     * @return a value of <E>, or null if input was null
     */
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws IOException {
        if (this.readBoolean()) {
            return f.apply(this.readUTF());
        } else {
            return null;
        }
    }

    /**
     * Reads a Mojang-style VarInt.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the read value
     */
    public int readVarInt() throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = this.readByte();
            result |= ((read & 0b01111111) << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

    /**
     * Reads a Mojang-style VarLong.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the read value
     */
    public long readVarLong() throws IOException {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = this.readByte();
            result |= ((read & 0b01111111L) << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

    /**
     * Reads a {@link CharSequence} using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param size    the length of the encoded {@link CharSequence} in bytes
     * @param charset the {@link Charset} to encode the text using
     * @return the read {@link CharSequence}
     */
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException  {
        if (size > Integer.MAX_VALUE)   {
            throw new IllegalArgumentException("size parameter too large!");
        }
        return new String(this.readFully(new byte[(int) size]), charset);
    }

    /**
     * Fills the given {@code byte[]} with data.
     *
     * @param dst the {@code byte[]} to read to
     * @throws EOFException if EOF is reached before the given {@code byte[]} could be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    public byte[] readFully(@NonNull byte[] dst) throws EOFException, IOException {
        return this.readFully(dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with data.
     *
     * @param dst    the {@code byte[]} to read to
     * @param start  the first index (inclusive) in the {@code byte[]} to start writing to
     * @param length the number of bytes to read into the {@code byte[]}
     * @return the {@code byte[]}
     * @throws EOFException if EOF is reached before the given number of bytes could be read
     * @throws IOException  if an IO exception occurs you dummy
     */
    public byte[] readFully(@NonNull byte[] dst, int start, int length) throws EOFException, IOException {
        PorkUtil.assertInRangeLen(dst.length, start, length);
        for (int i; length > 0 && (i = this.read(dst, start, length)) != -1; start += i, length -= i) ;
        if (length != 0) {
            throw new EOFException();
        }
        return dst;
    }

    /**
     * Reads the entire contents of this {@link DataIn} into a {@code byte[]}.
     *
     * @return the contents of this {@link DataIn} as a {@code byte[]}
     */
    public byte[] toByteArray() throws IOException {
        byte[] arr = new byte[4096];
        int pos = 0;
        for (int i; (i = this.read(arr, pos, arr.length - pos)) != -1; pos += i) {
            if (pos + i == arr.length) {
                //grow array
                byte[] old = arr;
                System.arraycopy(old, 0, arr = new byte[arr.length << 1], 0, old.length);
            }
        }
        return pos == arr.length ? arr : Arrays.copyOf(arr, pos); //don't copy if the size is exactly the size of the array already
    }

    /**
     * Gets an {@link InputStream} that may be used in place of this {@link DataIn} instance.
     * <p>
     * An implementation may choose to return itself.
     * <p>
     * This is intended for use where a {@link DataIn} instance must be passed to external code that only accepts a
     * traditional Java {@link InputStream}, and performance may benefit from not having all method calls be proxied
     * by a wrapper {@link DataIn} instance.
     *
     * @return an {@link InputStream} that may be used in place of this {@link DataIn} instance
     */
    public InputStream unwrap() {
        return this;
    }

    @Override
    public abstract void close() throws IOException;
}
