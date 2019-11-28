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

package net.daporkchop.lib.graphics.color;

import net.daporkchop.lib.graphics.bitmap.PImage;

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
