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

package net.daporkchop.lib.graphics.impl.icon;

import lombok.Getter;
import net.daporkchop.lib.common.util.DirectMemoryHolder;
import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.graphics.PIcon;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class DirectIcon implements PIcon, DirectMemoryHolder {
    protected long pos;
    protected final long size;

    protected final int width;
    protected final int height;
    protected final boolean bw;

    public DirectIcon(int width, int height, boolean bw)    {
        this.size = ((long) width * (long) height) << (bw ? 0L : 2L);
        this.pos = PUnsafe.allocateMemory(this.size);

        this.width = width;
        this.height = height;
        this.bw = bw;
    }

    @Override
    public int getARGB(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height)  {
            throw new ArrayIndexOutOfBoundsException(String.format("(%d,%d) w=%d,h=%d", x, y, this.width, this.height));
        } else if (this.bw)    {
            return 0xFF000000 | (PUnsafe.getByte(this.pos + (long) x * (long) this.height + (long) y) & 0xFF);
        } else {
            return PUnsafe.getInt(this.pos + (((long) x * (long) this.height + (long) y) << 2L));
        }
    }

    @Override
    public boolean isBW() {
        return this.bw;
    }

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
