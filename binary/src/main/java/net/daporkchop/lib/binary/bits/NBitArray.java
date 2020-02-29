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

package net.daporkchop.lib.binary.bits;

import lombok.Getter;
import lombok.NonNull;

/**
 * An array that stores integers using n bits per value.
 *
 * @author DaPorkchop_
 */
public final class NBitArray {
    private static int roundUp(int n, int to) {
        return (n + to - 1) / to * to;
    }

    /**
     * The actual bit data
     */
    @Getter
    private final long[] data;
    private final long mask;

    /**
     * The number of bits per entry
     */
    @Getter
    private final int bitsPer;

    public NBitArray(int size, int bitsPer) {
        this(new long[roundUp(size * bitsPer, 64) >> 6], bitsPer);
    }

    public NBitArray(@NonNull long[] data, int bitsPer) {
        this.data = data;
        this.mask = (1L << bitsPer) - 1;
        this.bitsPer = bitsPer;
    }

    /**
     * Gets a value
     *
     * @param index the index to get at
     * @return the value at the given index
     */
    public int get(int index) {
        int start = index * this.bitsPer;
        int firstPos = start >> 6;
        int endPos = ((index + 1) * this.bitsPer - 1) >> 6;
        int relPos = start & 0x3F;
        if (firstPos == endPos) {
            return (int) (this.data[firstPos] >>> relPos & this.mask);
        } else {
            int endBitSubIndex = 64 - relPos;
            return (int) ((this.data[firstPos] >>> relPos | this.data[endPos] << endBitSubIndex) & this.mask);
        }
    }

    /**
     * Sets a value
     *
     * @param index the index of the value to set
     * @param value the new value to set to
     */
    public void set(int index, int value) {
        int start = index * this.bitsPer;
        int firstPos = start >> 6;
        int endPos = ((index + 1) * this.bitsPer - 1) >> 6;
        int relPos = start & 0x3F;
        this.data[firstPos] = this.data[firstPos] & ~(this.mask << relPos) | ((long) value & this.mask) << relPos;
        if (firstPos != endPos) {
            int endBitSubIndex = 64 - relPos;
            this.data[endPos] = this.data[endPos] >>> endBitSubIndex << endBitSubIndex | ((long) value & this.mask) >> endBitSubIndex;
        }
    }
}
