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
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ReferenceType;
import net.daporkchop.lib.common.util.PArrays;

import java.lang.reflect.Array;
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
final class StrongPow2ArrayAllocator<V> extends AbstractArrayAllocator<V> {
    protected final Deque<V>[] arenas = uncheckedCast(PArrays.filled(32, Deque[]::new, (Supplier<Deque>) ArrayDeque::new));
    protected final int maxCapacity;

    public StrongPow2ArrayAllocator(@NonNull IntFunction<V> lambda, int maxCapacity) {
        super(lambda);

        this.maxCapacity = positive(maxCapacity, "maxCapacity");
    }

    public StrongPow2ArrayAllocator(@NonNull Class<?> componentClass, int maxCapacity) {
        super(componentClass);

        this.maxCapacity = positive(maxCapacity, "maxCapacity");
    }

    @Override
    public V atLeast(int length) {
        return this.getPooled(notNegative(length, "size"));
    }

    @Override
    public V exactly(int length) {
        notNegative(length, "size");
        if (length != 0 && !BinMath.isPow2(length)) {
            //requested size is not a power of 2, we can't return a pooled array
            return this.createArray(length);
        }
        return this.getPooled(length);
    }

    protected V getPooled(int length) {
        int bits = length == 0 ? 0 : 31 - Integer.numberOfLeadingZeros(length - 1);
        Deque<V> arena = this.arenas[bits];
        V value;
        synchronized (arena) {
            value = arena.poll();
        }
        if (value == null) {
            value = this.createArray(2 << bits);
        }
        return value;
    }

    @Override
    public void release(@NonNull V array) {
        int length = Array.getLength(array);
        int bits = length == 0 ? 0 : 31 - Integer.numberOfLeadingZeros(length - 1);
        Deque<V> arena = this.arenas[bits];
        synchronized (arena) {
            if (arena.size() < this.maxCapacity) {
                arena.addFirst(array);
            }
        }
    }
}
