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

package net.daporkchop.lib.graphics.bitmap;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.graphics.color.ColorFormat;
import net.daporkchop.lib.graphics.color.ColorFormatABW;
import net.daporkchop.lib.graphics.color.ColorFormatBW;
import net.daporkchop.lib.graphics.color.ColorFormatRGB;
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.capability.Releasable;

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
}
