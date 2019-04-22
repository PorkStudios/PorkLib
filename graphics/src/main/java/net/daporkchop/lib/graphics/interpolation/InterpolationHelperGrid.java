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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.graphics.bitmap.image.PImage;
import net.daporkchop.lib.math.arrays.grid.Grid2d;

import static net.daporkchop.lib.math.primitive.PMath.clamp;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterpolationHelperGrid implements Grid2d {
    @NonNull
    protected final PIcon img;
    protected final int mode;
    protected int shift;

    protected final int w;
    protected final int h;

    public InterpolationHelperGrid(@NonNull PIcon img, int mode)    {
        this.img = img;
        this.mode = mode;

        this.w = img.getWidth() - 1;
        this.h = img.getHeight() - 1;
    }

    @Override
    public int startX() {
        return 0;
    }

    @Override
    public int endX() {
        return this.img.getWidth();
    }

    @Override
    public int startY() {
        return 0;
    }

    @Override
    public int endY() {
        return this.img.getHeight();
    }

    @Override
    public double getD(int x, int y) {
        return this.getI(x, y);
    }

    @Override
    public int getI(int x, int y) {
        switch (this.mode)  {
            case 0: //ARGB
                return (this.img.getARGB(clamp(x, 0, this.w), clamp(y, 0, this.h)) >>> this.shift) & 0xFF;
            case 1: //RGB
                return (this.img.getRGB(clamp(x, 0, this.w), clamp(y, 0, this.h)) >>> this.shift) & 0xFF;
            case 2: //ABW
                return (this.img.getABW(clamp(x, 0, this.w), clamp(y, 0, this.h)) >>> this.shift) & 0xFF;
            case 3: //BW
                return this.img.getBW(clamp(x, 0, this.w), clamp(y, 0, this.h));
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
