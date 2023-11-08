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

import io.netty.util.concurrent.FastThreadLocal;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.ThreadSafe;

import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * An abstracted form of a {@link ThreadLocal}, allowing transparent selection of alternative implementations (such as {@link FastThreadLocal}).
 *
 * @author DaPorkchop_
 */
@ThreadSafe
public interface TL<T> {
    /**
     * Creates a new thread-local variable, automatically selecting the best implementation to use.
     *
     * @param <T> the value type
     * @return the new thread-local variable
     */
    static <T> TL<T> create() {
        return NETTY_PRESENT
                ? new NettyFastTL<>()
                : new JavaTL<>();
    }

    /**
     * Creates a new thread-local variable which will be initially set to the given value, automatically selecting the best implementation to use.
     *
     * @param initialValue the initial value
     * @param <T>          the value type
     * @return the new thread-local variable
     */
    static <T> TL<T> initializedTo(@NonNull T initialValue) {
        return NETTY_PRESENT
                ? new NettyFastTL.WithConstant<>(initialValue)
                : new JavaTL.WithConstant<>(initialValue);
    }

    /**
     * Creates a new thread-local variable which will be initially set to a value computed by the given {@link Supplier}, automatically selecting
     * the best implementation to use.
     *
     * @param initialSupplier the {@link Supplier} to use to compute the initial value
     * @param <T>             the value type
     * @return the new thread-local variable
     */
    static <T> TL<T> initializedWith(@NonNull @ThreadSafe Supplier<T> initialSupplier) {
        return NETTY_PRESENT
                ? new NettyFastTL.WithInitializer<>(initialSupplier)
                : new JavaTL.WithInitializer<>(initialSupplier);
    }

    /**
     * @return the value in the current thread, or {@code null} if there is none
     */
    T get();

    /**
     * Sets the value in the current thread.
     *
     * @param value the value to set
     */
    void set(T value);

    /**
     * Removes the value in the current thread.
     */
    void remove();
}
