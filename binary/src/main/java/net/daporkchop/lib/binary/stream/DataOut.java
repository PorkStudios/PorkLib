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

package net.daporkchop.lib.binary.stream;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.netty.GenericDirectByteBufOut;
import net.daporkchop.lib.binary.stream.netty.GenericHeapByteBufOut;
import net.daporkchop.lib.binary.stream.netty.NonGrowingGenericHeapByteBufOut;
import net.daporkchop.lib.binary.stream.netty.NonGrowingGenericDirectByteBufOut;
import net.daporkchop.lib.binary.stream.nio.DirectBufferOut;
import net.daporkchop.lib.binary.stream.nio.HeapBufferOut;
import net.daporkchop.lib.binary.stream.stream.StreamOut;
import net.daporkchop.lib.common.annotation.AliasOwnership;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.TransferOwnership;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Combination of {@link DataOutput}, {@link GatheringByteChannel} and {@link OutputStream}, plus some custom methods.
 * <p>
 * Unless otherwise specified by an implementation, instances of this class are not thread-safe. Notably, many multithreading aspects of
 * {@link GatheringByteChannel} are unlikely to work correctly, if at all.
 * <p>
 * This does not implement {@link GatheringByteChannel} or {@link OutputStream} entirely correctly. As in: this is not intended to be used for socket
 * I/O, and as such there is no concept of blocking/non-blocking, nothing can cause an ongoing write operation to be stopped prematurely
 *
 * @author DaPorkchop_
 * @see DataIn
 */
@NotThreadSafe
public interface DataOut extends DataOutput, GatheringByteChannel, Closeable {
    //
    //
    // creators
    //
    //

    /**
     * Wraps an {@link OutputStream} to make it a {@link DataOut}.
     *
     * @param out the {@link OutputStream} to wrap
     * @return the wrapped stream, or the original stream if it was already an instance of {@link DataOut}
     */
    static DataOut wrap(@NonNull OutputStream out) {
        /*if (out instanceof DataOutAsOutputStream)   {
            return ((DataOutAsOutputStream) out).delegate();
        } else if (out instanceof DataOut)  {
            return (DataOut) out;
        } else {
            return new StreamOut(out);
        }*/
        return new StreamOut(out);
    }

    /**
     * Wraps an {@link OutputStream} to make it a {@link DataOut}.
     * <p>
     * Calling {@link #close()} on the returned {@link DataOut} will not cause the wrapped stream to be closed.
     *
     * @param out the {@link OutputStream} to wrap
     * @return the wrapped stream, or the original stream if it was already an instance of {@link DataOut}
     */
    static DataOut wrapNonClosing(@NonNull OutputStream out) {
        /*if (out instanceof DataOutAsOutputStream)   {
            DataOut theOut = ((DataOutAsOutputStream) out).delegate();
            if (theOut instanceof StreamOut.NonClosing) {
                return theOut;
            }
        }*/
        return new StreamOut.NonClosing(out);
    }

    /**
     * @see #wrap(Path)
     */
    static DataOut wrap(@NonNull File file) throws IOException {
        return wrap(file.toPath());
    }

    /**
     * @see #wrapBuffered(Path)
     */
    static DataOut wrapBuffered(@NonNull File file) throws IOException {
        return wrapBuffered(file.toPath());
    }

    /**
     * @see #wrapBuffered(Path, int)
     */
    static DataOut wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrapBuffered(file.toPath(), bufferSize);
    }

    /**
     * @see #wrapUnbuffered(Path)
     */
    static DataOut wrapUnbuffered(@NonNull File file) throws IOException {
        return wrapUnbuffered(file.toPath());
    }

    /**
     * @see #wrapBuffered(Path)
     */
    static DataOut wrap(@NonNull Path file) throws IOException {
        return wrapBuffered(file);
    }

    /**
     * Gets a {@link DataOut} for writing to a {@link Path}.
     * <p>
     * This stream will additionally be buffered for faster write access, using the default buffer size of 8192 bytes.
     *
     * @param file the file to write to
     * @return a buffered {@link DataOut} that will write to the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    static DataOut wrapBuffered(@NonNull Path file) throws IOException {
        return wrap(new BufferedOutputStream(Files.newOutputStream(file)));
    }

    /**
     * Gets a {@link DataOut} for writing to a {@link Path}.
     * <p>
     * This stream will additionally be buffered for faster write access, using the given buffer size.
     *
     * @param file       the file to write to
     * @param bufferSize the size of the buffer to use
     * @return a buffered {@link DataOut} that will write to the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    static DataOut wrapBuffered(@NonNull Path file, int bufferSize) throws IOException {
        return wrap(new BufferedOutputStream(Files.newOutputStream(file), bufferSize));
    }

    /**
     * Gets a {@link DataOut} for writing to a {@link Path}.
     * <p>
     * {@link DataOut} instances returned by this method will NOT be buffered.
     *
     * @param file the file to write to
     * @return a direct {@link DataOut} that will write to the given file
     * @throws IOException if an IO exception occurs you dummy
     */
    static DataOut wrapUnbuffered(@NonNull Path file) throws IOException {
        return wrap(Files.newOutputStream(file));
    }

    /**
     * Gets a {@link DataOut} which writes to the given {@code byte[]}.
     *
     * @param arr the {@code byte[]} to write to
     * @return the wrapped {@code byte[]}
     */
    static DataOut wrap(@NonNull byte[] arr) {
        return wrap(ByteBuffer.wrap(arr));
    }

    /**
     * Gets a {@link DataOut} which writes to the given slice of the given {@code byte[]}.
     *
     * @param arr the {@code byte[]} to write to
     * @param off the offset in the array to begin writing at
     * @param len the maximum number of bytes to write
     * @return the wrapped {@code byte[]}
     */
    static DataOut wrap(@NonNull byte[] arr, int off, int len) {
        return wrap(ByteBuffer.wrap(arr, off, len));
    }

    /**
     * Wraps a {@link ByteBuffer} to make it a {@link DataOut}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer
     */
    static DataOut wrap(@NonNull ByteBuffer buffer) {
        return buffer.isDirect() ? new DirectBufferOut(buffer) : new HeapBufferOut(buffer);
    }

    /**
     * @deprecated use one of {@link #wrapView}, {@link #wrapReleasing}, {@link #wrapViewNonGrowing} or {@link #wrapReleasingNonGrowing}
     */
    @Deprecated
    static DataOut wrap(@NonNull ByteBuf buf) {
        return wrap(buf, true, true);
    }

    /**
     * @deprecated use one of {@link #wrapView}, {@link #wrapReleasing}, {@link #wrapViewNonGrowing} or {@link #wrapReleasingNonGrowing}
     */
    @Deprecated
    static DataOut wrap(@NonNull ByteBuf buf, boolean retain) {
        return wrap(buf, retain, true);
    }

    /**
     * @deprecated use one of {@link #wrapView}, {@link #wrapReleasing}, {@link #wrapViewNonGrowing} or {@link #wrapReleasingNonGrowing}
     */
    @Deprecated
    static DataOut wrap(@NonNull ByteBuf buf, boolean retain, boolean grow) {
        if (retain) {
            buf.retain();
        }
        if (buf.hasMemoryAddress()) {
            return grow ? new GenericDirectByteBufOut(buf, true) : new NonGrowingGenericDirectByteBufOut(buf, true);
        } else {
            return grow ? new GenericHeapByteBufOut(buf, true) : new NonGrowingGenericHeapByteBufOut(buf, true);
        }
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will <strong>not</strong> be {@link ByteBuf#release() released}.
     * <p>
     * As ownership of the {@link ByteBuf} is {@link AliasOwnership aliased} to the returned {@link DataOut}, the user must not {@link ByteBuf#release() released} the
     * {@link ByteBuf} until the returned {@link DataOut} has been {@link DataOut#close() closed}.
     *
     * @param buf    the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapViewNonGrowing(ByteBuf)
     */
    static DataOut wrapView(@NonNull @AliasOwnership ByteBuf buf) {
        return buf.isDirect()
                ? new GenericDirectByteBufOut(buf, false)
                : new GenericHeapByteBufOut(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will be {@link ByteBuf#release() released}.
     *
     * @param buf    the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapReleasingNonGrowing(ByteBuf)
     */
    static DataOut wrapReleasing(@NonNull @TransferOwnership ByteBuf buf) {
        return buf.isDirect()
                ? new GenericDirectByteBufOut(buf, true)
                : new GenericHeapByteBufOut(buf, true);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing. Writing to the returned {@link DataOut} will never cause the {@link ByteBuf}'s internal storage to be
     * grown, even if its {@link ByteBuf#capacity() capacity} is currently less than its {@link ByteBuf#maxCapacity() maximum capacity}.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will <strong>not</strong> be {@link ByteBuf#release() released}.
     * <p>
     * As ownership of the {@link ByteBuf} is {@link AliasOwnership aliased} to the returned {@link DataOut}, the user must not {@link ByteBuf#release() released} the
     * {@link ByteBuf} until the returned {@link DataOut} has been {@link DataOut#close() closed}.
     *
     * @param buf    the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapView(ByteBuf)
     */
    static DataOut wrapViewNonGrowing(@NonNull @AliasOwnership ByteBuf buf) {
        return buf.isDirect()
                ? new NonGrowingGenericDirectByteBufOut(buf, false)
                : new NonGrowingGenericHeapByteBufOut(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing. Writing to the returned {@link DataOut} will never cause the {@link ByteBuf}'s internal storage to be
     *      * grown, even if its {@link ByteBuf#capacity() capacity} is currently less than its {@link ByteBuf#maxCapacity() maximum capacity}.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will be {@link ByteBuf#release() released}.
     *
     * @param buf    the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapReleasing(ByteBuf)
     */
    static DataOut wrapReleasingNonGrowing(@NonNull @TransferOwnership ByteBuf buf) {
        return buf.isDirect()
                ? new NonGrowingGenericDirectByteBufOut(buf, true)
                : new NonGrowingGenericHeapByteBufOut(buf, true);
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
    void writeShort(int v) throws IOException;

    /**
     * Writes a little-endian {@code short}.
     *
     * @see #writeShort(int)
     */
    void writeShortLE(int v) throws IOException;

    /**
     * Writes a {@code short} in the given {@link ByteOrder}.
     *
     * @see #writeShort(int)
     * @see #writeShortLE(int)
     */
    default void writeShort(int v, @NonNull ByteOrder order) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeShort(v);
        } else {
            this.writeShortLE(v);
        }
    }

    /**
     * Writes a big-endian {@code char}.
     *
     * @see DataOutput#writeChar(int)
     */
    @Override
    void writeChar(int v) throws IOException;

    /**
     * Writes a little-endian {@code char}.
     *
     * @see #writeChar(int)
     */
    void writeCharLE(int v) throws IOException;

    /**
     * Writes a {@code char} in the given {@link ByteOrder}.
     *
     * @see #writeChar(int)
     * @see #writeCharLE(int)
     */
    default void writeChar(int v, @NonNull ByteOrder order) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeChar(v);
        } else {
            this.writeCharLE(v);
        }
    }

    /**
     * Writes a big-endian {@code int}.
     *
     * @see DataOutput#writeInt(int)
     */
    @Override
    void writeInt(int v) throws IOException;

    /**
     * Writes a little-endian {@code int}.
     *
     * @see DataOutput#writeInt(int)
     */
    void writeIntLE(int v) throws IOException;

    /**
     * Writes am {@code int} in the given {@link ByteOrder}.
     *
     * @see #writeInt(int)
     * @see #writeIntLE(int)
     */
    default void writeInt(int v, @NonNull ByteOrder order) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeInt(v);
        } else {
            this.writeIntLE(v);
        }
    }

    /**
     * Writes a big-endian {@code long}.
     *
     * @see DataOutput#writeLong(long)
     */
    @Override
    void writeLong(long v) throws IOException;

    /**
     * Writes a little-endian {@code long}.
     *
     * @see DataOutput#writeLong(long)
     */
    void writeLongLE(long v) throws IOException;

    /**
     * Writes a {@code long} in the given {@link ByteOrder}.
     *
     * @see #writeLong(long)
     * @see #writeLongLE(long)
     */
    default void writeLong(long v, @NonNull ByteOrder order) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeLong(v);
        } else {
            this.writeLongLE(v);
        }
    }

    /**
     * Writes a big-endian float (32-bit floating point) value.
     *
     * @param f the float to write
     */
    default void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToRawIntBits(f));
    }

    /**
     * Writes a little-endian float (32-bit floating point) value.
     *
     * @param f the float to write
     */
    default void writeFloatLE(float f) throws IOException {
        this.writeIntLE(Float.floatToRawIntBits(f));
    }

    /**
     * Writes a {@code float} in the given {@link ByteOrder}.
     *
     * @see #writeFloat(float)
     * @see #writeFloatLE(float)
     */
    default void writeFloat(float f, @NonNull ByteOrder order) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeFloat(f);
        } else {
            this.writeFloatLE(f);
        }
    }

    /**
     * Writes a big-endian double (64-bit floating point) value.
     *
     * @param d the double to write
     */
    default void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToRawLongBits(d));
    }

    /**
     * Writes a little-endian double (64-bit floating point) value.
     *
     * @param d the double to write
     */
    default void writeDoubleLE(double d) throws IOException {
        this.writeLongLE(Double.doubleToRawLongBits(d));
    }

    /**
     * Writes a {@code double} in the given {@link ByteOrder}.
     *
     * @see #writeDouble(double)
     * @see #writeDoubleLE(double)
     */
    default void writeDouble(double d, @NonNull ByteOrder order) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeDouble(d);
        } else {
            this.writeDoubleLE(d);
        }
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

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        int total = 0;
        do {
            int blockSize = min(length - total, PorkUtil.bufferSize());
            for (int i = 0; i < blockSize; i++) {
                buf[i] = (byte) text.charAt(start + total + i);
            }
            this.write(buf, 0, blockSize);
            total += blockSize;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
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

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        int total = 0;
        do {
            int blockSize = min(length - total, PorkUtil.bufferSize() / Character.BYTES);
            for (int i = 0; i < blockSize; i++) {
                PUnsafe.putUnalignedCharBE(buf, PUnsafe.arrayCharElementOffset(i), text.charAt(total + i));
            }
            this.write(buf, 0, blockSize * Character.BYTES);
            total += blockSize;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
        return (long) length << 1L;
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
     * As described at <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong">
     * https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong</a>.
     *
     * @param value the value to write
     */
    default void writeVarInt(int value) throws IOException {
        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        int i = 0;
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf[i++] = temp;
        } while (value != 0);
        this.write(buf, 0, i);

        recycler.release(buf); //release the buffer to the recycler
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
     * As described at <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong">
     * https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong</a>.
     *
     * @param value the value to write
     */
    default void writeVarLong(long value) throws IOException {
        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        int i = 0;
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7L;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf[i++] = temp;
        } while (value != 0);
        this.write(buf, 0, i);

        recycler.release(buf); //release the buffer to the recycler
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
    // bulk transfer methods - ByteBuffer and ByteBuf
    //
    //

    /**
     * Writes data from the given {@link ByteBuffer}.
     * <p>
     * This method will write data until the buffer has no bytes remaining.
     *
     * @param src the {@link ByteBuffer} to write data from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    @Override
    int write(@NonNull ByteBuffer src) throws IOException;

    /**
     * Writes data from the given {@link ByteBuffer}s.
     * <p>
     * This method will write data until the buffers have no bytes remaining.
     *
     * @param srcs the {@link ByteBuffer}s to write data from
     * @return the number of bytes written
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
     * This method will write data until the buffers have no bytes remaining.
     *
     * @param srcs   the {@link ByteBuffer}s to write data from
     * @param offset the index of the first {@link ByteBuffer} to write data from
     * @param length the number of {@link ByteBuffer}s to read write from
     * @return the number of bytes written
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
            l += this.write(srcs[i]);
        }
        return l;
    }

    /**
     * Writes all readable bytes from the given {@link ByteBuf}.
     * <p>
     * This method will write data until the buffer has no bytes remaining.
     * <p>
     * This method will also increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src the {@link ByteBuf} to write data from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    default int write(@NonNull ByteBuf src) throws IOException {
        return this.write(src, src.readableBytes());
    }

    /**
     * Writes the requested number of bytes from the given {@link ByteBuf}.
     * <p>
     * This method will write data until requested number of bytes have been written.
     * <p>
     * This method will also increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src   the {@link ByteBuf} to write data from
     * @param count the number of bytes to write
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    default int write(@NonNull ByteBuf src, int count) throws IOException {
        this.write(src, src.readerIndex(), count);
        src.skipBytes(count);
        return count;
    }

    /**
     * Writes all bytes in the given range in the given {@link ByteBuf}.
     * <p>
     * This method will write data until requested number of bytes have been written.
     * <p>
     * This method will not increase the buffer's {@link ByteBuf#readerIndex()}.
     *
     * @param src    the {@link ByteBuf} to write data from
     * @param start  the index of the first byte to write
     * @param length the number of bytes to write
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws IOException            if an IO exception occurs you dummy
     */
    int write(@NonNull ByteBuf src, int start, int length) throws IOException;

    //
    //
    // control methods
    //
    //

    /**
     * Transfers the entire contents of the given {@link DataIn} to this {@link DataOut}.
     * <p>
     * This will read until the given {@link DataIn} reaches EOF. If EOF was already reached, this method will always return {@code -1}.
     *
     * @param src the {@link DataIn} to transfer data from
     * @return the number of bytes transferred, or {@code -1} if the given {@link DataIn} had already reached EOF
     */
    long transferFrom(@NonNull DataIn src) throws IOException;

    /**
     * Transfers data from the given {@link DataIn} to this {@link DataOut}.
     * <p>
     * This will read until the requested number of bytes is transferred or given {@link DataIn} reaches EOF. If EOF was already reached, this
     * method will always return {@code -1}.
     *
     * @param src   the {@link DataIn} to transfer data from
     * @param count the number of bytes to transfer
     * @return the number of bytes transferred, or {@code -1} if the given {@link DataIn} had already reached EOF
     */
    long transferFrom(@NonNull DataIn src, long count) throws IOException;

    /**
     * Transfers data from the given {@link DataIn} to this {@link DataOut}.
     * <p>
     * This will read until the requested number of bytes is transferred.
     *
     * @param src   the {@link DataIn} to transfer data from
     * @param count the number of bytes to transfer
     * @return the number of bytes transferred
     * @throws EOFException if EOF is reached before the requested number of bytes can be transferred
     */
    default long transferFromFully(@NonNull DataIn src, long count) throws IOException {
        if (this.transferFrom(src, count) != count) {
            throw new EOFException();
        }
        return count;
    }

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

    /**
     * If this {@link DataOut} uses some kind of write buffer: attempts to flush all currently buffered data.
     *
     * @see OutputStream#flush()
     */
    void flush() throws IOException;

    /**
     * Checks whether or not this {@link DataOut} uses direct memory internally.
     * <p>
     * If {@code true}, then using native buffers when writing to this {@link DataOut} is likely to provide a performance boost.
     * <p>
     * Note that it is possible for both {@link #isDirect()} and {@link #isHeap()} to return {@code false}.
     *
     * @return whether or not this {@link DataOut} uses direct memory internally
     * @see #isHeap()
     */
    boolean isDirect();

    /**
     * Checks whether or not this {@link DataOut} uses heap memory internally.
     * <p>
     * If {@code true}, then using heap buffers when writing to this {@link DataOut} is likely to provide a performance boost.
     * <p>
     * Note that it is possible for both {@link #isDirect()} and {@link #isHeap()} to return {@code false}.
     *
     * @return whether or not this {@link DataOut} uses heap memory internally
     * @see #isDirect()
     */
    boolean isHeap();

    @Override
    boolean isOpen();

    @Override
    void close() throws IOException;
}
