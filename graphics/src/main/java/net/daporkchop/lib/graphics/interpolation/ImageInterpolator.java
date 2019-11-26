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
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.graphics.bitmap.image.PImage;
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

    public PImage interp(@NonNull PIcon src, double mult) {
        PImage dst = src.getFormat().createImage(floorI(src.getWidth() * mult), floorI(src.getHeight() * mult));
        this.interp(src, dst);
        return dst;
    }

    public PImage interp(@NonNull PIcon src, int w, int h) {
        PImage dst = src.getFormat().createImage(w, h);
        this.interp(src, dst);
        return dst;
    }

    //TODO: do this without an intermediary grid
    public void interp(@NonNull PIcon src, @NonNull PImage dst) {
        InterpolationHelperGrid grid = new InterpolationHelperGrid(src, src.getFormat().ordinal());

        double factX = (double) src.getWidth() / (double) dst.getWidth();
        double factY = (double) src.getHeight() / (double) dst.getHeight();
        switch (src.getFormat()) {
            case ARGB:
                dst.fillARGB(0);
                for (int x = dst.getWidth() - 1; x >= 0; x--) {
                    for (int y = dst.getHeight() - 1; y >= 0; y--) {
                        dst.setARGB(
                                x,
                                y,
                                (clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(24)), 0, 0xFF) << 24)
                                        | (clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(16)), 0, 0xFF) << 16)
                                        | (clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(8)), 0, 0xFF) << 8)
                                        | clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(0)), 0, 0xFF)
                        );
                    }
                }
                break;
            case RGB:
                dst.fillRGB(0);
                for (int x = dst.getWidth() - 1; x >= 0; x--) {
                    for (int y = dst.getHeight() - 1; y >= 0; y--) {
                        dst.setRGB(
                                x,
                                y,
                                (clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(16)), 0, 0xFF) << 16)
                                        | (clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(8)), 0, 0xFF) << 8)
                                        | clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(0)), 0, 0xFF)
                        );
                    }
                }
                break;
            case ABW:
                for (int x = dst.getWidth() - 1; x >= 0; x--) {
                    for (int y = dst.getHeight() - 1; y >= 0; y--) {
                        dst.setABW(
                                x,
                                y,
                                (clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(8)), 0, 0xFF) << 8)
                                        | clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid.setShift(0)), 0, 0xFF)
                        );
                    }
                }
                break;
            case BW:
                for (int x = dst.getWidth() - 1; x >= 0; x--) {
                    for (int y = dst.getHeight() - 1; y >= 0; y--) {
                        dst.setBW(
                                x,
                                y,
                                clamp(this.engine.getInterpolatedI(x * factX - 0.5d, y * factY - 0.5d, grid), 0, 0xFF)
                        );
                    }
                }
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown format: %s", src.getFormat()));
        }
    }
}
