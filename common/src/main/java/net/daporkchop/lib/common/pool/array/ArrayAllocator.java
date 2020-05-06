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
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.pool.handle.HandledPool;
import net.daporkchop.lib.common.ref.ReferenceType;

import java.util.function.IntFunction;

/**
 * Similar to {@link HandledPool}, but intended for allocating arrays rather than individual objects.
 *
 * @param <T> the array type
 * @author DaPorkchop_
 * @see HandledPool
 */
public interface ArrayAllocator<T> {
    /**
     * Creates a new global {@link ArrayAllocator} which groups arrays based on their size in powers of 2.
     *
     * @param lambda        a lambda function (e.g. {@code Object[]::new}) to use for creating new array instances
     * @param referenceType the {@link ReferenceType} that arrays will be stored with
     * @param maxCapacity   the maximum internal storage capacity of the allocator per power of 2
     * @param <T>           the array type
     * @return a new global {@link ArrayAllocator}
     */
    static <T> ArrayAllocator<T> pow2(@NonNull IntFunction<T> lambda, @NonNull ReferenceType referenceType, int maxCapacity) {
        return new Pow2ArrayAllocator<>(lambda, referenceType, maxCapacity);
    }

    /**
     * Creates a new global {@link ArrayAllocator} which groups arrays based on their size in powers of 2.
     *
     * @param componentType the array component type
     * @param referenceType the {@link ReferenceType} that arrays will be stored with
     * @param maxCapacity   the maximum internal storage capacity of the allocator per power of 2
     * @param <T>           the array type
     * @return a new global {@link ArrayAllocator}
     */
    static <T> ArrayAllocator<T[]> pow2(@NonNull Class<T> componentType, @NonNull ReferenceType referenceType, int maxCapacity) {
        return new Pow2ArrayAllocator<>(componentType, referenceType, maxCapacity);
    }

    /**
     * Gets an array of at least the requested size.
     * <p>
     * The exact behavior of this method is entirely up to the implementation.
     *
     * @param minSize the minimum size of the requested array
     * @return an array of at least the requested size
     */
    Handle<T> atLeast(int minSize);

    /**
     * Gets an array of exactly the requested size.
     * <p>
     * The exact behavior of this method is entirely up to the implementation.
     *
     * @param size the size of the requested array
     * @return an array of exactly the requested size
     */
    Handle<T> exactly(int size);
}