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

package net.daporkchop.lib.binary.util.big;

import net.daporkchop.lib.math.primitive.BinMath;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import static net.daporkchop.lib.common.util.PUnsafe.*;

/**
 * Allows storing a {@link BufferedImage} in RAM(/swap) with nearly no limit to maximum size
 *
 * @author DaPorkchop_
 */
public class MassiveBufferedImage extends BufferedImage implements AutoCloseable {
    private final int width;
    private final int height;

    private final long address;

    public MassiveBufferedImage(int width, int height) {
        super(1, 1, TYPE_INT_ARGB);

        this.address = allocateMemory(((long) width * (long) height) << 2L);

        this.width = width;
        this.height = height;
    }

    @Override
    public int getRGB(int x, int y) {
        assert x >= 0;
        assert x < this.width;
        assert y >= 0;
        assert y < this.height;

        long address = this.address + (((long) y * (long) this.width + x) << 2L);
        return getInt(address);
    }

    @Override
    public void setRGB(int x, int y, int rgb) {
        assert x >= 0;
        assert x < this.width;
        assert y >= 0;
        assert y < this.height;

        long address = this.address + (((long) y * (long) this.width + x) << 2L);
        putInt(address, rgb);
    }

    @Override
    public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
        int yoff = offset;
        int off;

        for (int y = startY; y < startY + h; y++, yoff += scansize) {
            off = yoff;
            for (int x = startX; x < startX + w; x++) {
                rgbArray[off++] = this.getRGB(x, y);
            }
        }

        return rgbArray;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public WritableRaster getRaster() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        freeMemory(this.address);
    }
}
