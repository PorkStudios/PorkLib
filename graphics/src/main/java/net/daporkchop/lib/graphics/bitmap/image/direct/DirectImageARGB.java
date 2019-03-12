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
import net.daporkchop.lib.graphics.util.bufferedimage.argb.FastARGBColorModel;
import net.daporkchop.lib.graphics.util.bufferedimage.argb.ImageARGBDataBuffer;
import net.daporkchop.lib.graphics.util.bufferedimage.argb.ImageARGBRaster;
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
        return new ImageARGBDataBuffer(this);
    }

    @Override
    protected WritableRaster newRaster() {
        return new ImageARGBRaster(this);
    }

    //optimizations
    @Override
    public void fillARGB(int col) {
        long pos = this.pos;
        for (long l = (long) this.width * (long) this.height - 5L; l >= 0L; l -= 4L)  {
            PUnsafe.putInt(pos + l, col);
        }
    }

    @Override
    public void fillRGB(int col) {
        this.fillARGB(0xFF000000 | col);
    }

    @Override
    public void fillABW(int col) {
        this.fillARGB((col << 16) | ((col & 0xFF) << 8) | (col & 0xFF));
    }

    @Override
    public void fillBW(int col) {
        this.fillARGB(0xFF000000 | (col << 16) | (col << 8) | col);
    }
}
