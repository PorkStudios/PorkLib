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

import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.PBitmap;
import net.daporkchop.lib.math.grid.Grid2d;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * An implementation of {@link Grid2d} as a wrapper around {@link PBitmap} for use by {@link ImageInterpolator}.
 *
 * @author DaPorkchop_
 */
abstract class HelperGrid implements Grid2d {
    protected final PBitmap bitmap;

    protected final int width;
    protected final int height;

    public HelperGrid(@NonNull PBitmap bitmap) {
        this.bitmap = bitmap;

        this.width = bitmap.width() - 1;
        this.height = bitmap.height() - 1;
    }

    @Override
    public int startX() {
        return 0;
    }

    @Override
    public int endX() {
        return this.width + 1;
    }

    @Override
    public int startY() {
        return 0;
    }

    @Override
    public int endY() {
        return this.height + 1;
    }

    @Override
    public double getD(int x, int y) {
        return this.getI(x, y);
    }

    @Override
    public void setD(int x, int y, double val) {
        throw new UnsupportedOperationException("set");
    }

    @Override
    public void setI(int x, int y, int val) {
        throw new UnsupportedOperationException("set");
    }

    @Override
    public boolean isOverflowing() {
        return true;
    }

    static final class Shift0ARGB extends HelperGrid {
        public Shift0ARGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return this.bitmap.getARGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) & 0xFF;
        }
    }

    static final class Shift1ARGB extends HelperGrid {
        public Shift1ARGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return (this.bitmap.getARGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) >>> 8) & 0xFF;
        }
    }

    static final class Shift2ARGB extends HelperGrid {
        public Shift2ARGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return (this.bitmap.getARGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) >>> 16) & 0xFF;
        }
    }

    static final class Shift3ARGB extends HelperGrid {
        public Shift3ARGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return (this.bitmap.getARGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) >>> 24) & 0xFF;
        }
    }

    static final class Shift0RGB extends HelperGrid {
        public Shift0RGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return this.bitmap.getRGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) & 0xFF;
        }
    }

    static final class Shift1RGB extends HelperGrid {
        public Shift1RGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return (this.bitmap.getRGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) >>> 8) & 0xFF;
        }
    }

    static final class Shift2RGB extends HelperGrid {
        public Shift2RGB(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return (this.bitmap.getRGB(clamp(x, 0, this.width), clamp(y, 0, this.height)) >>> 16) & 0xFF;
        }
    }

    static final class Shift0ABW extends HelperGrid {
        public Shift0ABW(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return this.bitmap.getABW(clamp(x, 0, this.width), clamp(y, 0, this.height)) & 0xFF;
        }
    }

    static final class Shift1ABW extends HelperGrid {
        public Shift1ABW(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return (this.bitmap.getABW(clamp(x, 0, this.width), clamp(y, 0, this.height)) >>> 8) & 0xFF;
        }
    }

    static final class Shift0BW extends HelperGrid {
        public Shift0BW(@NonNull PBitmap bitmap) {
            super(bitmap);
        }

        @Override
        public int getI(int x, int y) {
            return this.bitmap.getBW(clamp(x, 0, this.width), clamp(y, 0, this.height));
        }
    }
}
