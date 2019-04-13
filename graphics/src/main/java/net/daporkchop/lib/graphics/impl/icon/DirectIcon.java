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

package net.daporkchop.lib.graphics.impl.icon;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.graphics.PIcon;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

/**
 * @author DaPorkchop_
 */
@Getter
public class DirectIcon extends DirectMemoryHolder.AbstractConstantSize implements PIcon {
    protected final int width;
    protected final int height;
    protected final boolean bw;

    public DirectIcon(int width, int height, boolean bw) {
        super(((long) width * (long) height) << (bw ? 0L : 2L));

        this.width = width;
        this.height = height;
        this.bw = bw;
    }

    @Override
    public int getARGB(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new ArrayIndexOutOfBoundsException(String.format("(%d,%d) w=%d,h=%d", x, y, this.width, this.height));
        } else if (this.bw) {
            return 0xFF000000 | (PUnsafe.getByte(this.pos + (long) x * (long) this.height + (long) y) & 0xFF);
        } else {
            return PUnsafe.getInt(this.pos + (((long) x * (long) this.height + (long) y) << 2L));
        }
    }

    @Override
    public boolean isBW() {
        return this.bw;
    }

    //everything below this comment is compatibility code to be able to work with java AWT's godawful api
    @Override
    public BufferedImage getAsBufferedImage() {
        return new BufferedImage(this.bw ? new FastBWColorModel() : new FastARGBColorModel(), this.newRaster(), false, null);
    }

    @Override
    public Icon getAsSwingIcon() {
        return new ImageIcon(this.getAsBufferedImage());
    }

    protected DataBuffer newDataBuffer()    {
        return new DirectDataBuffer(this);
    }

    protected WritableRaster newRaster()    {
        return new DirectRaster(this);
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

    protected static class FastBWColorModel extends ColorModel {
        public FastBWColorModel() {
            super(8);
        }

        @Override
        public int getRed(int pixel) {
            return pixel & 0xFF;
        }

        @Override
        public int getGreen(int pixel) {
            return pixel & 0xFF;
        }

        @Override
        public int getBlue(int pixel) {
            return pixel & 0xFF;
        }

        @Override
        public int getAlpha(int pixel) {
            return 0xFF;
        }

        @Override
        public boolean isCompatibleRaster(Raster raster) {
            return raster instanceof DirectRaster;
        }
    }

    protected class DirectRaster extends WritableRaster {
        protected DirectRaster(@NonNull DirectIcon image) {
            super(new LargerSinglePixelPackedSampleModel(
                    image.bw ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_INT,
                    image.width, image.height,
                    image.bw ? new int[]{0xFF} : new int[]{0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF}, image.bw
            ), image.newDataBuffer(), new Point(0, 0));
        }

        @Override
        public void setPixel(int x, int y, int[] iArray) {
            throw new UnsupportedOperationException();
        }
    }

    protected class DirectDataBuffer extends DataBuffer {
        protected DirectDataBuffer(@NonNull DirectIcon image) {
            super(image.bw ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_INT, image.height, image.width);
        }

        @Override
        public int getElem(int bank, int i) {
            return DirectIcon.this.getARGB(bank, i);
        }

        @Override
        public void setElem(int bank, int i, int val) {
            throw new UnsupportedOperationException();
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
            return DirectIcon.this.newDataBuffer();
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
