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

package net.daporkchop.lib.graphics.bitmap;

import net.daporkchop.lib.graphics.color.ColorModel;
import net.daporkchop.lib.graphics.color.ColorModelABW;
import net.daporkchop.lib.graphics.color.ColorModelBW;
import net.daporkchop.lib.graphics.color.ColorModelRGB;
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.capability.Releasable;

/**
 * Base interface that represents a pixel bitmap.
 *
 * @author DaPorkchop_
 * @see PImage
 * @see PIcon
 */
public interface PBitmap extends Releasable {
    /**
     * @return the width (in pixels) of this bitmap
     */
    int width();

    /**
     * @return the height (in pixels) of this bitmap
     */
    int height();

    /**
     * @return this bitmap's {@link ColorModel}
     */
    ColorModel model();

    /**
     * Gets the raw color value at the given pixel coordinates, according to this bitmap's {@link #model()}.
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the raw color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    long getRaw(int x, int y) throws BitmapCoordinatesOutOfBoundsException;

    /**
     * Gets the RGB color value at the given pixel coordinates.
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the RGB color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getRGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException   {
        return ColorModelRGB.fromARGB(this.model().decode(this.getRaw(x, y)));
    }

    /**
     * Gets the ARGB color value at the given pixel coordinates.
     *
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the ARGB color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getARGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException  {
        return this.model().decode(this.getRaw(x, y));
    }

    /**
     * Gets the BW color value at the given pixel coordinates.
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the BW color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getBW(int x, int y) throws BitmapCoordinatesOutOfBoundsException   {
        return ColorModelBW.fromARGB(this.model().decode(this.getRaw(x, y)));
    }

    /**
     * Gets the ABW color value at the given pixel coordinates.
     * @param x the X coordinate of the pixel to get
     * @param y the Y coordinate of the pixel to get
     * @return the ABW color value at the given pixel coordinates
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    default int getABW(int x, int y) throws BitmapCoordinatesOutOfBoundsException   {
        return ColorModelABW.fromARGB(this.model().decode(this.getRaw(x, y)));
    }
}
