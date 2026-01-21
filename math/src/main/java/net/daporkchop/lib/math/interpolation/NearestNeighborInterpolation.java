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
 * This doesn't interpolate anything at all, but rather simply floors the coordinates to get the requested value (nearest
 * neighbor interpolation).
 *
 * @author DaPorkchop_
 */
public final class NearestNeighborInterpolation extends AbstractInterpolation {
    /**
     * @return an instance of {@link NearestNeighborInterpolation}
     */
    public static NearestNeighborInterpolation instance() {
        return InstancePool.getInstance(NearestNeighborInterpolation.class);
    }

    @Override
    public int requiredRadius() {
        return 0;
    }

    @Override
    public double getInterpolated(double x, @NonNull Grid1d grid) {
        this.ensureInRange(floorI(x), grid);

        return grid.getD(floorI(x));
    }

    @Override
    public double getInterpolated(double x, double y, @NonNull Grid2d grid) {
        this.ensureInRange(floorI(x), floorI(y), grid);

        return grid.getD(floorI(x), floorI(y));
    }

    @Override
    public double getInterpolated(double x, double y, double z, @NonNull Grid3d grid) {
        this.ensureInRange(floorI(x), floorI(y), floorI(z), grid);

        return grid.getD(floorI(x), floorI(y), floorI(z));
    }

    @Override
    public int getInterpolatedI(double x, Grid1d grid) {
        this.ensureInRange(floorI(x), grid);

        return grid.getI(floorI(x));
    }

    @Override
    public int getInterpolatedI(double x, double y, Grid2d grid) {
        this.ensureInRange(floorI(x), floorI(y), grid);

        return grid.getI(floorI(x), floorI(y));
    }

    @Override
    public int getInterpolatedI(double x, double y, double z, Grid3d grid) {
        this.ensureInRange(floorI(x), floorI(y), floorI(z), grid);

        return grid.getI(floorI(x), floorI(y), floorI(z));
    }
}
