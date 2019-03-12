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

package net.daporkchop.lib.graphics.util.bufferedimage.abw;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.image.ImageABW;
import net.daporkchop.lib.graphics.bitmap.image.ImageARGB;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

/**
 * @author DaPorkchop_
 */
@Getter
public class BiggerABWSampleModel extends SampleModel {
    public BiggerABWSampleModel(int dataType, int w, int h) {
        super(dataType, w, h, 2);
    }

    @Override
    public int getNumDataElements() {
        return 1;
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
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
        if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        if (obj instanceof int[]) {
            data.setElem(x, y, ((int[]) obj)[0] & 0xFFFF);
        } else {
            throw new IllegalArgumentException(String.format("Invalid argument type: %s", obj == null ? "null" : obj.getClass().getCanonicalName()));
        }
    }

    @Override
    public void setPixel(int x, int y, int iArray[], DataBuffer data) {
        if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        data.setElem(x, y, iArray[0] & 0xFFFF);
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
                data.setElem(x + j, y + i, iArray[srcOffset++] & 0xFFFF);
            }
        }
    }

    //jeff


    @Override
    public int getSample(int x, int y, int b, DataBuffer data) {
        return (data.getElem(x, y) >>> (b << 3)) & 0xFF;
    }

    @Override
    public void setSample(int x, int y, int b, int s, DataBuffer data) {
        data.setElem(x, y, (data.getElem(x, y) & ~(0xFF << (b << 3))) | (s << (b << 3)));
    }

    @Override
    public SampleModel createCompatibleSampleModel(int w, int h) {
        return new BiggerABWSampleModel(DataBuffer.TYPE_INT, w, h);
    }

    @Override
    public SampleModel createSubsetSampleModel(int[] bands) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataBuffer createDataBuffer() {
        return new DataBufferInt(this.height, this.width);
    }

    @Override
    public int[] getSampleSize() {
        return new int[] {
                8,
                8
        };
    }

    @Override
    public int getSampleSize(int band) {
        return 8;
    }

    //TODO: just translate all the data into ARGB
}
