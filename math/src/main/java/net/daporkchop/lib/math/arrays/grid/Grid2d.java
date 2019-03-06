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

package net.daporkchop.lib.math.arrays.grid;

import lombok.NonNull;
import net.daporkchop.lib.math.arrays.grid.impl.direct.DirectIntGrid2d;
import net.daporkchop.lib.math.arrays.grid.impl.direct.DirectOverflowingIntGrid2d;
import net.daporkchop.lib.math.arrays.grid.impl.heap.HeapDoubleGrid2d;
import net.daporkchop.lib.math.arrays.grid.impl.heap.HeapIntGrid2d;

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
