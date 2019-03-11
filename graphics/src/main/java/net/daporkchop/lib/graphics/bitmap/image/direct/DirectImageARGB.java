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

package net.daporkchop.lib.graphics.bitmap.image.direct;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.graphics.bitmap.image.ImageARGB;
import net.daporkchop.lib.reflection.PField;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 * @author DaPorkchop_
 */
public class DirectImageARGB extends DirectImage implements ImageARGB {
    public DirectImageARGB(int width, int height) {
        super(width, height);
    }

    @Override
    public long getByteScale() {
        return 4L;
    }

    @Override
    public void setARGB(int x, int y, int col) {
        PUnsafe.putInt(this.getPos(x, y), col);
    }

    @Override
    public int getARGB(int x, int y) {
        return PUnsafe.getInt(this.getPos(x, y));
    }

    @Override
    protected ColorModel newColorModel() {
        return new FastARGBColorModel();
    }

    @Override
    protected DataBuffer newDataBuffer() {
        return new DirectDataBuffer(this);
    }

    @Override
    protected WritableRaster newRaster() {
        return new DirectRaster(this);
    }

    protected static class FastARGBColorModel extends ColorModel {
        protected static PField field_numComponents = PField.of(ColorModel.class, "numComponents");
        protected static PField field_supportsAlpha = PField.of(ColorModel.class, "supportsAlpha");

        public FastARGBColorModel() {
            super(32);

            field_numComponents.setInt(this, 4);
            field_supportsAlpha.setBoolean(this, true);
        }

        @Override
        public int getAlpha(int pixel) {
            return (pixel >>> 24) & 0xFF;
        }

        @Override
        public int getRed(int pixel) {
            return (pixel >>> 16) & 0xFF;
        }

        @Override
        public int getGreen(int pixel) {
            return (pixel >>> 8) & 0xFF;
        }

        @Override
        public int getBlue(int pixel) {
            return pixel & 0xFF;
        }

        @Override
        public int getAlpha(Object inData) {
            return this.getAlpha(this.getRGB(inData));
        }

        @Override
        public int getRed(Object inData) {
            return this.getRed(this.getRGB(inData));
        }

        @Override
        public int getGreen(Object inData) {
            return this.getGreen(this.getRGB(inData));
        }

        @Override
        public int getBlue(Object inData) {
            return this.getBlue(this.getRGB(inData));
        }

        @Override
        public int getRGB(Object inData) {
            /*int[] arr = (int[]) inData;
            return (arr[0] << 24) | (arr[1] << 16) | (arr[2] << 8) | arr[3];*/
            return ((int[]) inData)[0];
        }

        @Override
        public boolean isCompatibleRaster(Raster raster) {
            return raster instanceof DirectRaster;
        }

        @Override
        public boolean isCompatibleSampleModel(SampleModel sm) {
            return sm instanceof LargerSinglePixelPackedSampleModel;
        }

        @Override
        public Object getDataElements(int argb, Object pixel) {
            int[] i;
            /*if (pixel instanceof int[]) {
                i = (int[]) pixel;
            } else {
                i = new int[4];
            }
            i[0] = (argb >>> 24) & 0xFF;
            i[1] = (argb >>> 16) & 0xFF;
            i[2] = (argb >>> 8) & 0xFF;
            i[3] = argb & 0xFF;*/
            if (pixel instanceof int[]) {
                i = (int[]) pixel;
            } else {
                i = new int[1];
            }
            i[0] = argb;
            return i;
        }
    }

    @Getter
    protected static class DirectRaster extends WritableRaster {
        protected final DirectImageARGB image;

        protected DirectRaster(@NonNull DirectImageARGB image) {
            super(new LargerSinglePixelPackedSampleModel(
                    DataBuffer.TYPE_INT,
                    image.width,
                    image.height,
                    //new int[]{0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF},
                    new int[]{0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000},
                    image
            ), image.newDataBuffer(), new Point(0, 0));

            this.image = image;
        }

        @Override
        public void setPixel(int x, int y, int[] iArray) {
            throw new UnsupportedOperationException();
        }
    }

    @Getter
    protected static class DirectDataBuffer extends DataBuffer {
        protected final DirectImageARGB image;

        protected DirectDataBuffer(@NonNull DirectImageARGB image) {
            super(DataBuffer.TYPE_INT, image.height, image.width);

            this.image = image;
        }

        @Override
        public int getElem(int bank, int i) {
            return this.image.getARGB(bank, i);
        }

        @Override
        public void setElem(int bank, int i, int val) {
            this.image.setARGB(bank, i, val);
        }
    }

    @Getter
    protected static class LargerSinglePixelPackedSampleModel extends SinglePixelPackedSampleModel {
        protected final DirectImageARGB image;

        public LargerSinglePixelPackedSampleModel(int dataType, int w, int h, int[] bitMasks, @NonNull DirectImageARGB image) {
            super(dataType, w, h, bitMasks);

            this.image = image;
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
            /*int[] pixels;
            if (iArray == null) {
                pixels = new int[4];
            } else {
                pixels = iArray;
            }

            int value = data.getElem(x, y);

            pixels[0] = (value >>> 24) & 0xFF;
            pixels[1] = (value >>> 16) & 0xFF;
            pixels[2] = (value >>> 8) & 0xFF;
            pixels[3] = value & 0xFF;
            return pixels;*/
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
            /*int pixels[];
            if (iArray != null) {
                pixels = iArray;
            } else {
                pixels = new int[w * h * 4];
            }
            int dstOffset = 0;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int value = data.getElem(x + j, y + i);
                    pixels[dstOffset++] = (value >>> 24) & 0xFF;
                    pixels[dstOffset++] = (value >>> 16) & 0xFF;
                    pixels[dstOffset++] = (value >>> 8) & 0xFF;
                    pixels[dstOffset++] = value & 0xFF;
                }
            }
            return pixels;*/
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
                /*int[] arr = (int[]) obj;
                data.setElem(x, y, (arr[0] << 24) | (arr[1] << 16) | (arr[2] << 8) | arr[3]);*/
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

            //data.setElem(x, y, (iArray[0] << 24) | (iArray[1] << 16) | (iArray[2] << 8) | iArray[3]);
            data.setElem(x, y, iArray[0]);
        }

        @Override
        public void setPixels(int x, int y, int w, int h, int iArray[], DataBuffer data) {
            int x1 = x + w;
            int y1 = y + h;

            if (x < 0 || x >= this.width || w > this.width || x1 < 0 || x1 > this.width || y < 0 || y >= this.height || h > this.height || y1 < 0 || y1 > this.height) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }

            /*int srcOffset = 0;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    data.setElem(x + j, y + i, (iArray[srcOffset++] << 24) | (iArray[srcOffset++] << 16) | (iArray[srcOffset++] << 8) | iArray[srcOffset++]);
                }
            }*/
            int srcOffset = 0;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    data.setElem(x + j, y + i, iArray[srcOffset++]);
                }
            }
        }
    }
}
