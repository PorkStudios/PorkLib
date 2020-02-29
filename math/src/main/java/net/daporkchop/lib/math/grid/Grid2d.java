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

package net.daporkchop.lib.math.grid;

import lombok.NonNull;
import net.daporkchop.lib.math.grid.impl.direct.DirectIntGrid2d;
import net.daporkchop.lib.math.grid.impl.direct.DirectOverflowingIntGrid2d;
import net.daporkchop.lib.math.grid.impl.heap.HeapDoubleGrid2d;
import net.daporkchop.lib.math.grid.impl.heap.HeapIntGrid2d;

/**
 * @author DaPorkchop_
 */
public interface Grid2d extends Grid1d {
    static Grid2d of(@NonNull int[] arr, int width, int height) {
        return of(arr, 0, 0, width, height);
    }

    static Grid2d of(@NonNull int[] arr, int startX, int startY, int width, int height)    {
        if (width * height > arr.length)    {
            throw new IllegalArgumentException(String.format("Array length %d too short to be used for grid of %dx%d!", arr.length, width, height));
        } else {
            return new HeapIntGrid2d(arr, startX, startY, width, height);
        }
    }

    static Grid2d of(@NonNull double[] arr, int width, int height) {
        return of(arr, 0, 0, width, height);
    }

    static Grid2d of(@NonNull double[] arr, int startX, int startY, int width, int height)    {
        if (width * height > arr.length)    {
            throw new IllegalArgumentException(String.format("Array length %d too short to be used for grid of %dx%d!", arr.length, width, height));
        } else {
            return new HeapDoubleGrid2d(arr, startX, startY, width, height);
        }
    }

    static Grid2d of(int width, int height) {
        return of(0, 0, width, height, false);
    }

    static Grid2d of(int width, int height, boolean overflowing) {
        return of(0, 0, width, height, overflowing);
    }

    static Grid2d of(int startX, int startY, int width, int height) {
        return of(startX, startY, width, height, false);
    }

    static Grid2d of(int startX, int startY, int width, int height, boolean overflowing) {
        return overflowing ? new DirectOverflowingIntGrid2d(startX, startY, width, height) : new DirectIntGrid2d(startX, startY, width, height);
    }

    int startY();
    int endY();

    //getters
    double getD(int x, int y);

    int getI(int x, int y);

    @Override
    default double getD(int x) {
        return this.getD(x, 0);
    }

    @Override
    default int getI(int x) {
        return this.getI(x, 0);
    }

    //setters
    void setD(int x, int y, double val);

    void setI(int x, int y, int val);

    @Override
    default void setD(int x, double val) {
        this.setD(x, 0, val);
    }

    @Override
    default void setI(int x, int val) {
        this.setI(x, 0, val);
    }
}
