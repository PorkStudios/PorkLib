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

package net.daporkchop.lib.common.pool.handle;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ReferenceStrength;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link HandledPool} which uses a single global allocation queue.
 *
 * @author DaPorkchop_
 */
final class BasicHandledPool<V> implements HandledPool<V> {
    private final Deque<Ref<V>> deque;
    private final Supplier<V> factory;
    private final ReferenceStrength strength;
    private final int maxCapacity;

    public BasicHandledPool(@NonNull Supplier<V> factory, @NonNull ReferenceStrength strength, int maxCapacity) {
        this.deque = new ArrayDeque<>(positive(maxCapacity, "maxCapacity"));
        this.factory = factory;
        this.strength = strength;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized Handle<V> get() {
        V value = null;
        Ref<V> ref;
        while ((ref = this.deque.poll()) != null && (value = ref.get()) == null) {
        }
        if (value == null)  {
            value = this.factory.get();
            ref = this.strength.create(value);
        }
        //important to create new instance because of reference-counting
        return new HandleImpl(value, ref);
    }

    @RequiredArgsConstructor
    private final class HandleImpl extends AbstractRefCounted implements Handle<V> {
        @NonNull
        protected final V value;
        @NonNull
        protected final Ref<V> ref;

        @Override
        protected void doRelease() {
            synchronized (BasicHandledPool.this) {
                if (BasicHandledPool.this.deque.size() < BasicHandledPool.this.maxCapacity) {
                    BasicHandledPool.this.deque.addFirst(this.ref);
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
