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
import io.netty.buffer.CompositeByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.netty.AddressDirectByteBufOut;
import net.daporkchop.lib.binary.stream.netty.ArrayHeapByteBufOut;
import net.daporkchop.lib.binary.stream.netty.GenericDirectByteBufOut;
import net.daporkchop.lib.binary.stream.netty.GenericHeapByteBufOut;
import net.daporkchop.lib.binary.stream.netty.NonGrowingGenericDirectByteBufOut;
import net.daporkchop.lib.binary.stream.netty.NonGrowingGenericHeapByteBufOut;
import net.daporkchop.lib.binary.stream.nio.ArrayHeapBufferOut;
import net.daporkchop.lib.binary.stream.nio.GenericDirectBufferOut;
import net.daporkchop.lib.binary.stream.nio.GenericHeapBufferOut;
import net.daporkchop.lib.binary.stream.stream.StreamOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
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
     * <p>
     * Writes to the {@link DataOut} which would require the {@link ByteBuffer}'s {@link ByteBuffer#position() position} to exceed its
     * {@link ByteBuffer#capacity() capacity} will throw a {@link NoMoreSpaceException}.
     * <p>
     * The {@link ByteBuffer}'s configured {@link ByteBuffer#order() byte order} will have no effect on the returned {@link DataOut}.
     *
     * @param buffer the buffer to wrap
     * @return the wrapped buffer
     */
    static DataOut wrap(@NonNull ByteBuffer buffer) {
        return buffer.isDirect()
                ? new GenericDirectBufferOut(buffer)
                : buffer.hasArray() ? new ArrayHeapBufferOut(buffer) : new GenericHeapBufferOut(buffer);
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
     * <p>
     * Writes to the {@link DataOut} which would require the {@link ByteBuf}'s {@link ByteBuf#writerIndex() writer index} to exceed its
     * {@link ByteBuf#maxCapacity() maximum capacity} will throw a {@link NoMoreSpaceException}.
     * <p>
     * The {@link ByteBuf}'s configured {@link ByteBuf#order() byte order} will have no effect on the returned {@link DataOut}.
     * <p>
     * If the given {@link ByteBuf} is a {@link CompositeByteBuf composite buffer}, components may not be added to or removed from the buffer until the returned
     * {@link DataOut} is closed.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapViewNonGrowing(ByteBuf)
     */
    @SuppressWarnings("deprecation")
    static DataOut wrapView(@NonNull @AliasOwnership ByteBuf buf) {
        buf = buf.order(ByteOrder.BIG_ENDIAN); //make sure buffer is big-endian (this should do nothing 99% of the time)

        return buf.isDirect()
                ? buf.hasMemoryAddress() ? new AddressDirectByteBufOut(buf, false) : new GenericDirectByteBufOut(buf, false)
                : buf.hasArray() ? new ArrayHeapByteBufOut(buf, false) : new GenericHeapByteBufOut(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will be {@link ByteBuf#release() released}.
     * <p>
     * Writes to the {@link DataOut} which would require the {@link ByteBuf}'s {@link ByteBuf#writerIndex() writer index} to exceed its
     * {@link ByteBuf#maxCapacity() maximum capacity} will throw a {@link NoMoreSpaceException}.
     * <p>
     * The {@link ByteBuf}'s configured {@link ByteBuf#order() byte order} will have no effect on the returned {@link DataOut}.
     * <p>
     * If the given {@link ByteBuf} is a {@link CompositeByteBuf composite buffer}, components may not be added to or removed from the buffer until the returned
     * {@link DataOut} is closed.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapReleasingNonGrowing(ByteBuf)
     */
    @SuppressWarnings("deprecation")
    static DataOut wrapReleasing(@NonNull @TransferOwnership ByteBuf buf) {
        buf = buf.order(ByteOrder.BIG_ENDIAN); //make sure buffer is big-endian (this should do nothing 99% of the time)

        return buf.isDirect()
                ? buf.hasMemoryAddress() ? new AddressDirectByteBufOut(buf, true) : new GenericDirectByteBufOut(buf, true)
                : buf.hasArray() ? new ArrayHeapByteBufOut(buf, true) : new GenericHeapByteBufOut(buf, true);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing. Writing to the returned {@link DataOut} will never cause the {@link ByteBuf}'s internal storage to be
     * grown, even if its {@link ByteBuf#capacity() capacity} is currently less than its {@link ByteBuf#maxCapacity() maximum capacity}. Instead, writes to the
     * {@link DataOut} which would cause the {@link ByteBuf}'s {@link ByteBuf#writerIndex() writer index} to exceed its {@link ByteBuf#capacity()}, and therefore require
     * its internal storage to be grown, will throw a {@link NoMoreSpaceException}.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will <strong>not</strong> be {@link ByteBuf#release() released}.
     * <p>
     * As ownership of the {@link ByteBuf} is {@link AliasOwnership aliased} to the returned {@link DataOut}, the user must not {@link ByteBuf#release() released} the
     * {@link ByteBuf} until the returned {@link DataOut} has been {@link DataOut#close() closed}.
     * <p>
     * The {@link ByteBuf}'s configured {@link ByteBuf#order() byte order} will have no effect on the returned {@link DataOut}.
     * <p>
     * If the given {@link ByteBuf} is a {@link CompositeByteBuf composite buffer}, components may not be added to or removed from the buffer until the returned
     * {@link DataOut} is closed.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapView(ByteBuf)
     */
    @SuppressWarnings("deprecation")
    static DataOut wrapViewNonGrowing(@NonNull @AliasOwnership ByteBuf buf) {
        buf = buf.order(ByteOrder.BIG_ENDIAN); //make sure buffer is big-endian (this should do nothing 99% of the time)

        return buf.isDirect()
                ? new NonGrowingGenericDirectByteBufOut(buf, false)
                : new NonGrowingGenericHeapByteBufOut(buf, false);
    }

    /**
     * Wraps a {@link ByteBuf} into a {@link DataOut} for writing. Writing to the returned {@link DataOut} will never cause the {@link ByteBuf}'s internal storage to be
     * grown, even if its {@link ByteBuf#capacity() capacity} is currently less than its {@link ByteBuf#maxCapacity() maximum capacity}. Instead, writes to the
     * {@link DataOut} which would cause the {@link ByteBuf}'s {@link ByteBuf#writerIndex() writer index} to exceed its {@link ByteBuf#capacity()}, and therefore require
     * its internal storage to be grown, will throw a {@link NoMoreSpaceException}.
     * <p>
     * When the {@link DataOut} is {@link DataOut#close() closed}, the {@link ByteBuf} will be {@link ByteBuf#release() released}.
     * <p>
     * The {@link ByteBuf}'s configured {@link ByteBuf#order() byte order} will have no effect on the returned {@link DataOut}.
     * <p>
     * If the given {@link ByteBuf} is a {@link CompositeByteBuf composite buffer}, components may not be added to or removed from the buffer until the returned
     * {@link DataOut} is closed.
     *
     * @param buf the {@link ByteBuf} to write to
     * @return a {@link DataOut} that can write data to the {@link ByteBuf}
     * @see #wrapReleasing(ByteBuf)
     */
    @SuppressWarnings("deprecation")
    static DataOut wrapReleasingNonGrowing(@NonNull @TransferOwnership ByteBuf buf) {
        buf = buf.order(ByteOrder.BIG_ENDIAN); //make sure buffer is big-endian (this should do nothing 99% of the time)

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
     * Writes a single byte.
     * <p>
     * Only the 8 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param b the byte to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#write(int)
     * @see OutputStream#write(int)
     */
    @Override
    void write(int b) throws ClosedChannelException, NoMoreSpaceException, IOException;

    //
    //
    // primitives
    //
    //

    /**
     * Writes a single byte: {@code 0} if the given {@code boolean} is {@code false}, and {@code 1} if it is {@code true}.
     *
     * @param b the byte to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#write(int)
     * @see OutputStream#write(int)
     */
    @Override
    default void writeBoolean(boolean b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.write(b ? 1 : 0);
    }

    /**
     * Writes a single byte.
     * <p>
     * Only the 8 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param b the byte to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeByte(int)
     * @see OutputStream#write(int)
     */
    @Override
    default void writeByte(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.write(b);
    }

    /**
     * Writes a big-endian {@code short}.
     * <p>
     * Only the 16 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param v the {@code short} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeShort(int)
     */
    @Override
    void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a little-endian {@code short}.
     * <p>
     * Only the 16 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param v the {@code short} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeShort(int)
     * @see #writeShortLE(int)
     */
    void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a {@code short} in the given {@link ByteOrder}.
     * <p>
     * Only the 16 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param v the {@code short} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeShort(int)
     */
    default void writeShort(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeShort(v);
        } else {
            this.writeShortLE(v);
        }
    }

    /**
     * Writes a big-endian {@code char}.
     * <p>
     * Only the 16 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param v the {@code char} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeChar(int)
     */
    @Override
    void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a little-endian {@code char}.
     * <p>
     * Only the 16 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param v the {@code char} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeChar(int)
     */
    void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a {@code char} in the given {@link ByteOrder}.
     * <p>
     * Only the 16 least significant bits of the {@code int} parameter are written, the rest will be silently discarded.
     *
     * @param v the {@code char} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeChar(int)
     * @see #writeCharLE(int)
     */
    default void writeChar(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeChar(v);
        } else {
            this.writeCharLE(v);
        }
    }

    /**
     * Writes a big-endian {@code int}.
     *
     * @param v the {@code int} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeInt(int)
     */
    @Override
    void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a little-endian {@code int}.
     *
     * @param v the {@code int} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeInt(int)
     */
    void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes an {@code int} in the given {@link ByteOrder}.
     *
     * @param v the {@code int} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeInt(int)
     * @see #writeIntLE(int)
     */
    default void writeInt(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeInt(v);
        } else {
            this.writeIntLE(v);
        }
    }

    /**
     * Writes a big-endian {@code long}.
     *
     * @param v the {@code long} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeLong(long)
     */
    @Override
    void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a little-endian {@code long}.
     *
     * @param v the {@code long} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeLong(long)
     */
    void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes a {@code long} in the given {@link ByteOrder}.
     *
     * @param v the {@code long} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeLong(long)
     * @see #writeLongLE(long)
     */
    default void writeLong(long v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeLong(v);
        } else {
            this.writeLongLE(v);
        }
    }

    /**
     * Writes a big-endian {@code float}.
     *
     * @param v the {@code float} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeFloat(float)
     */
    @Override
    default void writeFloat(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeInt(Float.floatToRawIntBits(v));
    }

    /**
     * Writes a little-endian {@code float}.
     *
     * @param v the {@code float} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeFloat(float)
     */
    default void writeFloatLE(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeIntLE(Float.floatToRawIntBits(v));
    }

    /**
     * Writes a {@code float} in the given {@link ByteOrder}.
     *
     * @param v the {@code float} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeFloat(float)
     * @see #writeFloatLE(float)
     */
    default void writeFloat(float v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeFloat(v);
        } else {
            this.writeFloatLE(v);
        }
    }

    /**
     * Writes a big-endian {@code double}.
     *
     * @param v the {@code double} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeDouble(double)
     */
    @Override
    default void writeDouble(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeLong(Double.doubleToRawLongBits(v));
    }

    /**
     * Writes a little-endian {@code double}.
     *
     * @param v the {@code double} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeDouble(double)
     */
    default void writeDoubleLE(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeLongLE(Double.doubleToRawLongBits(v));
    }

    /**
     * Writes a {@code double} in the given {@link ByteOrder}.
     *
     * @param v the {@code double} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeDouble(double)
     * @see #writeDoubleLE(double)
     */
    default void writeDouble(double v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            this.writeDouble(v);
        } else {
            this.writeDoubleLE(v);
        }
    }

    //
    //
    // other types
    //
    //

    /**
     * Writes the characters in the given {@link String} in the {@code ISO-8859-1} charset. Only one byte is written per character, the upper 8 bits of each character are
     * silently discarded.
     *
     * @param text the {@link String} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeBytes(String)
     * @see #writeBytes(CharSequence)
     */
    @Override
    default void writeBytes(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeBytes(text, 0, text.length());
    }

    /**
     * Writes the characters in the given {@link CharSequence} in the {@code ISO-8859-1} charset. Only one byte is written per character, the upper 8 bits of each
     * character are silently discarded.
     *
     * @param text the {@link CharSequence} to write
     * @return the number of bytes written. This will always be the value of {@link CharSequence#length()}
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeBytes(CharSequence, int, int)
     */
    default long writeBytes(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.writeBytes(text, 0, text.length());
    }

    /**
     * Writes the characters in the given range of the given {@link CharSequence} in the {@code ISO-8859-1} charset. Only one byte is written per character, the upper 8
     * bits of each character are silently discarded.
     *
     * @param text   the {@link CharSequence} to write
     * @param offset the index of the first character to write
     * @param length the number of characters to write
     * @return the number of bytes written. This will always be the value of the {@code length} parameter
     * @throws ClosedChannelException    if the {@link DataOut} was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link CharSequence}
     * @throws NoMoreSpaceException      if there is insufficient space remaining
     * @see #writeBytes(CharSequence)
     */
    default long writeBytes(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }

        checkRangeLen(text.length(), offset, length);
        if (length == 0) {
            return 0L;
        }

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        int total = 0;
        do {
            int blockSize = min(length - total, PorkUtil.bufferSize());
            for (int i = 0; i < blockSize; i++) {
                buf[i] = (byte) text.charAt(offset + total + i);
            }
            this.write(buf, 0, blockSize);
            total += blockSize;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
        return length;
    }

    /**
     * Writes the characters in the given {@link String} in the {@code UTF-16BE} charset.
     * <p>
     * Unlike {@link #writeBytes(String)}, this method does not discard any data.
     *
     * @param text the {@link String} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeChars(String)
     * @see #writeChars(CharSequence)
     */
    @Override
    default void writeChars(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeChars(text, 0, text.length());
    }

    /**
     * Writes the characters in the given {@link CharSequence} in the {@code UTF-16BE} charset.
     * <p>
     * Unlike {@link #writeBytes(CharSequence)}, this method does not discard any data.
     *
     * @param text the {@link CharSequence} to write
     * @return the number of bytes written. This will always be the value of {@link CharSequence#length()}, multiplied by {@code 2}
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeChars(CharSequence, int, int)
     */
    default long writeChars(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.writeChars(text, 0, text.length());
    }

    /**
     * Writes the characters in the given range of the given {@link CharSequence} in the {@code UTF-16BE} charset.
     * <p>
     * Unlike {@link #writeBytes(CharSequence, int, int)}, this method does not discard any data.
     *
     * @param text   the {@link CharSequence} to write
     * @param offset the index of the first character to write
     * @param length the number of characters to write
     * @return the number of bytes written. This will always be the value of the {@code length} parameter, multiplied by {@code 2}
     * @throws ClosedChannelException    if the {@link DataOut} was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link CharSequence}
     * @throws NoMoreSpaceException      if there is insufficient space remaining
     * @see #writeChars(CharSequence)
     */
    default long writeChars(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }

        checkRangeLen(text.length(), offset, length);
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
     * Writes the characters in the given {@link String} in the {@code UTF-16LE} charset.
     * <p>
     * Unlike {@link #writeBytes(String)}, this method does not discard any data.
     *
     * @param text the {@link String} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeCharsLE(CharSequence)
     */
    default void writeCharsLE(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeCharsLE(text, 0, text.length());
    }

    /**
     * Writes the characters in the given {@link CharSequence} in the {@code UTF-16LE} charset.
     * <p>
     * Unlike {@link #writeBytes(CharSequence)}, this method does not discard any data.
     *
     * @param text the {@link CharSequence} to write
     * @return the number of bytes written. This will always be the value of {@link CharSequence#length()}, multiplied by {@code 2}
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeCharsLE(CharSequence, int, int)
     */
    default long writeCharsLE(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.writeCharsLE(text, 0, text.length());
    }

    /**
     * Writes the characters in the given range of the given {@link CharSequence} in the {@code UTF-16LE} charset.
     * <p>
     * Unlike {@link #writeBytes(CharSequence, int, int)}, this method does not discard any data.
     *
     * @param text   the {@link CharSequence} to write
     * @param offset the index of the first character to write
     * @param length the number of characters to write
     * @return the number of bytes written. This will always be the value of the {@code length} parameter, multiplied by {@code 2}
     * @throws ClosedChannelException    if the {@link DataOut} was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link CharSequence}
     * @throws NoMoreSpaceException      if there is insufficient space remaining
     * @see #writeCharsLE(CharSequence)
     */
    default long writeCharsLE(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }

        checkRangeLen(text.length(), offset, length);
        if (length == 0) {
            return 0L;
        }

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        int total = 0;
        do {
            int blockSize = min(length - total, PorkUtil.bufferSize() / Character.BYTES);
            for (int i = 0; i < blockSize; i++) {
                PUnsafe.putUnalignedCharLE(buf, PUnsafe.arrayCharElementOffset(i), text.charAt(total + i));
            }
            this.write(buf, 0, blockSize * Character.BYTES);
            total += blockSize;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
        return (long) length << 1L;
    }

    /**
     * Writes a {@code UTF-8} encoded {@link String} with an {@link #writeShort(int) unsigned 16-bit big-endian} length prefix.
     *
     * @param text the {@link String} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeUTF(String)
     * @see #writeVarUTF(CharSequence)
     */
    @Override
    default void writeUTF(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeString(text, StandardCharsets.UTF_8);
    }

    /**
     * Writes a {@code UTF-8} encoded {@link CharSequence} with an {@link #writeShort(int) unsigned 16-bit big-endian} length prefix.
     *
     * @param text the {@link CharSequence} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeUTF(String)
     * @see #writeVarUTF(CharSequence)
     */
    default void writeUTF(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeString(text, StandardCharsets.UTF_8);
    }

    /**
     * Writes a {@code UTF-8} encoded {@link CharSequence} with a {@link #writeVarLong(long) VarLong} length prefix.
     *
     * @param text the {@link CharSequence} to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see DataOutput#writeUTF(String)
     * @see #writeUTF(CharSequence)
     */
    default void writeVarUTF(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeVarString(text, StandardCharsets.UTF_8);
    }

    /**
     * Writes a {@link CharSequence} with an {@link #writeShort(int) unsigned 16-bit big-endian}, encoded using the given {@link Charset}.
     *
     * @param text    the {@link CharSequence} to write
     * @param charset the {@link Charset} to encode the text with. Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore
     *                recommended to use values from {@link StandardCharsets} if possible
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeVarString(CharSequence, Charset)
     */
    default void writeString(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //TODO: optimize
        byte[] arr = text.toString().getBytes(charset);
        checkArg(arr.length <= Character.MAX_VALUE, "encoded value is too large (%d > %d)", arr.length, Character.MAX_VALUE);
        this.writeShort(arr.length);
        this.write(arr);
    }

    /**
     * Writes a {@link CharSequence} with a {@link #writeVarLong(long) VarLong} length prefix, encoded using the given {@link Charset}.
     *
     * @param text    the {@link CharSequence} to write
     * @param charset the {@link Charset} to encode the text with. Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore
     *                recommended to use values from {@link StandardCharsets} if possible
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeString(CharSequence, Charset)
     */
    default void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //TODO: optimize
        byte[] arr = text.toString().getBytes(charset);
        this.writeVarLong(arr.length);
        this.write(arr);
    }

    /**
     * Writes every character in the given {@link CharSequence} using the given {@link Charset}.
     * <p>
     * It will not be length-prefixed, meaning that it will not be able to be read directly using the corresponding method in {@link DataIn}.
     *
     * @param text    the {@link CharSequence} to write
     * @param charset the {@link Charset} to encode the text with. Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore
     *                recommended to use values from {@link StandardCharsets} if possible
     * @return the number of bytes written
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     * @see #writeText(CharSequence, int, int, Charset)
     */
    default long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.writeText(text, 0, text.length(), charset);
    }

    /**
     * Writes the characters in the given range of the given {@link CharSequence} using the given {@link Charset}.
     * <p>
     * It will not be length-prefixed, meaning that it will not be able to be read directly using the corresponding method in {@link DataIn}.
     *
     * @param text    the {@link CharSequence} to write
     * @param offset  the index of the first character to write
     * @param length  the number of characters to write
     * @param charset the {@link Charset} to encode the text with. Depending on the {@link Charset} used, certain optimizations may be applied. It is therefore
     *                recommended to use values from {@link StandardCharsets} if possible
     * @return the number of bytes written
     * @throws ClosedChannelException    if the {@link DataOut} was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link CharSequence}
     * @throws NoMoreSpaceException      if there is insufficient space remaining
     * @see #writeText(CharSequence, Charset)
     */
    default long writeText(@NonNull CharSequence text, int offset, int length, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (charset == StandardCharsets.US_ASCII || charset == StandardCharsets.ISO_8859_1) {
            return this.writeBytes(text, offset, length);
        } else if (charset == StandardCharsets.UTF_16BE) {
            return this.writeChars(text, offset, length);
        }

        //TODO: optimize
        byte[] b = text.subSequence(offset, offset + length).toString().getBytes(charset);
        this.write(b);
        return b.length;
    }

    /**
     * Writes an enum value.
     *
     * @param e   the value to write
     * @param <E> the type of the enum
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    @Deprecated
    default <E extends Enum<E>> void writeEnum(@NonNull E e) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeUTF(e.name());
    }

    /**
     * Writes a Mojang-style VarInt.
     * <p>
     * As described at <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong">
     * https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong</a>.
     *
     * @param value the value to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default void writeVarInt(int value) throws ClosedChannelException, NoMoreSpaceException, IOException {
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
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default void writeVarIntZigZag(int value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.writeVarInt((value << 1) ^ (value >> 31));
    }

    /**
     * Writes a Mojang-style VarLong.
     * <p>
     * As described at <a href="https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong">
     * https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong</a>.
     *
     * @param value the value to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default void writeVarLong(long value) throws ClosedChannelException, NoMoreSpaceException, IOException {
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
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default void writeVarLongZigZag(long value) throws ClosedChannelException, NoMoreSpaceException, IOException {
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
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    @Override
    default void write(@NonNull byte[] src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.write(src, 0, src.length);
    }

    /**
     * Writes the entire contents of the given region of the given {@code byte[]}.
     *
     * @param src    the {@code byte[]} to write
     * @param offset the index of the first byte to write
     * @param length the number of bytes to write
     * @throws ClosedChannelException if the {@link DataOut} was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    @Override
    void write(@NonNull byte[] src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException;

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
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    @Override
    int write(@NonNull ByteBuffer src) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Writes data from the given {@link ByteBuffer}s.
     * <p>
     * This method will write data until the buffers have no bytes remaining.
     *
     * @param srcs the {@link ByteBuffer}s to write data from
     * @return the number of bytes written
     * @throws ClosedChannelException if the channel was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    @Override
    default long write(@NonNull ByteBuffer[] srcs) throws ClosedChannelException, NoMoreSpaceException, IOException {
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
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@code srcs} array
     * @throws NoMoreSpaceException      if there is insufficient space remaining
     */
    @Override
    default long write(@NonNull ByteBuffer[] srcs, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
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
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default int write(@NonNull ByteBuf src) throws ClosedChannelException, NoMoreSpaceException, IOException {
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
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default int write(@NonNull ByteBuf src, int count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (notNegative(count, "count") == 0) {
            return 0;
        }

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
     * @param offset the index of the first byte to write
     * @param length the number of bytes to write
     * @return the number of bytes written
     * @throws ClosedChannelException    if the channel was already closed
     * @throws IndexOutOfBoundsException if the given {@code offset} and {@code length} do not describe a valid region of the given {@link ByteBuf}'s current capacity
     * @throws NoMoreSpaceException      if there is insufficient space remaining
     */
    int write(@NonNull ByteBuf src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException;

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
     * @throws ClosedChannelException if the channel was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    long transferFrom(@NonNull DataIn src) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Transfers data from the given {@link DataIn} to this {@link DataOut}.
     * <p>
     * This will read until the requested number of bytes is transferred or given {@link DataIn} reaches EOF. If EOF was already reached, this
     * method will always return {@code -1}.
     *
     * @param src   the {@link DataIn} to transfer data from
     * @param count the number of bytes to transfer
     * @return the number of bytes transferred, or {@code -1} if the given {@link DataIn} had already reached EOF
     * @throws ClosedChannelException if the channel was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    long transferFrom(@NonNull DataIn src, long count) throws ClosedChannelException, NoMoreSpaceException, IOException;

    /**
     * Transfers data from the given {@link DataIn} to this {@link DataOut}.
     * <p>
     * This will read until the requested number of bytes is transferred.
     *
     * @param src   the {@link DataIn} to transfer data from
     * @param count the number of bytes to transfer
     * @return the number of bytes transferred
     * @throws EOFException           if EOF is reached before the requested number of bytes can be transferred
     * @throws ClosedChannelException if the channel was already closed
     * @throws NoMoreSpaceException   if there is insufficient space remaining
     */
    default long transferFromFully(@NonNull DataIn src, long count) throws ClosedChannelException, EOFException, NoMoreSpaceException, IOException {
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
     * @throws ClosedChannelException if the channel was already closed
     */
    OutputStream asOutputStream() throws ClosedChannelException, IOException;

    /**
     * If this {@link DataOut} uses some kind of write buffer: attempts to flush all currently buffered data.
     *
     * @throws ClosedChannelException if the channel was already closed
     * @see OutputStream#flush()
     */
    void flush() throws ClosedChannelException, IOException;

    /**
     * Checks whether this {@link DataOut} is optimized for native (off-heap) memory.
     * <p>
     * If {@code true}, then using native buffers when writing to this {@link DataOut} is likely to provide a performance boost.
     * <p>
     * Note that it is possible for both {@link #isDirect()} and {@link #isHeap()} to return the same value.
     *
     * @return whether this {@link DataOut} is optimized for native (off-heap) memory
     * @see #isHeap()
     */
    boolean isDirect();

    /**
     * Checks whether this {@link DataOut} is optimized for on-heap memory.
     * <p>
     * If {@code true}, then using on-heap buffers or {@code byte[]}s when writing to this {@link DataOut} is likely to provide a performance boost.
     * <p>
     * Note that it is possible for both {@link #isDirect()} and {@link #isHeap()} to return the same value.
     *
     * @return whether this {@link DataOut} is optimized for on-heap memory
     * @see #isDirect()
     */
    boolean isHeap();

    @Override
    boolean isOpen();

    @Override
    void close() throws IOException;
}
