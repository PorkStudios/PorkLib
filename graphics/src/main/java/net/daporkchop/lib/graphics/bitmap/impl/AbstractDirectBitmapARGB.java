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

package net.daporkchop.lib.graphics.bitmap.impl;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.graphics.color.ColorFormat;
import net.daporkchop.lib.graphics.color.ColorFormatABW;
import net.daporkchop.lib.graphics.color.ColorFormatBW;
import net.daporkchop.lib.graphics.color.ColorFormatRGB;
import net.daporkchop.lib.graphics.util.exception.BitmapCoordinatesOutOfBoundsException;
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
