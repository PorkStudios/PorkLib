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

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * @author DaPorkchop_
 */
public class DirectOverflowingIntGrid3d extends DirectIntGrid3d {
    public DirectOverflowingIntGrid3d(int startX, int startY, int startZ, int width, int height, int depth) {
        super(startX, startY, startZ, width, height, depth);
    }

    protected long getPos(int x, int y, int z) {
        return this.pos + (((clamp(x - this.startX, 0, this.width - 1) * this.height + clamp(y - this.startY, 0, this.height - 1)) * this.depth + clamp(z - this.startZ, 0, this.depth - 1)) << 2L);
    }

    @Override
    public boolean isOverflowing() {
        return true;
    }
}
