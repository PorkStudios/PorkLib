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

package net.daporkchop.lib.math.interpolation.linear;

import lombok.NonNull;
import net.daporkchop.lib.math.arrays.grid.Grid1d;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.math.arrays.grid.Grid3d;
import net.daporkchop.lib.math.interpolation.InterpolationEngine;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
public class LinearInterpolationEngine implements InterpolationEngine {
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
