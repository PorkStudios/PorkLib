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
import net.daporkchop.lib.graphics.bitmap.image.ImageRGB;
import net.daporkchop.lib.graphics.util.bufferedimage.rgb.FastRGBColorModel;
import net.daporkchop.lib.graphics.util.bufferedimage.rgb.ImageRGBDataBuffer;
import net.daporkchop.lib.graphics.util.bufferedimage.rgb.ImageRGBRaster;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.unsafe.PUnsafe;

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
public class DirectImageRGB extends DirectImage implements ImageRGB {
    public DirectImageRGB(int width, int height) {
        super(width, height, 3L);
    }

    @Override
    public long getByteScale() {
        return 3L;
    }

    @Override
    public void setRGB(int x, int y, int col) {
        long pos = this.getPos(x, y);
        PUnsafe.putShort(pos, (short) ((col >>> 8) & 0xFFFF));
        PUnsafe.putByte(pos + 2L, (byte) (col & 0xFF));
    }

    @Override
    public int getRGB(int x, int y) {
        long pos = this.getPos(x, y);
        return ((PUnsafe.getShort(pos) & 0xFFFF) << 8) | (PUnsafe.getByte(pos + 2L) & 0xFF);
    }

    @Override
    protected ColorModel newColorModel() {
        return new FastRGBColorModel();
    }

    @Override
    protected DataBuffer newDataBuffer() {
        return new ImageRGBDataBuffer(this);
    }

    @Override
    protected WritableRaster newRaster() {
        return new ImageRGBRaster(this);
    }

    //optimizations
    @Override
    public void fillRGB(int col) {
        short shortPart = (short) ((col >>> 8) & 0xFFFF);
        byte bytePart = (byte) (col & 0xFF);
        long pos = this.pos;
        for (long l = (long) this.width * (long) this.height * 3L - 3L; l >= 0L; l -= 3L)   {
            PUnsafe.putShort(pos + l, shortPart);
            PUnsafe.putByte(pos + l + 2L, bytePart);
        }
    }

    @Override
    public void fillARGB(int col) {

    }

    @Override
    public void fillABW(int col) {

    }

    @Override
    public void fillBW(int col) {

    }
}
