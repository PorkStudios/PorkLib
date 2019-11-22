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

package net.daporkchop.lib.math.grid.impl.direct;

import net.daporkchop.lib.unsafe.capability.DirectMemoryHolder;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.math.grid.Grid1d;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
public class DirectIntGrid1d extends DirectMemoryHolder.AbstractConstantSize implements Grid1d {
    protected final int startX;
    protected final int width;

    public DirectIntGrid1d(int startX, int width) {
        super(width << 2L);

        this.startX = startX;
        this.width = width;
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
    public double getD(int x) {
        return this.getI(x);
    }

    @Override
    public int getI(int x) {
        return PUnsafe.getInt(this.getPos(x));
    }

    @Override
    public void setD(int x, double val) {
        this.setI(x, floorI(val));
    }

    @Override
    public void setI(int x, int val) {
        PUnsafe.putInt(this.getPos(x), val);
    }

    protected long getPos(int x) {
        long off = (x - this.startX) << 2L;
        if (off >= this.size || off < 0L) {
            throw new ArrayIndexOutOfBoundsException(String.format("%d", x));
        } else {
            return this.pos + off;
        }
    }
}
