/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.binary.buffer;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A buffer interface heavily inspired by Netty's {@link io.netty.buffer.ByteBuf}, but intended for use with 64-bit systems
 * where Netty's restriction to {@code int} for indexing is a limiting factor.
 * <p>
 * All methods involving reader/writer indexes are not thread-safe, however all methods that accept an absolute index are.
 *
 * @author DaPorkchop_
 */
public interface PorkBuf extends RefCounted {
    //
    //
    // Indexing methods
    //
    //

    /**
     * @return this {@link PorkBuf}'s current reader index
     */
    long readerIndex();

    /**
     * Sets this {@link PorkBuf}'s reader index.
     *
     * @param readerIndex the new reader index to set
     * @throws IndexOutOfBoundsException if the given reader index is less than {@code 0}, or greater than {@link #writerIndex()}
     */
    PorkBuf readerIndex(long readerIndex) throws IndexOutOfBoundsException;

    /**
     * @return this {@link PorkBuf}'s current writer index
     */
    long writerIndex();

    /**
     * Sets this {@link PorkBuf}'s writer index.
     *
     * @param writerIndex the new writer index to set
     * @throws IndexOutOfBoundsException if the given writer index is less than {@link #readerIndex()}, or greater than {@link #capacity()}
     */
    PorkBuf writerIndex(long writerIndex) throws IndexOutOfBoundsException;

    /**
     * @return this {@link PorkBuf}'s current capacity. Will always be less than or equal to {@link #maxCapacity()}.
     */
    long capacity();

    /**
     * @return this {@link PorkBuf}'s maximum capacity
     */
    long maxCapacity();

    /**
     * Ensures that the given number of bytes are writable in this buffer.
     * <p>
     * This will expand the buffer's {@link #capacity()} if needed.
     *
     * @param count the number of writable bytes to ensure
     * @throws IndexOutOfBoundsException if {@link #writerIndex()} + {@code count} > {@link #maxCapacity()}
     */
    PorkBuf ensureWritable(long count) throws IndexOutOfBoundsException;

    /**
     * Ensures that the buffer's capacity is at least the given size.
     * <p>
     * This will expand the buffer's {@link #capacity()} if needed.
     *
     * @param count the capacity to ensure
     * @throws IndexOutOfBoundsException if {@code count} > {@link #maxCapacity()}
     */
    PorkBuf ensureCapacity(long count) throws IndexOutOfBoundsException;

    /**
     * @return the number of readable bytes in this {@link PorkBuf}
     */
    long readableBytes();

    /**
     * @return the number of writable bytes in this {@link PorkBuf}
     */
    long writableBytes();

    //
    //
    // Indexed write methods
    //
    //

    /**
     * Sets a {@code boolean} at the given index.
     *
     * @param index the index of the {@code boolean} to set
     * @param val   the value to set
     */
    default PorkBuf setBoolean(long index, boolean val) {
        return this.setByte(index, val ? (byte) 1 : 0);
    }

    /**
     * Sets a {@code byte} at the given index.
     *
     * @param index the index of the {@code byte} to set
     * @param val   the value to set
     */
    PorkBuf setByte(long index, byte val);

    /**
     * Sets a big-endian {@code short} at the given index.
     *
     * @param index the index of the {@code short} to set
     * @param val   the value to set
     */
    PorkBuf setShort(long index, byte val);

    /**
     * Sets a little-endian {@code short} at the given index.
     *
     * @param index the index of the {@code short} to set
     * @param val   the value to set
     */
    PorkBuf setShortLE(long index, byte val);

    /**
     * Sets a big-endian {@code char} at the given index.
     *
     * @param index the index of the {@code char} to set
     * @param val   the value to set
     */
    PorkBuf setChar(long index, byte val);

    /**
     * Sets a little-endian {@code char} at the given index.
     *
     * @param index the index of the {@code char} to set
     * @param val   the value to set
     */
    PorkBuf setCharLE(long index, byte val);

    /**
     * Sets a big-endian {@code int} at the given index.
     *
     * @param index the index of the {@code int} to set
     * @param val   the value to set
     */
    PorkBuf setInt(long index, byte val);

    /**
     * Sets a little-endian {@code int} at the given index.
     *
     * @param index the index of the {@code int} to set
     * @param val   the value to set
     */
    PorkBuf setIntLE(long index, byte val);

    /**
     * Sets a big-endian {@code long} at the given index.
     *
     * @param index the index of the {@code long} to set
     * @param val   the value to set
     */
    PorkBuf setLong(long index, byte val);

    /**
     * Sets a little-endian {@code long} at the given index.
     *
     * @param index the index of the {@code long} to set
     * @param val   the value to set
     */
    PorkBuf setLongLE(long index, byte val);

    /**
     * Sets a big-endian {@code float} at the given index.
     *
     * @param index the index of the {@code float} to set
     * @param val   the value to set
     */
    PorkBuf setFloat(long index, byte val);

    /**
     * Sets a little-endian {@code float} at the given index.
     *
     * @param index the index of the {@code float} to set
     * @param val   the value to set
     */
    PorkBuf setFloatLE(long index, byte val);

    /**
     * Sets a big-endian {@code double} at the given index.
     *
     * @param index the index of the {@code double} to set
     * @param val   the value to set
     */
    PorkBuf setDouble(long index, byte val);

    /**
     * Sets a little-endian {@code double} at the given index.
     *
     * @param index the index of the {@code double} to set
     * @param val   the value to set
     */
    PorkBuf setDoubleLE(long index, byte val);

    /**
     * Equivalent to {@code setBytes(index, arr, 0, arr.length);}
     *
     * @see #setBytes(long, byte[], int, int)
     */
    default PorkBuf setBytes(long index, @NonNull byte[] arr) {
        return this.setBytes(index, arr, 0, arr.length);
    }

    /**
     * Equivalent to {@code setBytes(index, arr, 0, length);}
     *
     * @see #setBytes(long, byte[], int, int)
     */
    default PorkBuf setBytes(long index, @NonNull byte[] arr, int length) throws IndexOutOfBoundsException {
        return this.setBytes(index, arr, 0, length);
    }

    /**
     * Sets the bytes at the given index by getting them from the given array.
     *
     * @param index  the first index of the bytes to set
     * @param arr    the {@code byte[]} containing the new bytes
     * @param start  the first index in the array
     * @param length the number of bytes to set
     * @throws IndexOutOfBoundsException if {@code start} and {@code length} are out of bounds of the given array, or {@code index} + {@code length} > {@link #capacity()}
     */
    PorkBuf setBytes(long index, @NonNull byte[] arr, int start, int length) throws IndexOutOfBoundsException;

    /**
     * Equivalent to {@code setBytes(index, buf, buf.readableBytes());}
     *
     * @see #setBytes(long, PorkBuf, long)
     */
    default PorkBuf setBytes(long index, @NonNull PorkBuf buf) throws IndexOutOfBoundsException {
        return this.setBytes(index, buf, buf.readableBytes());
    }

    /**
     * Sets the bytes at the given index by reading them from the given {@link PorkBuf}.
     *
     * @param index  the first index of the bytes to set
     * @param buf    the {@link PorkBuf} containing the new bytes
     * @param length the number of bytes to set
     * @throws IndexOutOfBoundsException if {@code length} is out of bounds of the given {@link PorkBuf}, or {@code index} + {@code length} > {@link #capacity()}
     */
    PorkBuf setBytes(long index, @NonNull PorkBuf buf, long length) throws IndexOutOfBoundsException;

    /**
     * Sets the bytes at the given index by getting them from the given {@link PorkBuf}.
     *
     * @param index  the first index of the bytes to set
     * @param buf    the {@link PorkBuf} containing the new bytes
     * @param start  the first index in the {@link PorkBuf}
     * @param length the number of bytes to set
     * @throws IndexOutOfBoundsException if {@code start} and {@code length} are out of bounds of the given {@link PorkBuf}, or {@code index} + {@code length} > {@link #capacity()}
     */
    PorkBuf setBytes(long index, @NonNull PorkBuf buf, long start, long length) throws IndexOutOfBoundsException;

    //
    //
    // General methods
    //
    //

    /**
     * @return whether or not this {@link PorkBuf} is read-only
     */
    boolean readOnly();

    //
    //
    // RefCounted methods
    //
    //

    @Override
    int refCnt();

    @Override
    PorkBuf retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
