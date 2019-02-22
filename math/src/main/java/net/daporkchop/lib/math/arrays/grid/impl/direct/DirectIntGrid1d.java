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

import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.math.arrays.grid.Grid1d;
import sun.misc.Cleaner;

/**
 * @author DaPorkchop_
 */
public class DirectIntGrid1d implements Grid1d {
    protected final Object cleaner;
    protected final long pos;

    public DirectIntGrid1d(int startX, int x)   {
        this.pos = PUnsafe.allocateMemory(4L * x);
        this.cleaner = PorkUtil.cleaner(this, () -> PUnsafe.freeMemory(this.pos));
    }

    @Override
    public int startX() {
        return 0;
    }

    @Override
    public int endX() {
        return 0;
    }

    @Override
    public double getD(int x) {
        return 0;
    }

    @Override
    public int getI(int x) {
        return 0;
    }
}
