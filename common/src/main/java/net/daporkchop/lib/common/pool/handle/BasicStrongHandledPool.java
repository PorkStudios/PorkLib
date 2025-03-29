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
 *
 */

package net.daporkchop.lib.common.pool.handle;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link HandledPool} which uses a single global allocation queue.
 *
 * @author DaPorkchop_
 */
final class BasicStrongHandledPool<V> implements HandledPool<V> {
    private final Deque<V> deque;
    private final Supplier<V> factory;
    private final int maxCapacity;

    public BasicStrongHandledPool(@NonNull Supplier<V> factory, int maxCapacity) {
        this.deque = new ArrayDeque<>(positive(maxCapacity, "maxCapacity"));
        this.factory = factory;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized Handle<V> get() {
        V value = this.deque.poll();
        if (value == null) {
            value = Objects.requireNonNull(this.factory.get());
        }
        //important to create new instance because of reference-counting
        return new HandleImpl(value);
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    private final class HandleImpl extends AbstractRefCounted implements Handle<V> {
        private final @NonNull V value;

        @Override
        protected void doRelease() {
            synchronized (BasicStrongHandledPool.this) {
                if (BasicStrongHandledPool.this.deque.size() < BasicStrongHandledPool.this.maxCapacity) {
                    BasicStrongHandledPool.this.deque.addFirst(this.value);
                }
            }
        }

        @Override
        public V get() {
            this.ensureNotReleased();
            return this.value;
        }

        @Override
        public Handle<V> retain() throws AlreadyReleasedException {
            super.retain();
            return this;
        }
    }
}
