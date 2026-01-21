/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.imaging.bitmap.impl;

import net.daporkchop.lib.common.misc.refcount.RefCountedDirectMemory;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

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
