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

package net.daporkchop.lib.unsafe.block;

import lombok.NonNull;
import net.daporkchop.lib.unsafe.capability.AccessibleDirectMemoryHolder;

/**
 * Allows direct access to a region of memory
 *
 * @author DaPorkchop_
 */
public interface MemoryBlock extends AccessibleDirectMemoryHolder {
    /**
     * Gets a new direct (off-heap) memory block
     *
     * @param size the size of the new memory block to allocate (in bytes)
     * @return a direct memory block
     */
    static MemoryBlock direct(long size) {
        return new DirectMemoryBlock(size);
    }

    /**
     * Wraps a byte[] into a memory block.
     * <p>
     * This could be useful for e.g. quick serialization of other primitives to raw bytes
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given byte[]
     */
    static MemoryBlock wrap(@NonNull byte[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Wraps a short[] into a memory block
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given short[]
     */
    static MemoryBlock wrap(@NonNull short[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Wraps an int[] into a memory block
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given int[]
     */
    static MemoryBlock wrap(@NonNull int[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Wraps a long[] into a memory block
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given long[]
     */
    static MemoryBlock wrap(@NonNull long[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Wraps a float[] into a memory block
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given float[]
     */
    static MemoryBlock wrap(@NonNull float[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Wraps a double[] into a memory block
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given double[]
     */
    static MemoryBlock wrap(@NonNull double[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Wraps a char[] into a memory block
     *
     * @param arr the array to wrap
     * @return a memory block wrapping the given char[]
     */
    static MemoryBlock wrap(@NonNull char[] arr) {
        return new ArrayMemoryBlock(arr);
    }

    /**
     * Gets the size of this memory block (in bytes).
     *
     * @return the size of this memory block (in bytes)
     */
    default long size() {
        return this.memorySize();
    }

    //read operations

    /**
     * Gets a single byte at a given index
     *
     * @param index the index (in bytes) of the value to get
     * @return the byte at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block
     */
    byte getByte(long index);

    /**
     * Gets a short at a given index
     *
     * @param index the index (in bytes) of the value to get. If you wish to use this memory block as a short array, keep
     *              in mind that the index will have to be multiplied by 2.
     * @return the short at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    short getShort(long index);

    /**
     * Gets an int at a given index
     *
     * @param index the index (in bytes) of the value to get. If you wish to use this memory block as an int array, keep
     *              in mind that the index will have to be multiplied by 4.
     * @return the int at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    int getInt(long index);

    /**
     * Gets a long at a given index
     *
     * @param index the index (in bytes) of the value to get. If you wish to use this memory block as a long array, keep
     *              in mind that the index will have to be multiplied by 8.
     * @return the long at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    long getLong(long index);

    /**
     * Gets a float at a given index
     *
     * @param index the index (in bytes) of the value to get. If you wish to use this memory block as a float array, keep
     *              in mind that the index will have to be multiplied by 4.
     * @return the float at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    float getFloat(long index);

    /**
     * Gets a double at a given index
     *
     * @param index the index (in bytes) of the value to get. If you wish to use this memory block as a double array, keep
     *              in mind that the index will have to be multiplied by 8.
     * @return the double at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    double getDouble(long index);

    /**
     * Gets a char at a given index
     *
     * @param index the index (in bytes) of the value to get. If you wish to use this memory block as a char array, keep
     *              in mind that the index will have to be multiplied by 2.
     * @return the char at that index
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    char getChar(long index);

    //bulk read operations

    /**
     * Copies the contents of this buffer into a given byte array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the byte array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getBytes(long index, @NonNull byte[] arr) {
        this.getBytes(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given byte array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the byte array to read into
     * @param off   the offset in the byte array to start copying
     * @param len   the total number of bytes to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getBytes(long index, @NonNull byte[] arr, int off, int len);

    /**
     * Copies the contents of this buffer into a given short array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the short array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getShorts(long index, @NonNull short[] arr) {
        this.getShorts(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given short array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the short array to read into
     * @param off   the offset in the short array to start copying
     * @param len   the total number of shorts to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getShorts(long index, @NonNull short[] arr, int off, int len);

    /**
     * Copies the contents of this buffer into a given int array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the int array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getInts(long index, @NonNull int[] arr) {
        this.getInts(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given int array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the int array to read into
     * @param off   the offset in the int array to start copying
     * @param len   the total number of ints to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getInts(long index, @NonNull int[] arr, int off, int len);

    /**
     * Copies the contents of this buffer into a given long array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the long array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getLongs(long index, @NonNull long[] arr) {
        this.getLongs(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given long array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the long array to read into
     * @param off   the offset in the long array to start copying
     * @param len   the total number of longs to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getLongs(long index, @NonNull long[] arr, int off, int len);

    /**
     * Copies the contents of this buffer into a given float array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the float array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getFloats(long index, @NonNull float[] arr) {
        this.getFloats(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given float array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the float array to read into
     * @param off   the offset in the float array to start copying
     * @param len   the total number of floats to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getFloats(long index, @NonNull float[] arr, int off, int len);

    /**
     * Copies the contents of this buffer into a given double array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the double array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getDoubles(long index, @NonNull double[] arr) {
        this.getDoubles(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given double array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the double array to read into
     * @param off   the offset in the double array to start copying
     * @param len   the total number of doubles to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getDoubles(long index, @NonNull double[] arr, int off, int len);

    /**
     * Copies the contents of this buffer into a given char array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the char array to read into
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    default void getChars(long index, @NonNull char[] arr) {
        this.getChars(index, arr, 0, arr.length);
    }

    /**
     * Copies the contents of this buffer into a given char array
     *
     * @param index the index (in bytes) to start reading from the buffer at
     * @param arr   the char array to read into
     * @param off   the offset in the char array to start copying
     * @param len   the total number of chars to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the read
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void getChars(long index, @NonNull char[] arr, int off, int len);

    //write operations

    /**
     * Sets a single byte at the given index
     *
     * @param index the index of the byte to set
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setByte(long index, byte val);

    /**
     * Sets a short at the given index
     *
     * @param index the index of the short to set. If you wish to use this memory block as a short array, keep
     *              in mind that the index will have to be multiplied by 2.
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setShort(long index, short val);

    /**
     * Sets an int at the given index
     *
     * @param index the index of the int to set. If you wish to use this memory block as an int array, keep
     *              in mind that the index will have to be multiplied by 4.
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setInt(long index, int val);

    /**
     * Sets a long at the given index
     *
     * @param index the index of the long to set. If you wish to use this memory block as a long array, keep
     *              in mind that the index will have to be multiplied by 8.
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setLong(long index, long val);

    /**
     * Sets a float at the given index
     *
     * @param index the index of the float to set. If you wish to use this memory block as a float array, keep
     *              in mind that the index will have to be multiplied by 4.
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setFloat(long index, float val);

    /**
     * Sets a double at the given index
     *
     * @param index the index of the double to set. If you wish to use this memory block as a double array, keep
     *              in mind that the index will have to be multiplied by 8.
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setDouble(long index, double val);

    /**
     * Sets a char at the given index
     *
     * @param index the index of the char to set. If you wish to use this memory block as a char array, keep
     *              in mind that the index will have to be multiplied by 2.
     * @param val   the new value
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setChar(long index, char val);

    //bulk write operations

    /**
     * Copies the given bytes to this memory block
     *
     * @param index the index (in bytes) in the block to start writing bytes to
     * @param arr   the array to copy bytes from
     */
    default void setBytes(long index, @NonNull byte[] arr) {
        this.setBytes(index, arr, 0, arr.length);
    }

    /**
     * Copies the given bytes to this memory block
     *
     * @param index the index (in bytes) in the block to start writing bytes to
     * @param arr   the array to copy bytes from
     * @param off   the offset in the byte array to start copying from
     * @param len   the number of bytes to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setBytes(long index, @NonNull byte[] arr, int off, int len);

    /**
     * Copies the given shorts to this memory block
     *
     * @param index the index (in bytes) in the block to start writing shorts to
     * @param arr   the array to copy shorts from
     */
    default void setShorts(long index, @NonNull short[] arr) {
        this.setShorts(index, arr, 0, arr.length);
    }

    /**
     * Copies the given shorts to this memory block
     *
     * @param index the index (in bytes) in the block to start writing shorts to
     * @param arr   the array to copy shorts from
     * @param off   the offset in the short array to start copying from
     * @param len   the number of shorts to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setShorts(long index, @NonNull short[] arr, int off, int len);

    /**
     * Copies the given ints to this memory block
     *
     * @param index the index (in bytes) in the block to start writing ints to
     * @param arr   the array to copy ints from
     */
    default void setInts(long index, @NonNull int[] arr) {
        this.setInts(index, arr, 0, arr.length);
    }

    /**
     * Copies the given ints to this memory block
     *
     * @param index the index (in bytes) in the block to start writing ints to
     * @param arr   the array to copy ints from
     * @param off   the offset in the int array to start copying from
     * @param len   the number of ints to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setInts(long index, @NonNull int[] arr, int off, int len);

    /**
     * Copies the given longs to this memory block
     *
     * @param index the index (in bytes) in the block to start writing longs to
     * @param arr   the array to copy longs from
     */
    default void setLongs(long index, @NonNull long[] arr) {
        this.setLongs(index, arr, 0, arr.length);
    }

    /**
     * Copies the given longs to this memory block
     *
     * @param index the index (in bytes) in the block to start writing longs to
     * @param arr   the array to copy longs from
     * @param off   the offset in the long array to start copying from
     * @param len   the number of longs to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setLongs(long index, @NonNull long[] arr, int off, int len);

    /**
     * Copies the given floats to this memory block
     *
     * @param index the index (in bytes) in the block to start writing floats to
     * @param arr   the array to copy floats from
     */
    default void setFloats(long index, @NonNull float[] arr) {
        this.setFloats(index, arr, 0, arr.length);
    }

    /**
     * Copies the given floats to this memory block
     *
     * @param index the index (in bytes) in the block to start writing floats to
     * @param arr   the array to copy floats from
     * @param off   the offset in the float array to start copying from
     * @param len   the number of floats to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setFloats(long index, @NonNull float[] arr, int off, int len);

    /**
     * Copies the given doubles to this memory block
     *
     * @param index the index (in bytes) in the block to start writing doubles to
     * @param arr   the array to copy doubles from
     */
    default void setDoubles(long index, @NonNull double[] arr) {
        this.setDoubles(index, arr, 0, arr.length);
    }

    /**
     * Copies the given doubles to this memory block
     *
     * @param index the index (in bytes) in the block to start writing doubles to
     * @param arr   the array to copy doubles from
     * @param off   the offset in the double array to start copying from
     * @param len   the number of doubles to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setDoubles(long index, @NonNull double[] arr, int off, int len);

    /**
     * Copies the given chars to this memory block
     *
     * @param index the index (in bytes) in the block to start writing chars to
     * @param arr   the array to copy chars from
     */
    default void setChars(long index, @NonNull char[] arr) {
        this.setChars(index, arr, 0, arr.length);
    }

    /**
     * Copies the given chars to this memory block
     *
     * @param index the index (in bytes) in the block to start writing chars to
     * @param arr   the array to copy chars from
     * @param off   the offset in the char array to start copying from
     * @param len   the number of chars to copy
     * @throws ArrayIndexOutOfBoundsException if the given index is larger than the size of this memory block, or the write
     *                                        operation would overflow beyond the limits of this memory block.
     */
    void setChars(long index, @NonNull char[] arr, int off, int len);

    //other things

    /**
     * Clears this block of memory, overwriting the entire contents with zeroes
     */
    void clear();
}
