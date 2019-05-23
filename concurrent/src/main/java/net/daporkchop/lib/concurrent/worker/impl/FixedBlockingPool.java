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

package net.daporkchop.lib.concurrent.worker.impl;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.pool.FixedSizePool;
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.concurrent.worker.pool.WorkerPool;
import net.daporkchop.lib.concurrent.worker.pool.WorkerSelector;
import net.daporkchop.lib.concurrent.worker.selector.RoundRobinSelector;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link WorkerPool} that submits tasks to any one of a fixed number of workers, blocking the thread attempting
 * to submit a task until an idle one is found.
 *
 * @author DaPorkchop_
 */
public class FixedBlockingPool implements FixedSizePool {
    protected final WorkerSelector selector;
    protected final TaskWorker[] workers;

    public FixedBlockingPool(int workerCount)   {
        this(workerCount, new RoundRobinSelector());
    }

    public FixedBlockingPool(int workerCount, @NonNull WorkerSelector selector)   {
        this.workers = new TaskWorker[workerCount];
        this.selector = selector;
    }

    @Override
    public int maxWorkers() {
        return this.workers.length;
    }

    @Override
    public Worker next() {
        return this;
    }

    @Override
    public Promise stop() {
        return null;
    }

    @Override
    public Promise terminate() {
        return null;
    }

    @Override
    public Promise submit(@NonNull Runnable task) {
        return this.next().submit(task);
    }

    @Override
    public <R> Future<R> submit(@NonNull Callable<R> task) {
        return this.next().submit(task);
    }

    @Override
    public <P> Promise submit(P arg, @NonNull Consumer<P> task) {
        return this.next().submit(arg, task);
    }

    @Override
    public <P, R> Future<R> submit(P arg, @NonNull Function<P, R> task) {
        return this.next().submit(arg, task);
    }

    protected class TaskWorker extends Thread implements Worker   {
        protected final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);

        @Override
        public void run() {
            super.run();
        }

        @Override
        public WorkerPool pool() {
            return FixedBlockingPool.this;
        }

        @Override
        public Promise submit(@NonNull Runnable task) {
            return this.queue.put(task);
        }

        @Override
        public <R> Future<R> submit(@NonNull Callable<R> task) {
            return null;
        }

        @Override
        public <P> Promise submit(P arg, @NonNull Consumer<P> task) {
            return null;
        }

        @Override
        public <P, R> Future<R> submit(P arg, @NonNull Function<P, R> task) {
            return null;
        }
    }
}
