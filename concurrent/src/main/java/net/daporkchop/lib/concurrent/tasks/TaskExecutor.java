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

package net.daporkchop.lib.concurrent.tasks;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.ReturnableFuture;

import java.util.function.Supplier;

/**
 * A class that can execute tasks. This may be e.g. some sort of thread pool
 *
 * @author DaPorkchop_
 */
public interface TaskExecutor {
    /**
     * Submits a task to this executor. It will be executed at some point, and return an instance of {@link Future} that may be used
     * to track the task's execution status.
     *
     * @param task the task that will be run
     * @return an instance of {@link Future} that will be completed when the task has finished execution
     */
    Future submit(@NonNull Runnable task);

    /**
     * Submits a task to this executor. It will be executed at some point, and return an instance of {@link ReturnableFuture} that may
     * be used to track the task's execution status.
     *
     * @param task that task that will be run
     * @param <T>  the return type from the task
     * @return an instance of {@link ReturnableFuture} that will be completed with the task's return value when the task has finished execution
     */
    <T> ReturnableFuture<T> submit(@NonNull Supplier<T> task);

    /**
     * Queues this executor for shutdown.
     * <p>
     * Any tasks that are currently executing or queued will be executed to completion.
     * After all tasks have been eliminated from the queue, any resources allocated by this executor will be released.
     * <p>
     * Attempting to submit new tasks to this executor after invoking this method will result in undefined behavior.
     */
    void close();

    /**
     * Forcibly shuts down this executor.
     * <p>
     * Any tasks that are currently executing will be executed to completion.
     * Any queued tasks will be removed from the queue and not executed, and their corresponding {@link Future}s will be completed
     * with an {@link InterruptedException}.
     * After the currently running tasks have finished, any resources allocated by this executor will be released.
     * <p>
     * Attempting to submit new tasks to this executor after invoking this method will result in undefined behavior.
     */
    void cancel();
}
