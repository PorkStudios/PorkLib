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

import static net.daporkchop.lib.math.primitive.PMath.clamp;

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
