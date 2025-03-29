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

package net.daporkchop.lib.common.misc.classvalue;

import lombok.NonNull;
import net.daporkchop.lib.common.reference.ReferenceStrength;

import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public interface PClassValue<T> {
    /**
     * Creates a new {@link ClassValue} whose values are computed using the given {@link Function}.
     *
     * @param factory the function to use to compute values
     * @return a new {@link ClassValue}
     */
    static <T> PClassValue<T> create(@NonNull Function<? super Class<?>, ? extends T> factory) {
        return new BasicStrongClassValue<>(factory);
    }

    /**
     * Creates a new {@link ClassValue} whose values are computed using the given {@link Function}, and referenced by a reference
     * with the given {@link ReferenceStrength}. Garbage-collected values will be automatically re-computed by a subsequent call
     * to {@link ClassValue#get(Class)}.
     *
     * @param factory  the function to use to compute values
     * @param strength the value reference strength
     * @return a new {@link ClassValue}
     */
    static <T> PClassValue<T> create(@NonNull Function<? super Class<?>, ? extends T> factory, @NonNull ReferenceStrength strength) {
        return new BasicCollectableClassValue<>(factory, strength);
    }

    /**
     * Gets the value associated with the given class, computing it if no cached value exists.
     *
     * @param type the class
     * @return the associated value
     * @see ClassValue#get(Class)
     */
    T get(@NonNull Class<?> type);

    /**
     * Removes the existing value associated with the given class, if any.
     *
     * @param type the class
     * @see ClassValue#remove(Class)
     */
    void remove(@NonNull Class<?> type);
}
