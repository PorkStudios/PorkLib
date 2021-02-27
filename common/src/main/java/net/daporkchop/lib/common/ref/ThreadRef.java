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

package net.daporkchop.lib.common.ref;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.threadlocal.TL;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A thread cache is essentially a {@link ThreadLocal}, able to store objects per-thread
 *
 * @author DaPorkchop_
 */
public interface ThreadRef<T> extends Ref<T> {
    /**
     * @deprecated see {@link TL#initializedWith(Supplier)}
     */
    @Deprecated
    static <T> Ref<T> late(@NonNull Supplier<T> supplier) {
        return TL.initializedWith(supplier);
    }

    /**
     * Gets a simple {@link ThreadRef} will compute the value using the given {@link Supplier} once per thread when first requested, and store it in a
     * soft reference, allowing it to be garbage-collected later on if the garbage-collector deems it necessary. If garbage-collected, it will be
     * re-computed using the {@link Supplier} and cached again.
     *
     * @param supplier the {@link Supplier} for the value
     * @param <T>      the value type
     * @return a {@link ThreadRef}
     */
    static <T> Ref<T> soft(@NonNull Supplier<T> supplier) {
        return new CollectableThreadRef<>(TL.create(), supplier, true);
    }

    /**
     * Gets a simple {@link ThreadRef} will compute the value using the given {@link Supplier} once per thread when first requested, and store it in a
     * weak reference, allowing it to be garbage-collected later on if the garbage-collector deems it necessary. If garbage-collected, it will be
     * re-computed using the {@link Supplier} and cached again.
     *
     * @param supplier the {@link Supplier} for the value
     * @param <T>      the value type
     * @return a {@link ThreadRef}
     */
    static <T> Ref<T> weak(@NonNull Supplier<T> supplier) {
        return new CollectableThreadRef<>(TL.create(), supplier, false);
    }

    /**
     * Gets a {@link ThreadRef} that will cache a {@link Matcher} for the given regex.
     *
     * @param regex the regex to cache a {@link Matcher} for
     * @see #regex(Pattern)
     * @see #soft(Supplier)
     */
    static Ref<Matcher> regex(@NonNull String regex) {
        return regex(Pattern.compile(regex));
    }

    /**
     * Gets a {@link ThreadRef} that will cache a {@link Matcher} for the given {@link Pattern}.
     *
     * @param pattern the {@link Pattern} to cache a {@link Matcher} for
     * @see #soft(Supplier)
     */
    static Ref<Matcher> regex(@NonNull Pattern pattern) {
        return soft(() -> pattern.matcher(""));
    }
}
