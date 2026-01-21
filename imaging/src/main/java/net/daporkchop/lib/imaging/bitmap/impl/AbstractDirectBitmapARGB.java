/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.imaging.bitmap.impl;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.imaging.color.ColorFormat;
import net.daporkchop.lib.imaging.color.ColorFormatABW;
import net.daporkchop.lib.imaging.color.ColorFormatBW;
import net.daporkchop.lib.imaging.color.ColorFormatRGB;
import net.daporkchop.lib.imaging.util.exception.BitmapCoordinatesOutOfBoundsException;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractDirectBitmapARGB extends AbstractDirectBitmap {
    public AbstractDirectBitmapARGB(int width, int height) {
        super(width, height);
    }

    public AbstractDirectBitmapARGB(int width, int height, Object copySrcRef, long copySrcOff) {
        super(width, height, copySrcRef, copySrcOff);
    }

    public AbstractDirectBitmapARGB(int width, int height, RefCountedDirectMemory memory) {
        super(width, height, memory);
    }

    @Override
    public ColorFormat format() {
        return ColorFormat.ARGB;
    }

    @Override
    public long getRaw(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return Integer.toUnsignedLong(PUnsafe.getInt(this.addr(x, y)));
    }

    @Override
    public int getRGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return ColorFormatRGB.fromARGB(PUnsafe.getInt(this.addr(x, y)));
    }

    @Override
    public int getARGB(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return PUnsafe.getInt(this.addr(x, y));
    }

    @Override
    public int getBW(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return ColorFormatBW.fromARGB(PUnsafe.getInt(this.addr(x, y)));
    }

    @Override
    public int getABW(int x, int y) throws BitmapCoordinatesOutOfBoundsException {
        return ColorFormatABW.fromARGB(PUnsafe.getInt(this.addr(x, y)));
    }

    @Override
    public long memorySize() {
        return super.memorySize() << 2L;
    }

    @Override
    protected long addr(int x, int y) {
        return this.ptr + (super.addr(x, y) << 2L);
    }
}
