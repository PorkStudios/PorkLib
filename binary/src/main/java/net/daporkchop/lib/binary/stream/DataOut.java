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
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.binary.stream.netty.ByteBufOut;
import net.daporkchop.lib.binary.stream.nio.BufferOut;
import net.daporkchop.lib.binary.stream.stream.StreamOut;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Combination of {@link DataOutput}, {@link GatheringByteChannel} and {@link OutputStream}, plus some custom methods.
 * <p>
 * Unless otherwise specified by an implementation, instances of this class are not thread-safe. Notably, many multithreading aspects of
 * {@link GatheringByteChannel} are unlikely to work correctly, if at all.
 *
 * @author DaPorkchop_
 * @see DataIn
 */
public interface DataOut extends DataOutput, GatheringByteChannel, Closeable {
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
               : new StreamOut(out instanceof DataOut ? ((DataOut) out).asOutputStream() : out);
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
        return new ByteBufOut.Default(buf);
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

    //
    //
    // single byte write methods
    //
    //

    /**
     * Writes a single unsigned byte.
     *
     * @param b the byte to write
     * @see DataOutput#write(int)
     * @see OutputStream#write(int)
     */
    void write(int b) throws IOException;

    //
    //
    // primitives
    //
    //

    @Override
    default void writeBoolean(boolean b) throws IOException {
        this.write(b ? 1 : 0);
    }

    @Override
    default void writeByte(int b) throws IOException {
        this.write(b);
    }

    /**
     * Writes a big-endian {@code short}.
     *
     * @see DataOutput#writeShort(int)
     */
    @Override
    default void writeShort(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (short) v);
            } else {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Short.reverseBytes((short) v));
            }
            this.write(arr, 0, Short.BYTES);
        }
    }

    /**
     * Writes a little-endian {@code short}.
     *
     * @see #writeShort(int)
     */
    default void writeShortLE(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (short) v);
            } else {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Short.reverseBytes((short) v));
            }
            this.write(arr, 0, Short.BYTES);
        }
    }

    /**
     * Writes a big-endian {@code char}.
     *
     * @see DataOutput#writeChar(int)
     */
    @Override
    default void writeChar(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (char) v);
            } else {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Character.reverseBytes((char) v));
            }
            this.write(arr, 0, Character.BYTES);
        }
    }

    /**
     * Writes a little-endian {@code char}.
     *
     * @see #writeChar(int)
     */
    default void writeCharLE(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (char) v);
            } else {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Character.reverseBytes((char) v));
            }
            this.write(arr, 0, Character.BYTES);
        }
    }

    /**
     * Writes a big-endian {@code char}.
     *
     * @see DataOutput#writeChar(int)
     */
    @Override
    default void writeInt(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Integer.reverseBytes(v));
            }
            this.write(arr, 0, Integer.BYTES);
        }
    }

    default void writeIntLE(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Integer.reverseBytes(v));
            }
            this.write(arr, 0, Integer.BYTES);
        }
    }

    @Override
    default void writeLong(long v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Long.reverseBytes(v));
            }
            this.write(arr, 0, Long.BYTES);
        }
    }

    default void writeLongLE(long v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Long.reverseBytes(v));
            }
            this.write(arr, 0, Long.BYTES);
        }
    }

    /**
     * Writes a big-endian float (32-bit floating point) value.
     *
     * @param f the float to write
     */
    default void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    /**
     * Writes a little-endian float (32-bit floating point) value.
     *
     * @param f the float to write
     */
    default void writeFloatLE(float f) throws IOException {
        this.writeIntLE(Float.floatToIntBits(f));
    }

    /**
     * Writes a big-endian double (64-bit floating point) value.
     *
     * @param d the double to write
     */
    default void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Writes a little-endian double (64-bit floating point) value.
     *
     * @param d the double to write
     */
    default void writeDoubleLE(double d) throws IOException {
        this.writeLongLE(Double.doubleToLongBits(d));
    }

    //
    //
    // other types
    //
    //

    /**
     * Proxy method to {@link #writeBytes(CharSequence)}.
     *
     * @see DataOutput#writeBytes(String)
     */
    @Override
    default void writeBytes(@NonNull String text) throws IOException {
        this.writeBytes(text, 0, text.length());
    }

    /**
     * Writes the lower 8 bits of every character in the given {@link CharSequence}.
     *
     * @param text the {@link CharSequence} to write
     * @return the number of bytes written
     * @see #writeBytes(String)
     * @see DataOutput#writeBytes(String)
     */
    default long writeBytes(@NonNull CharSequence text) throws IOException {
        return this.writeBytes(text, 0, text.length());
    }

    /**
     * Writes the lower 8 bits of the characters in the given range of the given {@link CharSequence}.
     *
     * @param text   the {@link CharSequence} to write
     * @param start  the index of the first character to write
     * @param length the number of characters to write
     * @return the number of bytes written
     * @see #writeBytes(String)
     * @see DataOutput#writeBytes(String)
     */
    default long writeBytes(@NonNull CharSequence text, int start, int length) throws IOException {
        checkRangeLen(text.length(), start, length);
        if (length == 0) {
            return 0L;
        }
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            int total = 0;
            do {
                int blockSize = min(length - total, PorkUtil.BUFFER_SIZE);
                for (int i = 0; i < blockSize; i++) {
                    arr[i] = (byte) text.charAt(start + total + i);
                }
                this.write(arr, 0, blockSize);
                total += blockSize;
            } while (total < length);
        }
        return length;
    }

    @Override
    default void writeChars(@NonNull String text) throws IOException {
        this.writeChars(text, 0, text.length());
    }

    /**
     * Writes every character in the given {@link CharSequence} in the UTF-16BE charset.
     *
     * @param text the {@link CharSequence} to write
     * @return the number of bytes written
     * @see #writeChars(String)
     * @see DataOutput#writeChars(String)
     */
    default long writeChars(@NonNull CharSequence text) throws IOException {
        return this.writeChars(text, 0, text.length());
    }

    /**
     * Writes the characters in the given range of the given {@link CharSequence} in the UTF-16BE charset.
     *
     * @param text   the {@link CharSequence} to write
     * @param start  the index of the first character to write
     * @param length the number of characters to write
     * @return the number of bytes written
     * @see #writeChars(String)
     * @see DataOutput#writeChars(String)
     */
    default long writeChars(@NonNull CharSequence text, int start, int length) throws IOException {
        checkRangeLen(text.length(), start, length);
        if (length == 0) {
            return 0L;
        }
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            int total = 0;
            do {
                int blockSize = min(length - total, PorkUtil.BUFFER_SIZE / Character.BYTES);
                for (int i = 0; i < blockSize; i++) {
                    if (PlatformInfo.IS_BIG_ENDIAN) {
                        PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + i * Character.BYTES, text.charAt(total + i));
                    } else {
                        PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET + i * Character.BYTES, Character.reverseBytes(text.charAt(total + i)));
                    }
                }
                this.write(arr, 0, blockSize * Character.BYTES);
                total += blockSize;
            } while (total < length);
        }
        return length << 1L;
    }

    /**
     * Writes a UTF-8 encoded {@link String}, with a 16-bit length prefix
     *
     * @see DataOutput#writeUTF(String)
     */
    @Override
    default void writeUTF(@NonNull String text) throws IOException {
        this.writeString(text, StandardCharsets.UTF_8);
    }

    /**
     * Writes a UTF-8 encoded {@link CharSequence} with a 16-bit length prefix.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     */
    default void writeUTF(@NonNull CharSequence text) throws IOException {
        this.writeString(text, StandardCharsets.UTF_8);
    }

    /**
     * Writes a UTF-8 encoded {@link CharSequence} with a varlong length prefix.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     */
    default void writeVarUTF(@NonNull CharSequence text) throws IOException {
        this.writeVarString(text, StandardCharsets.UTF_8);
    }

    /**
     * Writes a {@link CharSequence} with a 16-bit length prefix, encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     */
    default void writeString(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        //TODO: optimize
        byte[] arr = text.toString().getBytes(charset);
        checkArg(arr.length <= Character.MAX_VALUE, "encoded value is too large (%d > %d)", arr.length, Character.MAX_VALUE);
        this.writeShort(arr.length);
        this.write(arr);
    }

    /**
     * Writes a {@link CharSequence} with a varlong length prefix, encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     */
    default void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        //TODO: optimize
        byte[] arr = text.toString().getBytes(charset);
        this.writeVarLong(arr.length);
        this.write(arr);
    }

    /**
     * Writes every character in the given {@link CharSequence} using the given {@link Charset}.
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
    default long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return this.writeText(text, 0, text.length(), charset);
    }

    /**
     * Writes the characters in the given range of the given {@link CharSequence} using the given {@link Charset}.
     * <p>
     * It will not be length-prefixed, meaning that it will not be able to be read directly using the corresponding method in {@link DataIn}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param text    the {@link CharSequence} to write
     * @param start   the index of the first character to write
     * @param length  the number of characters to write
     * @param charset the {@link Charset} to encode the text using
     * @return the number of bytes written
     */
    default long writeText(@NonNull CharSequence text, int start, int length, @NonNull Charset charset) throws IOException {
        if (charset == StandardCharsets.UTF_16BE) {
            return this.writeChars(text, start, length);
        }
        //TODO: optimize
        byte[] b = text.toString().getBytes(charset);
        this.write(b);
        return b.length;
    }

    /**
     * Writes an enum value.
     *
     * @param e   the value to write
     * @param <E> the type of the enum
     */
    @Deprecated
    default <E extends Enum<E>> void writeEnum(@NonNull E e) throws IOException {
        this.writeUTF(e.name());
    }

    /**
     * Writes a Mojang-style VarInt.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @param value the value to write
     */
    default void writeVarInt(int value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            this.write(temp);
        } while (value != 0);
    }

    /**
     * Writes a VarInt with ZigZag encoding.
     *
     * @param value the value to write
     */
    default void writeVarIntZigZag(int value) throws IOException {
        this.writeVarInt((value << 1) ^ (value >> 31));
    }

    /**
     * Writes a Mojang-style VarLong.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @param value the value to write
     */
    default void writeVarLong(long value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7L;
            if (value != 0) {
                temp |= 0b10000000;
            }
            this.write(temp);
        } while (value != 0L);
    }

    /**
     * Writes a VarLong with ZigZag encoding.
     *
     * @param value the value to write
     */
    default void writeVarLongZigZag(long value) throws IOException {
        this.writeVarLong((value << 1L) ^ (value >> 63L));
    }

    //
    //
    // bulk data transfer methods - byte[]
    //
    //

    /**
     * Writes the entire contents of the given {@code byte[]}.
     *
     * @param src the {@code byte[]} to write
     */
    @Override
    default void write(@NonNull byte[] src) throws IOException {
        this.write(src, 0, src.length);
    }

    /**
     * Writes the entire contents of the given region of the given {@code byte[]}.
     *
     * @param src    the {@code byte[]} to write
     * @param start  the index of the first byte to write
     * @param length the number of bytes to write
     */
    @Override
    void write(@NonNull byte[] src, int start, int length) throws IOException;

    //
    //
    // bulk transfer methods - ByteBuffer and ByteBuf - non-blocking
    //
    //

    /**
     * Writes data from the given {@link ByteBuffer}.
     * <p>
     * This method will write data until the buffer has no bytes remaining or more data cannot be written without blocking. However, it is not guaranteed
     * to write any bytes at all.
     *
     * @param src the {@link ByteBuffer} to write data from
     * @return the actual number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    int write(@NonNull ByteBuffer src) throws IOException;

    /**
     * Writes data from the given {@link ByteBuffer}s.
     * <p>
     * This method will write data until the buffers have no bytes remaining or more data cannot be written without blocking. However, it is not guaranteed
     * to write any bytes at all.
     *
     * @param srcs the {@link ByteBuffer}s to write data from
     * @return the actual number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    default long write(@NonNull ByteBuffer[] srcs) throws IOException {
        return this.write(srcs, 0, srcs.length);
    }

    /**
     * Writes data from the given {@link ByteBuffer}s.
     * <p>
     * This method will write data until the buffers have no bytes remaining or more data cannot be written without blocking. However, it is not guaranteed
     * to write any bytes at all.
     *
     * @param srcs   the {@link ByteBuffer}s to write data from
     * @param offset the index of the first {@link ByteBuffer} to write data from
     * @param length the number of {@link ByteBuffer}s to read write from
     * @return the actual number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    default long write(@NonNull ByteBuffer[] srcs, int offset, int length) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        checkRangeLen(srcs.length, offset, length);
        long l = 0L;
        for (int i = 0; i < length; i++) {
            int written = 0;
            if (srcs[i].hasRemaining() && (written = this.write(srcs[i])) == 0) {
                //remaining space in buffer could not be read, there is no more data available
                break;
            }
            l += written;
        }
        return l;
    }

    /**
     * Writes all readable bytes from the given {@link ByteBuf}.
     * <p>
     * This method will write data until the buffer has no bytes remaining or more data cannot be written without blocking. However, it is not guaranteed
     * to write any bytes at all.
     *
     * This method will also increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src the {@link ByteBuf} to write data from
     * @return the actual number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int write(@NonNull ByteBuf src) throws IOException;

    /**
     * Writes the requested number of bytes from the given {@link ByteBuf}.
     * <p>
     * This method will write data until requested number of bytes have been written or more data cannot be written without blocking. However, it is not
     * guaranteed to write any bytes at all.
     *
     * This method will also increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src   the {@link ByteBuf} to write data from
     * @param count the number of bytes to write
     * @return the actual number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int write(@NonNull ByteBuf src, int count) throws IOException;

    /**
     * Writes all bytes in the given range in the given {@link ByteBuf}.
     * <p>
     * This method will write data until requested number of bytes have been written or more data cannot be written without blocking. However, it is not
     * guaranteed to write any bytes at all.
     *
     * This method will not increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src   the {@link ByteBuf} to write data from
     * @param start  the index of the first byte to write
     * @param length the number of bytes to write
     * @return the actual number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int write(@NonNull ByteBuf src, int start, int length) throws IOException;

    //
    //
    // bulk transfer methods - ByteBuffer and ByteBuf - blocking
    //
    //

    /**
     * Writes all data from the given {@link ByteBuffer}.
     *
     * @param src the {@link ByteBuffer} to write data from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int writeFully(@NonNull ByteBuffer src) throws IOException;

    /**
     * Writes all data from the given {@link ByteBuffer}s.
     *
     * @param srcs the {@link ByteBuffer}s to write data from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    default long writeFully(@NonNull ByteBuffer[] srcs) throws IOException {
        return this.writeFully(srcs, 0, srcs.length);
    }

    /**
     * Writes all data from the given {@link ByteBuffer}s.
     * <p>
     * This method will write data until the buffers have no bytes remaining or more data cannot be written without blocking. However, it is not guaranteed
     * to write any bytes at all.
     *
     * @param srcs   the {@link ByteBuffer}s to write data from
     * @param offset the index of the first {@link ByteBuffer} to write data from
     * @param length the number of {@link ByteBuffer}s to read write from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    default long writeFully(@NonNull ByteBuffer[] srcs, int offset, int length) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        checkRangeLen(srcs.length, offset, length);
        long l = 0L;
        for (int i = 0; i < length; i++) {
            int written = 0;
            if (srcs[i].hasRemaining() && (written = this.writeFully(srcs[i])) == 0) {
                //remaining space in buffer could not be read, there is no more data available
                break;
            }
            l += written;
        }
        return l;
    }

    /**
     * Writes all readable bytes from the given {@link ByteBuf}.
     *
     * This method will also increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src the {@link ByteBuf} to write data from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int writeFully(@NonNull ByteBuf src) throws IOException;

    /**
     * Writes the requested number of bytes from the given {@link ByteBuf}.
     *
     * This method will also increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src   the {@link ByteBuf} to write data from
     * @param count the number of bytes to write
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int writeFully(@NonNull ByteBuf src, int count) throws IOException;

    /**
     * Writes all bytes in the given range in the given {@link ByteBuf}.
     *
     * This method will not increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src   the {@link ByteBuf} to write data from
     * @param start  the index of the first byte to write
     * @param length the number of bytes to write
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int writeFully(@NonNull ByteBuf src, int start, int length) throws IOException;

    //
    //
    // control methods
    //
    //

    /**
     * Gets an {@link OutputStream} that may be used in place of this {@link DataOut} instance.
     * <p>
     * An implementation may choose to return itself.
     * <p>
     * Closing the resulting {@link OutputStream} will also close this {@link DataOut} instance, and vice-versa.
     *
     * @return an {@link OutputStream} that may be used in place of this {@link DataOut} instance
     */
    OutputStream asOutputStream() throws IOException;

    @Override
    boolean isOpen();

    @Override
    void close() throws IOException;
}
