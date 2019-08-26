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

package net.daporkchop.lib.binary.buf;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * @author DaPorkchop_
 */
public abstract class ReferenceCountedPorkBuf implements PorkBuf {
    protected final Counter refCounter = new Counter() {
        @Override
        protected void doRelease() {
            ReferenceCountedPorkBuf.this.doRelease();
        }
    };

    protected abstract void doRelease();

    @Override
    public PorkBuf retain() throws AlreadyReleasedException {
        this.refCounter.retain();
        return this;
    }

    @Override
    public PorkBuf release() throws AlreadyReleasedException {
        this.refCounter.release();
        return this;
    }

    @Override
    public int refCount() {
        return this.refCounter.refCount();
    }

    /**
     * Holds the reference count for a {@link PorkBuf}.
     *
     * @author DaPorkchop_
     */
    @Getter
    @Accessors(fluent = true)
    public abstract static class Counter {
        protected static final long REFCOUNT_OFFSET = PUnsafe.pork_getOffset(Counter.class, "refCount");

        protected volatile int refCount = 1;

        public void retain()    {
            int v;
            do {
                if ((v = this.refCount) == 0)   {
                    throw new AlreadyReleasedException();
                }
            } while (!PUnsafe.compareAndSwapInt(this, REFCOUNT_OFFSET, v, v + 1));
        }

        public void release()   {
            int v;
            do {
                if ((v = this.refCount) == 0)   {
                    throw new AlreadyReleasedException();
                }
            } while (!PUnsafe.compareAndSwapInt(this, REFCOUNT_OFFSET, v, v - 1));
            if (v - 1 == 0) {
                this.doRelease();
            }
        }

        protected abstract void doRelease();
    }
}
