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

package net.daporkchop.lib.common.pool.array;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ReferenceType;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Simple implementation of {@link ArrayAllocator} which caches arrays by their size in powers of 2.
 *
 * @author DaPorkchop_
 */
final class Pow2ArrayAllocator<T> extends AbstractArrayAllocator<T> {
    protected final Deque<Ref<T>>[] arenas = uncheckedCast(PArrays.filled(32, Deque[]::new, (Supplier<Deque>) ArrayDeque::new));
    protected final ReferenceType referenceType;
    protected final int maxCapacity;

    public Pow2ArrayAllocator(@NonNull IntFunction<T> lambda, @NonNull ReferenceType referenceType, int maxCapacity) {
        super(lambda);

        this.referenceType = referenceType;
        this.maxCapacity = positive(maxCapacity, "maxCapacity");
    }

    public Pow2ArrayAllocator(@NonNull Class<?> componentClass, @NonNull ReferenceType referenceType, int maxCapacity) {
        super(componentClass);

        this.referenceType = referenceType;
        this.maxCapacity = positive(maxCapacity, "maxCapacity");
    }

    @Override
    public Handle<T> atLeast(int minSize) {
        return this.getPooled(notNegative(minSize, "minSize"));
    }

    @Override
    public Handle<T> exactly(int size) {
        notNegative(size, "size");
        if (size != 0 && !BinMath.isPow2(size))  {
            //requested size is not a power of 2, we can't return a pooled array
            return new HandleImpl<>(this.createArray(size), null, null, this.maxCapacity);
        }
        return this.getPooled(size);
    }

    protected HandleImpl<T> getPooled(int size)  {
        int bits = size == 0 ? 0 : 31 - Integer.numberOfLeadingZeros(size - 1);
        Deque<Ref<T>> arena = this.arenas[bits];
        T value = null;
        Ref<T> ref;
        synchronized (arena) {
            while ((ref = arena.poll()) != null && (value = ref.get()) == null) {
            }
        }
        if (value == null)  {
            value = this.createArray(size);
            ref = this.referenceType.create(value);
        }
        return new HandleImpl<>(value, ref, arena, this.maxCapacity);
    }

    @RequiredArgsConstructor
    private static final class HandleImpl<T> extends AbstractRefCounted implements Handle<T>    {
        @NonNull
        protected final T value;
        protected final Ref<T> ref;
        protected final Deque<Ref<T>> arena;
        protected final int maxCapacity; //store copy of field here to avoid holding a reference to main allocator instance

        @Override
        protected void doRelease() {
            if (this.arena != null && this.ref != null) {
                synchronized (this.arena) {
                    if (this.arena.size() < this.maxCapacity) {
                        this.arena.addFirst(this.ref);
                    }
                }
            }
        }

        @Override
        public T get() {
            this.ensureNotReleased();
            return this.value;
        }

        @Override
        public Handle<T> retain() throws AlreadyReleasedException {
            super.retain();
            return this;
        }
    }
}
