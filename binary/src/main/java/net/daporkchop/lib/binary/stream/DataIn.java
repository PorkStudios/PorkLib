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
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Combination of {@link DataInput}, {@link ScatteringByteChannel} and {@link InputStream}, plus some custom methods.
 * <p>
 * Unless otherwise specified by an implementation, instances of this class are not thread-safe. Notably, many multithreading aspects of
 * {@link ScatteringByteChannel} are unlikely to work correctly, if at all.
 *
 * @author DaPorkchop_
 * @see DataOut
 */
public interface DataIn extends DataInput, ScatteringByteChannel, Closeable {
    //
    //
    // creators
    //
    //

    /**
     * Wraps an {@link InputStream} to make it into a {@link DataIn}.
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link DataIn}
     */
    static DataIn wrap(@NonNull InputStream in) {
        return in instanceof DataIn ? (DataIn) in : new StreamIn.NonClosing(in);
    }

    /**
     * Wraps an {@link InputStream} to make it into a {@link DataIn}.
     * <p>
     * Calling {@link #close()} on the returned {@link DataIn} will not cause the wrapped stream to be closed.
     *
     * @param in the stream to wrap
     * @return the wrapped stream, or the original stream if it was already a {@link StreamIn}
     */
    static DataIn wrapNonClosing(@NonNull InputStream in) {
        return in instanceof StreamIn && !(in instanceof StreamIn.NonClosing)
               ? (StreamIn) in
               : new StreamIn(in instanceof DataIn ? ((DataIn) in).unwrap() : in);
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into a {@link DataIn}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as a {@link DataIn}
     */
    static DataIn wrap(@NonNull ByteBuffer buffer) {
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
    static InputStream wrapAsStream(@NonNull ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining());
        } else {
            return new BufferIn(buffer);
        }
    }

    /**
     * @see #wrapBuffered(File)
     */
    static DataIn wrap(@NonNull File file) throws IOException {
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
    static DataIn wrapBuffered(@NonNull File file) throws IOException {
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
    static DataIn wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
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
    static DataIn wrapNonBuffered(@NonNull File file) throws IOException {
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
    static DataIn wrap(@NonNull ByteBuf buf) {
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
    static DataIn wrap(@NonNull ByteBuf buf, boolean release) {
        return release ? new NettyByteBufIn.Releasing(buf) : new NettyByteBufIn(buf);
    }

    //
    //
    // single byte read methods
    //
    //

    /**
     * Reads a single unsigned byte.
     * <p>
     * Unlike {@link #readUnsignedByte()}, this method will never throw {@link EOFException}. Once EOF is reached, it will always return {@code -1}.
     *
     * @return an unsigned byte, or {@code -1} if EOF has been reached
     * @see InputStream#read()
     */
    int read() throws IOException;

    //
    //
    // primitives
    //
    //

    @Override
    default boolean readBoolean() throws IOException {
        return this.readUnsignedByte() != 0;
    }

    @Override
    default byte readByte() throws IOException {
        return (byte) this.readUnsignedByte();
    }

    @Override
    int readUnsignedByte() throws IOException;

    /**
     * Reads a big-endian {@code short}.
     *
     * @see DataInput#readShort()
     */
    @Override
    default short readShort() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Short.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Short.reverseBytes(PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads an unsigned big-endian {@code short}.
     *
     * @see DataInput#readUnsignedShort()
     */
    @Override
    default int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }

    /**
     * Reads a little-endian {@code short}.
     *
     * @see #readShort()
     */
    default short readShortLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Short.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Short.reverseBytes(PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads an unsigned little-endian {@code short}.
     *
     * @see #readUnsignedShort()
     */
    default int readUnsignedShortLE() throws IOException {
        return this.readShortLE() & 0xFFFF;
    }

    /**
     * Reads a big-endian {@code char}.
     *
     * @see DataInput#readChar()
     */
    @Override
    default char readChar() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Character.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Character.reverseBytes(PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads a little-endian {@code char}.
     *
     * @see #readChar()
     */
    default char readCharLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Character.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Character.reverseBytes(PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads a big-endian {@code int}.
     *
     * @see DataInput#readInt()
     */
    @Override
    default int readInt() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Integer.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Integer.reverseBytes(PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads a little-endian {@code int}.
     *
     * @see #readInt()
     */
    default int readIntLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Integer.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Integer.reverseBytes(PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads a big-endian {@code long}.
     *
     * @see DataInput#readLong()
     */
    @Override
    default long readLong() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Long.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Long.reverseBytes(PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads a little-endian {@code long}.
     *
     * @see #readLong()
     */
    default long readLongLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Long.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Long.reverseBytes(PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    /**
     * Reads a big-endian {@code float}.
     *
     * @see DataInput#readFloat()
     */
    @Override
    default float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Reads a little-endian {@code float}.
     *
     * @see #readFloat()
     */
    default float readFloatLE() throws IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }

    /**
     * Reads a big-endian {@code double}.
     *
     * @see DataInput#readDouble()
     */
    @Override
    default double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    /**
     * Reads a little-endian {@code double}.
     *
     * @see #readDouble()
     */
    default double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }

    //
    //
    // other types
    //
    //

    /**
     * Reads a UTF-8 encoded {@link String} with a 16-bit length prefix.
     *
     * @see DataInput#readUTF()
     */
    @Override
    default String readUTF() throws IOException {
        return this.readString(this.readUnsignedShort(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a UTF-8 encoded {@link String} with a varlong length prefix.
     *
     * @see #readUTF()
     * @see #readVarString(Charset)
     */
    default String readVarUTF() throws IOException {
        return this.readString(this.readVarLong(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a {@link String} with a 16-bit length prefix, encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param charset the {@link Charset} that the {@link String} is encoded with
     * @return the decoded {@link String}
     * @see #readString(long, Charset)
     */
    default String readString(@NonNull Charset charset) throws IOException {
        return this.readString(this.readUnsignedShort(), charset);
    }

    /**
     * Reads a {@link String} with a varlong length prefix, encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param charset the {@link Charset} that the {@link String} is encoded with
     * @return the decoded {@link String}
     * @see #readString(long, Charset)
     */
    default String readVarString(@NonNull Charset charset) throws IOException {
        return this.readString(this.readVarLong(), charset);
    }

    /**
     * Reads a {@link String} encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param size    the length of the encoded {@link CharSequence} in bytes
     * @param charset the {@link Charset} to encode the text using
     * @return the read {@link CharSequence}
     */
    default String readString(long size, @NonNull Charset charset) throws IOException {
        //TODO: it's possible for the encoded form to be more than 2^31-1 bytes without the decoded form being too large for a String
        int length = PValidation.toInt(size, "size");
        if (length <= PorkUtil.BUFFER_SIZE) {
            try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
                return new String(this.fill(handle.get(), 0, length), 0, length, charset);
            }
        } else {
            return new String(this.fill(new byte[length]), charset);
        }
    }

    /**
     * This method is not supported unless specifically stated so by an implementation. Its definition is incompatible with the streaming nature of
     * {@link DataIn}, and as such cannot be implemented generically.
     */
    @Override
    @Deprecated
    default String readLine() throws IOException {
        throw new UnsupportedOperationException(this.toString());
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
    default CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        return this.readString(size, charset);
    }

    /**
     * Reads an enum value.
     *
     * @param f   a function to calculate the enum value from the name (i.e. MyEnum::valueOf)
     * @param <E> the enum type
     * @return a value of <E>, or null if input was null
     */
    @Deprecated
    default <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws IOException {
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
    default int readVarInt() throws IOException {
        int bytesRead = 0;
        int value = 0;
        int b;
        do {
            b = this.readUnsignedByte();
            value |= ((b & 0b01111111) << (7 * bytesRead));

            if (++bytesRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((b & 0b10000000) != 0);
        return value;
    }

    /**
     * Reads a VarInt with ZigZag encoding.
     *
     * @return the read value
     */
    default int readVarIntZigZag() throws IOException {
        int i = this.readVarInt();
        return (i >> 1) ^ -(i & 1);
    }

    /**
     * Reads a Mojang-style VarLong.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the read value
     */
    default long readVarLong() throws IOException {
        int bytesRead = 0;
        long value = 0;
        int b;
        do {
            b = this.readUnsignedByte();
            value |= ((b & 0b01111111L) << (7 * bytesRead));

            if (++bytesRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((b & 0b10000000) != 0);
        return value;
    }

    /**
     * Reads a VarLong with ZigZag encoding.
     *
     * @return the read value
     */
    default long readVarLongZigZag() throws IOException {
        long l = this.readVarLong();
        return (l >> 1L) ^ -(l & 1L);
    }

    //
    //
    // bulk data transfer methods - byte[]
    //
    //

    /**
     * Fills the given {@code byte[]} with as much data as possible.
     * <p>
     * <p>
     * Like {@link InputStream#read(byte[])}, this method will read data until the byte array is filled, EOF is reached, or further data cannot
     * be read without blocking.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dst the {@code byte[]} to read to
     * @return the number of bytes actually read
     * @throws IOException if an IO exception occurs you dummy
     * @see InputStream#read(byte[])
     */
    default int read(@NonNull byte[] dst) throws IOException {
        return this.read(dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with as much data as possible.
     * <p>
     * Like {@link InputStream#read(byte[], int, int)}, this method will read data until the given number of bytes have been read, EOF is reached,
     * or further data cannot be read without blocking.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dst    the {@code byte[]} to read to
     * @param start  the first index (inclusive) in the {@code byte[]} to start writing to
     * @param length the number of bytes to read into the {@code byte[]}
     * @return the number of bytes actually read
     * @throws IOException if an IO exception occurs you dummy
     * @see InputStream#read(byte[], int, int)
     */
    int read(@NonNull byte[] dst, int start, int length) throws IOException;

    /**
     * Fills the given {@code byte[]} with data.
     *
     * @param dst the {@code byte[]} to read to
     * @throws EOFException if EOF is reached before the given {@code byte[]} could be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    @Override
    default void readFully(@NonNull byte[] dst) throws IOException {
        this.readFully(dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with data.
     *
     * @param dst    the {@code byte[]} to read to
     * @param start  the first index (inclusive) in the {@code byte[]} to start writing to
     * @param length the number of bytes to read into the {@code byte[]}
     * @throws EOFException if EOF is reached before the given {@code byte[]} could be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    @Override
    void readFully(@NonNull byte[] dst, int start, int length) throws IOException;

    /**
     * Fills the given {@code byte[]} with data.
     *
     * @param dst the {@code byte[]} to read to
     * @return the given {@code byte[]}
     * @throws EOFException if EOF is reached before the given {@code byte[]} could be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    default byte[] fill(@NonNull byte[] dst) throws IOException {
        this.readFully(dst);
        return dst;
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
    default byte[] fill(@NonNull byte[] dst, int start, int length) throws IOException {
        this.readFully(dst, start, length);
        return dst;
    }

    /**
     * Reads the entire contents of this {@link DataIn} into a {@code byte[]}.
     *
     * @return the contents of this {@link DataIn} as a {@code byte[]}
     */
    default byte[] toByteArray() throws IOException {
        byte[] arr = new byte[PUnsafe.PAGE_SIZE];
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

    //
    //
    // bulk transfer methods - ByteBuffer and ByteBuf - non-blocking
    //
    //

    /**
     * Reads data into the given {@link ByteBuffer}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no bytes available, EOF is reached, or more data cannot be read without
     * blocking. However, it is not guaranteed to read any bytes at all.
     *
     * @param dst the {@link ByteBuffer} to read data into
     * @return the actual number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    int read(@NonNull ByteBuffer dst) throws IOException;

    /**
     * Reads data into the given {@link ByteBuffer}s.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no bytes available, EOF is reached, or more data cannot be read without
     * blocking. However, it is not guaranteed to read any bytes at all.
     *
     * @param dsts the {@link ByteBuffer}s to read data into
     * @return the actual number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    default long read(@NonNull ByteBuffer[] dsts) throws IOException {
        return this.read(dsts, 0, dsts.length);
    }

    /**
     * Reads data into the given {@link ByteBuffer}s.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no bytes available, EOF is reached, or more data cannot be read without
     * blocking. However, it is not guaranteed to read any bytes at all.
     *
     * @param dsts   the {@link ByteBuffer}s to read data into
     * @param offset the index of the first {@link ByteBuffer} to read data into
     * @param length the number of {@link ByteBuffer}s to read data into
     * @return the actual number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    default long read(@NonNull ByteBuffer[] dsts, int offset, int length) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        PValidation.checkRangeLen(dsts.length, offset, length);
        long l = 0L;
        for (int i = 0; i < length; i++) {
            int read = 0;
            if (dsts[i].hasRemaining() && (read = this.read(dsts[i])) == 0) {
                //remaining space in buffer could not be filled, there is no more data available
                break;
            }
            l += read;
        }
        return l;
    }

    /**
     * Reads data into the given {@link ByteBuf}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the requested number of bytes have been read, EOF is reached, or more data
     * cannot be read without blocking. However, it is not guaranteed to read any bytes at all.
     *
     * @param dst   the {@link ByteBuf} to read data into
     * @param count the number of bytes to read
     * @return the actual number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int read(@NonNull ByteBuf dst, int count) throws IOException;

    /**
     * Reads data into the given {@link ByteBuf}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the requested number of bytes have been read, EOF is reached, or more data
     * cannot be read without blocking. However, it is not guaranteed to read any bytes at all.
     *
     * @param dst    the {@link ByteBuf} to read data into
     * @param start  the first index in the {@link ByteBuf} to read into
     * @param length the number of bytes to read
     * @return the actual number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int read(@NonNull ByteBuf dst, int start, int length) throws IOException;

    //
    //
    // bulk transfer methods - ByteBuffer and ByteBuf - blocking
    //
    //

    /**
     * Fills the given {@link ByteBuffer} with data.
     *
     * @param dst the {@link ByteBuffer} to read data into
     * @return the number of bytes read
     * @throws EOFException if EOF is reached before the buffer can be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    int readFully(@NonNull ByteBuffer dst) throws IOException;

    /**
     * Fills the given {@link ByteBuffer}s with data.
     *
     * @param dsts the {@link ByteBuffer}s to read data into
     * @return the number of bytes read
     * @throws EOFException if EOF is reached before the buffers can be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    default long readFully(@NonNull ByteBuffer[] dsts) throws IOException {
        return this.readFully(dsts, 0, dsts.length);
    }

    /**
     * Fills the given {@link ByteBuffer}s with data.
     *
     * @param dsts   the {@link ByteBuffer}s to read data into
     * @param offset the index of the first {@link ByteBuffer} to read data into
     * @param length the number of {@link ByteBuffer}s to read data into
     * @return the number of bytes read
     * @throws EOFException if EOF is reached before the buffers can be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    default long readFully(@NonNull ByteBuffer[] dsts, int offset, int length) throws IOException {
        PValidation.checkRangeLen(dsts.length, offset, length);
        long l = 0L;
        for (int i = 0; i < length; i++) {
            if (dsts[i].hasRemaining()) {
                i += this.readFully(dsts[i]);
            }
        }
        return l;
    }

    /**
     * Fills the given {@link ByteBuf} with data.
     *
     * @param dst   the {@link ByteBuf} to read data into
     * @param count the number of bytes to read
     * @return the number of bytes read
     * @throws EOFException if EOF is reached before the buffer can be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    int readFully(@NonNull ByteBuf dst, int count) throws IOException;

    /**
     * Fills the given {@link ByteBuf} with data.
     *
     * @param dst    the {@link ByteBuf} to read data into
     * @param start  the first index in the {@link ByteBuf} to read into
     * @param length the number of bytes to read
     * @return the number of bytes read
     * @throws EOFException if EOF is reached before the buffer can be filled
     * @throws IOException  if an IO exception occurs you dummy
     */
    int readFully(@NonNull ByteBuf dst, int start, int length) throws IOException;

    //
    //
    // control methods
    //
    //

    /**
     * @see DataInput#skipBytes(int)
     */
    @Override
    default int skipBytes(int n) throws IOException {
        return PValidation.toInt(this.skipBytes((long) n));
    }

    /**
     * @see DataInput#skipBytes(int)
     */
    long skipBytes(long n) throws IOException;

    /**
     * Gets an {@link InputStream} that may be used in place of this {@link DataIn} instance.
     * <p>
     * Some implementations may choose to return itself.
     * <p>
     * This is intended for use where a {@link DataIn} instance must be passed to external code that only accepts a
     * traditional Java {@link InputStream}, and performance may benefit from not having all method calls be proxied
     * by a wrapper {@link DataIn} instance.
     *
     * @return an {@link InputStream} that may be used in place of this {@link DataIn} instance
     */
    InputStream asStream() throws IOException;

    @Override
    boolean isOpen();

    @Override
    void close() throws IOException;
}
