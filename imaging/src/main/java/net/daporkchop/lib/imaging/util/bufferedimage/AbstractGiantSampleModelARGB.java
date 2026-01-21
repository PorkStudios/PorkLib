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

package net.daporkchop.lib.imaging.util.bufferedimage;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PBitmap;

import java.awt.image.DataBuffer;
import java.awt.image.SinglePixelPackedSampleModel;

/**
 * This variant of a {@link java.awt.image.SampleModel} allows indexing images with a total pixel count greater than
 * {@link Integer#MAX_VALUE}, by always using the x coordinate as the "bank" parameter and the y coordinate as
 * the "index".
 *
 * @author DaPorkchop_
 */
//TODO: reimplement samplemodel from scratch
//TODO: figure out why i put the above todo there
@Getter
public abstract class AbstractGiantSampleModelARGB<P extends PBitmap> extends SinglePixelPackedSampleModel {
    protected final P bitmap;

    public AbstractGiantSampleModelARGB(@NonNull P bitmap) {
        super(
                DataBuffer.TYPE_INT,
                bitmap.width(),
                bitmap.height(),
                new int[]{0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000}
        );

        this.bitmap = bitmap;
    }

    @Override
    public int getNumDataElements() {
        return 1;
    }

    @Override
    public int getOffset(int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        if (obj == null) {
            obj = new int[]{data.getElem(x, y)};
        } else {
            ((int[]) obj)[0] = data.getElem(x, y);
        }
        return obj;
    }

    @Override
    public int[] getPixel(int x, int y, int iArray[], DataBuffer data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        if (iArray == null) {
            iArray = new int[]{data.getElem(x, y)};
        } else {
            iArray[0] = data.getElem(x, y);
        }
        return iArray;
    }

    @Override
    public int[] getPixels(int x, int y, int w, int h, int iArray[], DataBuffer data) {
        int x1 = x + w;
        int y1 = y + h;
        if (x < 0 || x >= this.width || w > this.width || x1 < 0 || x1 > this.width || y < 0 || y >= this.height || h > this.height || y1 < 0 || y1 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        if (iArray == null) {
            iArray = new int[w * h];
        }

        int dstOffset = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                iArray[dstOffset++] = data.getElem(x + j, y + i);
            }
        }
        return iArray;
    }

    @Override
    public abstract void setDataElements(int x, int y, Object obj, DataBuffer data);

    @Override
    public abstract void setPixel(int x, int y, int iArray[], DataBuffer data);

    @Override
    public abstract void setPixels(int x, int y, int w, int h, int iArray[], DataBuffer data);
}
