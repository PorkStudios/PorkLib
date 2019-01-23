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

package net.daporkchop.lib.encoding.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.BitSet;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class XYIndexedBitSet {
    protected final int size;

    @NonNull
    protected final BitSet bitSet;

    public boolean get(int x, int y) {
        return this.bitSet.get(x * this.size + y);
    }

    public void set(int x, int y) {
        this.bitSet.set(x * this.size + y);
    }

    public void set(int x, int y, boolean val) {
        this.bitSet.set(x * this.size + y, val);
    }

    public void clear(int x, int y) {
        this.bitSet.clear(x * this.size + y);
    }

    public void clear() {
        this.bitSet.clear();
    }

    public void setArea(int x, int y, int w, int h) {
        for (int yy = h - 1; yy >= 0; yy--)  {
            this.bitSet.set(x * this.size + y + yy, (x + w) * this.size + y + yy);
        }
    }

    public void clearArea(int x, int y, int w, int h) {
        for (int yy = h - 1; yy >= 0; yy--)  {
            this.bitSet.clear(x * this.size + y + yy, (x + w) * this.size + y + yy);
        }
    }
}
