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

package net.daporkchop.lib.common.cache;

import lombok.NonNull;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A thread cache is essentially a {@link ThreadLocal}, able to store objects per-thread
 *
 * @author DaPorkchop_
 */
public interface ThreadCache<T> extends Cache<T> {
    /**
     * Gets a simple {@link ThreadCache} will compute the value using the given {@link Supplier} once per thread when first requested.
     *
     * @param supplier the {@link Supplier} for the value
     * @param <T>      the value type
     * @return a {@link ThreadCache}
     */
    static <T> ThreadCache<T> late(@NonNull Supplier<T> supplier) {
        try {
            Class.forName("io.netty.util.concurrent.FastThreadLocal"); //make sure class exists

            return new FastLateThreadCache<>(supplier);
        } catch (ClassNotFoundException e) {
            return new JavaLateThreadCache<>(supplier);
        }
    }

    /**
     * Gets a simple {@link ThreadCache} will compute the value using the given {@link Supplier} once per thread when first requested, and store it in a
     * soft reference, allowing it to be garbage-collected later on if the garbage-collector deems it necessary. If garbage-collected, it will be
     * re-computed using the {@link Supplier} and cached again.
     *
     * @param supplier the {@link Supplier} for the value
     * @param <T>      the value type
     * @return a {@link ThreadCache}
     */
    static <T> ThreadCache<T> soft(@NonNull Supplier<T> supplier) {
        try {
            Class.forName("io.netty.util.concurrent.FastThreadLocal"); //make sure class exists

            return new FastSoftThreadCache<>(supplier);
        } catch (ClassNotFoundException e) {
            return new JavaSoftThreadCache<>(supplier);
        }
    }

    /**
     * Gets a {@link ThreadCache} that will cache a {@link Matcher} for the given regex.
     *
     * @param regex the regex to cache a {@link Matcher} for
     * @see #regex(Pattern)
     * @see #soft(Supplier)
     */
    static ThreadCache<Matcher> regex(@NonNull String regex) {
        return regex(Pattern.compile(regex));
    }

    /**
     * Gets a {@link ThreadCache} that will cache a {@link Matcher} for the given {@link Pattern}.
     *
     * @param pattern the {@link Pattern} to cache a {@link Matcher} for
     * @see #soft(Supplier)
     */
    static ThreadCache<Matcher> regex(@NonNull Pattern pattern) {
        return soft(() -> pattern.matcher(""));
    }

    @Override
    T get();

    /**
     * Create a new instance, regardless of thread-local state
     *
     * @return a new instance
     */
    T getUncached();
}
