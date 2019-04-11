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

package net.daporkchop.lib.math.arrays.grid.impl.direct;

import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
public class DirectIntGrid2d implements Grid2d, DirectMemoryHolder {
    protected final long pos;
    protected final long size;

    protected final int startX;
    protected final int width;
    protected final int startY;
    protected final int height;

    protected final PCleaner cleaner;

    public DirectIntGrid2d(int startX, int startY, int width, int height) {
        this.size = ((long) width * (long) height) << 2L;
        this.pos = PUnsafe.allocateMemory(this.size);
        this.cleaner = PCleaner.cleaner(this, this.pos);

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

    @Override
    public void release() throws AlreadyReleasedException {
        synchronized (this.cleaner) {
            if (this.cleaner.isCleaned())   {
                throw new AlreadyReleasedException();
            }
            this.cleaner.clean();
        }
    }
}
