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

package net.daporkchop.lib.minecraft.format.common.nibble;

import net.daporkchop.lib.common.misc.Cloneable;

import static net.daporkchop.lib.common.util.PValidation.checkIndex;

/**
 * A 16³ array of nibbles (4-bit integers).
 * <p>
 * Warning: this class is not reference-counted, and doesn't have safety checks to see if it was closed. Accessing a closed {@link NibbleArray} can cause
 * exceptions or even segfaults.
 *
 * @author DaPorkchop_
 */
public interface NibbleArray extends Cloneable<NibbleArray>, AutoCloseable {
    int MAX_INDEX = 16 * 16 * 16; //the size of a nibble array in bytes

    static void checkCoords(int x, int y, int z)    {
        checkIndex(x >= 0 && x < 16, "x");
        checkIndex(y >= 0 && y < 16, "y");
        checkIndex(z >= 0 && z < 16, "z");
    }

    static int extractNibble(int index, int value)  {
        //this adds some extra shifts, but look! no branches!
        //as a result this should (in theory) be quite a lot faster
        return (value >> ((index & 1) << 2)) & 0xF;
    }

    static int insertNibble(int index, int existing, int value)  {
        int shift = (index & 1) << 2;
        return (existing & ~(0xF << shift)) | (value << shift);
    }

    /**
     * Gets the nibble at the given coordinates.
     *
     * @param x the X coordinate of the nibble to get
     * @param y the Y coordinate of the nibble to get
     * @param z the Z coordinate of the nibble to get
     * @return the nibble at the given coordinates
     */
    int get(int x, int y, int z);

    /**
     * Gets the nibble at the given offset.
     * <p>
     * Note that this may use an unpredictable order depending on the implementation.
     *
     * @param offset the offset of the nibble to get
     * @return the nibble at the given offset
     */
    int get(int offset);

    /**
     * Sets the nibble at the given coordinates.
     *
     * @param x     the X coordinate of the nibble to set
     * @param y     the Y coordinate of the nibble to set
     * @param z     the Z coordinate of the nibble to set
     * @param value the new nibble value
     */
    void set(int x, int y, int z, int value);

    /**
     * Sets the nibble at the given offset.
     * <p>
     * Note that this may use an unpredictable order depending on the implementation.
     *
     * @param offset the offset of the nibble to set
     * @param value  the new nibble value
     */
    void set(int offset, int value);

    @Override
    NibbleArray clone();

    @Override
    void close();
}
