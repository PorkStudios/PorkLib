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

package net.daporkchop.lib.common.reference.cache;

import lombok.NonNull;
import net.daporkchop.lib.common.reference.ReferenceStrength;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A reference to a lazily computed value.
 *
 * @author DaPorkchop_
 */
public interface Cached<T> {
    /**
     * Creates a new {@link Cached} which is shared between all threads and strongly referenced.
     * <p>
     * The value will be computed once on the first thread to call {@link #get()}, and never be re-computed.
     * <p>
     * Functionally equivalent to {@code global(factory, ReferenceStrength.STRONG)}.
     *
     * @param factory the {@link Supplier} to use. May never return {@code null}
     */
    static <T> Cached<T> global(@NonNull Supplier<T> factory) {
        return new GlobalStrongCached<>(factory);
    }

    /**
     * Creates a new {@link Cached} which is shared between all threads and referenced using the given {@link ReferenceStrength}.
     * <p>
     * The value will be computed once on the first thread to call {@link #get()}. If the value is garbage-collected, the first thread to call {@link #get()}
     * after the value was garbage-collected will re-compute it.
     *
     * @param factory the {@link Supplier} to use. May never return {@code null}
     */
    static <T> Cached<T> global(@NonNull Supplier<T> factory, @NonNull ReferenceStrength strength) {
        return strength == ReferenceStrength.STRONG
                ? new GlobalStrongCached<>(factory)
                : new GlobalReferencedCached<>(factory, strength);
    }

    /**
     * Creates a new {@link Cached} which is thread-local and strongly referenced.
     * <p>
     * The value will be computed once per thread on the thread's first call to {@link #get()}, and never be re-computed.
     * <p>
     * Functionally equivalent to {@code threadLocal(factory, ReferenceStrength.STRONG)}.
     *
     * @param factory the {@link Supplier} to use. May never return {@code null}
     */
    static <T> Cached<T> threadLocal(@NonNull Supplier<T> factory) {
        return new ThreadLocalStrongCached<>(factory);
    }

    /**
     * Creates a new {@link Cached} which is thread-local and referenced using the given {@link ReferenceStrength}.
     * <p>
     * The value will be computed once per thread on the thread's first call to {@link #get()}. If a thread's value is garbage-collected, that thread's first call
     * to {@link #get()} after the value was garbage-collected will re-compute it.
     *
     * @param factory the {@link Supplier} to use. May never return {@code null}
     */
    static <T> Cached<T> threadLocal(@NonNull Supplier<T> factory, @NonNull ReferenceStrength strength) {
        return strength == ReferenceStrength.STRONG
                ? new ThreadLocalStrongCached<>(factory)
                : new GlobalReferencedCached<>(factory, strength);
    }

    /**
     * Creates a new {@link Cached} which is thread-local and caches a {@link Matcher} for a {@link Pattern}.
     * <p>
     * The {@link Matcher}'s {@link Pattern} must never be changed.
     *
     * @param pattern the {@link Pattern}
     */
    static Cached<Matcher> regex(@NonNull Pattern pattern) {
        return regex(pattern, false);
    }

    /**
     * Creates a new {@link Cached} which is thread-local and caches a {@link Matcher} for a {@link Pattern}.
     * <p>
     * The {@link Matcher}'s {@link Pattern} must never be changed.
     *
     * @param pattern the {@link Pattern}
     */
    static Cached<Matcher> regex(@NonNull Pattern pattern, boolean keepPatternInstance) {
        if (keepPatternInstance) {
            return threadLocal(() -> pattern.matcher(""), ReferenceStrength.WEAK);
        } else {
            return regex(pattern.pattern(), pattern.flags());
        }
    }

    /**
     * Creates a new {@link Cached} which is thread-local and caches a {@link Matcher} for a regex.
     * <p>
     * The {@link Matcher}'s {@link Pattern} must never be changed.
     *
     * @param regex the regex
     */
    static Cached<Matcher> regex(@NonNull String regex) {
        return regex(regex, 0);
    }

    /**
     * Creates a new {@link Cached} which is thread-local and caches a {@link Matcher} for a regex.
     * <p>
     * The {@link Matcher}'s {@link Pattern} must never be changed.
     *
     * @param regex the regex
     * @param flags the regex compilation flags. See {@link Pattern#compile(String, int)}
     */
    static Cached<Matcher> regex(@NonNull String regex, int flags) {
        Cached<Pattern> patternCache = global(() -> Pattern.compile(regex, flags), ReferenceStrength.WEAK);
        return threadLocal(() -> patternCache.get().matcher(""), ReferenceStrength.WEAK);
    }

    /**
     * @return the {@link Supplier} used to compute the value
     */
    Supplier<T> factory();

    /**
     * Gets the value, computing it as needed.
     * <p>
     * The value may be re-computed at any time, and is not guaranteed to be consistent between invocations or threads.
     *
     * @return the value
     */
    T get();
}
