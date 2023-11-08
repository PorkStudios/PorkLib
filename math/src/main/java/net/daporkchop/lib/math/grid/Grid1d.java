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
import net.daporkchop.lib.math.grid.impl.direct.DirectIntGrid1d;
import net.daporkchop.lib.math.grid.impl.direct.DirectOverflowingIntGrid1d;
import net.daporkchop.lib.math.grid.impl.heap.HeapDoubleGrid1d;
import net.daporkchop.lib.math.grid.impl.heap.HeapIntGrid1d;

/**
 * @author DaPorkchop_
 */
public interface Grid1d {
    static Grid1d of(@NonNull int[] arr)    {
        return of(arr, 0);
    }

    static Grid1d of(@NonNull int[] arr, int startX)    {
        return new HeapIntGrid1d(arr, startX);
    }

    static Grid1d of(@NonNull double[] arr)    {
        return of(arr, 0);
    }

    static Grid1d of(@NonNull double[] arr, int startX)    {
        return new HeapDoubleGrid1d(arr, startX);
    }

    static Grid1d of(int width) {
        return of(0, width, false);
    }

    static Grid1d of(int width, boolean overflowing) {
        return of(0, width, overflowing);
    }

    static Grid1d of(int startX, int width) {
        return of(startX, width, false);
    }

    static Grid1d of(int startX, int width, boolean overflowing) {
        return overflowing ? new DirectOverflowingIntGrid1d(startX, width) : new DirectIntGrid1d(startX, width);
    }

    int startX();
    int endX();
    default boolean isOverflowing() {
        return false;
    }

    //getters
    double getD(int x);
    
    int getI(int x);

    //setters
    void setD(int x, double val);

    void setI(int x, int val);
}
