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

package net.daporkchop.lib.graphics.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.graphics.PIcon;
import net.daporkchop.lib.graphics.PImage;
import net.daporkchop.lib.graphics.impl.image.DirectImage;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.math.interpolation.InterpolationEngine;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ImageInterpolator {
    @NonNull
    protected final InterpolationEngine engine;

    public PImage interp(@NonNull PIcon src, int w, int h)  {
        PImage dst = new DirectImage(w, h, src.isBW());
        this.interp(src, dst, null);
        return dst;
    }

    public Grid2d interp(@NonNull PIcon src, @NonNull PImage dst, Grid2d grid)   {
        if (grid == null || grid.endX() - grid.startX() != src.getWidth() || grid.endY() - grid.startY() != src.getHeight())    {
            grid = Grid2d.of(src.getWidth(), src.getHeight(), true);
        }
        double factX = (double) src.getWidth() / (double) dst.getWidth();
        double factY = (double) src.getHeight() / (double) dst.getHeight();
        dst.fill(0);
        for (int c = src.isBW() ? 0 : 3; c >= 0; c--)   {
            int shift = c << 3;
            for (int x = src.getWidth() - 1; x >= 0; x--)   {
                for (int y  = src.getHeight() - 1; y >= 0; y--) {
                    grid.setI(x, y, (src.getARGB(x, y) >>> shift) & 0xFF);
                }
            }
            for (int x = dst.getWidth() - 1; x >= 0; x--)   {
                for (int y = dst.getHeight() - 1; y >= 0; y--)  {
                    int col = dst.getARGB(x, y);
                    col |= floorI(this.engine.getInterpolated(x * factX, y * factY, grid)) << shift;
                    dst.setARGB(x, y, col);
                }
            }
        }
        return grid;
    }
}
