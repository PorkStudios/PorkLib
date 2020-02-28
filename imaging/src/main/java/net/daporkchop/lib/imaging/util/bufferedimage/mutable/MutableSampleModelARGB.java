/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.imaging.util.bufferedimage.mutable;

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.util.bufferedimage.AbstractGiantSampleModelARGB;

import java.awt.image.DataBuffer;

/**
 * @author DaPorkchop_
 */
public final class MutableSampleModelARGB extends AbstractGiantSampleModelARGB<PImage> {
    public MutableSampleModelARGB(@NonNull PImage bitmap) {
        super(bitmap);
    }

    @Override
    public void setDataElements(int x, int y, Object obj, DataBuffer data) {
        if ((x < 0) || (y < 0) || (x >= this.width) || (y >= this.height)) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }

        if (obj instanceof int[]) {
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

        data.setElem(x, y, iArray[0]);
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
                data.setElem(x + j, y + i, iArray[srcOffset++]);
            }
        }
    }
}
