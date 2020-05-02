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

package net.daporkchop.lib.unsafe.util;

import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.capability.Releasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An abstract implementation of {@link Releasable}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractReleasable implements Releasable {
    private static final long RELEASED_OFFSET = PUnsafe.pork_getOffset(AbstractReleasable.class, "released");

    private volatile int released = 0;

    @Override
    public void release() throws AlreadyReleasedException {
        if (!PUnsafe.compareAndSwapInt(this, RELEASED_OFFSET, 0, 1)) {
            throw new AlreadyReleasedException();
        }

        this.doRelease();
    }

    /**
     * Actually releases this instance's resources.
     * <p>
     * Will only be called once.
     */
    protected abstract void doRelease();

    /**
     * Asserts that this instance has not been released.
     *
     * @throws AlreadyReleasedException if this instance has been released
     */
    protected final void assertNotReleased() throws AlreadyReleasedException {
        if (this.released != 0) {
            throw new AlreadyReleasedException();
        }
    }

    /**
     * Variant of {@link AbstractReleasable} that is synchronized on a given mutex.
     * <p>
     * Implementations of this class are expected to synchronize access to all methods that could fail if the instance is released on {@code this.mutex}.
     *
     * @author DaPorkchop_
     */
    public abstract static class Synchronized extends AbstractReleasable {
        protected final Object mutex;

        public Synchronized() {
            this.mutex = this;
        }

        public Synchronized(@NonNull Object mutex) {
            this.mutex = mutex;
        }

        @Override
        public void release() throws AlreadyReleasedException {
            synchronized (this.mutex)   {
                super.release();
            }
        }
    }
}
