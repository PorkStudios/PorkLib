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

package net.daporkchop.lib.binary.buf;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.RefCounted;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A buffer for bytes.
 * <p>
 * Heavily inspired by Netty's ByteBuf, however engineered in a way that allows making things even faster by removing
 * the requirement for everything to be backed by a NIO ByteBuffer, allowing for e.g. gargantuan blocks of memory
 * allocated with PUnsafe.
 * <p>
 * All indices and sizes use 64-bit integers instead of 32-bit, because this is explicitly intended to work with
 * massive buffers.
 *
 * @author DaPorkchop_
 */
public interface PorkBuf extends RefCounted {
    long capacity();
    PorkBuf capacity(long capacity);
    long maxCapacity();

    long readerIndex();
    PorkBuf readerIndex(long readerIndex);
    long writerIndex();
    PorkBuf writerIndex(long writerIndex);

    default long readableBytes() { return this.writerIndex() - this.readerIndex(); }
    default boolean isReadable() { return this.writerIndex() > this.readerIndex(); }

    //
    //
    // put methods
    //
    //

    default PorkBuf put(byte b) { return this.put(b & 0xFF); }
    PorkBuf put(int b);
    default PorkBuf putBoolean(boolean b) { return this.put(b ? 1 : 0); }
    PorkBuf putShort(short s);
    PorkBuf putShortLE(short s);
    PorkBuf putInt(int i);
    PorkBuf putIntLE(int i);
    PorkBuf putLong(long i);
    PorkBuf putLongLE(long i);
    default PorkBuf put(@NonNull byte[] b) { return this.put(b, 0, b.length); }
    PorkBuf put(@NonNull byte[] b, int start, int count);
    default PorkBuf put(@NonNull ByteBuf src) { return this.put(src, src.readerIndex(), src.readableBytes()); }
    default PorkBuf put(@NonNull ByteBuf src, int count) { return this.put(src, src.readerIndex(), count); }
    PorkBuf put(@NonNull ByteBuf src, int start, int count);
    default PorkBuf put(@NonNull ByteBuffer src) { return this.put(src, src.position(), src.remaining()); }
    default PorkBuf put(@NonNull ByteBuffer src, int count) { return this.put(src, src.position(), count); }
    PorkBuf put(@NonNull ByteBuffer src, int start, int count);
    long put(@NonNull ReadableByteChannel ch) throws IOException;
    long put(@NonNull ReadableByteChannel ch, long max) throws IOException;
    PorkBuf putAll(@NonNull ReadableByteChannel ch, long count) throws IOException;
    long put(@NonNull ScatteringByteChannel ch) throws IOException;
    long put(@NonNull ScatteringByteChannel ch, long max) throws IOException;
    PorkBuf putAll(@NonNull ScatteringByteChannel ch, long count) throws IOException;
    long put(@NonNull FileChannel ch) throws IOException;
    long put(@NonNull FileChannel ch, long max) throws IOException;
    long put(@NonNull FileChannel ch, long pos, long max) throws IOException;
    PorkBuf putAll(@NonNull FileChannel ch, long count) throws IOException;
    PorkBuf putAll(@NonNull FileChannel ch, long pos, long count) throws IOException;
    long put(@NonNull InputStream in) throws IOException;
    long put(@NonNull InputStream in, long max) throws IOException;
    PorkBuf putAll(@NonNull InputStream in, long count) throws IOException;
    PorkBuf putZeroes(long count);

    //
    //
    // set methods
    //
    //

    default PorkBuf set(long index, byte b) { return this.set(index, b & 0xFF); }
    PorkBuf set(long index, int b);
    default PorkBuf setBoolean(long index, boolean b) { return this.set(index, b ? 1 : 0); }
    PorkBuf setShort(long index, short s);
    PorkBuf setShortLE(long index, short s);
    PorkBuf setInt(long index, int i);
    PorkBuf setIntLE(long index, int i);
    PorkBuf setLong(long index, long i);
    PorkBuf setLongLE(long index, long i);
    default PorkBuf set(long index, @NonNull byte[] b) { return this.set(index, b, 0, b.length); }
    PorkBuf set(long index, @NonNull byte[] b, int start, int count);
    default PorkBuf set(long index, @NonNull ByteBuf src) { return this.set(index, src, src.readerIndex(), src.readableBytes()); }
    default PorkBuf set(long index, @NonNull ByteBuf src, int count) { return this.set(index, src, src.readerIndex(), count); }
    PorkBuf set(long index, @NonNull ByteBuf src, int start, int count);
    default PorkBuf set(long index, @NonNull ByteBuffer src) { return this.set(index, src, src.position(), src.remaining()); }
    default PorkBuf set(long index, @NonNull ByteBuffer src, int count) { return this.set(index, src, src.position(), count); }
    PorkBuf set(long index, @NonNull ByteBuffer src, int start, int count);
    long set(long index, @NonNull ReadableByteChannel ch) throws IOException;
    long set(long index, @NonNull ReadableByteChannel ch, long max) throws IOException;
    PorkBuf setAll(long index, @NonNull ReadableByteChannel ch, long count) throws IOException;
    long set(long index, @NonNull ScatteringByteChannel ch) throws IOException;
    long set(long index, @NonNull ScatteringByteChannel ch, long max) throws IOException;
    PorkBuf setAll(long index, @NonNull ScatteringByteChannel ch, long count) throws IOException;
    long set(long index, @NonNull FileChannel ch) throws IOException;
    long set(long index, @NonNull FileChannel ch, long max) throws IOException;
    long set(long index, @NonNull FileChannel ch, long pos, long max) throws IOException;
    PorkBuf setAll(long index, @NonNull FileChannel ch, long count) throws IOException;
    PorkBuf setAll(long index, @NonNull FileChannel ch, long pos, long count) throws IOException;
    long set(long index, @NonNull InputStream in) throws IOException;
    long set(long index, @NonNull InputStream in, long max) throws IOException;
    PorkBuf setAll(long index, @NonNull InputStream in, long count) throws IOException;
    PorkBuf setZeroes(long index, long count);

    //
    //
    // read methods
    //
    //

    default byte read() { return (byte) this.readByte(); }
    int readByte();
    default boolean readBoolean() { return this.readByte() != 0; }
    short readShort();
    short readShortLE();
    int readInt();
    int readIntLE();
    long readLong();
    long readLongLE();
    default PorkBuf read(@NonNull byte[] b) { return this.read(b, 0, b.length); }
    PorkBuf read(@NonNull byte[] b, int start, int count);
    PorkBuf read(@NonNull ByteBuf dst);
    PorkBuf read(@NonNull ByteBuf dst, int count);
    PorkBuf read(@NonNull ByteBuf dst, int start, int count);
    PorkBuf read(@NonNull ByteBuffer dst);
    PorkBuf read(@NonNull ByteBuffer dst, int count);
    PorkBuf read(@NonNull ByteBuffer src, int start, int count);
    long read(@NonNull WritableByteChannel ch) throws IOException;
    long read(@NonNull WritableByteChannel ch, long max) throws IOException;
    PorkBuf readAll(@NonNull WritableByteChannel ch) throws IOException;
    PorkBuf readAll(@NonNull WritableByteChannel ch, long count) throws IOException;
    long read(@NonNull GatheringByteChannel ch) throws IOException;
    long read(@NonNull GatheringByteChannel ch, long max) throws IOException;
    PorkBuf readAll(@NonNull GatheringByteChannel ch) throws IOException;
    PorkBuf readAll(@NonNull GatheringByteChannel ch, long count) throws IOException;
    PorkBuf read(@NonNull FileChannel ch) throws IOException;
    PorkBuf read(@NonNull FileChannel ch, long count) throws IOException;
    PorkBuf read(@NonNull FileChannel ch, long pos, long count) throws IOException;
    PorkBuf read(@NonNull OutputStream out) throws IOException;
    PorkBuf read(@NonNull OutputStream out, long count) throws IOException;

    //
    //
    // get methods
    //
    //

    default byte get(long index) { return (byte) this.getByte(index); }
    int getByte(long index);
    default boolean getBoolean(long index) { return this.getByte(index) != 0; }
    short getShort(long index);
    short getShortLE(long index);
    int getInt(long index);
    int getIntLE(long index);
    long getLong(long index);
    long getLongLE(long index);
    default PorkBuf get(long index, @NonNull byte[] b) { return this.get(index, b, 0, b.length); }
    PorkBuf get(long index, @NonNull byte[] b, int start, int count);
    PorkBuf get(long index, @NonNull ByteBuf dst);
    PorkBuf get(long index, @NonNull ByteBuf dst, int count);
    PorkBuf get(long index, @NonNull ByteBuf dst, int start, int count);
    PorkBuf get(long index, @NonNull ByteBuffer dst);
    PorkBuf get(long index, @NonNull ByteBuffer dst, int count);
    PorkBuf get(long index, @NonNull ByteBuffer src, int start, int count);
    long get(long index, @NonNull WritableByteChannel ch) throws IOException;
    long get(long index, @NonNull WritableByteChannel ch, long max) throws IOException;
    PorkBuf getAll(long index, @NonNull WritableByteChannel ch) throws IOException;
    PorkBuf getAll(long index, @NonNull WritableByteChannel ch, long count) throws IOException;
    long get(long index, @NonNull GatheringByteChannel ch) throws IOException;
    long get(long index, @NonNull GatheringByteChannel ch, long max) throws IOException;
    PorkBuf getAll(long index, @NonNull GatheringByteChannel ch) throws IOException;
    PorkBuf getAll(long index, @NonNull GatheringByteChannel ch, long count) throws IOException;
    PorkBuf get(long index, @NonNull FileChannel ch) throws IOException;
    PorkBuf get(long index, @NonNull FileChannel ch, long count) throws IOException;
    PorkBuf get(long index, @NonNull FileChannel ch, long pos, long count) throws IOException;
    PorkBuf get(long index, @NonNull OutputStream out) throws IOException;
    PorkBuf get(long index, @NonNull OutputStream out, long count) throws IOException;

    @Override
    PorkBuf retain() throws AlreadyReleasedException;
    @Override
    PorkBuf release() throws AlreadyReleasedException;
}
