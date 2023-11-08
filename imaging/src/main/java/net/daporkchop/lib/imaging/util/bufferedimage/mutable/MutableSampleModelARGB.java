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

package net.daporkchop.lib.imaging.util.bufferedimage.mutable;

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.util.bufferedimage.AbstractGiantSampleModelARGB;

import java.awt.image.DataBuffer;

/**
 * @author DaPorkchop_
 */
public final class MutableSampleModelARGB extends AbstractGiantSampleModelARGB<PImage> {
    public MutableSampleModelARGB(@NonNull PImage bitmap) {
        super(bitmap);
    }

    @Override
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
        if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        if (obj instanceof int[]) {
            data.setElem(x, y, ((int[]) obj)[0]);
        } else {
            throw new IllegalArgumentException(String.format("Invalid argument type: %s", obj == null ? "null" : obj.getClass().getCanonicalName()));
        }
    }

    @Override
    public void setPixel(int x, int y, int iArray[], DataBuffer data) {
        if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        data.setElem(x, y, iArray[0]);
    }

    @Override
    public void setPixels(int x, int y, int w, int h, int iArray[], DataBuffer data) {
        int x1 = x + w;
        int y1 = y + h;

        if (x < 0 || x >= this.width || w > this.width || x1 < 0 || x1 > this.width || y < 0 || y >= this.height || h > this.height || y1 < 0 || y1 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        int srcOffset = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                data.setElem(x + j, y + i, iArray[srcOffset++]);
            }
        }
    }
}
