/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
import net.daporkchop.lib.graphics.bitmap.PBitmap;
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
    private static void interpolateARGB(Interpolation engine, PBitmap src, PImage dst, int dstWidth, int dstHeight, double factX, double factY) {
        Grid2d grid0 = new HelperGrid.Shift0ARGB(src);
        Grid2d grid1 = new HelperGrid.Shift1ARGB(src);
        Grid2d grid2 = new HelperGrid.Shift2ARGB(src);
        Grid2d grid3 = new HelperGrid.Shift3ARGB(src);

        for (int x = dstWidth - 1; x >= 0; x--) {
            for (int y = dstHeight - 1; y >= 0; y--) {
                dst.setARGB(x, y,
                        (clamp(engine.getInterpolatedI(x * factX, y * factY, grid3), 0, 0xFF) << 24)
                                | (clamp(engine.getInterpolatedI(x * factX, y * factY, grid2), 0, 0xFF) << 16)
                                | (clamp(engine.getInterpolatedI(x * factX, y * factY, grid1), 0, 0xFF) << 8)
                                | clamp(engine.getInterpolatedI(x * factX, y * factY, grid0), 0, 0xFF));
            }
        }
    }

    private static void interpolateRGB(Interpolation engine, PBitmap src, PImage dst, int dstWidth, int dstHeight, double factX, double factY) {
        Grid2d grid0 = new HelperGrid.Shift0RGB(src);
        Grid2d grid1 = new HelperGrid.Shift1RGB(src);
        Grid2d grid2 = new HelperGrid.Shift2RGB(src);

        for (int x = dstWidth - 1; x >= 0; x--) {
            for (int y = dstHeight - 1; y >= 0; y--) {
                dst.setRGB(x, y,
                        (clamp(engine.getInterpolatedI(x * factX, y * factY, grid2), 0, 0xFF) << 16)
                                | (clamp(engine.getInterpolatedI(x * factX, y * factY, grid1), 0, 0xFF) << 8)
                                | clamp(engine.getInterpolatedI(x * factX, y * factY, grid0), 0, 0xFF));
            }
        }
    }

    private static void interpolateABW(Interpolation engine, PBitmap src, PImage dst, int dstWidth, int dstHeight, double factX, double factY) {
        Grid2d grid0 = new HelperGrid.Shift0ABW(src);
        Grid2d grid1 = new HelperGrid.Shift1ABW(src);

        for (int x = dstWidth - 1; x >= 0; x--) {
            for (int y = dstHeight - 1; y >= 0; y--) {
                dst.setABW(x, y,
                        (clamp(engine.getInterpolatedI(x * factX, y * factY, grid1), 0, 0xFF) << 8)
                                | clamp(engine.getInterpolatedI(x * factX, y * factY, grid0), 0, 0xFF));
            }
        }
    }

    private static void interpolateBW(Interpolation engine, PBitmap src, PImage dst, int dstWidth, int dstHeight, double factX, double factY) {
        Grid2d grid0 = new HelperGrid.Shift0BW(src);

        for (int x = dstWidth - 1; x >= 0; x--) {
            for (int y = dstHeight - 1; y >= 0; y--) {
                dst.setBW(x, y, clamp(engine.getInterpolatedI(x * factX, y * factY, grid0), 0, 0xFF));
            }
        }
    }

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

    public void interp(@NonNull PBitmap src, @NonNull PImage dst) {
        int dstWidth = dst.width();
        int dstHeight = dst.height();

        double factX = (double) src.width() / (double) dstWidth;
        double factY = (double) src.height() / (double) dstHeight;

        if (src.format() == ColorFormat.RGB) {
            interpolateRGB(this.engine, src, dst, dstWidth, dstHeight, factX, factY);
        } else if (src.format() == ColorFormat.ABW) {
            interpolateABW(this.engine, src, dst, dstWidth, dstHeight, factX, factY);
        } else if (src.format() == ColorFormat.BW) {
            interpolateBW(this.engine, src, dst, dstWidth, dstHeight, factX, factY);
        } else {
            interpolateARGB(this.engine, src, dst, dstWidth, dstHeight, factX, factY);
        }
    }
}
