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

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * An interpolation algorithm.
 *
 * @author DaPorkchop_
 */
public interface Interpolation {
    int requiredRadius();

    //single values
    double getInterpolated(double x, @NonNull Grid1d grid);

    double getInterpolated(double x, double y, @NonNull Grid2d grid);

    double getInterpolated(double x, double y, double z, @NonNull Grid3d grid);

    default int getInterpolatedI(double x, @NonNull Grid1d grid)    {
        return floorI(this.getInterpolated(x, grid));
    }

    default int getInterpolatedI(double x, double y, @NonNull Grid2d grid)  {
        return floorI(this.getInterpolated(x, y, grid));
    }

    default int getInterpolatedI(double x, double y, double z, @NonNull Grid3d grid)    {
        return floorI(this.getInterpolated(x, y, z, grid));
    }
}
