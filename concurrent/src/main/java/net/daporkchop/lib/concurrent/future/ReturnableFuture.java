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

package net.daporkchop.lib.concurrent.future;

/**
 * A {@link Future} which has a return value
 *
 * @author DaPorkchop_
 */
public interface ReturnableFuture<V> extends Future {
    /**
     * Gets the return value from this {@link ReturnableFuture}.
     * <p>
     * If this {@link ReturnableFuture} has not been completed, this will block the invoking thread until
     * it is completed.
     * <p>
     * If this {@link ReturnableFuture} was completed with an exception, the exception will be thrown (unsafely).
     *
     * @return the return value
     */
    default V get() {
        try {
            return this.getInterruptably();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
    }

    /**
     * Gets the return value from this {@link ReturnableFuture}.
     * <p>
     * If this {@link ReturnableFuture} has not been completed, this will block the invoking thread until
     * it is completed.
     * <p>
     * If this {@link ReturnableFuture} completes with an exception, the exception will be thrown (unsafely).
     *
     * @return the return value
     */
    V getInterruptably() throws InterruptedException;

    /**
     * Gets the return value from this {@link ReturnableFuture}.
     * <p>
     * If this {@link ReturnableFuture} has not been completed, this will return {@code null}.
     * <p>
     * If this {@link ReturnableFuture} was completed with an exception, the exception will be thrown (unsafely).
     */
    default V getOrNull() {
        return this.isCompleted() ? this.get() : null;
    }

    /**
     * Completes this {@link ReturnableFuture} with a given return value.
     * <p>
     * Any waiting threads will be notified, and this instance will be marked as completed.
     * <p>
     * If this {@link ReturnableFuture} is already completed, the results are undefined.
     *
     * @param value the return value
     */
    void complete(V value);

    @Override
    default void sync() {
        this.get();
    }

    @Override
    default void syncInterruptably() throws InterruptedException {
        this.getInterruptably();
    }

    @Override
    default void complete() {
        this.complete(null);
    }
}
