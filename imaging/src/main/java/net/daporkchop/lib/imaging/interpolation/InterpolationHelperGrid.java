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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
import net.daporkchop.lib.math.grid.Grid2d;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public final class InterpolationHelperGrid implements Grid2d {
    @NonNull
    protected final PBitmap bitmap;
    protected final int mode;
    protected int shift;

    protected final int w;
    protected final int h;

    public InterpolationHelperGrid(@NonNull PBitmap bitmap, int mode)    {
        this.bitmap = bitmap;
        this.mode = mode;

        this.w = bitmap.width() - 1;
        this.h = bitmap.height() - 1;
    }

    @Override
    public int startX() {
        return 0;
    }

    @Override
    public int endX() {
        return this.bitmap.width();
    }

    @Override
    public int startY() {
        return 0;
    }

    @Override
    public int endY() {
        return this.bitmap.height();
    }

    @Override
    public double getD(int x, int y) {
        return this.getI(x, y);
    }

    @Override
    public int getI(int x, int y) {
        switch (this.mode)  {
            case 0: //ARGB
                return (this.bitmap.getARGB(clamp(x, 0, this.w), clamp(y, 0, this.h)) >>> this.shift) & 0xFF;
            case 1: //RGB
                return (this.bitmap.getRGB(clamp(x, 0, this.w), clamp(y, 0, this.h)) >>> this.shift) & 0xFF;
            case 2: //ABW
                return (this.bitmap.getABW(clamp(x, 0, this.w), clamp(y, 0, this.h)) >>> this.shift) & 0xFF;
            case 3: //BW
                return this.bitmap.getBW(clamp(x, 0, this.w), clamp(y, 0, this.h));
            default:
                throw new IllegalStateException(String.format("Invalid mode: %d", this.mode));
        }
    }

    @Override
    public void setD(int x, int y, double val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setI(int x, int y, int val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOverflowing() {
        return true;
    }
}
