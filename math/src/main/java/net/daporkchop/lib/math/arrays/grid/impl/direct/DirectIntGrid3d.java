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

import net.daporkchop.lib.common.util.DirectMemoryHolder;
import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.math.arrays.grid.Grid2d;
import net.daporkchop.lib.math.arrays.grid.Grid3d;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
public class DirectIntGrid3d implements Grid3d, DirectMemoryHolder {
    protected long pos;
    protected final long size;

    protected final int startX;
    protected final int width;
    protected final int startY;
    protected final int height;
    protected final int startZ;
    protected final int depth;

    public DirectIntGrid3d(int startX, int startY, int startZ, int width, int height, int depth) {
        this.size = ((long) width * (long) height * (long) depth) << 2L;
        this.pos = PUnsafe.allocateMemory(this, this.size);

        this.startX = startX;
        this.width = width;
        this.startY = startY;
        this.height = height;
        this.startZ = startZ;
        this.depth = depth;
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
    public int startZ() {
        return this.startZ;
    }

    @Override
    public int endZ() {
        return this.startZ + this.depth;
    }

    @Override
    public double getD(int x, int y, int z) {
        return this.getI(x, y, z);
    }

    @Override
    public int getI(int x, int y, int z) {
        return PUnsafe.getInt(this.getPos(x, y, z));
    }

    @Override
    public void setD(int x, int y, int z, double val) {
        this.setI(x, y, z, floorI(val));
    }

    @Override
    public void setI(int x, int y, int z, int val) {
        PUnsafe.putInt(this.getPos(x, y, z), val);
    }

    protected long getPos(int x, int y, int z) {
        long off = (((x - this.startX) * this.height + y - this.startY) * this.depth + z - this.startZ) << 2L;
        if (off >= this.size || off < 0L) {
            throw new ArrayIndexOutOfBoundsException(String.format("(%d,%d,%d)", x, y, z));
        } else {
            return this.pos + off;
        }
    }

    //directmemoryholder implementations
    @Override
    public synchronized long getMemoryAddress() {
        return this.pos;
    }

    @Override
    public synchronized void releaseMemory() {
        if (this.isMemoryReleased())    {
            throw new IllegalStateException("Memory already released!");
        } else {
            PUnsafe.freeMemory(this.pos);
            this.pos = -1L;
        }
    }
}
