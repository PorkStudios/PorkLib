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

package net.daporkchop.lib.binary.util.unsafe.block;

import lombok.NonNull;
import net.daporkchop.lib.binary.util.unsafe.Freeable;
import net.daporkchop.lib.binary.util.unsafe.offset.Offsettable;
import net.daporkchop.lib.common.util.PUnsafe;

/**
 * Allows direct access to a region of memory
 *
 * @author DaPorkchop_
 */
public interface MemoryBlock extends Freeable, Offsettable {
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
    long size();

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

    /**
     * Clears this block of memory, overwriting the entire contents with zeroes
     */
    default void clear() {
        if (this.isAbsolute()) {
            PUnsafe.setMemory(this.memoryOffset(), this.memoryLength(), (byte) 0);
        } else {
            PUnsafe.setMemory(this.refObj(), this.memoryOffset(), this.memoryLength(), (byte) 0);
        }
    }

    @Override
    default long memoryLength() {
        return this.size();
    }
}
