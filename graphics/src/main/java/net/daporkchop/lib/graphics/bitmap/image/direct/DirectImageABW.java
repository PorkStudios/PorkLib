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

import net.daporkchop.lib.graphics.bitmap.image.ImageABW;
import net.daporkchop.lib.graphics.util.bufferedimage.abw.FastABWColorModel;
import net.daporkchop.lib.graphics.util.bufferedimage.abw.ImageABWDataBuffer;
import net.daporkchop.lib.graphics.util.bufferedimage.abw.ImageABWRaster;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

/**
 * @author DaPorkchop_
 */
public class DirectImageABW extends DirectImage implements ImageABW {
    public DirectImageABW(int width, int height) {
        super(width, height, 2L);
    }

    @Override
    public long getByteScale() {
        return 2L;
    }

    @Override
    public void setABW(int x, int y, int col) {
        PUnsafe.putShort(this.getPos(x, y), (short) col);
    }

    @Override
    public int getABW(int x, int y) {
        return PUnsafe.getShort(this.getPos(x, y)) & 0xFFFF;
    }

    @Override
    protected ColorModel newColorModel() {
        return new FastABWColorModel();
    }

    @Override
    protected DataBuffer newDataBuffer() {
        return new ImageABWDataBuffer(this);
    }

    @Override
    protected WritableRaster newRaster() {
        return new ImageABWRaster(this);
    }

    //optimizations
    @Override
    public void fillABW(int col) {
        short s = (short) col;
        long pos = this.pos;
        for (long l = (long) this.width * (long) this.height * 2L - 2L; l >= 0L; l -= 2L)    {
            PUnsafe.putShort(pos + l, s);
        }
    }

    @Override
    public void fillARGB(int col) {
        this.fillABW((col >>> 16) | ((col >>> 8) & 0xFF) | (col & 0xFF));
    }

    @Override
    public void fillRGB(int col) {
        this.fillABW(0xFF00 | (col >>> 16) | ((col >>> 8) & 0xFF) | (col & 0xFF));
    }

    @Override
    public void fillBW(int col) {
        this.fillABW(0xFF00 | col);
    }
}
