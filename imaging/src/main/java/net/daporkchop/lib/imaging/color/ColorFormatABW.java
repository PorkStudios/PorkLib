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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.bitmap.image.DirectImageABW;

/**
 * An implementation of the simple ABW (8-bit greyscale with alpha) color format.
 *
 * @author DaPorkchop_
 * @see ColorFormat#ABW
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ColorFormatABW implements ColorFormat {
    public static int toARGB(int abw)    {
        return (abw << 16) | ((abw & 0xFF) << 8) | (abw & 0xFF);
    }

    public static int fromARGB(int argb)    {
        return (argb >>> 16) | ((argb >>> 8) & 0xFF) | (argb & 0xFF);
    }

    @Override
    public int decode(long color) {
        int i = (int) color;
        //the first shift also gets the alpha channel in place
        return (i << 16) | ((i & 0xFF) << 8) | (i & 0xFF);
    }

    @Override
    public long encode(int argb) {
        return (argb >>> 16) | ((argb >>> 8) & 0xFF) | (argb & 0xFF);
    }

    @Override
    public int encodedBits() {
        return 16;
    }

    @Override
    public boolean alpha() {
        return true;
    }

    @Override
    public PImage createImage(int width, int height) {
        return new DirectImageABW(width, height);
    }
}
