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

package net.daporkchop.lib.common.ref;

import lombok.NonNull;

import java.util.function.Supplier;

/**
 * A cache holds a reference to an object
 *
 * @author DaPorkchop_
 */
public interface Ref<T> extends Supplier<T> {
    /**
     * Gets a simple {@link Ref} will compute the value using the given {@link Supplier} once first requested.
     *
     * @param factory the {@link Supplier} for the value
     * @param <T>     the value type
     * @return a {@link Ref}
     */
    static <T> Ref<T> late(@NonNull Supplier<T> factory) {
        return new LateReferencedRef<>(factory);
    }

    /**
     * Gets a simple {@link Ref} will compute the value using the given {@link Supplier} once first requested, and store it in a soft reference,
     * allowing it to be garbage-collected later on if the garbage-collector deems it necessary. If garbage-collected, it will be re-computed using the
     * {@link Supplier} and cached again.
     *
     * @param factory the {@link Supplier} for the value
     * @param <T>     the value type
     * @return a {@link Ref}
     */
    static <T> Ref<T> soft(@NonNull Supplier<T> factory) {
        return new SoftRef<>(factory);
    }

    /**
     * Get an instance
     *
     * @return an instance
     */
    T get();
}
