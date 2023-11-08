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

package net.daporkchop.lib.imaging.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.interpolation.ImageInterpolator;
import net.daporkchop.lib.math.interpolation.Interpolation;
import net.daporkchop.lib.math.interpolation.LinearInterpolation;

import java.util.Arrays;

/**
 * A thumbnail is a collection of square icons, with the same contents at different scales.
 *
 * @author DaPorkchop_
 */
public final class Thumbnail {
    protected final int[] sizes;
    protected final PBitmap[] icons;
    @Getter
    protected boolean baked = false;

    public Thumbnail(@NonNull int... sizes) {
        this.sizes = sizes = Arrays.stream(sizes)
                .distinct()
                .sorted()
                .toArray();
        this.icons = new PBitmap[sizes.length];
        if (sizes.length == 0) {
            this.baked = true;
        }
    }

    public Thumbnail bake() {
        return this.bake(new ImageInterpolator(LinearInterpolation.instance()));
    }

    public Thumbnail bake(@NonNull Interpolation engine) {
        return this.bake(new ImageInterpolator(engine));
    }

    public Thumbnail bake(@NonNull ImageInterpolator interpolator) {
        if (!this.baked) {
            PBitmap highestRes = null;
            for (int i = this.sizes.length - 1; i >= 0; i--) {
                if (this.icons[i] != null) {
                    highestRes = this.icons[i];
                    break;
                }
            }
            if (highestRes == null) {
                throw new IllegalStateException();
            }
            for (int i = this.sizes.length - 1; i >= 0; i--) {
                if (this.icons[i] == null) {
                    this.icons[i] = interpolator.interp(highestRes, this.sizes[i], this.sizes[i]).immutableSnapshot();
                }
            }
            this.baked = true;
        }
        return this;
    }

    public Thumbnail submit(@NonNull PBitmap icon)    {
        if (icon.empty() || icon.width() != icon.height())  {
            throw new IllegalArgumentException("Icon is not a square!");
        }
        if (!this.baked)    {
            for (int i = this.sizes.length - 1; i >= 0; i--)    {
                if (this.sizes[i] == icon.width())   {
                    this.icons[i] = icon instanceof PImage ? ((PImage) icon).immutableSnapshot() : icon;
                    return this;
                }
            }

            throw new IllegalArgumentException(String.format("Icon with size %d doesn't match any of the thumbnail resolutions!", icon.width()));
        }
        return this;
    }

    public PBitmap[] getIcons() {
        if (this.baked) {
            return this.icons.clone();
        } else {
            throw new IllegalStateException("Not baked!");
        }
    }
}
