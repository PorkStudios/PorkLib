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
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;

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
    ColorModel model();

    @Override
    int getARGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException;

    /**
     * Sets the ARGB color value at the given pixel coordinates.
     *
     * @param x    the X coordinate of the pixel to get
     * @param y    the Y coordinate of the pixel to get
     * @param argb the new ARGB color value to set
     * @throws BitmapCoordinatesOutOfBoundsException if the given pixel coordinates are out of bounds
     */
    void setARGB(int x, int y, int argb) throws BitmapCoordinatesOutOfBoundsException;

    /**
     * @return an immutable snapshot of this image
     */
    PIcon immutableSnapshot();
}
