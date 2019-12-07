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

package net.daporkchop.lib.common.pool.handle;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.cache.FastThreadCache;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.function.Supplier;

/**
 * Implementation of {@link HandledPool} which uses a sized-capped arena for each thread.
 *
 * @author DaPorkchop_
 */
public final class DefaultThreadHandledPool<V> implements HandledPool<V> {
    protected final FastThreadCache<Arena<V>> arenas;
    protected final Supplier<V>               factory;

    public DefaultThreadHandledPool(@NonNull Supplier<V> factory, int maxSize) {
        if (maxSize <= 0)   {
            throw new IllegalArgumentException("maxSize must be at least 1!");
        }
        this.arenas = new FastThreadCache<>(() -> new Arena<>(this, maxSize));
        this.factory = factory;
    }

    @Override
    public Handle<V> get() {
        return this.arenas.get().get();
    }

    private static final class Arena<V> {
        protected final DefaultThreadHandledPool<V> pool;
        protected final HandleImpl<V>[]             handles;
        protected int index = 0;

        @SuppressWarnings("unchecked")
        public Arena(@NonNull DefaultThreadHandledPool<V> pool, int maxSize) {
            this.pool = pool;
            this.handles = (HandleImpl<V>[]) new HandleImpl[maxSize];
        }

        public synchronized Handle<V> get() {
            if (this.index > 0) {
                HandleImpl<V> handle = this.handles[--this.index];
                this.handles[this.index] = null;
                return handle;
            } else {
                return new HandleImpl<>(this.pool, this, this.pool.factory.get());
            }
        }

        public synchronized void put(@NonNull HandleImpl<V> handle) {
            if (this.index < this.handles.length) {
                this.handles[this.index++] = handle;
            } //ignore handle if arena is already at max size
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    private static final class HandleImpl<V> implements Handle<V> {
        protected static final long ACTIVE_OFFSET = PUnsafe.pork_getOffset(HandleImpl.class, "active");

        protected final DefaultThreadHandledPool<V> pool;

        protected final Arena<V> arena;
        protected final V        value;

        protected volatile int active = 1;

        @Override
        public void close() {
            if (PUnsafe.compareAndSwapInt(this, ACTIVE_OFFSET, 1, 0)) {
                this.arena.put(this);
            }
        }

        protected HandleImpl<V> activate() {
            if (!PUnsafe.compareAndSwapInt(this, ACTIVE_OFFSET, 0, 1)) {
                throw new IllegalStateException();
            }
            return this;
        }
    }
}
