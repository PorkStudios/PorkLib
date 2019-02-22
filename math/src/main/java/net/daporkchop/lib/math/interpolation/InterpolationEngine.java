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
import net.daporkchop.lib.math.arrays.grid.Grid1d;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.math.arrays.grid.Grid3d;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
public interface InterpolationEngine {
    int requiredRadius();

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
    
    //validation
    default boolean isInRange(int x, @NonNull Grid1d grid)    {
        int radius = this.requiredRadius();
        
        if (x < grid.startX() + radius - 1 || x > grid.endX() - radius - 1) {
            return false;
        } else {
            return true;
        }
    }

    default boolean isInRange(int x, int y, @NonNull Grid2d grid)    {
        int radius = this.requiredRadius();

        if (x < grid.startX() + radius - 1 || x > grid.endX() - radius - 1
                || y < grid.startY() + radius - 1 || y > grid.endY() - radius - 1) {
            return false;
        } else {
            return true;
        }
    }

    default boolean isInRange(int x, int y, int z, @NonNull Grid3d grid)    {
        int radius = this.requiredRadius();

        if (x < grid.startX() + radius - 1 || x > grid.endX() - radius - 1
                || y < grid.startY() + radius - 1 || y > grid.endY() - radius - 1
                || z < grid.startZ() + radius - 1 || z > grid.endZ() - radius - 1) {
            return false;
        } else {
            return true;
        }
    }

    default void ensureInRange(int x, @NonNull Grid1d grid) {
        if (!this.isInRange(x, grid))   {
            throw new IndexOutOfBoundsException(String.format("Pos %d out of bounds of range %d-%d", x, grid.startX(), grid.endX()));
        }
    }

    default void ensureInRange(int x, int y, @NonNull Grid2d grid)  {
        if (!this.isInRange(x, y, grid))    {
            throw new IndexOutOfBoundsException(String.format("Pos (%d,%d) out of bounds of range (%d,%d)-(%d,%d)", x, y, grid.startX(), grid.startY(), grid.endX(), grid.endY()));
        }
    }

    default void ensureInRange(int x, int y, int z, @NonNull Grid3d grid)  {
        if (!this.isInRange(x, y, z, grid))    {
            throw new IndexOutOfBoundsException(String.format("Pos (%d,%d,%d) out of bounds of range (%d,%d,%d)-(%d,%d,%d)", x, y, z, grid.startX(), grid.startY(), grid.startZ(), grid.endX(), grid.endY(), grid.endZ()));
        }
    }
}
