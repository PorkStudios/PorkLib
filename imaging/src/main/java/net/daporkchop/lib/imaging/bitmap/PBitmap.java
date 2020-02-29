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

package net.daporkchop.lib.imaging.bitmap;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.imaging.color.ColorFormat;
import net.daporkchop.lib.imaging.color.ColorFormatABW;
import net.daporkchop.lib.imaging.color.ColorFormatBW;
import net.daporkchop.lib.imaging.color.ColorFormatRGB;
import net.daporkchop.lib.imaging.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Base interface that represents a pixel bitmap.
 *
 * @author DaPorkchop_
 * @see PImage
 * @see PIcon
 */
public interface PBitmap extends RefCounted {
    /**
     * @return the width (in pixels) of this bitmap
     */
    int width();

    /**
     * @return the height (in pixels) of this bitmap
     */
    int height();

    /**
     * @return whether or not this bitmap is empty (i.e. has no contents)
     */
    default boolean empty() {
        return this.width() == 0 || this.height() == 0;
    }

    /**
     * @return this bitmap's {@link ColorFormat}
     */
    ColorFormat format();

    /**
     * Gets the raw color value at the given pixel coordinates, according to this bitmap's {@link #format()}.
     *
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the raw color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    long getRaw(int x, int y) throws BitmapCoordinatesOutOfBoundsException;

    /**
     * Gets the RGB color value at the given pixel coordinates.
     *
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the RGB color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getRGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return ColorFormatRGB.fromARGB(this.format().decode(this.getRaw(x, y)));
    }

    /**
     * Gets the ARGB color value at the given pixel coordinates.
     *
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the ARGB color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getARGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return this.format().decode(this.getRaw(x, y));
    }

    /**
     * Gets the BW color value at the given pixel coordinates.
     *
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the BW color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getBW(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return ColorFormatBW.fromARGB(this.format().decode(this.getRaw(x, y)));
    }

    /**
     * Gets the ABW color value at the given pixel coordinates.
     *
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the ABW color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getABW(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return ColorFormatABW.fromARGB(this.format().decode(this.getRaw(x, y)));
    }

    //compat with AWT
    /**
     * Gets a {@link BufferedImage} instance with identical contents to this bitmap.
     * <p>
     * When this {@link PBitmap} is released, the behavior of all {@link BufferedImage}s returned by this method
     * will be undefined.
     *
     * @return a {@link BufferedImage} instance with identical contents to this bitmap
     */
    BufferedImage asBufferedImage();

    /**
     * @return a {@link Image} instance with identical contents to this bitmap
     */
    default Image asImage() {
        return this.asBufferedImage();
    }

    /**
     * @return a {@link Icon} instance with identical contents to this bitmap
     */
    default Icon asSwingIcon() {
        return new ImageIcon(this.asImage());
    }

    @Override
    PBitmap retain() throws AlreadyReleasedException;
}
