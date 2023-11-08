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
import net.daporkchop.lib.imaging.color.ColorFormatABW;
import net.daporkchop.lib.imaging.color.ColorFormatBW;
import net.daporkchop.lib.imaging.color.ColorFormatRGB;
import net.daporkchop.lib.imaging.render.GraphicsRenderer2d;
import net.daporkchop.lib.imaging.render.Renderer2d;
import net.daporkchop.lib.imaging.util.bufferedimage.FastColorModelARGB;
import net.daporkchop.lib.imaging.util.bufferedimage.mutable.MutableRasterARGB;
import net.daporkchop.lib.imaging.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.awt.image.BufferedImage;

/**
 * A mutable image.
 *
 * @author DaPorkchop_
 * @see PIcon
 */
public interface PImage extends PBitmap {
    @Override
    int width();

    @Override
    int height();

    @Override
    ColorFormat format();

    /**
     * Sets the raw color value at the given pixel coordinates, according to this image's {@link #format()}.
     *
     * @param x     the X coordinate of the pixel to get
     * @param y     the Y coordinate of the pixel to get
     * @param color the new raw color value to set
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    void setRaw(int x, int y, long color) throws BitmapCoordinatesOutOfBoundsException;

    /**
     * Sets the RGB color value at the given pixel coordinates.
     *
     * @param x   the X coordinate of the pixel to get
     * @param y   the Y coordinate of the pixel to get
     * @param rgb the new RGB color value to set
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default void setRGB(int x, int y, int rgb) throws BitmapCoordinatesOutOfBoundsException {
        this.setRaw(x, y, Integer.toUnsignedLong(ColorFormatRGB.fromARGB(rgb)));
    }

    /**
     * Sets the ARGB color value at the given pixel coordinates.
     *
     * @param x    the X coordinate of the pixel to get
     * @param y    the Y coordinate of the pixel to get
     * @param argb the new ARGB color value to set
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default void setARGB(int x, int y, int argb) throws BitmapCoordinatesOutOfBoundsException {
        this.setRaw(x, y, Integer.toUnsignedLong(argb));
    }

    /**
     * Sets the BW color value at the given pixel coordinates.
     *
     * @param x  the X coordinate of the pixel to get
     * @param y  the Y coordinate of the pixel to get
     * @param bw the new BW color value to set
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default void setBW(int x, int y, int bw) throws BitmapCoordinatesOutOfBoundsException {
        this.setRaw(x, y, Integer.toUnsignedLong(ColorFormatBW.fromARGB(bw)));
    }

    /**
     * Sets the ABW color value at the given pixel coordinates.
     *
     * @param x   the X coordinate of the pixel to get
     * @param y   the Y coordinate of the pixel to get
     * @param abw the new ABW color value to set
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default void setABW(int x, int y, int abw) throws BitmapCoordinatesOutOfBoundsException {
        this.setRaw(x, y, Integer.toUnsignedLong(ColorFormatABW.fromARGB(abw)));
    }

    /**
     * @return an immutable snapshot of this image
     */
    PIcon immutableSnapshot();

    /**
     * Gets a view of this image's data as an immutable {@link PIcon}.
     * <p>
     * This will not duplicate the pixel data, making this an unsafe operation (as the {@link PIcon}'s contents will
     * change when this {@link PImage} is modified).
     *
     * @return an immutable view of this image
     */
    PIcon unsafeImmutableView();

    /**
     * @see #unsafeImmutableView()
     */
    default PIcon retainedUnsafeImmutableView() {
        return this.retain().unsafeImmutableView();
    }

    /**
     * Gets a {@link BufferedImage} instance with identical contents to this bitmap.
     * <p>
     * When this {@link PImage} is released, the behavior of all {@link BufferedImage}s returned by this method
     * will be undefined.
     * <p>
     * The returned {@link BufferedImage} will be write-through, as in any changes made to the contents of the
     * {@link BufferedImage} will be reflected in this {@link PImage} and vice-versa.
     *
     * @return a {@link BufferedImage} instance with identical contents to this bitmap
     */
    @Override
    default BufferedImage asBufferedImage() {
        return new BufferedImage(FastColorModelARGB.instance(), new MutableRasterARGB(this), false, null);
    }

    /**
     * @return an {@link Renderer2d} which can be used to draw to this {@link PImage}
     */
    default Renderer2d renderer() {
        return new GraphicsRenderer2d(this.asImage().getGraphics());
    }

    @Override
    PImage retain() throws AlreadyReleasedException;
}
