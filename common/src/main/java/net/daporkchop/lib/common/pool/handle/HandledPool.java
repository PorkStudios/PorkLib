/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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
import net.daporkchop.lib.common.reference.ReferenceStrength;

import java.util.function.Supplier;

/**
 * A simple form of resource pooling, using {@link Handle}s to manage references to pooled objects.
 *
 * @param <V> the type of value to pool
 * @author DaPorkchop_
 */
public interface HandledPool<V> {
    /**
     * Creates a new global {@link HandledPool}.
     *
     * @param factory       a {@link Supplier} for new value instances
     * @param strength the {@link ReferenceStrength} to use for storing references to values
     * @param maxCapacity   the maximum number of values to be stored
     * @param <V>           the value type
     * @return a new global {@link HandledPool}
     */
    static <V> HandledPool<V> global(@NonNull Supplier<V> factory, @NonNull ReferenceStrength strength, int maxCapacity) {
        return new BasicHandledPool<>(factory, strength, maxCapacity);
    }

    /**
     * Creates a new thread-local {@link HandledPool}.
     *
     * @param factory              a {@link Supplier} for new value instances
     * @param maxCapacityPerThread the maximum number of values to be stored per thread
     * @param <V>                  the value type
     * @return a new thread-local {@link HandledPool}
     */
    static <V> HandledPool<V> threadLocal(@NonNull Supplier<V> factory, int maxCapacityPerThread) {
        try {
            Class.forName("io.netty.util.Recycler"); //make sure class exists

            return new RecyclingHandledPool<>(factory, maxCapacityPerThread);
        } catch (ClassNotFoundException e) {
            return new JavaRecyclingHandledPool<>(factory, maxCapacityPerThread);
        }
    }

    /**
     * Gets a value from this pool.
     *
     * @return a {@link Handle} for accessing a value from this pool
     */
    Handle<V> get();
}
