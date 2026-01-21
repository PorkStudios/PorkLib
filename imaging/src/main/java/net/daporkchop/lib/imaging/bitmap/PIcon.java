/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.imaging.bitmap;

import net.daporkchop.lib.imaging.color.ColorFormat;
import net.daporkchop.lib.imaging.util.bufferedimage.FastColorModelARGB;
import net.daporkchop.lib.imaging.util.bufferedimage.immutable.ImmutableRasterARGB;
import net.daporkchop.lib.imaging.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.awt.image.BufferedImage;

/**
 * An immutable image.
 *
 * @author DaPorkchop_
 * @see PImage
 */
public interface PIcon extends PBitmap {
    @Override
    int width();

    @Override
    int height();

    @Override
    ColorFormat format();

    @Override
    int getARGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException;

    /**
     * @return a mutable copy of this icon
     */
    PImage mutableCopy();

    /**
     * Gets a view of this icons's data as a mutable {@link PImage}.
     * <p>
     * This will not duplicate the pixel data, making this an unsafe operation (as this {@link PIcon}'s contents will
     * change when the returned {@link PImage} is modified).
     *
     * @return a mutable view of this icon
     */
    PImage unsafeMutableView();

    /**
     * @see #unsafeMutableView()
     */
    default PImage retainedUnsafeMutableView() {
        return this.retain().unsafeMutableView();
    }

    @Override
    default BufferedImage asBufferedImage() {
        return new BufferedImage(FastColorModelARGB.instance(), new ImmutableRasterARGB(this), false, null);
    }

    @Override
    PIcon retain() throws AlreadyReleasedException;
}
