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

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

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
    PorkBuf put(@NonNull byte[] b, int start, int len);
    default PorkBuf put(@NonNull ByteBuf src) { return this.put(src, src.readerIndex(), src.readableBytes()); }
    default PorkBuf put(@NonNull ByteBuf src, int count) { return this.put(src, src.readerIndex(), count); }
    PorkBuf put(@NonNull ByteBuf src, int start, int count);
    default PorkBuf put(@NonNull ByteBuffer src) { return this.put(src, src.position(), src.remaining()); }
    default PorkBuf put(@NonNull ByteBuffer src, int count) { return this.put(src, src.position(), count); }
    PorkBuf put(@NonNull ByteBuffer src, int start, int count);
    long put(@NonNull ReadableByteChannel ch);
    long put(@NonNull ReadableByteChannel ch, long maxCount);
    PorkBuf putAll(@NonNull ReadableByteChannel ch);
    PorkBuf putAll(@NonNull ReadableByteChannel ch, long count);
}
