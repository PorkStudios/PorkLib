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
import net.daporkchop.lib.graphics.bitmap.icon.DirectIconABW;
import net.daporkchop.lib.graphics.bitmap.impl.AbstractDirectBitmapABW;
import net.daporkchop.lib.graphics.color.ColorFormatABW;
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An implementation of {@link PImage} that uses the ABW color format, backed by direct memory.
 *
 * @author DaPorkchop_
 */
public final class DirectImageABW extends AbstractDirectBitmapABW implements PImage {
    public DirectImageABW(int width, int height) {
        super(width, height);
    }

    public DirectImageABW(int width, int height, Object copySrcRef, long copySrcOff) {
        super(width, height, copySrcRef, copySrcOff);
    }

    public DirectImageABW(int width, int height, RefCountedDirectMemory memory) {
        super(width, height, memory);
    }

    @Override
    public void setRaw(int x, int y, long color) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putChar(this.addr(x, y), (char) color);
    }

    @Override
    public void setRGB(int x, int y, int rgb) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putChar(this.addr(x, y), (char) (0xFF00 | ColorFormatABW.fromARGB(rgb)));
    }

    @Override
    public void setARGB(int x, int y, int argb) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putChar(this.addr(x, y), (char) ColorFormatABW.fromARGB(argb));
    }

    @Override
    public void setBW(int x, int y, int bw) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putChar(this.addr(x, y), (char) (0xFF00 | bw));
    }

    @Override
    public void setABW(int x, int y, int abw) throws BitmapCoordinatesOutOfBoundsException {
        PUnsafe.putChar(this.addr(x, y), (char) abw);
    }

    @Override
    public PIcon immutableSnapshot() {
        return new DirectIconABW(this.width, this.height, null, this.ptr);
    }

    @Override
    public PIcon unsafeImmutableView() {
        return new DirectIconABW(this.width, this.height, this.memory);
    }

    @Override
    public PImage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
