/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.concurrent.cache;

import lombok.NonNull;

import java.util.function.Supplier;

/**
 * A thread cache is essentially a {@link ThreadLocal}, able to store objects per-thread
 *
 * @author DaPorkchop_
 */
public interface ThreadCache<T> extends Cache<T> {
    /**
     * Creates a new {@link ThreadCache} using a given supplier
     *
     * @param theSupplier the supplier to use
     * @param <T>         the type to be cached
     * @return a {@link ThreadCache} for the given type using the given supplier
     */
    static <T> ThreadCache<T> of(@NonNull Supplier<T> theSupplier) {
        return new ThreadCache<T>() {
            private final Supplier<T> supplier = theSupplier;
            private final ThreadLocal<T> threadLocal = ThreadLocal.withInitial(this.supplier);

            @Override
            public T get() {
                return this.threadLocal.get();
            }

            @Override
            public T getUncached() {
                return this.supplier.get();
            }
        };
    }

    /**
     * Create a new instance, regardless of thread-local state
     *
     * @return a new instance
     */
    T getUncached();
}
