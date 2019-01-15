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

import java.nio.BufferOverflowException;

/**
 * Version 2.0 of the PorkBuf! However, unlike the old one, this one isn't crap.
 * <p>
 * Doesn't really have any advantages over NIO's {@link java.nio.ByteBuffer} or Netty's
 * {@link io.netty.buffer.ByteBuf} except for the fact that it supports 64-bit length.
 *
 * @author DaPorkchop_
 */
public interface PorkBuf {
    //size methods

    /**
     * Gets the current capacity of this buffer (i.e. the number of bytes it can hold without expanding)
     *
     * @return the current capacity of this buffer
     */
    long capacity();

    /**
     * Sets this buffer's capacity to the given number of bytes.
     * <p>
     * If larger than the current capacity, newly added bytes will be set to 0. If smaller than the current
     * capacity, the contents will be truncated. If larger than the maximum capacity (see {@link #maxCapacity()})
     * an exception will be thrown.
     * <p>
     * Optional operation.
     *
     * @param capacity the new capacity
     * @return this buffer
     */
    default PorkBuf setCapacity(long capacity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if this buffer's capacity is mutable.
     * <p>
     * If {@code true}, the methods {@link #setCapacity(long)} and {@link #setMaxCapacity(long)} must be implemented.
     *
     * @return whether or not this buffer's capacity is mutable.
     */
    default boolean isCapacityMutable() {
        return false;
    }

    /**
     * Ensures that the buffer has at least a certain capacity, expanding if required.
     *
     * @param numBytes the minimum number of bytes required
     * @return this buffer
     */
    default PorkBuf requireBytes(long numBytes) {
        this.requireBytes(0L, numBytes);
        return this;
    }

    /**
     * Ensures that the buffer has at least a certain capacity, expanding if required.
     *
     * @param offset   the offset that bytes will be required at
     * @param numBytes the minimum number of bytes required
     * @return this buffer
     */
    default PorkBuf requireBytes(long offset, long numBytes) {
        if (this.capacity() < offset + numBytes) {
            this.setCapacity(offset + numBytes);
        }
        return this;
    }

    /**
     * Gets this buffer's maximum allowed capacity
     *
     * @return this buffer's maximum allows capacity
     */
    default long maxCapacity() {
        return this.capacity();
    }

    /**
     * Sets the maximum capacity of this buffer.
     * <p>
     * Optional operation.
     *
     * @param maxCapacity the new maximum capacity
     * @return this buffer
     */
    default PorkBuf setMaxCapacity(long maxCapacity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets this buffer's current writer index.
     *
     * @return this buffer's current writer index.
     */
    long writerIndex();

    /**
     * Sets this buffer's writer index.
     *
     * @param index the new index to set. Must be >= 0 and < {@link #capacity()}
     * @return this buffer
     */
    PorkBuf writerIndex(long index);

    /**
     * Gets this buffer's current reader index.
     *
     * @return this buffer's current reader index.
     */
    long readerIndex();

    /**
     * Sets this buffer's reader index.
     *
     * @param index the new index to set. Must be >= 0 and < {@link #capacity()}
     * @return this buffer
     */
    PorkBuf readerIndex(long index);

    //write operations

    /**
     * Writes a single byte to the buffer at the current writer index, incrementing the writer index by 1
     *
     * @param b the byte to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    PorkBuf putByte(byte b);

    /**
     * Writes a single boolean to the buffer at the current writer index, incrementing the writer index by 1
     *
     * @param b the boolean to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBoolean(boolean b) {
        if (!this.canWrite(2)) {
            throw new BufferOverflowException();
        }
        return this.putByte(b ? (byte) 1 : 0);
    }

    /**
     * Writes a single short to the buffer at the current writer index, incrementing the writer index by 2
     *
     * @param s the short to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putShort(short s) {
        if (!this.canWrite(2)) {
            throw new BufferOverflowException();
        }
        return this.putByte((byte) (s & 0xFF))
                .putByte((byte) ((s >>> 8) & 0xFF));
    }

    /**
     * Writes a single medium to the buffer at the current writer index, incrementing the writer index by 3
     *
     * @param i the medium to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putMedium(int i) {
        if (!this.canWrite(3)) {
            throw new BufferOverflowException();
        }
        return this.putByte((byte) (i & 0xFF))
                .putByte((byte) ((i >>> 8) & 0xFF))
                .putByte((byte) ((i >>> 16) & 0xFF));
    }

    /**
     * Writes a single int to the buffer at the current writer index, incrementing the writer index by 4
     *
     * @param i the int to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putInt(int i) {
        if (!this.canWrite(4)) {
            throw new BufferOverflowException();
        }
        return this.putByte((byte) (i & 0xFF))
                .putByte((byte) ((i >>> 8) & 0xFF))
                .putByte((byte) ((i >>> 16) & 0xFF))
                .putByte((byte) ((i >>> 24) & 0xFF));
    }

    /**
     * Writes a single long to the buffer at the current writer index, incrementing the writer index by 8
     *
     * @param l the long to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putLong(long l) {
        if (!this.canWrite(8)) {
            throw new BufferOverflowException();
        }
        return this.putByte((byte) (l & 0xFFL))
                .putByte((byte) ((l >>> 8L) & 0xFFL))
                .putByte((byte) ((l >>> 16L) & 0xFFL))
                .putByte((byte) ((l >>> 24L) & 0xFFL))
                .putByte((byte) ((l >>> 32L) & 0xFFL))
                .putByte((byte) ((l >>> 40L) & 0xFFL))
                .putByte((byte) ((l >>> 48L) & 0xFFL))
                .putByte((byte) ((l >>> 56L) & 0xFFL));
    }

    /**
     * Writes a single float to the buffer at the current writer index, incrementing the writer index by 4
     *
     * @param f the float to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putFloat(float f) {
        return this.putInt(Float.floatToRawIntBits(f));
    }

    /**
     * Writes a single double to the buffer at the current writer index, incrementing the writer index by 8
     *
     * @param d the double to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putDouble(double d) {
        return this.putLong(Double.doubleToRawLongBits(d));
    }

    /**
     * Writes a single byte to the buffer at the given index
     *
     * @param index the index to write at
     * @param b     the byte to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    PorkBuf putByte(long index, byte b);

    /**
     * Writes a single boolean to the buffer at the given index
     *
     * @param index the index to write at
     * @param b     the boolean to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBoolean(long index, boolean b) {
        if (!this.canWrite(index, 1)) {
            throw new BufferOverflowException();
        }
        return this.putByte(index, b ? (byte) 1 : 0);
    }

    /**
     * Writes a single short to the buffer at the given index
     *
     * @param index the index to write at
     * @param s     the short to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putShort(long index, short s) {
        if (!this.canWrite(index, 2)) {
            throw new BufferOverflowException();
        }
        return this.putByte(index, (byte) (s & 0xFF))
                .putByte(index + 1L, (byte) ((s >>> 8) & 0xFF));
    }

    /**
     * Writes a single medium to the buffer at the given index
     *
     * @param index the index to write at
     * @param i     the medium to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putMedium(long index, int i) {
        if (!this.canWrite(index, 3)) {
            throw new BufferOverflowException();
        }
        return this.putByte(index, (byte) (i & 0xFF))
                .putByte(index + 1L, (byte) ((i >>> 8) & 0xFF))
                .putByte(index + 2L, (byte) ((i >>> 16) & 0xFF));
    }

    /**
     * Writes a single int to the buffer at the given index
     *
     * @param index the index to write at
     * @param i     the int to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putInt(long index, int i) {
        if (!this.canWrite(index, 4)) {
            throw new BufferOverflowException();
        }
        return this.putByte(index, (byte) (i & 0xFF))
                .putByte(index + 1L, (byte) ((i >>> 8) & 0xFF))
                .putByte(index + 2L, (byte) ((i >>> 16) & 0xFF))
                .putByte(index + 3L, (byte) ((i >>> 24) & 0xFF));
    }

    /**
     * Writes a single long to the buffer at the given index
     *
     * @param index the index to write at
     * @param l     the long to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putLong(long index, long l) {
        if (!this.canWrite(index, 8)) {
            throw new BufferOverflowException();
        }
        return this.putByte(index, (byte) (l & 0xFFL))
                .putByte(index + 1L, (byte) ((l >>> 8L) & 0xFFL))
                .putByte(index + 2L, (byte) ((l >>> 16L) & 0xFFL))
                .putByte(index + 3L, (byte) ((l >>> 24L) & 0xFFL))
                .putByte(index + 4L, (byte) ((l >>> 32L) & 0xFFL))
                .putByte(index + 5L, (byte) ((l >>> 40L) & 0xFFL))
                .putByte(index + 6L, (byte) ((l >>> 48L) & 0xFFL))
                .putByte(index + 7L, (byte) ((l >>> 56L) & 0xFFL));
    }

    /**
     * Writes a single float to the buffer at the given index
     *
     * @param index the index to write at
     * @param f     the float to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putFloat(long index, float f) {
        return this.putInt(index, Float.floatToRawIntBits(f));
    }

    /**
     * Writes a single double to the buffer at the given index
     *
     * @param index the index to write at
     * @param d     the double to be written
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putDouble(long index, double d) {
        return this.putLong(index, Double.doubleToRawLongBits(d));
    }

    //sanity checks

    /**
     * Checks if the given number of bytes can be written at the current writer index without causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at the current writer index without causing errors
     */
    default boolean canWrite(int count) {
        return this.canWrite(this.writerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be written at the given position without causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param writerIndex the index to start writing at
     * @param count       the number of bytes to write
     * @return whether or not the given number of bytes can be written at the given position without causing errors
     */
    default boolean canWrite(long writerIndex, int count) {
        return writerIndex >= 0L && writerIndex + count < this.maxCapacity();
    }

    /**
     * Checks if the given number of bytes can be written at the current writer index without causing errors.
     *
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at the current writer index without causing errors
     */
    default boolean canWrite(long count) {
        return this.canWrite(this.writerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be written at the given position without causing errors.
     *
     * @param writerIndex the index to start writing at
     * @param count       the number of bytes to write
     * @return whether or not the given number of bytes can be written at the given position without causing errors
     */
    default boolean canWrite(long writerIndex, long count) {
        return writerIndex >= 0L && writerIndex + count < this.maxCapacity();
    }
}
