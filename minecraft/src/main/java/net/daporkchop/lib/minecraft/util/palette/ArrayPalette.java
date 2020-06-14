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

package net.daporkchop.lib.minecraft.util.palette;

import lombok.NonNull;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link Palette} backed by a fixed-size array. Array searching is done using brute-force.
 *
 * @author DaPorkchop_
 */
public class ArrayPalette implements Palette {
    protected IntBinaryOperator resizeCallback;
    protected final int[] values;
    protected final int bits;
    protected int size;

    public ArrayPalette(int bits) {
        Arrays.fill(this.values = new int[1 << positive(bits, "bits")], -1);
        this.bits = bits;
    }

    public ArrayPalette(@NonNull IntBinaryOperator resizeCallback, int bits) {
        this(bits);
        this.resizeCallback = resizeCallback;
    }

    @Override
    public boolean contains(int value) {
        int[] values = this.values;
        for (int i = 0, size = this.size; i < size; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int get(int value) {
        int[] values = this.values;
        int size = this.size;
        for (int i = 0; i < size; i++) {
            if (values[i] == value) {
                return i;
            }
        }

        if (size < values.length) {
            values[size] = value;
            this.size = size + 1;
            return size;
        } else {
            return this.resizeCallback.applyAsInt(this.bits + 1, value);
        }
    }

    @Override
    public int getReverse(int id) {
        return id >= 0 && id < this.size ? this.values[id] : -1;
    }

    @Override
    public Palette setResizeCallback(@NonNull IntBinaryOperator resizeCallback) {
        checkState(this.resizeCallback == null, "resizeCallback was already set!");
        this.resizeCallback = resizeCallback;
        return this;
    }
}
