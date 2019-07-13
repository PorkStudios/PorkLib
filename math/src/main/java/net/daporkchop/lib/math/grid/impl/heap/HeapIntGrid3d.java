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

package net.daporkchop.lib.math.grid.impl.heap;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.math.grid.Grid3d;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class HeapIntGrid3d implements Grid3d {
    @NonNull
    protected final int[] values;

    protected final int startX;
    protected final int startY;
    protected final int startZ;

    protected final int width;
    protected final int height;
    protected final int depth;

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
        return this.values[((x - this.startX) * this.height + y - this.startY) * this.depth + z - this.startZ];
    }

    @Override
    public void setD(int x, int y, int z, double val) {
        this.setI(x, y, z, floorI(val));
    }

    @Override
    public void setI(int x, int y, int z, int val) {
        this.values[((x - this.startX) * this.height + y - this.startY) * this.depth + z - this.startZ] = val;
    }
}
