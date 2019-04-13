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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.unsafe.block.offset.Offsettable;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Version 2.0 of the PorkBuf! However, unlike the old one, this one isn't crap.
 * <p>
 * Doesn't really have any advantages over NIO's {@link java.nio.ByteBuffer} or Netty's
 * {@link io.netty.buffer.ByteBuf} except for the fact that it supports 64-bit length.
 *
 * @author DaPorkchop_
 */
public interface PorkBuf extends Offsettable {
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
        if (!this.isInBounds(2)) {
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
        if (!this.isInBounds(2)) {
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
        if (!this.isInBounds(3)) {
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
        if (!this.isInBounds(4)) {
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
        if (!this.isInBounds(8)) {
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
     * Writes a byte array to the buffer at the current writer index, incrementing the writer index by the number of bytes
     * in the array
     *
     * @param arr the byte array to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(@NonNull byte[] arr) {
        for (byte b : arr) {
            this.putByte(b);
        }
        return this;
    }

    /**
     * Writes a byte array to the buffer at the current writer index, incrementing the writer index by the number of bytes
     * in the array
     *
     * @param arr the byte array to write
     * @param off the offset in the byte array to start reading
     * @param len the number of bytes to read
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(@NonNull byte[] arr, int off, int len) {
        for (int i = 0; i < len; i++) {
            this.putByte(arr[off + i]);
        }
        return this;
    }

    /**
     * Writes a {@link ByteBuf} to the buffer at the current writer index, incrementing the writer index by the number of
     * readable bytes in the buffer
     *
     * @param buf the {@link ByteBuf} to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(@NonNull ByteBuf buf) {
        for (int i = buf.readableBytes() - 1; i >= 0; i--) {
            this.putByte(buf.readByte());
        }
        return this;
    }

    /**
     * Writes a {@link ByteBuffer} to the buffer at the current writer index, incrementing the writer index by the number of
     * readable bytes in the buffer
     *
     * @param buf the {@link ByteBuffer} to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(@NonNull ByteBuffer buf) {
        for (int i = buf.remaining() - 1; i >= 0; i--) {
            this.putByte(buf.get());
        }
        return this;
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
        if (!this.isInBounds(index, 1)) {
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
        if (!this.isInBounds(index, 2)) {
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
        if (!this.isInBounds(index, 3)) {
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
        if (!this.isInBounds(index, 4)) {
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
        if (!this.isInBounds(index, 8)) {
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

    /**
     * Writes a byte array to the buffer at the given index
     *
     * @param index the index to write at
     * @param arr   the byte array to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(long index, @NonNull byte[] arr) {
        for (byte b : arr) {
            this.putByte(index++, b);
        }
        return this;
    }

    /**
     * Writes a byte array to the buffer at the given index
     *
     * @param index the index to write at
     * @param arr   the byte array to write
     * @param off   the offset in the byte array to start reading
     * @param len   the number of bytes to read
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(long index, @NonNull byte[] arr, int off, int len) {
        for (int i = 0; i < len; i++) {
            this.putByte(index++, arr[off + i]);
        }
        return this;
    }

    /**
     * Writes a {@link ByteBuf} to the buffer at the given index
     *
     * @param index the index to write at
     * @param buf   the {@link ByteBuf} to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(long index, @NonNull ByteBuf buf) {
        for (int i = buf.readableBytes() - 1; i >= 0; i--) {
            this.putByte(index++, buf.readByte());
        }
        return this;
    }

    /**
     * Writes a {@link ByteBuffer} to the buffer at the given index
     *
     * @param index the index to write at
     * @param buf   the {@link ByteBuffer} to write
     * @return this buffer
     * @throws BufferOverflowException if the capacity limit (see {@link #maxCapacity()}) is reached
     */
    default PorkBuf putBytes(long index, @NonNull ByteBuffer buf) {
        for (int i = buf.remaining() - 1; i >= 0; i--) {
            this.putByte(index++, buf.get());
        }
        return this;
    }

    //read operations

    /**
     * Reads a byte from the buffer at the current reader index
     *
     * @return a byte
     */
    byte readByte();

    /**
     * Reads a boolean from the buffer at the current reader index
     *
     * @return a boolean
     */
    default boolean readBoolean() {
        return this.readByte() != 0;
    }

    /**
     * Reads a short from the buffer at the current reader index
     *
     * @return a short
     */
    default short readShort() {
        return (short) ((this.readByte() & 0xFF)
                | ((this.readByte() & 0xFF) << 8));
    }

    /**
     * Reads a medium from the buffer at the current reader index
     *
     * @return a medium
     */
    default int readMedium() {
        return (this.readByte() & 0xFF)
                | ((this.readByte() & 0xFF) << 8)
                | ((this.readByte() & 0xFF) << 16);
    }

    /**
     * Reads an int from the buffer at the current reader index
     *
     * @return an int
     */
    default int readInt() {
        return (this.readByte() & 0xFF)
                | ((this.readByte() & 0xFF) << 8)
                | ((this.readByte() & 0xFF) << 16)
                | ((this.readByte() & 0xFF) << 24);
    }

    /**
     * Reads a long from the buffer at the current reader index
     *
     * @return a long
     */
    default long readLong() {
        return (this.readByte() & 0xFFL)
                | ((this.readByte() & 0xFFL) << 8L)
                | ((this.readByte() & 0xFFL) << 16L)
                | ((this.readByte() & 0xFFL) << 24L)
                | ((this.readByte() & 0xFFL) << 32L)
                | ((this.readByte() & 0xFFL) << 40L)
                | ((this.readByte() & 0xFFL) << 48L)
                | ((this.readByte() & 0xFFL) << 56L);
    }

    /**
     * Reads a float from the buffer at the current reader index
     *
     * @return a float
     */
    default float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }

    /**
     * Reads a double from the buffer at the current reader index
     *
     * @return a double
     */
    default double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     */
    default void readBytes(@NonNull byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = this.readByte();
        }
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     * @param off the offset in the byte array to start putting bytes in
     * @param len the number of bytes to read
     */
    default void readBytes(@NonNull byte[] arr, int off, int len) {
        for (int i = 0; i < len; i++) {
            arr[off + i] = this.readByte();
        }
    }

    /**
     * Fills a {@link ByteBuf} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuf} to fill
     */
    default void readBytes(@NonNull ByteBuf buf) {
        for (int i = buf.writableBytes() - 1; i >= 0; i--) {
            buf.writeByte(this.readByte() & 0xFF);
        }
    }

    /**
     * Fills a {@link ByteBuffer} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuffer} to fill
     */
    default void readBytes(@NonNull ByteBuffer buf) {
        for (int i = buf.remaining() - 1; i >= 0; i--) {
            buf.put(this.readByte());
        }
    }

    /**
     * Reads a byte from the buffer at the given index
     *
     * @param index the index to read at
     * @return a byte
     */
    byte readByte(long index);

    /**
     * Reads a boolean from the buffer at the given index
     *
     * @param index the index to read at
     * @return a boolean
     */
    default boolean readBoolean(long index) {
        return this.readByte(index) != 0;
    }

    /**
     * Reads a short from the buffer at the given index
     *
     * @param index the index to read at
     * @return a short
     */
    default short readShort(long index) {
        return (short) ((this.readByte(index) & 0xFF)
                | ((this.readByte(index + 1L) & 0xFF) << 8));
    }

    /**
     * Reads a medium from the buffer at the given index
     *
     * @param index the index to read at
     * @return a medium
     */
    default int readMedium(long index) {
        return (this.readByte(index) & 0xFF)
                | ((this.readByte(index + 1L) & 0xFF) << 8)
                | ((this.readByte(index + 2L) & 0xFF) << 16);
    }

    /**
     * Reads an int from the buffer at the given index
     *
     * @param index the index to read at
     * @return an int
     */
    default int readInt(long index) {
        return (this.readByte(index) & 0xFF)
                | ((this.readByte(index + 1L) & 0xFF) << 8)
                | ((this.readByte(index + 2L) & 0xFF) << 16)
                | ((this.readByte(index + 3L) & 0xFF) << 24);
    }

    /**
     * Reads a long from the buffer at the given index
     *
     * @param index the index to read at
     * @return a long
     */
    default long readLong(long index) {
        return (this.readByte(index) & 0xFFL)
                | ((this.readByte(index + 1L) & 0xFFL) << 8L)
                | ((this.readByte(index + 2L) & 0xFFL) << 16L)
                | ((this.readByte(index + 3L) & 0xFFL) << 24L)
                | ((this.readByte(index + 4L) & 0xFFL) << 32L)
                | ((this.readByte(index + 5L) & 0xFFL) << 40L)
                | ((this.readByte(index + 6L) & 0xFFL) << 48L)
                | ((this.readByte(index + 7L) & 0xFFL) << 56L);
    }

    /**
     * Reads a float from the buffer at the given index
     *
     * @param index the index to read at
     * @return a float
     */
    default float readFloat(long index) {
        return Float.intBitsToFloat(this.readInt(index));
    }

    /**
     * Reads a double from the buffer at the given index
     *
     * @param index the index to read at
     * @return a double
     */
    default double readDouble(long index) {
        return Double.longBitsToDouble(this.readLong(index));
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     */
    default void readBytes(long index, @NonNull byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = this.readByte(index++);
        }
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     * @param off the offset in the byte array to start putting bytes in
     * @param len the number of bytes to read
     */
    default void readBytes(long index, @NonNull byte[] arr, int off, int len) {
        for (int i = 0; i < len; i++) {
            arr[off + i] = this.readByte(index++);
        }
    }

    /**
     * Fills a {@link ByteBuf} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuf} to fill
     */
    default void readBytes(long index, @NonNull ByteBuf buf) {
        for (int i = buf.writableBytes() - 1; i >= 0; i--) {
            buf.writeByte(this.readByte(index++) & 0xFF);
        }
    }

    /**
     * Fills a {@link ByteBuffer} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuffer} to fill
     */
    default void readBytes(long index, @NonNull ByteBuffer buf) {
        for (int i = buf.remaining() - 1; i >= 0; i--) {
            buf.put(this.readByte(index++));
        }
    }

    //stream methods

    /**
     * Gets a {@link DataOut} that can write to this buffer.
     *
     * @return a {@link DataOut} that can write to this buffer
     */
    default DataOut outputStream() {
        return this.outputStream(0L, -1L);
    }

    /**
     * Gets a {@link DataOut} that can write to this buffer.
     *
     * @param offset the index that the {@link DataOut} will start writing at
     * @return a {@link DataOut} that can write to this buffer
     */
    default DataOut outputStream(long offset) {
        return this.outputStream(offset, -1L);
    }

    /**
     * Gets a {@link DataOut} that can write to this buffer.
     *
     * @param offset the index that the {@link DataOut} will start writing at
     * @param limit  the maximum number of bytes that may be written to the {@link DataOut}. If less than 0, the limit
     *               will not be enforced.
     * @return a {@link DataOut} that can write to this buffer
     */
    default DataOut outputStream(long offset, long limit) {
        return new DataOut() {
            private long l;

            @Override
            public void close() throws IOException {
            }

            @Override
            public void write(int b) throws IOException {
                if (limit >= 0L && this.l > limit) {
                    throw new BufferOverflowException();
                }
                PorkBuf.this.putByte(offset + this.l++, (byte) b);
            }
        };
    }

    /**
     * Gets a {@link DataIn} that can read from this buffer.
     *
     * @return a {@link DataIn} that can read from this buffer
     */
    default DataIn inputStream() {
        return this.inputStream(0L, -1L);
    }

    /**
     * Gets a {@link DataIn} that can read from this buffer.
     *
     * @param offset the index that the {@link DataIn} will start reading at
     * @return a {@link DataIn} that can read from this buffer
     */
    default DataIn inputStream(long offset) {
        return this.inputStream(offset, -1L);
    }

    /**
     * Gets a {@link DataIn} that can read from this buffer.
     *
     * @param offset the index that the {@link DataIn} will start reading at
     * @param limit  the maximum number of bytes that may be read from the {@link DataIn}. If less than 0, the limit
     *               will not be enforced.
     * @return a {@link DataIn} that can read from this buffer
     */
    default DataIn inputStream(long offset, long limit) {
        return new DataIn() {
            private long l;

            @Override
            public void close() throws IOException {
            }

            @Override
            public int read() throws IOException {
                if (limit >= 0L && this.l > limit) {
                    throw new BufferUnderflowException();
                }
                return PorkBuf.this.readByte(offset + this.l++) & 0xFF;
            }
        };
    }

    //mirroring methods

    /**
     * Gets this buffer as a byte array.
     * <p>
     * The byte array returned by this method will reflect changes to the buffer, and changes to the byte array will be reflected
     * in the buffer.
     * <p>
     * Optional implementation.
     *
     * @return this buffer's contents as a byte array
     */
    default byte[] getAsByteArray() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets an instance of {@link ByteBuf} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link ByteBuf} will change the content of this buffer, and changes to this buffer will affect
     * the {@link ByteBuf}.
     *
     * @return a mirror of a section of this buffer, stored in a {@link ByteBuf}
     */
    default ByteBuf netty() {
        long cap = this.capacity();
        return this.netty(0L, cap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) cap);
    }

    /**
     * Gets an instance of {@link ByteBuf} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link ByteBuf} will change the content of this buffer, and changes to this buffer will affect
     * the {@link ByteBuf}.
     *
     * @param offset the offset to begin the mirror at
     * @return a mirror of a section of this buffer, stored in a {@link ByteBuf}
     */
    default ByteBuf netty(long offset) {
        long cap = this.capacity() - offset;
        return this.netty(offset, cap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) cap);
    }

    /**
     * Gets an instance of {@link ByteBuf} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link ByteBuf} will change the content of this buffer, and changes to this buffer will affect
     * the {@link ByteBuf}.
     *
     * @param offset the offset to begin the mirror at
     * @param len    the size of the mirror.
     * @return a mirror of a section of this buffer, stored in a {@link ByteBuf}
     */
    default ByteBuf netty(long offset, int len) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets an instance of {@link ByteBuffer} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link ByteBuffer} will change the content of this buffer, and changes to this buffer will affect
     * the {@link ByteBuffer}.
     *
     * @return a mirror of a section of this buffer, stored in a {@link ByteBuffer}
     */
    default ByteBuffer nio() {
        long cap = this.capacity();
        return this.nio(0L, cap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) cap);
    }

    /**
     * Gets an instance of {@link ByteBuffer} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link ByteBuffer} will change the content of this buffer, and changes to this buffer will affect
     * the {@link ByteBuffer}.
     *
     * @param offset the offset to begin the mirror at
     * @return a mirror of a section of this buffer, stored in a {@link ByteBuffer}
     */
    default ByteBuffer nio(long offset) {
        long cap = this.capacity() - offset;
        return this.nio(offset, cap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) cap);
    }

    /**
     * Gets an instance of {@link ByteBuffer} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link ByteBuffer} will change the content of this buffer, and changes to this buffer will affect
     * the {@link ByteBuffer}.
     *
     * @param offset the offset to begin the mirror at
     * @param len    the size of the mirror.
     * @return a mirror of a section of this buffer, stored in a {@link ByteBuffer}
     */
    default ByteBuffer nio(long offset, int len) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets an instance of {@link PorkBuf} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link PorkBuf} will change the content of this buffer, and changes to this buffer will affect
     * the {@link PorkBuf}.
     *
     * @return a mirror of a section of this buffer, stored in a {@link PorkBuf}
     */
    default PorkBuf snippet() {
        long cap = this.capacity();
        return this.snippet(0L, cap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) cap);
    }

    /**
     * Gets an instance of {@link PorkBuf} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link PorkBuf} will change the content of this buffer, and changes to this buffer will affect
     * the {@link PorkBuf}.
     *
     * @param offset the offset to begin the mirror at
     * @return a mirror of a section of this buffer, stored in a {@link PorkBuf}
     */
    default PorkBuf snippet(long offset) {
        long cap = this.capacity() - offset;
        return this.snippet(offset, cap > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) cap);
    }

    /**
     * Gets an instance of {@link PorkBuf} that will mirror a section of this buffer.
     * <p>
     * Modifying the {@link PorkBuf} will change the content of this buffer, and changes to this buffer will affect
     * the {@link PorkBuf}.
     *
     * @param offset the offset to begin the mirror at
     * @param len    the size of the mirror.
     * @return a mirror of a section of this buffer, stored in a {@link PorkBuf}
     */
    default PorkBuf snippet(long offset, long len) {
        //TODO: optimize more (implement more methods)
        return new AbstractPorkBuf() {
            {
                this.setCapacity(len);
                this.setMaxCapacity(len);
            }

            @Override
            public PorkBuf putByte(byte b) {
                long l = this.writerIndex++;
                if (this.isInBounds(l, 1)) {
                    PorkBuf.this.putByte(offset + l, b);
                    return this;
                } else {
                    throw new BufferOverflowException();
                }
            }

            @Override
            public PorkBuf putByte(long index, byte b) {
                PorkBuf.this.putByte(offset + index, b);
                return this;
            }

            @Override
            public byte readByte() {
                long l = this.readerIndex++;
                if (this.isInBounds(l, 1)) {
                    return PorkBuf.this.readByte(offset + l);
                } else {
                    throw new BufferOverflowException();
                }
            }

            @Override
            public byte readByte(long index) {
                if (this.isInBounds(index, 1)) {
                    return PorkBuf.this.readByte(offset + index);
                } else {
                    throw new BufferOverflowException();
                }
            }

            @Override
            public long memoryAddress() {
                return PorkBuf.this.memoryAddress() + offset;
            }

            @Override
            public long memorySize() {
                return len;
            }

            @Override
            public Object refObj() {
                return PorkBuf.this.refObj();
            }
        };
    }

    //sanity checks

    /**
     * Checks if the given number of bytes can be written at/read from the current writer index without causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at/read from the current writer index without causing errors
     */
    default boolean isInBounds(int count) {
        return this.isInBounds(this.writerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be written at/read from the given position without causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param index the index to start writing at
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at/read from the given position without causing errors
     */
    default boolean isInBounds(long index, int count) {
        return index >= 0L && index + count < this.maxCapacity();
    }

    /**
     * Checks if the given number of bytes can be written at/read from the current writer index without causing errors.
     *
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at/read from the current writer index without causing errors
     */
    default boolean isInBounds(long count) {
        return this.isInBounds(this.writerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be written at/read from the given position without causing errors.
     *
     * @param index the index to start writing at
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at/read from the given position without causing errors
     */
    default boolean isInBounds(long index, long count) {
        return index >= 0L && index + count < this.maxCapacity();
    }
}
