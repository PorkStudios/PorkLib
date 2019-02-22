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

package net.daporkchop.lib.math.interpolation.cubic;

import lombok.NonNull;
import net.daporkchop.lib.math.arrays.grid.Grid1d;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.math.arrays.grid.Grid3d;
import net.daporkchop.lib.math.interpolation.InterpolationEngine;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
public class CubicInterpolator implements InterpolationEngine {
    @Override
    public int requiredRadius() {
        return 2;
    }

    @Override
    public double getInterpolated(double x, @NonNull Grid1d grid) {
        int xFloor = floorI(x);
        this.ensureInRange(xFloor, grid);

        x -= xFloor;

        double v1 = grid.getD(xFloor - 1);
        double v2 = grid.getD(xFloor);
        double v3 = grid.getD(xFloor + 1);
        double v4 = grid.getD(xFloor + 2);

        return v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));
        //return p[1] + 0.5 * x*(p[2] - p[0] + x*(2.0*p[0] - 5.0*p[1] + 4.0*p[2] - p[3] + x*(3.0*(p[1] - p[2]) + p[3] - p[0])));
    }

    @Override
    public double getInterpolated(double x, double y, @NonNull Grid2d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        this.ensureInRange(xFloor, yFloor, grid);

        x -= xFloor;
        y -= yFloor;

        double v1 = grid.getD(xFloor - 1, yFloor - 1);
        double v2 = grid.getD(xFloor, yFloor - 1);
        double v3 = grid.getD(xFloor + 1, yFloor - 1);
        double v4 = grid.getD(xFloor + 2, yFloor - 1);
        double x1 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor);
        v2 = grid.getD(xFloor, yFloor);
        v3 = grid.getD(xFloor + 1, yFloor);
        v4 = grid.getD(xFloor + 2, yFloor);
        double x2 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 1);
        v2 = grid.getD(xFloor, yFloor + 1);
        v3 = grid.getD(xFloor + 1, yFloor + 1);
        v4 = grid.getD(xFloor + 2, yFloor + 1);
        double x3 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 2);
        v2 = grid.getD(xFloor, yFloor + 2);
        v3 = grid.getD(xFloor + 1, yFloor + 2);
        v4 = grid.getD(xFloor + 2, yFloor + 2);
        double x4 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        return x2 + 0.5d * y * (x3 - x1 + y * (2.0d * x1 - 5.0d * x2 + 4.0d * x3 - x4 + y * (3.0d * (x2 - x3) + x4 - x1)));
    }

    @Override
    public double getInterpolated(double x, double y, double z, @NonNull Grid3d grid) {
        int xFloor = floorI(x);
        int yFloor = floorI(y);
        int zFloor = floorI(z);
        this.ensureInRange(xFloor, yFloor, zFloor, grid);

        x -= xFloor;
        y -= yFloor;
        z -= zFloor;

        double v1 = grid.getD(xFloor - 1, yFloor - 1, zFloor - 1);
        double v2 = grid.getD(xFloor, yFloor - 1, zFloor - 1);
        double v3 = grid.getD(xFloor + 1, yFloor - 1, zFloor - 1);
        double v4 = grid.getD(xFloor + 2, yFloor - 1, zFloor - 1);
        double x1 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor, zFloor - 1);
        v2 = grid.getD(xFloor, yFloor, zFloor - 1);
        v3 = grid.getD(xFloor + 1, yFloor, zFloor - 1);
        v4 = grid.getD(xFloor + 2, yFloor, zFloor - 1);
        double x2 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 1, zFloor - 1);
        v2 = grid.getD(xFloor, yFloor + 1, zFloor - 1);
        v3 = grid.getD(xFloor + 1, yFloor + 1, zFloor - 1);
        v4 = grid.getD(xFloor + 2, yFloor + 1, zFloor - 1);
        double x3 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 2, zFloor - 1);
        v2 = grid.getD(xFloor, yFloor + 2, zFloor - 1);
        v3 = grid.getD(xFloor + 1, yFloor + 2, zFloor - 1);
        v4 = grid.getD(xFloor + 2, yFloor + 2, zFloor - 1);
        double x4 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        double y1 = x2 + 0.5d * y * (x3 - x1 + y * (2.0d * x1 - 5.0d * x2 + 4.0d * x3 - x4 + y * (3.0d * (x2 - x3) + x4 - x1)));

        v1 = grid.getD(xFloor - 1, yFloor - 1, zFloor);
        v2 = grid.getD(xFloor, yFloor - 1, zFloor);
        v3 = grid.getD(xFloor + 1, yFloor - 1, zFloor);
        v4 = grid.getD(xFloor + 2, yFloor - 1, zFloor);
        x1 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor, zFloor);
        v2 = grid.getD(xFloor, yFloor, zFloor);
        v3 = grid.getD(xFloor + 1, yFloor, zFloor);
        v4 = grid.getD(xFloor + 2, yFloor, zFloor);
        x2 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 1, zFloor);
        v2 = grid.getD(xFloor, yFloor + 1, zFloor);
        v3 = grid.getD(xFloor + 1, yFloor + 1, zFloor);
        v4 = grid.getD(xFloor + 2, yFloor + 1, zFloor);
        x3 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 2, zFloor);
        v2 = grid.getD(xFloor, yFloor + 2, zFloor);
        v3 = grid.getD(xFloor + 1, yFloor + 2, zFloor);
        v4 = grid.getD(xFloor + 2, yFloor + 2, zFloor);
        x4 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        double y2 = x2 + 0.5d * y * (x3 - x1 + y * (2.0d * x1 - 5.0d * x2 + 4.0d * x3 - x4 + y * (3.0d * (x2 - x3) + x4 - x1)));

        v1 = grid.getD(xFloor - 1, yFloor - 1, zFloor + 1);
        v2 = grid.getD(xFloor, yFloor - 1, zFloor + 1);
        v3 = grid.getD(xFloor + 1, yFloor - 1, zFloor + 1);
        v4 = grid.getD(xFloor + 2, yFloor - 1, zFloor + 1);
        x1 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor, zFloor + 1);
        v2 = grid.getD(xFloor, yFloor, zFloor + 1);
        v3 = grid.getD(xFloor + 1, yFloor, zFloor + 1);
        v4 = grid.getD(xFloor + 2, yFloor, zFloor + 1);
        x2 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 1, zFloor + 1);
        v2 = grid.getD(xFloor, yFloor + 1, zFloor + 1);
        v3 = grid.getD(xFloor + 1, yFloor + 1, zFloor + 1);
        v4 = grid.getD(xFloor + 2, yFloor + 1, zFloor + 1);
        x3 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 2, zFloor + 1);
        v2 = grid.getD(xFloor, yFloor + 2, zFloor + 1);
        v3 = grid.getD(xFloor + 1, yFloor + 2, zFloor + 1);
        v4 = grid.getD(xFloor + 2, yFloor + 2, zFloor + 1);
        x4 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        double y3 = x2 + 0.5d * y * (x3 - x1 + y * (2.0d * x1 - 5.0d * x2 + 4.0d * x3 - x4 + y * (3.0d * (x2 - x3) + x4 - x1)));

        v1 = grid.getD(xFloor - 1, yFloor - 1, zFloor + 2);
        v2 = grid.getD(xFloor, yFloor - 1, zFloor + 2);
        v3 = grid.getD(xFloor + 1, yFloor - 1, zFloor + 2);
        v4 = grid.getD(xFloor + 2, yFloor - 1, zFloor + 2);
        x1 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor, zFloor + 2);
        v2 = grid.getD(xFloor, yFloor, zFloor + 2);
        v3 = grid.getD(xFloor + 1, yFloor, zFloor + 2);
        v4 = grid.getD(xFloor + 2, yFloor, zFloor + 2);
        x2 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 1, zFloor + 2);
        v2 = grid.getD(xFloor, yFloor + 1, zFloor + 2);
        v3 = grid.getD(xFloor + 1, yFloor + 1, zFloor + 2);
        v4 = grid.getD(xFloor + 2, yFloor + 1, zFloor + 2);
        x3 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        v1 = grid.getD(xFloor - 1, yFloor + 2, zFloor + 2);
        v2 = grid.getD(xFloor, yFloor + 2, zFloor + 2);
        v3 = grid.getD(xFloor + 1, yFloor + 2, zFloor + 2);
        v4 = grid.getD(xFloor + 2, yFloor + 2, zFloor + 2);
        x4 = v2 + 0.5d * x * (v3 - v1 + x * (2.0d * v1 - 5.0d * v2 + 4.0d * v3 - v4 + x * (3.0d * (v2 - v3) + v4 - v1)));

        double y4 = x2 + 0.5d * y * (x3 - x1 + y * (2.0d * x1 - 5.0d * x2 + 4.0d * x3 - x4 + y * (3.0d * (x2 - x3) + x4 - x1)));

        return y2 + 0.5d * z * (y3 - y1 + z * (2.0d * y1 - 5.0d * y2 + 4.0d * y3 - y4 + z * (3.0d * (y2 - y3) + y4 - y1)));
    }
}
