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

package net.daporkchop.lib.common.pool.handle;

import io.netty.util.Recycler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.util.function.Supplier;

/**
 * An implementation of {@link HandledPool} that operates using Netty's {@link Recycler}.
 *
 * @author DaPorkchop_
 */
final class RecyclingHandledPool<V> implements HandledPool<V> {
    private final Recycler<Wrapper<V>> recycler;

    public RecyclingHandledPool(@NonNull Supplier<V> factory, int maxCapacityPerThread) {
        this.recycler = new Recycler<Wrapper<V>>(maxCapacityPerThread) {
            @Override
            protected Wrapper<V> newObject(Handle<Wrapper<V>> handle) {
                return new Wrapper<>(handle, factory.get());
            }
        };
    }

    @Override
    public Handle<V> get() {
        //important to create new instance because of reference-counting
        return new HandleImpl<>(this.recycler.get());
    }

    @RequiredArgsConstructor
    private static final class Wrapper<V> {
        @NonNull
        private final Recycler.Handle<Wrapper<V>> handle;
        @NonNull
        private final V value;
    }

    @RequiredArgsConstructor
    private static final class HandleImpl<V> extends AbstractRefCounted implements Handle<V> {
        @NonNull
        private final Wrapper<V> wrapper;

        @Override
        protected void doRelease() {
            this.wrapper.handle.recycle(this.wrapper);
        }

        @Override
        public V get() {
            this.ensureNotReleased();
            return this.wrapper.value;
        }

        @Override
        public Handle<V> retain() throws AlreadyReleasedException {
            super.retain();
            return this;
        }
    }
}
