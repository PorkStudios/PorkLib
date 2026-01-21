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
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.math.grid.Grid1d;
import net.daporkchop.lib.math.grid.Grid2d;
import net.daporkchop.lib.math.grid.Grid3d;

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * Linear interpolation.
 *
 * @author DaPorkchop_
 */
public final class LinearInterpolation extends AbstractInterpolation {
    /**
     * @return an instance of {@link LinearInterpolation}
     */
    public static LinearInterpolation instance() {
        return InstancePool.getInstance(LinearInterpolation.class);
    }

    @Override
    public int requiredRadius() {
        return 1;
    }

    @Override
    public double getInterpolated(double x, @NonNull Grid1d grid) {
        int xFloor = floorI(x);
        this.ensureInRange(xFloor, grid);

        double v1 = grid.getD(xFloor);
        double v2 = grid.getD(xFloor + 1);

        return v1 + (x - xFloor) * (v2 - v1);
    }

    @Override
    public double getInterpolated(double x, double y, @NonNull Grid2d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        this.ensureInRange(xFloor, yFloor, grid);

        double v1 = grid.getD(xFloor, yFloor);
        double v2 = grid.getD(xFloor + 1, yFloor);
        double v3 = grid.getD(xFloor, yFloor + 1);
        double v4 = grid.getD(xFloor + 1, yFloor + 1);

        double ix1 = v1 + (x - xFloor) * (v2 - v1);
        double ix2 = v3 + (x - xFloor) * (v4 - v3);

        return ix1 + (y - yFloor) * (ix2 - ix1);
        //this code doesn't look very fast, i'll just let JIT optimize it out :P
    }

    @Override
    public double getInterpolated(double x, double y, double z, @NonNull Grid3d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        int zFloor = floorI(z);
        this.ensureInRange(xFloor, yFloor, zFloor, grid);

        double v1 = grid.getD(xFloor, yFloor, zFloor);
        double v2 = grid.getD(xFloor + 1, yFloor, zFloor);
        double v3 = grid.getD(xFloor, yFloor + 1, zFloor);
        double v4 = grid.getD(xFloor + 1, yFloor + 1, zFloor);
        double v5 = grid.getD(xFloor, yFloor, zFloor + 1);
        double v6 = grid.getD(xFloor + 1, yFloor, zFloor + 1);
        double v7 = grid.getD(xFloor, yFloor + 1, zFloor + 1);
        double v8 = grid.getD(xFloor + 1, yFloor + 1, zFloor + 1);

        double ix1 = v1 + (x - xFloor) * (v2 - v1);
        double ix2 = v3 + (x - xFloor) * (v4 - v3);
        double ix3 = v5 + (x - xFloor) * (v6 - v5);
        double ix4 = v7 + (x - xFloor) * (v8 - v7);

        double iy1 = ix1 + (y - yFloor) * (ix2 - ix1);
        double iy2 = ix3 + (y - yFloor) * (ix4 - ix3);

        return iy1 + (z - zFloor) * (iy2 - iy1);
    }

    @Override
    public int getInterpolatedI(double x, @NonNull Grid1d grid) {
        int xFloor = floorI(x);
        this.ensureInRange(xFloor, grid);

        int v1 = grid.getI(xFloor);
        int v2 = grid.getI(xFloor + 1);

        return floorI(v1 + (x - xFloor) * (v2 - v1));
    }

    @Override
    public int getInterpolatedI(double x, double y, @NonNull Grid2d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        this.ensureInRange(xFloor, yFloor, grid);

        int v1 = grid.getI(xFloor, yFloor);
        int v2 = grid.getI(xFloor + 1, yFloor);
        int v3 = grid.getI(xFloor, yFloor + 1);
        int v4 = grid.getI(xFloor + 1, yFloor + 1);

        double ix1 = v1 + (x - xFloor) * (v2 - v1);
        double ix2 = v3 + (x - xFloor) * (v4 - v3);

        return floorI(ix1 + (y - yFloor) * (ix2 - ix1));
    }

    @Override
    public int getInterpolatedI(double x, double y, double z, @NonNull Grid3d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        int zFloor = floorI(z);
        this.ensureInRange(xFloor, yFloor, zFloor, grid);

        int v1 = grid.getI(xFloor, yFloor, zFloor);
        int v2 = grid.getI(xFloor + 1, yFloor, zFloor);
        int v3 = grid.getI(xFloor, yFloor + 1, zFloor);
        int v4 = grid.getI(xFloor + 1, yFloor + 1, zFloor);
        int v5 = grid.getI(xFloor, yFloor, zFloor + 1);
        int v6 = grid.getI(xFloor + 1, yFloor, zFloor + 1);
        int v7 = grid.getI(xFloor, yFloor + 1, zFloor + 1);
        int v8 = grid.getI(xFloor + 1, yFloor + 1, zFloor + 1);

        double ix1 = v1 + (x - xFloor) * (v2 - v1);
        double ix2 = v3 + (x - xFloor) * (v4 - v3);
        double ix3 = v5 + (x - xFloor) * (v6 - v5);
        double ix4 = v7 + (x - xFloor) * (v8 - v7);

        double iy1 = ix1 + (y - yFloor) * (ix2 - ix1);
        double iy2 = ix3 + (y - yFloor) * (ix4 - ix3);

        return floorI(iy1 + (z - zFloor) * (iy2 - iy1));
    }
}
