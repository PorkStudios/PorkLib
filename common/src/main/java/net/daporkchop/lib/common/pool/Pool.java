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

package net.daporkchop.lib.common.pool;

import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A pool allows pooling (reusing) instances of a certain class type. This can be useful in scenarios
 * where a number of somewhat memory-heavy instances of specific types are created frequently and only
 * used once before being left to the garbage collector. By pooling them, one is able to very simply
 * re-use instances of these classes, reducing garbage collection overhead.
 * <p>
 * All methods in this class are expected to be thread-safe.
 *
 * @param <T> the type that is pooled
 * @author DaPorkchop_
 */
public interface Pool<T> {
    /**
     * Gets an instance from this pool.
     * <p>
     * Implementations may choose to obtain this instance in whatever way they choose, however a best-effort
     * attempt should always be made to avoid having to create new instances.
     * <p>
     * The instance returned by this method must be returned to the pool when no longer needed by passing
     * it to {@link #release(Object)}.
     *
     * @return an instance from this pool
     */
    T get();

    /**
     * Returns an instance to this pool.
     * <p>
     * This method must be invoked for every value obtained from this pool using {@link #get()},
     * otherwise the instance will be unable to be re-used by the pool. Depending on the implementation
     * of this pool, failing to release instances after use may also cause a memory leak.
     * <p>
     * If the instance passed to this method was not obtained using {@link #get()}, the results are
     * undefined.
     * <p>
     * If the instance passed to this method was already returned to the pool, the results are undefined.
     *
     * @param instance the instance to return to the pool
     */
    void release(@NonNull T instance);

    /**
     * Obtains an instance from this pool and runs a given function on it.
     *
     * @param consumer the function to run
     */
    default void getAndDoWith(@NonNull Consumer<T> consumer) {
        T instance = this.get();
        consumer.accept(instance);
        this.release(instance);
    }

    /**
     * Obtains an instance from this pool, runs a given function on it, and returns the return value of
     * the given function.
     *
     * @param function the function to run
     * @param <R>      the type of the function return value
     * @return the return value
     */
    default <R> R getAndDoWith(@NonNull Function<T, R> function) {
        T instance = this.get();
        R val = function.apply(instance);
        this.release(instance);
        return val;
    }
}
