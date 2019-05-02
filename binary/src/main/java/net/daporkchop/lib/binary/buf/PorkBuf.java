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
import net.daporkchop.lib.binary.buf.exception.PorkBufCannotExpandException;
import net.daporkchop.lib.binary.buf.exception.PorkBufReadOutOfBoundsException;
import net.daporkchop.lib.binary.buf.exception.PorkBufWriteOutOfBoundsException;
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
//TODO: endianess
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
     * @throws PorkBufCannotExpandException if the buffer was unable to expand to the requested size
     */
    default PorkBuf requireBytes(long numBytes) throws PorkBufCannotExpandException {
        this.requireBytes(0L, numBytes);
        return this;
    }

    /**
     * Ensures that the buffer has at least a certain capacity, expanding if required.
     *
     * @param offset   the offset that bytes will be required at
     * @param numBytes the minimum number of bytes required
     * @return this buffer
     * @throws PorkBufCannotExpandException if the buffer was unable to expand to the requested size
     */
    default PorkBuf requireBytes(long offset, long numBytes) throws PorkBufCannotExpandException {
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
     * @throws PorkBufCannotExpandException if, for whatever reason, the buffer's maximum size could not be changed
     */
    default PorkBuf setMaxCapacity(long maxCapacity) throws PorkBufCannotExpandException {
        throw new PorkBufCannotExpandException();
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
     * @throws PorkBufWriteOutOfBoundsException if the given writer index is greater than the buffer's current capacity
     */
    PorkBuf writerIndex(long index) throws PorkBufWriteOutOfBoundsException;

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
     * @throws PorkBufReadOutOfBoundsException if the given reader index is greater than the buffer's current capacity
     */
    PorkBuf readerIndex(long index) throws PorkBufReadOutOfBoundsException;

    //write operations

    /**
     * Writes a single byte to the buffer at the current writer index, incrementing the writer index by 1
     *
     * @param b the byte to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    PorkBuf putByte(byte b) throws PorkBufWriteOutOfBoundsException;

    /**
     * Writes a single boolean to the buffer at the current writer index, incrementing the writer index by 1
     *
     * @param b the boolean to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBoolean(boolean b) throws PorkBufWriteOutOfBoundsException {
        return this.putByte(b ? (byte) 1 : 0);
    }

    /**
     * Writes a single short to the buffer at the current writer index, incrementing the writer index by 2
     *
     * @param s the short to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putShort(short s) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(2);
        return this.putByte((byte) (s & 0xFF))
                .putByte((byte) ((s >>> 8) & 0xFF));
    }

    /**
     * Writes a single medium to the buffer at the current writer index, incrementing the writer index by 3
     *
     * @param i the medium to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putMedium(int i) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(3);
        return this.putByte((byte) (i & 0xFF))
                .putByte((byte) ((i >>> 8) & 0xFF))
                .putByte((byte) ((i >>> 16) & 0xFF));
    }

    /**
     * Writes a single int to the buffer at the current writer index, incrementing the writer index by 4
     *
     * @param i the int to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putInt(int i) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(4);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putLong(long l) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(8);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putFloat(float f) throws PorkBufWriteOutOfBoundsException {
        return this.putInt(Float.floatToRawIntBits(f));
    }

    /**
     * Writes a single double to the buffer at the current writer index, incrementing the writer index by 8
     *
     * @param d the double to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putDouble(double d) throws PorkBufWriteOutOfBoundsException {
        return this.putLong(Double.doubleToRawLongBits(d));
    }

    /**
     * Writes a byte array to the buffer at the current writer index, incrementing the writer index by the number of bytes
     * in the array
     *
     * @param arr the byte array to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(@NonNull byte[] arr) throws PorkBufWriteOutOfBoundsException {
        this.ensureWriteInBounds(arr.length);
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
     * @throws ArrayIndexOutOfBoundsException   if the given offset and/or length are invalid
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(@NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufWriteOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.ensureWriteInBounds(len);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(@NonNull ByteBuf buf) throws PorkBufWriteOutOfBoundsException {
        int i = buf.readableBytes();
        this.ensureWriteInBounds(i);
        for (i--; i >= 0; i--) {
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(@NonNull ByteBuffer buf) throws PorkBufWriteOutOfBoundsException {
        int i = buf.remaining();
        this.ensureWriteInBounds(i);
        for (i--; i >= 0; i--) {
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    PorkBuf putByte(long index, byte b) throws PorkBufWriteOutOfBoundsException;

    /**
     * Writes a single boolean to the buffer at the given index
     *
     * @param index the index to write at
     * @param b     the boolean to be written
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBoolean(long index, boolean b) throws PorkBufWriteOutOfBoundsException {
        return this.putByte(index, b ? (byte) 1 : 0);
    }

    /**
     * Writes a single short to the buffer at the given index
     *
     * @param index the index to write at
     * @param s     the short to be written
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putShort(long index, short s) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 2, false);
        return this.putByte(index, (byte) (s & 0xFF))
                .putByte(index + 1L, (byte) ((s >>> 8) & 0xFF));
    }

    /**
     * Writes a single medium to the buffer at the given index
     *
     * @param index the index to write at
     * @param i     the medium to be written
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putMedium(long index, int i) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 3, false);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putInt(long index, int i) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 4, false);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putLong(long index, long l) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, 8, false);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putFloat(long index, float f) throws PorkBufWriteOutOfBoundsException {
        return this.putInt(index, Float.floatToRawIntBits(f));
    }

    /**
     * Writes a single double to the buffer at the given index
     *
     * @param index the index to write at
     * @param d     the double to be written
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putDouble(long index, double d) throws PorkBufWriteOutOfBoundsException {
        return this.putLong(index, Double.doubleToRawLongBits(d));
    }

    /**
     * Writes a byte array to the buffer at the given index
     *
     * @param index the index to write at
     * @param arr   the byte array to write
     * @return this buffer
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(long index, @NonNull byte[] arr) throws PorkBufWriteOutOfBoundsException {
        this.ensureInBounds(index, arr.length, false);
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
     * @throws ArrayIndexOutOfBoundsException   if the given offset and/or length are invalid
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(long index, @NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufWriteOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.ensureInBounds(index, len, false);
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(long index, @NonNull ByteBuf buf) throws PorkBufWriteOutOfBoundsException {
        int i = buf.readableBytes();
        this.ensureInBounds(index, i, false);
        for (i--; i >= 0; i--) {
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
     * @throws PorkBufWriteOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default PorkBuf putBytes(long index, @NonNull ByteBuffer buf) throws PorkBufWriteOutOfBoundsException {
        int i = buf.remaining();
        this.ensureInBounds(index, i, false);
        for (i--; i >= 0; i--) {
            this.putByte(index++, buf.get());
        }
        return this;
    }

    //read operations

    /**
     * Reads a byte from the buffer at the current reader index
     *
     * @return a byte
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    byte getByte() throws PorkBufReadOutOfBoundsException;

    /**
     * Reads a boolean from the buffer at the current reader index
     *
     * @return a boolean
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default boolean getBoolean() throws PorkBufReadOutOfBoundsException {
        return this.getByte() != 0;
    }

    /**
     * Reads a short from the buffer at the current reader index
     *
     * @return a short
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default short getShort() throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(2);
        return (short) ((this.getByte() & 0xFF)
                | ((this.getByte() & 0xFF) << 8));
    }

    /**
     * Reads a medium from the buffer at the current reader index
     *
     * @return a medium
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default int getMedium() throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(3);
        return (this.getByte() & 0xFF)
                | ((this.getByte() & 0xFF) << 8)
                | ((this.getByte() & 0xFF) << 16);
    }

    /**
     * Reads an int from the buffer at the current reader index
     *
     * @return an int
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default int getInt() throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(4);
        return (this.getByte() & 0xFF)
                | ((this.getByte() & 0xFF) << 8)
                | ((this.getByte() & 0xFF) << 16)
                | ((this.getByte() & 0xFF) << 24);
    }

    /**
     * Reads a long from the buffer at the current reader index
     *
     * @return a long
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default long getLong() throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(8);
        return (this.getByte() & 0xFFL)
                | ((this.getByte() & 0xFFL) << 8L)
                | ((this.getByte() & 0xFFL) << 16L)
                | ((this.getByte() & 0xFFL) << 24L)
                | ((this.getByte() & 0xFFL) << 32L)
                | ((this.getByte() & 0xFFL) << 40L)
                | ((this.getByte() & 0xFFL) << 48L)
                | ((this.getByte() & 0xFFL) << 56L);
    }

    /**
     * Reads a float from the buffer at the current reader index
     *
     * @return a float
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default float getFloat() throws PorkBufReadOutOfBoundsException {
        return Float.intBitsToFloat(this.getInt());
    }

    /**
     * Reads a double from the buffer at the current reader index
     *
     * @return a double
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default double getDouble() throws PorkBufReadOutOfBoundsException {
        return Double.longBitsToDouble(this.getLong());
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(@NonNull byte[] arr) throws PorkBufReadOutOfBoundsException {
        this.ensureReadInBounds(arr.length);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = this.getByte();
        }
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     * @param off the offset in the byte array to start putting bytes in
     * @param len the number of bytes to read
     * @throws ArrayIndexOutOfBoundsException  if the given offset and/or length are invalid
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(@NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufReadOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.ensureReadInBounds(len);
        for (int i = 0; i < len; i++) {
            arr[off + i] = this.getByte();
        }
    }

    /**
     * Fills a {@link ByteBuf} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuf} to fill
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(@NonNull ByteBuf buf) throws PorkBufReadOutOfBoundsException {
        int i = buf.writableBytes();
        this.ensureReadInBounds(i);
        for (i--; i >= 0; i--) {
            buf.writeByte(this.getByte() & 0xFF);
        }
    }

    /**
     * Fills a {@link ByteBuffer} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuffer} to fill
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(@NonNull ByteBuffer buf) throws PorkBufReadOutOfBoundsException {
        int i = buf.remaining();
        this.ensureReadInBounds(i);
        for (i--; i >= 0; i--) {
            buf.put(this.getByte());
        }
    }

    /**
     * Reads a byte from the buffer at the given index
     *
     * @param index the index to read at
     * @return a byte
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    byte getByte(long index) throws PorkBufReadOutOfBoundsException;

    /**
     * Reads a boolean from the buffer at the given index
     *
     * @param index the index to read at
     * @return a boolean
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default boolean getBoolean(long index) throws PorkBufReadOutOfBoundsException {
        return this.getByte(index) != 0;
    }

    /**
     * Reads a short from the buffer at the given index
     *
     * @param index the index to read at
     * @return a short
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default short getShort(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 2, true);
        return (short) ((this.getByte(index) & 0xFF)
                | ((this.getByte(index + 1L) & 0xFF) << 8));
    }

    /**
     * Reads a medium from the buffer at the given index
     *
     * @param index the index to read at
     * @return a medium
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default int getMedium(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 3, true);
        return (this.getByte(index) & 0xFF)
                | ((this.getByte(index + 1L) & 0xFF) << 8)
                | ((this.getByte(index + 2L) & 0xFF) << 16);
    }

    /**
     * Reads an int from the buffer at the given index
     *
     * @param index the index to read at
     * @return an int
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default int getInt(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 4, true);
        return (this.getByte(index) & 0xFF)
                | ((this.getByte(index + 1L) & 0xFF) << 8)
                | ((this.getByte(index + 2L) & 0xFF) << 16)
                | ((this.getByte(index + 3L) & 0xFF) << 24);
    }

    /**
     * Reads a long from the buffer at the given index
     *
     * @param index the index to read at
     * @return a long
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default long getLong(long index) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, 8, true);
        return (this.getByte(index) & 0xFFL)
                | ((this.getByte(index + 1L) & 0xFFL) << 8L)
                | ((this.getByte(index + 2L) & 0xFFL) << 16L)
                | ((this.getByte(index + 3L) & 0xFFL) << 24L)
                | ((this.getByte(index + 4L) & 0xFFL) << 32L)
                | ((this.getByte(index + 5L) & 0xFFL) << 40L)
                | ((this.getByte(index + 6L) & 0xFFL) << 48L)
                | ((this.getByte(index + 7L) & 0xFFL) << 56L);
    }

    /**
     * Reads a float from the buffer at the given index
     *
     * @param index the index to read at
     * @return a float
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default float getFloat(long index) throws PorkBufReadOutOfBoundsException {
        return Float.intBitsToFloat(this.getInt(index));
    }

    /**
     * Reads a double from the buffer at the given index
     *
     * @param index the index to read at
     * @return a double
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default double getDouble(long index) throws PorkBufReadOutOfBoundsException {
        return Double.longBitsToDouble(this.getLong(index));
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(long index, @NonNull byte[] arr) throws PorkBufReadOutOfBoundsException {
        this.ensureInBounds(index, arr.length, true);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = this.getByte(index++);
        }
    }

    /**
     * Fills a byte array with data, starting at the current reader index
     *
     * @param arr the byte array to fill
     * @param off the offset in the byte array to start putting bytes in
     * @param len the number of bytes to read
     * @throws ArrayIndexOutOfBoundsException  if the given offset and/or length are invalid
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(long index, @NonNull byte[] arr, int off, int len) throws ArrayIndexOutOfBoundsException, PorkBufReadOutOfBoundsException {
        if (off + len >= arr.length || off < 0 || len < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.ensureInBounds(index, len, true);
        for (int i = 0; i < len; i++) {
            arr[off + i] = this.getByte(index++);
        }
    }

    /**
     * Fills a {@link ByteBuf} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuf} to fill
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(long index, @NonNull ByteBuf buf) throws PorkBufReadOutOfBoundsException {
        int i = buf.writableBytes();
        this.ensureInBounds(index, i, true);
        for (i--; i >= 0; i--) {
            buf.writeByte(this.getByte(index++) & 0xFF);
        }
    }

    /**
     * Fills a {@link ByteBuffer} with data, starting at the current reader index
     *
     * @param buf the {@link ByteBuffer} to fill
     * @throws PorkBufReadOutOfBoundsException if the capacity limit (see {@link #maxCapacity()}) is exceeded
     */
    default void getBytes(long index, @NonNull ByteBuffer buf) throws PorkBufReadOutOfBoundsException {
        int i = buf.remaining();
        this.ensureInBounds(index, i, true);
        for (i--; i >= 0; i--) {
            buf.put(this.getByte(index++));
        }
    }

    //stream methods

    /**
     * Gets a {@link DataOut} that can write to this buffer.
     *
     * @return a {@link DataOut} that can write to this buffer
     */
    default DataOut outputStream() {
        return this.outputStream(0L, this.maxCapacity());
    }

    /**
     * Gets a {@link DataOut} that can write to this buffer.
     *
     * @param offset the index that the {@link DataOut} will start writing at
     * @return a {@link DataOut} that can write to this buffer
     */
    default DataOut outputStream(long offset) {
        return this.outputStream(offset, this.maxCapacity() - offset);
    }

    /**
     * Gets a {@link DataOut} that can write to this buffer.
     *
     * @param offset the index that the {@link DataOut} will start writing at
     * @param limit  the maximum writer index within this buffer at which the stream may write
     * @return a {@link DataOut} that can write to this buffer
     */
    default DataOut outputStream(long offset, long limit) {
        this.ensureInBounds(offset, limit - offset, false);
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
        return this.inputStream(0L, this.maxCapacity());
    }

    /**
     * Gets a {@link DataIn} that can read from this buffer.
     *
     * @param offset the index that the {@link DataIn} will start reading at
     * @return a {@link DataIn} that can read from this buffer
     */
    default DataIn inputStream(long offset) {
        return this.inputStream(offset, this.maxCapacity() - offset);
    }

    /**
     * Gets a {@link DataIn} that can read from this buffer.
     *
     * @param offset the index that the {@link DataIn} will start reading at
     * @param limit  the maximum reader index within this buffer at which the stream may read
     * @return a {@link DataIn} that can read from this buffer
     */
    default DataIn inputStream(long offset, long limit) {
        this.ensureInBounds(offset, limit - offset, true);
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
                return PorkBuf.this.getByte(offset + this.l++) & 0xFF;
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
        return new SnippetImpl(offset, len, this);
    }

    //sanity checks

    /**
     * Checks if the given number of bytes can be written at the current writer index without
     * causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at the current writer index without causing errors
     */
    default boolean isWriteInBounds(int count) {
        return this.isInBounds(this.writerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be written at the current writer index without
     * causing errors.
     * <p>
     * If not, throws an exception.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param count the number of bytes to write
     */
    default void ensureWriteInBounds(int count) {
        if (!this.isWriteInBounds(count)) {
            throw new PorkBufWriteOutOfBoundsException(String.format("writerIndex(%d) + count(%d) >= maxCapacity(%d)", this.writerIndex(), count, this.maxCapacity()));
        }
    }

    /**
     * Checks if the given number of bytes can be read from the current reader index without
     * causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param count the number of bytes to read
     * @return whether or not the given number of bytes can be read from the current reader index without causing errors
     */
    default boolean isReadInBounds(int count) {
        return this.isInBounds(this.readerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be read from the current reader index without
     * causing errors.
     * <p>
     * If not, throws an exception.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param count the number of bytes to read
     */
    default void ensureReadInBounds(int count) {
        if (!this.isReadInBounds(count)) {
            throw new PorkBufReadOutOfBoundsException(String.format("readerIndex(%d) + count(%d) >= maxCapacity(%d)", this.readerIndex(), count, this.maxCapacity()));
        }
    }

    /**
     * Checks if the given number of bytes can be written at/read from the given position without
     * causing errors.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param index the index to start writing at
     * @param count the number of bytes to read/write
     * @return whether or not the given number of bytes can be written at/read from the given position without causing errors
     */
    default boolean isInBounds(long index, int count) {
        return index >= 0L && index + count < this.maxCapacity();
    }

    /**
     * Checks if the given number of bytes can be written at/read from the given position without
     * causing errors.
     * <p>
     * If not, throws an exception.
     * <p>
     * 32-bit method for speed. Why? Because microoptimization.
     *
     * @param index the index to start writing at
     * @param count the number of bytes to read/write
     */
    default void ensureInBounds(long index, int count, boolean read) {
        if (!this.isInBounds(index, count)) {
            String msg = String.format("index(%d) + count(%d) >= maxCapacity(%d)", index, count, this.maxCapacity());
            throw read ? new PorkBufReadOutOfBoundsException(msg) : new PorkBufWriteOutOfBoundsException(msg);
        }
    }

    /**
     * Checks if the given number of bytes can be written at the current writer index without
     * causing errors.
     *
     * @param count the number of bytes to write
     * @return whether or not the given number of bytes can be written at the current writer index without causing errors
     */
    default boolean isWriteInBounds(long count) {
        return this.isInBounds(this.writerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be written at the current writer index without
     * causing errors.
     * <p>
     * If not, throws an exception.
     *
     * @param count the number of bytes to write
     */
    default void ensureWriteInBounds(long count) {
        if (!this.isWriteInBounds(count)) {
            throw new PorkBufWriteOutOfBoundsException(String.format("writerIndex(%d) + count(%d) >= maxCapacity(%d)", this.writerIndex(), count, this.maxCapacity()));
        }
    }

    /**
     * Checks if the given number of bytes can be read from the current reader index without
     * causing errors.
     *
     * @param count the number of bytes to read
     * @return whether or not the given number of bytes can be read from the current reader index without causing errors
     */
    default boolean isReadInBounds(long count) {
        return this.isInBounds(this.readerIndex(), count);
    }

    /**
     * Checks if the given number of bytes can be read from the current reader index without
     * causing errors.
     * <p>
     * If not, throws an exception.
     *
     * @param count the number of bytes to read
     */
    default void ensureReadInBounds(long count) {
        if (!this.isReadInBounds(count)) {
            throw new PorkBufReadOutOfBoundsException(String.format("readerIndex(%d) + count(%d) >= maxCapacity(%d)", this.readerIndex(), count, this.maxCapacity()));
        }
    }

    /**
     * Checks if the given number of bytes can be written at/read from the given position without causing errors.
     *
     * @param index the index to start writing at
     * @param count the number of bytes to read/write
     * @return whether or not the given number of bytes can be written at/read from the given position without causing errors
     */
    default boolean isInBounds(long index, long count) {
        return index >= 0L && index + count < this.maxCapacity();
    }

    /**
     * Checks if the given number of bytes can be written at/read from the given position without
     * causing errors.
     * <p>
     * If not, throws an exception.
     *
     * @param index the index to start writing at
     * @param count the number of bytes to read/write
     */
    default void ensureInBounds(long index, long count, boolean read) {
        if (!this.isInBounds(index, count)) {
            String msg = String.format("index(%d) + count(%d) >= maxCapacity(%d)", index, count, this.maxCapacity());
            throw read ? new PorkBufReadOutOfBoundsException(msg) : new PorkBufWriteOutOfBoundsException(msg);
        }
    }
}
