/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.common.misc.refcount;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.daporkchop.lib.common.annotation.Borrow;
import net.daporkchop.lib.common.annotation.Move;
import net.daporkchop.lib.common.annotation.ThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.closeable.QuietCloseable;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A wrapper which adds reference counting to a {@link AutoCloseable closeable} resource.
 * <p>
 * This uses the {@link QuietCloseable} model for all {@link AutoCloseable} types. Any checked exceptions thrown when closing the value will be rethrown unsafely.
 *
 * @author DaPorkchop_
 */
@ThreadSafe
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RefCount<T extends AutoCloseable> implements QuietCloseable {
    private static final AtomicIntegerFieldUpdater<RefCount> REFCOUNT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(RefCount.class, "refCount");

    public static <T extends AutoCloseable> RefCount<T> create(@Move @NonNull T value) {
        return new RefCount<>(value);
    }

    private final @NonNull T value;
    private volatile int refCount = 1;

    /**
     * @return the current reference count
     */
    public @NotNegative int refCount() {
        //can be negative for a moment if multiple threads try to release at once
        return Math.max(this.refCount, 0);
    }

    /**
     * @return {@code true} if this instance is live, or {@code false} if the reference count has already reached 0
     */
    public boolean isLive() {
        return this.refCount <= 0;
    }

    /**
     * Ensures that this {@link RefCount} instance is live.
     *
     * @throws AlreadyReleasedException if the reference count has already reached zero
     */
    public void checkLive() throws AlreadyReleasedException {
        if (this.refCount <= 0) {
            throw new AlreadyReleasedException();
        }
    }

    /**
     * @return a reference to the current value
     * @throws AlreadyReleasedException if the reference count has already reached zero
     */
    public @Borrow T get() throws AlreadyReleasedException {
        this.checkLive();
        return this.value;
    }

    /**
     * Increments the reference count by 1.
     *
     * @throws IllegalStateException if the reference count has already reached zero
     */
    public void retain() {
        int refCount;
        do {
            if ((refCount = this.refCount) <= 0) {
                throw new IllegalStateException("reference count has already reached zero!");
            }
        } while (!REFCOUNT_UPDATER.weakCompareAndSet(this, refCount, Math.incrementExact(refCount)));
    }

    /**
     * Increments the reference count by the given amount.
     *
     * @param delta the number of times to increment the reference count
     * @throws IllegalStateException if the reference count has already reached zero
     */
    public void retain(@NotNegative int delta) {
        notNegative(delta);

        int refCount;
        do {
            if ((refCount = this.refCount) <= 0) {
                throw new IllegalStateException("reference count has already reached zero!");
            }
        } while (!REFCOUNT_UPDATER.weakCompareAndSet(this, refCount, Math.addExact(refCount, delta)));
    }

    /**
     * Decrements the reference count by 1. If the reference count reaches 0, releases the value owned by this instance.
     *
     * @throws IllegalStateException if the reference count has already reached zero
     */
    public void release() {
        int prevRefCount = REFCOUNT_UPDATER.getAndDecrement(this);
        if (prevRefCount == 1) { //the reference count is now 0, release
            this.releaseValue();
        } else if (prevRefCount <= 0) { //the reference count was already zero!
            //we don't need to release this instance, but we should still reset the reference count to zero so that it doesn't underflow if someone keeps spamming close() on an
            //  already released instance.
            //in theory this could break if more than Integer.MAX_VALUE threads tried to run this at the same time, but that doesn't seem like a realistic scenario.
            REFCOUNT_UPDATER.lazySet(this, 0);
            throw new IllegalStateException("reference count has already reached zero!");
        }
    }

    /**
     * Decrements the reference count by the given amount. If the reference count reaches 0, releases the value owned by this instance.
     *
     * @param delta the number of times to decrement the reference count
     * @throws IllegalStateException    if the reference count has already reached zero
     * @throws IllegalArgumentException if the reference count has already reached zero
     */
    public void release(@NotNegative int delta) {
        notNegative(delta);

        int refCount;
        do {
            if ((refCount = this.refCount) <= 0) {
                throw new IllegalStateException("reference count has already reached zero!");
            } else if (refCount < delta) {
                throw new IllegalArgumentException("cannot decrement reference count " + refCount + " by " + delta);
            }
        } while (!REFCOUNT_UPDATER.weakCompareAndSet(this, refCount, refCount - delta));

        if (delta > 0 && refCount == delta) {
            this.releaseValue();
        }
    }

    /**
     * Sets the reference count to 0 and releases the value owned by this instance.
     *
     * @throws IllegalStateException if the reference count has already reached zero
     */
    public void releaseAll() {
        if (REFCOUNT_UPDATER.getAndSet(this, 0) > 0) { //the reference count is now 0, release
            this.releaseValue();
        } else {
            throw new IllegalStateException("reference count has already reached zero!");
        }
    }

    /**
     * Sets the reference count to 0 and releases the value owned by this instance. If the reference count has already reached 0, this method does nothing.
     *
     * @return {@code true} if the reference count was previously positive and is now 0, or {@code false} if no action was performed
     */
    public boolean tryReleaseAll() {
        if (REFCOUNT_UPDATER.getAndSet(this, 0) > 0) { //the reference count is now 0, release
            this.releaseValue();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Decrements the reference count by 1. If the reference count reaches 0, releases the value owned by this instance. If the reference count has already reached 0,
     * this method does nothing.
     */
    @Override
    public void close() {
        int prevRefCount = REFCOUNT_UPDATER.getAndDecrement(this);
        if (prevRefCount == 1) { //the reference count is now 0, release
            this.releaseValue();
        } else if (prevRefCount <= 0) { //the reference count was already zero!
            //we don't need to release this instance, but we should still reset the reference count to zero so that it doesn't underflow if someone keeps spamming close() on an
            //  already released instance.
            //in theory this could break if more than Integer.MAX_VALUE threads tried to run this at the same time, but that doesn't seem like a realistic scenario.
            REFCOUNT_UPDATER.lazySet(this, 0);
        }
    }

    @SneakyThrows
    @SuppressWarnings("RedundantThrows")
    private void releaseValue() {
        this.value.close();
    }
}
