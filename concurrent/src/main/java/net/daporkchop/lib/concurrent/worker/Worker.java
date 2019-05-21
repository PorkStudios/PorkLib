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

import java.util.concurrent.Callable;

/**
 * A wrapper around (an) asynchronous thread(s) which can execute tasks.
 *
 * @author DaPorkchop_
 */
public interface Worker {
    /**
     * Schedules a task to be run by this worker at some point.
     *
     * @param task the task to be run
     * @return a {@link Promise} which may be used to track the task
     */
    Promise submit(@NonNull Runnable task);

    /**
     * Schedules a task to be run by this worker at some point.
     *
     * @param task the task to be run
     * @param <R>  the return type of the task
     * @return a {@link Future} which may be used to track the task and get the return value
     */
    <R> Future<R> submit(@NonNull Callable<R> task);

    /**
     * @return a new {@link Promise} backed by this worker
     */
    Promise newPromise();

    /**
     * @return a new {@link Future} backed by this worker
     */
    <R> Future<R> newFuture();
}
