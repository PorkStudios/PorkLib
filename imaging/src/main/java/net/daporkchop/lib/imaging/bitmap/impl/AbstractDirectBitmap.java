/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.imaging.bitmap.impl;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Base implementation of {@link PBitmap} backed by direct memory.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDirectBitmap extends AbstractBitmap {
    protected final long ptr;

    protected final RefCountedDirectMemory memory;

    public AbstractDirectBitmap(int width, int height) {
        super(width, height);

        this.memory = new RefCountedDirectMemory(this.memorySize());
        this.ptr = this.memory.addr();
    }

    public AbstractDirectBitmap(int width, int height, Object copySrcRef, long copySrcOff) {
        this(width, height);

        PUnsafe.copyMemory(copySrcRef, copySrcOff, null, this.ptr, this.memorySize());
    }

    public AbstractDirectBitmap(int width, int height, RefCountedDirectMemory memory)   {
        super(width, height);

        this.ptr = (this.memory = memory).addr();
    }

    public long memorySize() {
        return (long) this.width * (long) this.height;
    }

    protected long addr(int x, int y) {
        this.assertInBounds(x, y);
        return (Integer.toUnsignedLong(y) * this.width + x);
    }

    @Override
    public int refCnt() {
        return this.memory.refCnt();
    }

    @Override
    public PBitmap retain() throws AlreadyReleasedException {
        this.memory.retain();
        return this;
    }

    @Override
    public boolean release() throws AlreadyReleasedException {
        return this.memory.release();
    }
}
