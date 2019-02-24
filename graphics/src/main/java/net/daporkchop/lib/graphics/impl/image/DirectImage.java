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

package net.daporkchop.lib.graphics.impl.image;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.graphics.PImage;
import net.daporkchop.lib.graphics.impl.icon.DirectIcon;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 * @author DaPorkchop_
 */
public class DirectImage extends DirectIcon implements PImage {
    public DirectImage(int width, int height, boolean bw) {
        super(width, height, bw);
    }

    @Override
    public void setARGB(int x, int y, int argb) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new ArrayIndexOutOfBoundsException(String.format("(%d,%d) w=%d,h=%d", x, y, this.width, this.height));
        } else if (this.bw) {
            PUnsafe.putByte(this.pos + (long) x * (long) this.height + (long) y, (byte) argb);
        } else {
            PUnsafe.putInt(this.pos + (((long) x * (long) this.height + (long) y) << 2L), argb);
        }
    }

    @Override
    public BufferedImage getAsBufferedImage() {
        ColorModel colorModel;
        if (this.bw) {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            int[] nBits = {8};
            colorModel = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        } else {
            //colorModel = ColorModel.getRGBdefault();
            colorModel = new FastARGBColorModel();
        }
        return new BufferedImage(colorModel, new DirectRaster(this), false, null);
    }

    protected static class FastARGBColorModel extends ColorModel {
        public FastARGBColorModel() {
            super(32);
        }

        @Override
        public int getRed(int pixel) {
            return (pixel >> 16) & 0xFF;
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
        public int getAlpha(int pixel) {
            return pixel >>> 24;
        }

        @Override
        public boolean isCompatibleRaster(Raster raster) {
            return raster instanceof DirectRaster;
        }
    }

    protected class DirectRaster extends WritableRaster {
        protected DirectRaster(@NonNull DirectImage image) {
            super(new LargerSinglePixelPackedSampleModel(
                    image.bw ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_INT,
                    image.width, image.height,
                    image.bw ? new int[]{0xFF} : new int[]{0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF}, image.bw
            ), new DirectDataBuffer(image), new Point(0, 0));
        }

        @Override
        public void setPixel(int x, int y, int[] iArray) {
            DirectImage.this.setARGB(x, y, iArray[0]);
        }
    }

    protected class DirectDataBuffer extends DataBuffer {
        protected DirectDataBuffer(@NonNull DirectImage image) {
            super(image.bw ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_INT, image.height, image.width);
        }

        @Override
        public int getElem(int bank, int i) {
            return DirectImage.this.getARGB(bank, i);
        }

        @Override
        public void setElem(int bank, int i, int val) {
            DirectImage.this.setARGB(bank, i, val);
        }
    }

    protected class LargerSinglePixelPackedSampleModel extends SinglePixelPackedSampleModel {
        protected final boolean bw;

        public LargerSinglePixelPackedSampleModel(int dataType, int w, int h, int[] bitMasks, boolean bw) {
            super(dataType, w, h, bitMasks);

            this.bw = bw;
        }

        @Override
        public int getNumDataElements() {
            return this.width * this.height;
        }

        @Override
        public DataBuffer createDataBuffer() {
            return new DirectDataBuffer(DirectImage.this);
        }

        @Override
        public int getOffset(int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
            if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
                throw new ArrayIndexOutOfBoundsException
                        ("Coordinate out of bounds!");
            }

            if (this.bw) {
                byte[] bdata;
                if (obj == null) {
                    bdata = new byte[1];
                } else {
                    bdata = (byte[]) obj;
                }

                bdata[0] = (byte) data.getElem(x, y);
                obj = bdata;
            } else {
                int[] idata;
                if (obj == null) {
                    idata = new int[1];
                } else {
                    idata = (int[]) obj;
                }

                idata[0] = data.getElem(x, y);
                obj = idata;
            }
            return obj;
        }

        @Override
        public int[] getPixel(int x, int y, int iArray[], DataBuffer data) {
            if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }
            int pixels[];
            if (iArray == null) {
                pixels = new int[this.numBands];
            } else {
                pixels = iArray;
            }

            int value = data.getElem(x, y);

            if (this.bw) {
                pixels[0] = value & 0xFF;
            } else {
                pixels[0] = (value >>> 24) & 0xFF;
                pixels[1] = (value >>> 16) & 0xFF;
                pixels[2] = (value >>> 8) & 0xFF;
                pixels[3] = value & 0xFF;
            }
            return pixels;
        }

        @Override
        public int[] getPixels(int x, int y, int w, int h, int iArray[], DataBuffer data) {
            int x1 = x + w;
            int y1 = y + h;

            if (x < 0 || x >= this.width || w > this.width || x1 < 0 || x1 > this.width || y < 0 || y >= this.height || h > this.height || y1 < 0 || y1 > this.height) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }
            int pixels[];
            if (iArray != null) {
                pixels = iArray;
            } else {
                pixels = new int[w * h * this.numBands];
            }
            int dstOffset = 0;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int value = data.getElem(x + j, y + i);
                    if (this.bw) {
                        pixels[dstOffset++] = value & 0xFF;
                    } else {
                        pixels[dstOffset++] = (value >>> 24) & 0xFF;
                        pixels[dstOffset++] = (value >>> 16) & 0xFF;
                        pixels[dstOffset++] = (value >>> 8) & 0xFF;
                        pixels[dstOffset++] = value & 0xFF;
                    }
                }
            }
            return pixels;
        }

        @Override
        public void setDataElements(int x, int y, Object obj, DataBuffer data) {
            if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }

            if (this.bw)    {
                data.setElem(x, y, ((byte[]) obj)[0] & 0xFF);
            } else {
                data.setElem(x, y, ((int[]) obj)[0]);
            }
        }

        @Override
        public void setPixel(int x, int y, int iArray[], DataBuffer data) {
            if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }

            if (this.bw)    {
                data.setElem(x, y, iArray[0] & 0xFF);
            } else {
                data.setElem(x, y, (iArray[0] << 24) | (iArray[1] << 16) | (iArray[2] << 8) | iArray[3]);
            }
        }

        @Override
        public void setPixels(int x, int y, int w, int h, int iArray[], DataBuffer data) {
            int x1 = x + w;
            int y1 = y + h;

            if (x < 0 || x >= this.width || w > this.width || x1 < 0 || x1 > this.width || y < 0 || y >= this.height || h > this.height || y1 < 0 || y1 >  this.height) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }

            int srcOffset = 0;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (this.bw)    {
                        data.setElem(x + j, y + i, iArray[srcOffset++] & 0xFF);
                    } else {
                        data.setElem(x + j, y + i, (iArray[srcOffset++] << 24) | (iArray[srcOffset++] << 16) | (iArray[srcOffset++] << 8) | iArray[srcOffset++]);
                    }
                }
            }
        }
    }
}
