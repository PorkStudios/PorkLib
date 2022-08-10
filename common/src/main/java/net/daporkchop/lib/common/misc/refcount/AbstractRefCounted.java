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

package net.daporkchop.lib.common.misc.refcount;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

/**
 * An abstract implementation of {@link RefCounted}, which handles incrementing and decrementing the counter internally
 * and provides a single method to be implemented by subclasses which does the actual releasing.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractRefCounted implements RefCounted {
    protected static final long REFCNT_OFFSET = PUnsafe.pork_getOffset(AbstractRefCounted.class, "refCnt");

    private volatile int refCnt = 1;

    @Override
    public RefCounted retain() throws AlreadyReleasedException {
        int refCnt;
        do {
            if ((refCnt = PUnsafe.getIntVolatile(this, REFCNT_OFFSET)) == 0) {
                throw new AlreadyReleasedException();
            }
        } while (!PUnsafe.compareAndSwapInt(this, REFCNT_OFFSET, refCnt, refCnt + 1));
        return this;
    }

    @Override
    public boolean release() throws AlreadyReleasedException {
        int refCnt;
        do {
            if ((refCnt = PUnsafe.getIntVolatile(this, REFCNT_OFFSET)) == 0) {
                throw new AlreadyReleasedException();
            }
        } while (!PUnsafe.compareAndSwapInt(this, REFCNT_OFFSET, refCnt, refCnt - 1));

        if (refCnt == 1) {
            //old reference count was 1, meaning now it's 0 and the instance should be released
            this.doRelease();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Actually releases this instance.
     * <p>
     * This method will only be called once once the reference count reaches 0.
     */
    protected abstract void doRelease();

    protected void ensureNotReleased()  {
        if (PUnsafe.getIntVolatile(this, REFCNT_OFFSET) == 0)    {
            throw new AlreadyReleasedException();
        }
    }

    /**
     * Variant of {@link AbstractRefCounted} that is synchronized on a given mutex.
     * <p>
     * Implementations of this class are expected to synchronize access to all methods that could fail if the instance is released on {@code this.mutex}.
     *
     * @author DaPorkchop_
     */
    public abstract static class Synchronized extends AbstractRefCounted {
        protected final Object mutex;

        public Synchronized() {
            this.mutex = this;
        }

        public Synchronized(@NonNull Object mutex) {
            this.mutex = mutex;
        }

        @Override
        public RefCounted retain() throws AlreadyReleasedException {
            synchronized (this.mutex)   {
                return super.retain();
            }
        }

        @Override
        public boolean release() throws AlreadyReleasedException {
            synchronized (this.mutex)   {
                return super.release();
            }
        }
    }
}
