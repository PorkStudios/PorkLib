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
import net.daporkchop.lib.binary.stream.netty.GenericDirectByteBufIn;
import net.daporkchop.lib.binary.stream.netty.GenericHeapByteBufIn;
import net.daporkchop.lib.binary.stream.nio.ArrayHeapBufferIn;
import net.daporkchop.lib.binary.stream.nio.GenericDirectBufferIn;
import net.daporkchop.lib.binary.stream.nio.GenericHeapBufferIn;
import net.daporkchop.lib.binary.stream.stream.StreamIn;
import net.daporkchop.lib.binary.stream.wrapper.DataInAsInputStream;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.AliasOwnership;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.TransferOwnership;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Combination of {@link DataInput}, {@link ScatteringByteChannel} and {@link InputStream}, plus some custom methods.
 * <p>
 * Unless otherwise specified by an implementation, instances of this class are not thread-safe. Notably, many thread-safety aspects of
 * {@link ScatteringByteChannel} are unlikely to work correctly, if at all.
 * <p>
 * This does not implement {@link ScatteringByteChannel} or {@link InputStream} entirely correctly. As in: this is not intended to be used for socket
 * I/O, and as such there is no concept of blocking/non-blocking, the only thing that can cause an ongoing read operation to be stopped prematurely
 * is EOF being reached.
 *
 * @author DaPorkchop_
 * @see DataOut
 */
@NotThreadSafe
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
        if (in instanceof DataInAsInputStream) {
            return ((DataInAsInputStream) in).delegate();
        } else if (in instanceof DataIn) {
            return (DataIn) in;
        } else {
            return new StreamIn(in);
        }
    }

    /**
     * @see #wrap(Path)
     */
    static DataIn wrap(@NonNull File file) throws IOException {
        return wrap(file.toPath());
    }

    /**
     * @see #wrapBuffered(Path)
     */
    static DataIn wrapBuffered(@NonNull File file) throws IOException {
        return wrapBuffered(file.toPath());
    }

    /**
     * @see #wrapBuffered(Path, int)
     */
    static DataIn wrapBuffered(@NonNull File file, int bufferSize) throws IOException {
        return wrapBuffered(file.toPath(), bufferSize);
    }

    /**
     * @see #wrapUnbuffered(Path)
     */
    static DataIn wrapUnbuffered(@NonNull File file) throws IOException {
        return wrapUnbuffered(file.toPath());
    }

    /**
     * @see #wrapBuffered(Path)
     */
    static DataIn wrap(@NonNull Path file) throws IOException {
        return wrapBuffered(file);
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link Path}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for potentially faster read access, using the default buffer size of
     * {@link BufferedInputStream#DEFAULT_BUFFER_SIZE}.
     *
     * @param file the file to read from
     * @return a buffered {@link DataIn} that will read from the given file
     */
    @SuppressWarnings("JavadocReference")
    static DataIn wrapBuffered(@NonNull Path file) throws IOException {
        return wrap(new BufferedInputStream(Files.newInputStream(file)));
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link Path}.
     * <p>
     * The file will additionally be wrapped in a {@link BufferedInputStream} for faster read/write access, using
     * the given buffer size.
     *
     * @param file       the file to read from
     * @param bufferSize the size of the buffer to use
     * @return a buffered {@link DataIn} that will read from the given file
     */
    static DataIn wrapBuffered(@NonNull Path file, int bufferSize) throws IOException {
        return wrap(new BufferedInputStream(Files.newInputStream(file), bufferSize));
    }

    /**
     * Gets a {@link DataIn} for reading from a {@link Path}.
     * <p>
     * {@link DataIn} instances returned from this method will NOT be buffered.
     *
     * @param file the file to read from
     * @return a direct {@link DataIn} that will read from the given file
     */
    static DataIn wrapUnbuffered(@NonNull Path file) throws IOException {
        return wrap(Files.newInputStream(file));
    }

    /**
     * Gets a {@link DataIn} which reads from the given {@code byte[]}.
     *
     * @param arr the {@code byte[]} to read from
     * @return the wrapped {@code byte[]}
     */
    static DataIn wrap(@NonNull byte[] arr) {
        return wrap(ByteBuffer.wrap(arr));
    }

    /**
     * Gets a {@link DataIn} which reads from the given slice of the given {@code byte[]}.
     *
     * @param arr the {@code byte[]} to read from
     * @param off the offset in the array to begin reading from
     * @param len the maximum number of bytes to read
     * @return the wrapped {@code byte[]}
     */
    static DataIn wrap(@NonNull byte[] arr, int off, int len) {
        return wrap(ByteBuffer.wrap(arr, off, len));
    }

    /**
     * Wraps a {@link ByteBuffer} to make it into a {@link DataIn}.
     * <p>
     * The {@link ByteBuffer}'s configured {@link ByteBuffer#order() byte order} will have no effect on the returned {@link DataOut}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer as a {@link DataIn}
     */
    static DataIn wrap(@NonNull ByteBuffer buffer) {
        return buffer.isDirect()
                ? new GenericDirectBufferIn(buffer)
                : buffer.hasArray() ? new ArrayHeapBufferIn(buffer) : new GenericHeapBufferIn(buffer);
    }

    /**
     * @deprecated use {@link #wrapView(ByteBuf)} or {@link #wrapReleasing(ByteBuf)}
     */
    @Deprecated
    static DataIn wrap(@NonNull ByteBuf buf) {
        return wrap(buf, true);
    }

    /**
     * @deprecated use {@link #wrapView(ByteBuf)} or {@link #wrapReleasing(ByteBuf)}
     */
    @Deprecated
    static DataIn wrap(@NonNull ByteBuf buf, boolean retain) {
        if (retain) {
            buf.retain();
        }
        return buf.hasMemoryAddress() ? new GenericDirectByteBufIn(buf, true) : new GenericHeapByteBufIn(buf, true);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataIn} for reading.
     * <p>
     * When the {@link DataIn} is {@link DataIn#close() closed}, the {@link ByteBuf} will <strong>not</strong> be {@link ByteBuf#release() released}.
     * <p>
     * As ownership of the {@link ByteBuf} is {@link AliasOwnership aliased} to the returned {@link DataIn}, the user must not {@link ByteBuf#release() released} the
     * {@link ByteBuf} until the returned {@link DataIn} has been {@link DataIn#close() closed}.
     * <p>
     * The {@link ByteBuf}'s configured {@link ByteBuf#order() byte order} will have no effect on the returned {@link DataOut}.
     *
     * @param buf the {@link ByteBuf} to read from
     * @return a {@link DataIn} that can read data from the {@link ByteBuf}
     */
    static DataIn wrapView(@NonNull @AliasOwnership ByteBuf buf) {
        buf = buf.order(ByteOrder.BIG_ENDIAN); //make sure buffer is big-endian (this should do nothing 99% of the time)

        return buf.isDirect()
                ? new GenericDirectByteBufIn(buf, false)
                : new GenericHeapByteBufIn(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataIn} for reading.
     * <p>
     * When the {@link DataIn} is {@link DataIn#close() closed}, the {@link ByteBuf} will be {@link ByteBuf#release() released}.
     * <p>
     * The {@link ByteBuf}'s configured {@link ByteBuf#order() byte order} will have no effect on the returned {@link DataOut}.
     *
     * @param buf the {@link ByteBuf} to read from
     * @return a {@link DataIn} that can read data from the {@link ByteBuf}
     */
    static DataIn wrapReleasing(@NonNull @TransferOwnership ByteBuf buf) {
        buf = buf.order(ByteOrder.BIG_ENDIAN); //make sure buffer is big-endian (this should do nothing 99% of the time)

        return buf.isDirect()
                ? new GenericDirectByteBufIn(buf, true)
                : new GenericHeapByteBufIn(buf, true);
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
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @see InputStream#read()
     */
    int read() throws ClosedChannelException, IOException;

    //
    //
    // primitives
    //
    //

    /**
     * Reads a single byte, returning {@code false} if the byte is zero and {@code true} if it is nonzero.
     *
     * @return a {@code boolean}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readBoolean()
     */
    @Override
    default boolean readBoolean() throws ClosedChannelException, EOFException, IOException {
        return this.readUnsignedByte() != 0;
    }

    /**
     * Reads and returns a single byte.
     *
     * @return a {@code byte}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readByte()
     */
    @Override
    default byte readByte() throws ClosedChannelException, EOFException, IOException {
        return (byte) this.readUnsignedByte();
    }

    /**
     * Reads and returns a single unsigned byte, represented as an {@code int} in range {@code [0,255]}.
     *
     * @return an unsigned byte, represented as an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readUnsignedByte()
     */
    @Override
    int readUnsignedByte() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns a big-endian {@code short}.
     *
     * @return a {@code short}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readShort()
     */
    @Override
    short readShort() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns an unsigned big-endian {@code short}, represented as an {@code int} in range {@code [0,65535]}.
     *
     * @return an unsigned {@code short}, represented as an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readUnsignedShort()
     */
    @Override
    default int readUnsignedShort() throws ClosedChannelException, EOFException, IOException {
        return this.readShort() & 0xFFFF;
    }

    /**
     * Reads and returns a little-endian {@code short}.
     *
     * @return a {@code short}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readShort()
     */
    short readShortLE() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns an unsigned little-endian {@code short}, represented as an {@code int} in range {@code [0,65535]}.
     *
     * @return an unsigned {@code short}, represented as an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readUnsignedShort()
     */
    default int readUnsignedShortLE() throws ClosedChannelException, EOFException, IOException {
        return this.readShortLE() & 0xFFFF;
    }

    /**
     * Reads and returns a {@code short} in the given {@link ByteOrder}.
     *
     * @return a {@code short}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readShort()
     * @see #readShortLE()
     */
    default short readShort(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readShort() : this.readShortLE();
    }

    /**
     * Reads and returns an unsigned {@code short} in the given {@link ByteOrder}, represented as an {@code int} in range {@code [0,65535]}.
     *
     * @return an unsigned {@code short}, represented as an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readUnsignedShort()
     * @see #readUnsignedShortLE()
     */
    default int readUnsignedShort(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readUnsignedShort() : this.readUnsignedShortLE();
    }

    /**
     * Reads and returns a big-endian {@code char}.
     *
     * @return a {@code char}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readChar()
     */
    @Override
    char readChar() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns a little-endian {@code char}.
     *
     * @return a {@code char}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readChar()
     */
    char readCharLE() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns a {@code char} in the given {@link ByteOrder}.
     *
     * @return a {@code char}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readChar()
     * @see #readCharLE()
     */
    default char readChar(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readChar() : this.readCharLE();
    }

    /**
     * Reads and returns a big-endian {@code int}.
     *
     * @return an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readInt()
     */
    @Override
    int readInt() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns a little-endian {@code int}.
     *
     * @return an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readInt()
     */
    int readIntLE() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns an {@code int} in the given {@link ByteOrder}.
     *
     * @return an {@code int}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readInt()
     * @see #readIntLE()
     */
    default int readInt(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readInt() : this.readIntLE();
    }

    /**
     * Reads and returns a big-endian {@code long}.
     *
     * @return a {@code long}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readLong()
     */
    @Override
    long readLong() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns a little-endian {@code long}.
     *
     * @return a {@code long}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readLong()
     */
    long readLongLE() throws ClosedChannelException, EOFException, IOException;

    /**
     * Reads and returns a {@code long} in the given {@link ByteOrder}.
     *
     * @return a {@code long}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readLong()
     * @see #readLongLE()
     */
    default long readLong(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readLong() : this.readLongLE();
    }

    /**
     * Reads and returns a big-endian {@code float}.
     *
     * @return a {@code float}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readFloat()
     */
    @Override
    default float readFloat() throws ClosedChannelException, EOFException, IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Reads and returns a little-endian {@code float}.
     *
     * @return a {@code float}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readFloat()
     */
    default float readFloatLE() throws ClosedChannelException, EOFException, IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }

    /**
     * Reads and returns a {@code float} in the given {@link ByteOrder}.
     *
     * @return a {@code float}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readFloat()
     * @see #readFloatLE()
     */
    default float readFloat(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readFloat() : this.readFloatLE();
    }

    /**
     * Reads and returns a big-endian {@code double}.
     *
     * @return a {@code double}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readDouble()
     */
    @Override
    default double readDouble() throws ClosedChannelException, EOFException, IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    /**
     * Reads and returns a little-endian {@code double}.
     *
     * @return a {@code double}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readDouble()
     */
    default double readDoubleLE() throws ClosedChannelException, EOFException, IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }

    /**
     * Reads and returns a {@code double} in the given {@link ByteOrder}.
     *
     * @return a {@code double}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readDouble()
     * @see #readDoubleLE()
     */
    default double readDouble(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return order == ByteOrder.BIG_ENDIAN ? this.readDouble() : this.readDoubleLE();
    }

    //
    //
    // other types
    //
    //

    /**
     * Reads and returns a {@code UTF-8} encoded {@link String} with an {@link #readUnsignedShort() unsigned 16-bit big-endian} length prefix.
     *
     * @return a {@link String}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see DataInput#readUTF()
     */
    @Override
    default String readUTF() throws ClosedChannelException, EOFException, IOException {
        return this.readString(this.readUnsignedShort(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a {@code UTF-8} encoded {@link String} with a {@link #readVarLong() VarLong} length prefix.
     *
     * @return a {@link String}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readUTF()
     * @see #readVarString(Charset)
     */
    default String readVarUTF() throws ClosedChannelException, EOFException, IOException {
        return this.readString(this.readVarLong(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a {@link String} with an {@link #readUnsignedShort() unsigned 16-bit big-endian} length prefix, encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param charset the {@link Charset} that the {@link String} is encoded with
     * @return a {@link String}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readString(long, Charset)
     */
    default String readString(@NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.readString(this.readUnsignedShort(), charset);
    }

    /**
     * Reads a {@link String} with a {@link #readVarLong() VarLong} length prefix, encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param charset the {@link Charset} that the {@link String} is encoded with
     * @return a {@link String}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     * @see #readString(long, Charset)
     */
    default String readVarString(@NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.readString(this.readVarLong(), charset);
    }

    /**
     * Reads a {@link String} encoded using the given {@link Charset}.
     * <p>
     * Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore recommended to use values from {@link StandardCharsets}
     * if possible.
     *
     * @param size    the length of the encoded {@link CharSequence} in bytes. May not be negative
     * @param charset the {@link Charset} to encode the text using
     * @return a {@link String}
     * @throws ClosedChannelException   if the {@link DataIn} was already closed
     * @throws IllegalArgumentException if the given {@code size} is negative
     * @throws EOFException             if EOF is reached before all the bytes can be read
     */
    default String readString(long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        //TODO: it's possible for the encoded form to be more than 2^31-1 bytes without the decoded form being too large for a String
        int length = PValidation.toInt(notNegative(size, "size"), "size");
        if (length <= PorkUtil.bufferSize()) { //sequence small enough that it can fit in a recycled buffer
            Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
            byte[] buf = recycler.allocate();

            String result = new String(this.fill(buf, 0, length), 0, length, charset);

            recycler.release(buf); //release the buffer to the recycler
            return result;
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
     * @return a {@link CharSequence}
     * @throws ClosedChannelException   if the {@link DataIn} was already closed
     * @throws IllegalArgumentException if the given {@code size} is negative
     * @throws EOFException             if EOF is reached before all the bytes can be read
     */
    default CharSequence readText(long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return this.readString(size, charset);
    }

    /**
     * Reads an enum value.
     *
     * @param f   a function to calculate the enum value from the name (i.e. MyEnum::valueOf)
     * @param <E> the enum type
     * @return a value of <E>, or null if input was null
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     */
    @Deprecated
    default <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws ClosedChannelException, EOFException, IOException {
        if (this.readBoolean()) {
            return f.apply(this.readUTF());
        } else {
            return null;
        }
    }

    /**
     * Reads a Mojang-style VarInt.
     * <p>
     * As described at <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong">
     * https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong</a>.
     *
     * @return the read value
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     */
    default int readVarInt() throws ClosedChannelException, EOFException, IOException {
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
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     */
    default int readVarIntZigZag() throws ClosedChannelException, EOFException, IOException {
        int i = this.readVarInt();
        return (i >> 1) ^ -(i & 1);
    }

    /**
     * Reads a Mojang-style VarLong.
     * <p>
     * As described at <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong">
     * https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong</a>.
     *
     * @return the read value
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     */
    default long readVarLong() throws ClosedChannelException, EOFException, IOException {
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
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before all the bytes can be read
     */
    default long readVarLongZigZag() throws ClosedChannelException, EOFException, IOException {
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
     * This method will read data until the byte array is filled or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dst the {@code byte[]} to read to
     * @return the number of bytes actually read
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     */
    default int read(@NonNull byte[] dst) throws ClosedChannelException, IOException {
        return this.read(dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with as much data as possible.
     * <p>
     * This method will read data until the byte array is filled or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dst    the {@code byte[]} to read to
     * @param start  the first index (inclusive) in the {@code byte[]} to start writing to
     * @param length the number of bytes to read into the {@code byte[]}
     * @return the number of bytes actually read
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     */
    int read(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, IOException;

    /**
     * Fills the given {@code byte[]} with data.
     * <p>
     * Functionally equivalent to {@link #read(byte[])} except that it throws {@link EOFException} on EOF.
     *
     * @param dst the {@code byte[]} to read to
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before the given {@code byte[]} could be filled
     */
    @Override
    default void readFully(@NonNull byte[] dst) throws ClosedChannelException, EOFException, IOException {
        this.readFully(dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with data.
     * <p>
     * Functionally equivalent to {@link #read(byte[], int, int)} except that it throws {@link EOFException} on EOF.
     *
     * @param dst    the {@code byte[]} to read to
     * @param start  the first index (inclusive) in the {@code byte[]} to start writing to
     * @param length the number of bytes to read into the {@code byte[]}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before the given {@code byte[]} could be filled
     */
    @Override
    default void readFully(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        if (this.read(dst, start, length) != length) {
            throw new EOFException();
        }
    }

    /**
     * Fills the given {@code byte[]} with data.
     *
     * @param dst the {@code byte[]} to read to
     * @return the given {@code byte[]}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before the given {@code byte[]} could be filled
     */
    default byte[] fill(@NonNull byte[] dst) throws ClosedChannelException, EOFException, IOException {
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
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     * @throws EOFException           if EOF is reached before the given number of bytes could be read
     */
    default byte[] fill(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        this.readFully(dst, start, length);
        return dst;
    }

    /**
     * Continually reads data until EOF is reached, and returns a {@code byte[]} containing the read data.
     *
     * @return the contents of this {@link DataIn} as a {@code byte[]}
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     */
    default byte[] toByteArray() throws ClosedChannelException, IOException {
        byte[] arr = new byte[PUnsafe.pageSize()];
        int pos = 0;
        for (int i; (i = this.read(arr, pos, arr.length - pos)) != -1; pos += i) {
            if (pos + i == arr.length) {
                //grow array
                byte[] old = arr;
                System.arraycopy(old, 0, arr = new byte[multiplyExact(arr.length, 2)], 0, old.length);
            }
        }
        return pos == arr.length ? arr : Arrays.copyOf(arr, pos); //don't copy if the size is exactly the size of the array already
    }

    //
    //
    // bulk transfer methods - ByteBuffer and ByteBuf
    //
    //

    /**
     * Reads data into the given {@link ByteBuffer}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no bytes available or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dst the {@link ByteBuffer} to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     */
    @Override
    int read(@NonNull ByteBuffer dst) throws ClosedChannelException, IOException;

    /**
     * Reads data into the given {@link ByteBuffer}s.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no bytes available or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dsts the {@link ByteBuffer}s to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException if the {@link DataIn} was already closed
     */
    @Override
    default long read(@NonNull ByteBuffer[] dsts) throws ClosedChannelException, IOException {
        return this.read(dsts, 0, dsts.length);
    }

    /**
     * Reads data into the given {@link ByteBuffer}s.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no bytes available or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     *
     * @param dsts   the {@link ByteBuffer}s to read data into
     * @param offset the index of the first {@link ByteBuffer} to read data into
     * @param length the number of {@link ByteBuffer}s to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@code dsts} array
     */
    @Override
    default long read(@NonNull ByteBuffer[] dsts, int offset, int length) throws ClosedChannelException, IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }

        //TODO: this might not be able to return -1L if EOF was already reached in all cases...

        checkRangeLen(dsts.length, offset, length);
        long total = 0L;
        for (int i = 0; i < length; i++) {
            ByteBuffer dst = dsts[offset + i];
            if (dst.hasRemaining()) {
                int read = this.read(dst);
                if (read < 0 || dst.hasRemaining()) {
                    break;
                }
                total += read;
            }
        }
        return total;
    }

    /**
     * Reads data into the given {@link ByteBuf}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the buffer has no {@link ByteBuf#writableBytes() writable space} remaining or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     * <p>
     * This method will also increase the buffer's {@link ByteBuf#writerIndex()}, but will not increase its {@link ByteBuf#capacity() capacity}.
     *
     * @param dst the {@link ByteBuf} to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     */
    default int read(@NonNull ByteBuf dst) throws ClosedChannelException, IOException {
        return this.read(dst, dst.writableBytes());
    }

    /**
     * Reads data into the given {@link ByteBuf}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the requested number of bytes have been read or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     * <p>
     * This method will also increase the buffer's {@link ByteBuf#writerIndex()}, and may increase its {@link ByteBuf#capacity() capacity}.
     *
     * @param dst   the {@link ByteBuf} to read data into
     * @param count the number of bytes to read
     * @return the number of bytes read
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IllegalArgumentException  if the given {@code count} is negative
     * @throws IndexOutOfBoundsException if the given {@code count} exceeds the given {@link ByteBuf}'s {@link ByteBuf#maxWritableBytes() maximum writable bytes}
     */
    default int read(@NonNull ByteBuf dst, int count) throws ClosedChannelException, IOException {
        dst.ensureWritable(notNegative(count, "count"));
        int writerIndex = dst.writerIndex();
        int read = this.read(dst, writerIndex, count);
        if (read > 0) { //some bytes were read
            dst.writerIndex(writerIndex + read);
        }
        return read;
    }

    /**
     * Reads data into the given {@link ByteBuf}.
     * <p>
     * Like {@link #read(byte[], int, int)}, this will read until the requested number of bytes have been read or EOF is reached.
     * <p>
     * If EOF was already reached, this method will always return {@code -1}.
     * <p>
     * This method will not increase the buffer's {@link ByteBuf#writerIndex()}, but will not increase its {@link ByteBuf#capacity() capacity}.
     *
     * @param dst    the {@link ByteBuf} to read data into
     * @param offset the first index in the {@link ByteBuf} to read into
     * @param length the number of bytes to read
     * @return the number of bytes read
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link ByteBuf}'s current capacity
     */
    int read(@NonNull ByteBuf dst, int offset, int length) throws ClosedChannelException, IOException;

    /**
     * Fills the given {@link ByteBuffer} with data.
     *
     * @param dst the {@link ByteBuffer} to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws EOFException           if EOF is reached before the buffer can be filled
     */
    default int readFully(@NonNull ByteBuffer dst) throws ClosedChannelException, EOFException, IOException {
        int read = this.read(dst);
        if (read < 0 || dst.hasRemaining()) { //EOF was reached before the requested number of bytes could be read
            throw new EOFException();
        }
        return read;
    }

    /**
     * Fills the given {@link ByteBuffer}s with data.
     *
     * @param dsts the {@link ByteBuffer}s to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws EOFException           if EOF is reached before the buffers can be filled
     */
    default long readFully(@NonNull ByteBuffer[] dsts) throws ClosedChannelException, EOFException, IOException {
        return this.readFully(dsts, 0, dsts.length);
    }

    /**
     * Fills the given {@link ByteBuffer}s with data.
     *
     * @param dsts   the {@link ByteBuffer}s to read data into
     * @param offset the index of the first {@link ByteBuffer} to read data into
     * @param length the number of {@link ByteBuffer}s to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@code dsts} array
     * @throws EOFException              if EOF is reached before the buffers can be filled
     */
    default long readFully(@NonNull ByteBuffer[] dsts, int offset, int length) throws ClosedChannelException, EOFException, IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }

        //TODO: this might not be able to return -1L if EOF was already reached in all cases...

        checkRangeLen(dsts.length, offset, length);
        long total = 0L;
        for (int i = 0; i < length; i++) {
            ByteBuffer dst = dsts[offset + i];
            if (dst.hasRemaining()) {
                total += this.readFully(dst);
            }
        }
        return total;
    }

    /**
     * Fills the given {@link ByteBuf} with data, reading until the buffer has no {@link ByteBuf#writableBytes() writable space} remaining.
     * <p>
     * This method will also increase the buffer's {@link ByteBuf#writerIndex()}, but will not increase its {@link ByteBuf#capacity() capacity}.
     *
     * @param dst the {@link ByteBuf} to read data into
     * @return the number of bytes read
     * @throws ClosedChannelException if the channel was already closed
     * @throws EOFException           if EOF is reached before the buffer can be filled
     */
    default int readFully(@NonNull ByteBuf dst) throws ClosedChannelException, EOFException, IOException {
        int read = this.read(dst, dst.writableBytes());
        if (read < 0 || dst.isWritable()) { //EOF was reached before the requested number of bytes could be read
            throw new EOFException();
        }
        return read;
    }

    /**
     * Fills the given {@link ByteBuf} with data, reading until the requested number of bytes have been read.
     * <p>
     * This method will also increase the buffer's {@link ByteBuf#writerIndex()}, and may increase its {@link ByteBuf#capacity() capacity}.
     *
     * @param dst   the {@link ByteBuf} to read data into
     * @param count the number of bytes to read
     * @return the number of bytes read
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IllegalArgumentException  if the given {@code count} is negative
     * @throws IndexOutOfBoundsException if the given {@code count} exceeds the given {@link ByteBuf}'s {@link ByteBuf#maxWritableBytes() maximum writable bytes}
     * @throws EOFException              if EOF is reached before the buffer can be filled
     */
    default int readFully(@NonNull ByteBuf dst, int count) throws ClosedChannelException, EOFException, IOException {
        int read = this.read(dst, count);
        if (read != count) { //EOF was reached before the requested number of bytes could be read
            throw new EOFException();
        }
        return read;
    }

    /**
     * Fills the given {@link ByteBuf} with data, reading until the requested number of bytes have been read.
     * <p>
     * This method will not increase the buffer's {@link ByteBuf#writerIndex()}, but will not increase its {@link ByteBuf#capacity() capacity}.
     *
     * @param dst    the {@link ByteBuf} to read data into
     * @param start  the first index in the {@link ByteBuf} to read into
     * @param length the number of bytes to read
     * @return the number of bytes read
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link ByteBuf}'s current capacity
     * @throws EOFException              if EOF is reached before the buffer can be filled
     */
    default int readFully(@NonNull ByteBuf dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        int read = this.read(dst, start, length);
        if (read != length) { //EOF was reached before the requested number of bytes could be read
            throw new EOFException();
        }
        return read;
    }

    //
    //
    // control methods
    //
    //

    /**
     * Transfers the entire contents of this {@link DataIn} to the given {@link DataOut}.
     * <p>
     * This will read until EOF is reached. If EOF was already reached, this method will always return {@code -1L}.
     *
     * @param dst the {@link DataOut} to transfer data to
     * @return the number of bytes transferred, or {@code -1L} if EOF was already reached
     * @throws ClosedChannelException if the channel was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining in the destination
     */
    long transferTo(@NonNull DataOut dst) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Transfers data from this {@link DataIn} to the given {@link DataOut}.
     * <p>
     * This will read until the requested number of bytes is transferred or EOF is reached. If EOF was already reached, this method will always
     * return {@code -1L}.
     *
     * @param dst   the {@link DataOut} to transfer data to
     * @param count the number of bytes to transfer
     * @return the number of bytes transferred, or {@code -1L} if EOF was already reached
     * @throws ClosedChannelException   if the channel was already closed
     * @throws IllegalArgumentException if the given {@code count} is negative
     * @throws NoMoreSpaceException   if there is insufficient space remaining in the destination
     */
    long transferTo(@NonNull DataOut dst, long count) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Transfers data from this {@link DataIn} to the given {@link DataOut}.
     * <p>
     * This will read until the requested number of bytes is transferred.
     *
     * @param dst   the {@link DataOut} to transfer data to
     * @param count the number of bytes to transfer
     * @return the number of bytes transferred
     * @throws ClosedChannelException if the channel was already closed
     * @throws EOFException           if EOF is reached before the requested number of bytes can be transferred
     * @throws NoMoreSpaceException   if there is insufficient space remaining in the destination
     */
    default long transferToFully(@NonNull DataOut dst, long count) throws ClosedChannelException, EOFException, NoMoreSpaceException, IOException {
        if (this.transferTo(dst, count) != count) {
            throw new EOFException();
        }
        return count;
    }

    /**
     * Gets an {@link InputStream} that may be used in place of this {@link DataIn} instance.
     * <p>
     * Some implementations may choose to return itself.
     * <p>
     * Closing the resulting {@link InputStream} will also close this {@link DataIn} instance, and vice-versa.
     *
     * @return an {@link InputStream} that may be used in place of this {@link DataIn} instance
     * @throws ClosedChannelException if the channel was already closed
     */
    InputStream asInputStream() throws ClosedChannelException, IOException;

    /**
     * Gets an estimate of the number of bytes that may be read or skipped before reaching EOF.
     * <p>
     * If EOF has not been reached, this method returns a non-negative {@code long} indicating an estimate of how many bytes may be read or skipped before reaching EOF.
     * Implementations may return either {@code 0L} or {@link Long#MAX_VALUE} if this stream is infinite, or if the remaining length is unknown or too expensive to
     * compute.
     * <p>
     * If EOF has already been reached, {@code 0L} is returned.
     *
     * @return an estimate of the number of bytes that may be read or skipped before reaching EOF, {@code 0L} if EOF has already been reached
     * @throws ClosedChannelException if the channel was already closed
     * @see InputStream#available()
     */
    default long remaining() throws ClosedChannelException, IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        return 0L;
    }

    /**
     * Skips the given number of bytes.
     * <p>
     * This method will skip data until the requested number of bytes are skipped or EOF is reached.
     *
     * @param n the number of bytes to skip. If less than or equal to {@code 0}, no bytes will be skipped
     * @return the actual number of bytes skipped, or {@code 0} if none were skipped
     * @throws ClosedChannelException if the channel was already closed
     * @see DataInput#skipBytes(int)
     */
    @Override
    default int skipBytes(int n) throws ClosedChannelException, IOException {
        return toInt(this.skipBytes((long) n));
    }

    /**
     * Skips the given number of bytes.
     * <p>
     * This method will skip data until the requested number of bytes are skipped or EOF is reached.
     *
     * @param n the number of bytes to skip. If less than or equal to {@code 0L}, no bytes will be skipped
     * @return the actual number of bytes skipped, or {@code 0L} if none were skipped
     * @throws ClosedChannelException if the channel was already closed
     * @see DataInput#skipBytes(int)
     */
    long skipBytes(long n) throws ClosedChannelException, IOException;

    /**
     * Skips the given number of bytes.
     * <p>
     * This method will skip data until the requested number of bytes are skipped.
     *
     * @param n the number of bytes to skip. If less than or equal to {@code 0}, no bytes will be skipped
     * @return the actual number of bytes skipped, or {@code 0} if none were skipped
     * @throws ClosedChannelException if the channel was already closed
     * @throws EOFException           if EOF is reached before the requested number of bytes can be skipped
     * @see DataInput#skipBytes(int)
     */
    default int skipBytesFully(int n) throws ClosedChannelException, EOFException, IOException {
        if (this.skipBytes(n) != max(n, 0)) {
            throw new EOFException();
        }
        return max(n, 0);
    }

    /**
     * Skips the given number of bytes.
     * <p>
     * This method will skip data until the requested number of bytes are skipped.
     *
     * @param n the number of bytes to skip. If less than or equal to {@code 0L}, no bytes will be skipped
     * @return the actual number of bytes skipped, or {@code 0L} if none were skipped
     * @throws ClosedChannelException if the channel was already closed
     * @throws EOFException           if EOF is reached before the requested number of bytes can be skipped
     * @see DataInput#skipBytes(int)
     */
    default long skipBytesFully(long n) throws ClosedChannelException, EOFException, IOException {
        if (this.skipBytes(n) != max(n, 0L)) {
            throw new EOFException();
        }
        return max(n, 0L);
    }

    /**
     * Checks whether this {@link DataIn} is optimized for native (off-heap) memory.
     * <p>
     * If {@code true}, then using native buffers when reading from this {@link DataIn} is likely to provide a performance boost.
     * <p>
     * Note that it is possible for both {@link #isDirect()} and {@link #isHeap()} to return the same value.
     *
     * @return whether this {@link DataIn} is optimized for native (off-heap) memory
     * @see #isHeap()
     */
    boolean isDirect();

    /**
     * Checks whether this {@link DataIn} is optimized for on-heap memory.
     * <p>
     * If {@code true}, then using on-heap buffers or {@code byte[]}s when reading from this {@link DataIn} is likely to provide a performance boost.
     * <p>
     * Note that it is possible for both {@link #isDirect()} and {@link #isHeap()} to return the same value.
     *
     * @return whether this {@link DataIn} is optimized for on-heap memory.
     * @see #isDirect()
     */
    boolean isHeap();

    @Override
    boolean isOpen();

    @Override
    void close() throws IOException;
}
