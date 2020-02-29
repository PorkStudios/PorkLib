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

package net.daporkchop.lib.imaging.color;

import net.daporkchop.lib.imaging.bitmap.PImage;

/**
 * A format for encoding colors.
 *
 * @author DaPorkchop_
 */
public interface ColorFormat {
    ColorFormatRGB RGB = new ColorFormatRGB();
    ColorFormatARGB ARGB = new ColorFormatARGB();
    ColorFormatBW BW = new ColorFormatBW();
    ColorFormatABW ABW = new ColorFormatABW();

    /**
     * Decodes the given color into ARGB.
     * <p>
     * If this {@link ColorFormat} doesn't contain an alpha channel, the alpha value should be set to {@code 0xFF} (fully opaque).
     *
     * @param color the color to decode
     * @return the color as a standard ARGB color
     */
    int decode(long color);

    /**
     * Encodes the given ARGB color into this color format's format.
     * <p>
     * If this {@link ColorFormat} doesn't contain an alpha channel, implementations are permitted to silently discard the input
     * value's alpha level.
     *
     * @param argb the ARGB color to encode
     * @return the encoded color
     */
    long encode(int argb);

    /**
     * @return the number of bits that are used by this color format
     */
    int encodedBits();

    /**
     * @return whether or not this {@link ColorFormat} contains an alpha channel
     */
    boolean alpha();

    /**
     * Creates a new {@link PImage} with the given dimensions using this {@link ColorFormat}.
     *
     * @param width  the width of the image (in pixels)
     * @param height the height of the image (in pixels)
     * @return a new {@link PImage} with the given dimensions using this {@link ColorFormat}
     */
    PImage createImage(int width, int height);
}
