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

package net.daporkchop.lib.graphics.interpolation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.graphics.bitmap.PBitmap;
import net.daporkchop.lib.graphics.bitmap.PIcon;
import net.daporkchop.lib.graphics.bitmap.PImage;
import net.daporkchop.lib.graphics.color.ColorFormat;
import net.daporkchop.lib.math.grid.Grid2d;
import net.daporkchop.lib.math.interpolation.Interpolation;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ImageInterpolator {
    @NonNull
    protected final Interpolation engine;

    public PImage interp(@NonNull PBitmap src, double mult) {
        PImage dst = src.format().createImage(floorI(src.width() * mult), floorI(src.height() * mult));
        this.interp(src, dst);
        return dst;
    }

    public PImage interp(@NonNull PBitmap src, int w, int h) {
        PImage dst = src.format().createImage(w, h);
        this.interp(src, dst);
        return dst;
    }

    //TODO: do this without an intermediary grid
    public void interp(@NonNull PBitmap src, @NonNull PImage dst) {
        int dstWidth = dst.width();
        int dstHeight = dst.height();

        double factX = (double) src.width() / (double) dstWidth;
        double factY = (double) src.height() / (double) dstHeight;

        if (src.format() == ColorFormat.RGB)    {
            Grid2d grid0 = new HelperGrid.Shift0(src);
            Grid2d grid1 = new HelperGrid.Shift1(src);
            Grid2d grid2 = new HelperGrid.Shift2(src);

            for (int x = dstWidth - 1; x >= 0; x--) {
                for (int y = dstHeight - 1; y >= 0; y--)    {
                    dst.setRGB(
                            x, y,
                            (clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid2), 0, 0xFF) << 16)
                                    | (clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid1), 0, 0xFF) << 8)
                                    | clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid0), 0, 0xFF)
                            );
                }
            }
        } else {
            //default implementation for ARGB
            Grid2d grid0 = new HelperGrid.Shift0(src);
            Grid2d grid1 = new HelperGrid.Shift1(src);
            Grid2d grid2 = new HelperGrid.Shift2(src);
            Grid2d grid3 = new HelperGrid.Shift3(src);

            for (int x = dstWidth - 1; x >= 0; x--) {
                for (int y = dstHeight - 1; y >= 0; y--)    {
                    dst.setARGB(
                            x, y,
                            (clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid3), 0, 0xFF) << 24)
                                    | (clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid2), 0, 0xFF) << 16)
                                    | (clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid1), 0, 0xFF) << 8)
                                    | clamp(this.engine.getInterpolatedI(x * factX, y * factY, grid0), 0, 0xFF)
                    );
                }
            }
        }
    }
}
