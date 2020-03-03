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

import io.netty.util.concurrent.FastThreadLocal;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A {@link ThreadRef} that keeps only a soft reference to objects, and is backed by a Netty {@link FastThreadLocal}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class FastSoftThreadRef<T> implements ThreadRef<T> {
    @NonNull
    private final Supplier<T> factory;
    private final FastThreadLocal<SoftReference<T>> threadLocal = new FastThreadLocal<>();

    @Override
    public T get() {
        SoftReference<T> ref = this.threadLocal.get();
        T val;
        if (ref == null || (val = ref.get()) == null) {
            this.threadLocal.set(new SoftReference<>(val = Objects.requireNonNull(this.factory.get())));
        }
        if (val == null) {
            throw new NullPointerException();
        }
        return val;
    }

    @Override
    public T getUncached() {
        return this.factory.get();
    }
}
