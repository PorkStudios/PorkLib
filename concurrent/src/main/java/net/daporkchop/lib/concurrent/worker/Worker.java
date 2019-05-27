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

package net.daporkchop.lib.concurrent.worker;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.future.impl.DefaultFuture;
import net.daporkchop.lib.concurrent.future.impl.DefaultPromise;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A wrapper around (an) asynchronous thread(s) which can execute tasks.
 *
 * @author DaPorkchop_
 */
public interface Worker {
    /**
     * Submits a task to be run by this worker at some point.
     * <p>
     * This will not allocate an additional {@link Promise} or {@link Future} instance, which may provide a
     * performance boost.
     *
     * @param task the task to run
     */
    void submitFast(@NonNull Runnable task);

    /**
     * Schedules a task to be run by this worker at some point.
     *
     * @param task the task to be run
     * @return a {@link Promise} which may be used to track the task
     */
    default Promise submit(@NonNull Runnable task) {
        Promise promise = this.newPromise();
        this.submitFast(new Task.Run(promise, task));
        return promise;
    }

    /**
     * Schedules a task to be run by this worker at some point.
     *
     * @param param the argument to pass to the task
     * @param task  the task to be run
     * @param <P>   the parameter type of the task
     * @return a {@link Future} which may be used to track the task and get the return value
     */
    default <P> Promise submit(P param, @NonNull Consumer<P> task) {
        Promise promise = this.newPromise();
        this.submitFast(new Task.RunParam<>(promise, task, param));
        return promise;
    }

    /**
     * Schedules a task to be run by this worker at some point.
     *
     * @param <R>  the return type of the task
     * @param task the task to be run
     * @return a {@link Future} which may be used to track the task and get the return value
     */
    default <R> Future<R> submit(@NonNull Supplier<R> task) {
        Future<R> future = this.newFuture();
        this.submitFast(new Task.Compute<>(future, task));
        return future;
    }

    /**
     * Schedules a task to be run by this worker at some point.
     *
     * @param param the argument to pass to the task
     * @param task  the task to be run
     * @param <P>   the parameter type of the task
     * @param <R>   the return type of the task
     * @return a {@link Future} which may be used to track the task and get the return value
     */
    default <P, R> Future<R> submit(P param, @NonNull Function<P, R> task) {
        Future<R> future = this.newFuture();
        this.submitFast(new Task.ComputeParam<>(future, task, param));
        return future;
    }

    /**
     * @return a new {@link Promise} backed by this worker
     */
    default Promise newPromise() {
        return new DefaultPromise(this);
    }

    /**
     * @return a new {@link Future} backed by this worker
     */
    default <R> Future<R> newFuture() {
        return new DefaultFuture<>(this);
    }
}
