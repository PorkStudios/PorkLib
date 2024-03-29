/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.common.misc.threadlocal;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.annotation.ThreadSafe;

import java.util.function.Supplier;

/**
 * Implementation of {@link TL} using Java's {@link ThreadLocal}.
 *
 * @author DaPorkchop_
 */
public class JavaTL<T> extends ThreadLocal<T> implements TL<T> {
    /**
     * Extension of {@link JavaTL} with a fixed default initialization value.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class WithConstant<T> extends JavaTL<T> {
        protected final T initialValue;
    }

    /**
     * Extension of {@link JavaTL} which computes the default initialization value on-demand.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    public static class WithInitializer<T> extends JavaTL<T> {
        @NonNull
        protected final @ThreadSafe Supplier<T> initialSupplier;

        @Override
        protected T initialValue() {
            return this.initialSupplier.get();
        }
    }
}
