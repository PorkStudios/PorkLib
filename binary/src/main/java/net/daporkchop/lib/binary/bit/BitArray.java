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

package net.daporkchop.lib.binary.bit;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An array of ints, stored in a compact manner by using a fixed number of bits.
 *
 * @author DaPorkchop_
 */
public interface BitArray extends RefCounted {
    /**
     * @return the number of entries in the array
     */
    int size();

    /**
     * @return the number of bits used by a single value in the array
     */
    int bits();

    /**
     * Gets the value in the array at the given index.
     *
     * @param i the index of the value to get
     * @return the value at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    int get(int i);

    /**
     * Set the value in the array at the given index to the given value.
     *
     * @param i     the index of the value to set
     * @param value the new value
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     * @throws IllegalArgumentException  if the given value cannot fit
     */
    void set(int i, int value);

    /**
     * Replaces the value in the array at the given index with the given value.
     *
     * @param i     the index of the value to replace
     * @param value the new value
     * @return the old value
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     * @throws IllegalArgumentException  if the given value cannot fit
     */
    int replace(int i, int value);

    @Override
    int refCnt();

    @Override
    BitArray retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
