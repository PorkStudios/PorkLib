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

package net.daporkchop.lib.math.interpolation;

import lombok.NonNull;
import net.daporkchop.lib.math.grid.Grid1d;
import net.daporkchop.lib.math.grid.Grid2d;
import net.daporkchop.lib.math.grid.Grid3d;

/**
 * Base class for implementations of {@link Interpolation}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractInterpolation implements Interpolation {
    protected boolean isInRange(int x, @NonNull Grid1d grid)    {
        if (grid.isOverflowing())   {
            return true;
        } else {
            int radius = this.requiredRadius();

            if (x < grid.startX() + radius - 1 || x > grid.endX() - radius - 1) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected boolean isInRange(int x, int y, @NonNull Grid2d grid)    {
        if (grid.isOverflowing())   {
            return true;
        } else {
            int radius = this.requiredRadius();

            if (x < grid.startX() + radius - 1 || x > grid.endX() - radius - 1
                    || y < grid.startY() + radius - 1 || y > grid.endY() - radius - 1) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected boolean isInRange(int x, int y, int z, @NonNull Grid3d grid)    {
        if (grid.isOverflowing())   {
            return true;
        } else {
            int radius = this.requiredRadius();

            if (x < grid.startX() + radius - 1 || x > grid.endX() - radius - 1
                    || y < grid.startY() + radius - 1 || y > grid.endY() - radius - 1
                    || z < grid.startZ() + radius - 1 || z > grid.endZ() - radius - 1) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected void ensureInRange(int x, @NonNull Grid1d grid) {
        if (!this.isInRange(x, grid))   {
            throw new IndexOutOfBoundsException(String.format("Pos %d out of bounds required range %d-%d", x, grid.startX(), grid.endX()));
        }
    }

    protected void ensureInRange(int x, int y, @NonNull Grid2d grid)  {
        if (!this.isInRange(x, y, grid))    {
            throw new IndexOutOfBoundsException(String.format("Pos (%d,%d) out of bounds required range (%d,%d)-(%d,%d)", x, y, grid.startX(), grid.startY(), grid.endX(), grid.endY()));
        }
    }

    protected void ensureInRange(int x, int y, int z, @NonNull Grid3d grid)  {
        if (!this.isInRange(x, y, z, grid))    {
            throw new IndexOutOfBoundsException(String.format("Pos (%d,%d,%d) out of bounds required range (%d,%d,%d)-(%d,%d,%d)", x, y, z, grid.startX(), grid.startY(), grid.startZ(), grid.endX(), grid.endY(), grid.endZ()));
        }
    }
}
