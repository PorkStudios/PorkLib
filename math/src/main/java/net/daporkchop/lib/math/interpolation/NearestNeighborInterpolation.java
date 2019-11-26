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

package net.daporkchop.lib.math.interpolation;

import lombok.NonNull;
import net.daporkchop.lib.common.reference.InstancePool;
import net.daporkchop.lib.math.grid.Grid1d;
import net.daporkchop.lib.math.grid.Grid2d;
import net.daporkchop.lib.math.grid.Grid3d;

import static net.daporkchop.lib.math.primitive.PMath.*;

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
