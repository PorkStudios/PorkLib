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

package net.daporkchop.lib.graphics.bitmap.image;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.graphics.bitmap.PIcon;
import net.daporkchop.lib.graphics.bitmap.PImage;
import net.daporkchop.lib.graphics.bitmap.icon.DirectIconARGB;
import net.daporkchop.lib.graphics.bitmap.impl.AbstractDirectBitmap;
import net.daporkchop.lib.graphics.bitmap.impl.AbstractDirectBitmapARGB;
import net.daporkchop.lib.graphics.color.ColorFormat;
import net.daporkchop.lib.graphics.color.ColorFormatABW;
import net.daporkchop.lib.graphics.color.ColorFormatBW;
import net.daporkchop.lib.graphics.color.ColorFormatRGB;
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An implementation of {@link PImage} that uses the ARGB color format, backed by direct memory.
 *
 * @author DaPorkchop_
 */
public final class DirectImageARGB extends AbstractDirectBitmapARGB implements PImage {
    public DirectImageARGB(int width, int height) {
        super(width, height);
    }

    public DirectImageARGB(int width, int height, Object copySrcRef, long copySrcOff) {
        super(width, height, copySrcRef, copySrcOff);
    }

    public DirectImageARGB(int width, int height, RefCountedDirectMemory memory) {
        super(width, height, memory);
    }

    @Override
    public void setRaw(int x, int y, long color) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putInt(this.addr(x, y), (int) color);
    }

    @Override
    public void setRGB(int x, int y, int rgb) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putInt(this.addr(x, y), ColorFormatRGB.toARGB(rgb));
    }

    @Override
    public void setARGB(int x, int y, int argb) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putInt(this.addr(x, y), argb);
    }

    @Override
    public void setBW(int x, int y, int bw) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putInt(this.addr(x, y), ColorFormatBW.toARGB(bw));
    }

    @Override
    public void setABW(int x, int y, int abw) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putInt(this.addr(x, y), ColorFormatABW.toARGB(abw));
    }

    @Override
    public PIcon immutableSnapshot() {
        return new DirectIconARGB(this.width, this.height, null, this.ptr);
    }

    @Override
    public PIcon unsafeImmutableView() {
        return new DirectIconARGB(this.width, this.height, this.memory);
    }

    @Override
    public PImage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
