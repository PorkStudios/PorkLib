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
import net.daporkchop.lib.graphics.bitmap.image.PImage;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class DirectImage extends DirectMemoryHolder.AbstractConstantSize implements PImage {
    protected final int width;
    protected final int height;

    protected SoftReference<BufferedImage> bufferedImageCache;

    protected DirectImage(int width, int height, long byteScale)   {
        super((long) width * (long) height * byteScale);
        if (width < 0 || height < 0)    {
            throw new IllegalArgumentException("Width and height must be greater than 0!");
        }

        this.width = width;
        this.height = height;
    }

    public abstract long getByteScale();

    protected long getPos(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height)  {
            throw new IllegalArgumentException(String.format("Coordinate out of bounds: (%d,%d) (width=%d,height=%d)", x, y, this.width, this.height));
        } else {
            return this.pos + (((long) x * (long) this.height + (long) y) * this.getByteScale());
        }
    }

    @Override
    public synchronized BufferedImage getAsBufferedImage() {
        BufferedImage img;
        if (this.bufferedImageCache == null || (img = this.bufferedImageCache.get()) == null)   {
            this.bufferedImageCache = new SoftReference<>(img = new BufferedImage(
                    this.newColorModel(),
                    this.newRaster(),
                    false,
                    null
            ));
        }
        return img;
    }

    protected abstract ColorModel newColorModel();

    protected abstract DataBuffer newDataBuffer();

    protected abstract WritableRaster newRaster();
}
