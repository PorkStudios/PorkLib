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

package net.daporkchop.lib.imaging.interpolation;

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
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
