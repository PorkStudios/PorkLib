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

package net.daporkchop.lib.math.grid.impl.direct;

import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.math.grid.Grid2d;

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * @author DaPorkchop_
 */
public class DirectIntGrid2d extends DirectMemoryHolder.AbstractConstantSize implements Grid2d {
    protected final int startX;
    protected final int width;
    protected final int startY;
    protected final int height;

    public DirectIntGrid2d(int startX, int startY, int width, int height) {
        super(((long) width * (long) height) << 2L);

        this.startX = startX;
        this.width = width;
        this.startY = startY;
        this.height = height;
    }

    @Override
    public int startX() {
        return this.startX;
    }

    @Override
    public int endX() {
        return this.startX + this.width;
    }

    @Override
    public int startY() {
        return this.startY;
    }

    @Override
    public int endY() {
        return this.startY + this.height;
    }

    @Override
    public double getD(int x, int y) {
        return this.getI(x, y);
    }

    @Override
    public int getI(int x, int y) {
        return PUnsafe.getInt(this.getPos(x, y));
    }

    @Override
    public void setD(int x, int y, double val) {
        this.setI(x, y, floorI(val));
    }

    @Override
    public void setI(int x, int y, int val) {
        PUnsafe.putInt(this.getPos(x, y), val);
    }

    protected long getPos(int x, int y) {
        long off = ((x - this.startX) * this.height + y - this.startY) << 2L;
        if (off >= this.size || off < 0L) {
            throw new ArrayIndexOutOfBoundsException(String.format("(%d,%d)", x, y));
        } else {
            return this.pos + off;
        }
    }
}
