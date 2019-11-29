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

package net.daporkchop.lib.graphics.bitmap.image;

import net.daporkchop.lib.graphics.bitmap.PIcon;
import net.daporkchop.lib.graphics.bitmap.PImage;
import net.daporkchop.lib.graphics.bitmap.icon.DirectIconBW;
import net.daporkchop.lib.graphics.bitmap.impl.AbstractDirectBitmapBW;
import net.daporkchop.lib.graphics.color.ColorFormatBW;
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * An implementation of {@link PImage} that uses the BW color format, backed by direct memory.
 *
 * @author DaPorkchop_
 */
public final class DirectImageBW extends AbstractDirectBitmapBW implements PImage {
    public DirectImageBW(int width, int height) {
        super(width, height);
    }

    public DirectImageBW(int width, int height, Object copySrcRef, long copySrcOff) {
        super(width, height, copySrcRef, copySrcOff);
    }

    @Override
    public void setRaw(int x, int y, long color) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putByte(this.addr(x, y), (byte) color);
    }

    @Override
    public void setRGB(int x, int y, int rgb) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putByte(this.addr(x, y), (byte) ColorFormatBW.fromARGB(rgb)); //doesn't matter that the input isn't ARGB, the alpha layer is discarded anyway
    }

    @Override
    public void setARGB(int x, int y, int argb) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putByte(this.addr(x, y), (byte) ColorFormatBW.fromARGB(argb));
    }

    @Override
    public void setBW(int x, int y, int bw) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putByte(this.addr(x, y), (byte) bw);
    }

    @Override
    public void setABW(int x, int y, int abw) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putByte(this.addr(x, y), (byte) abw);
    }

    @Override
    public PIcon immutableSnapshot() {
        return new DirectIconBW(this.width, this.height, null, this.ptr);
    }
}
