/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.imaging.bitmap.image;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.bitmap.icon.DirectIconBW;
import net.daporkchop.lib.imaging.bitmap.impl.AbstractDirectBitmapBW;
import net.daporkchop.lib.imaging.color.ColorFormatBW;
import net.daporkchop.lib.imaging.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

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

    public DirectImageBW(int width, int height, RefCountedDirectMemory memory) {
        super(width, height, memory);
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

    @Override
    public PIcon unsafeImmutableView() {
        return new DirectIconBW(this.width, this.height, this.memory);
    }

    @Override
    public PImage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
