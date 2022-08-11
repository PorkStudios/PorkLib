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

package net.daporkchop.lib.common.pool.recycler;

import lombok.NonNull;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.ThreadSafe;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A simple recycler for re-usable object instances.
 * <p>
 * For convenience reasons, it is not <i>absolutely</i> necessary to release instances once no longer needed. This eliminates the need for users to add an additional
 * {@code try-finally} block every time an object is acquired from a recycler, at the cost of potentially degraded performance from having to allocate new instances when an
 * older instance fails to be released due to an exception.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface Recycler<T> {
    /**
     * Creates a new pooling {@link Recycler} which will pool an unlimited number of instances.
     *
     * @param creator a function to use for creating new {@link T} instances
     * @param <T>     the type of object to pool
     * @return a new pooling {@link Recycler}
     */
    static <T> Recycler<T> unbounded(@NonNull Supplier<? extends T> creator) {
        return new UnboundedStackRecycler<T>() {
            @Override
            public T allocateNew() {
                return creator.get();
            }
        };
    }

    /**
     * Creates a new pooling {@link Recycler} which will pool an unlimited number of instances.
     *
     * @param creator a function to use for creating new {@link T} instances
     * @param finalizer a function to use to reset {@link T} instances when they're released
     * @param <T>     the type of object to pool
     * @return a new pooling {@link Recycler}
     */
    static <T> Recycler<T> unbounded(@NonNull Supplier<? extends T> creator, @NonNull Consumer<? super T> finalizer) {
        return new UnboundedStackRecycler<T>() {
            @Override
            public T allocateNew() {
                return creator.get();
            }

            @Override
            public void reset(@NonNull T value) {
                finalizer.accept(value);
            }
        };
    }

    /**
     * Creates a new {@link Recycler} which simply allocates a new instance for each request.
     *
     * @param creator a function to use for creating new {@link T} instances
     * @param <T>     the type of object to pool
     * @return a new unpooled {@link Recycler}
     */
    static <T> Recycler<T> unpooled(@NonNull @ThreadSafe Supplier<? extends T> creator) {
        return new UnpooledRecycler<T>() {
            @Override
            public T allocateNew() {
                return creator.get();
            }
        };
    }

    /**
     * Creates a new {@link Recycler} which simply allocates a new instance for each request.
     *
     * @param creator   a function to use for creating new {@link T} instances
     * @param finalizer a function to use to reset {@link T} instances when they're released
     * @param <T>       the type of object to pool
     * @return a new unpooled {@link Recycler}
     */
    static <T> Recycler<T> unpooled(@NonNull @ThreadSafe Supplier<? extends T> creator, @NonNull @ThreadSafe Consumer<? super T> finalizer) {
        return new UnpooledRecycler<T>() {
            @Override
            public T allocateNew() {
                return creator.get();
            }

            @Override
            public void reset(@NonNull T value) {
                finalizer.accept(value);
            }
        };
    }

    /**
     * Obtains an instance of {@link T} from this recycler.
     * <p>
     * The implementation may choose to return a previously {@link #release(T) released} instance, or it may allocate a new one if none are available.
     * <p>
     * The instance should be released using {@link #release(Object)} once no longer needed.
     *
     * @return the instance
     */
    T allocate();

    /**
     * Releases an instance by returning it to the recycler.
     * <p>
     * The instance to release <strong>must</strong> have been acquired by invoking {@link #allocate()} on this same {@link Recycler} instance, and
     * <strong>must not</strong> be released more than once.
     * <p>
     * Once released, the instance must no longer be used in any way, as the implementation may save it to be re-used in the future.
     *
     * @param value the instance to release
     */
    void release(@NonNull T value);
}
