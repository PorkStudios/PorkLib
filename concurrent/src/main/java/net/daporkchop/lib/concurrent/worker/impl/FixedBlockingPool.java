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
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.concurrent.worker.pool.FixedSizePool;
import net.daporkchop.lib.concurrent.worker.pool.WorkerPool;
import net.daporkchop.lib.concurrent.worker.pool.WorkerSelector;
import net.daporkchop.lib.concurrent.worker.selector.RoundRobinSelector;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link WorkerPool} that submits tasks to any one of a fixed number of workers, blocking the thread attempting
 * to submit a task until an idle one is found.
 *
 * @author DaPorkchop_
 */
public class FixedBlockingPool implements FixedSizePool {
    protected static final long ACTIVE_OFFSET = PUnsafe.pork_getOffset(TaskWorker.class, "active");
    protected static final long TASK_OFFSET = PUnsafe.pork_getOffset(TaskWorker.class, "task");

    protected final WorkerSelector selector;
    protected final TaskWorker[] workers;
    protected final Promise termination = this.newPromise();
    protected volatile int active;

    public FixedBlockingPool(int workerCount) {
        this(workerCount, new RoundRobinSelector());
    }

    public FixedBlockingPool(int workerCount, @NonNull WorkerSelector selector) {
        this.workers = new TaskWorker[this.active = workerCount];
        this.selector = selector;
        for (int i = 0; i < workerCount; i++)    {
            (this.workers[i] = new TaskWorker()).start();
        }
    }

    @Override
    public int maxWorkers() {
        return this.workers.length;
    }

    @Override
    public Worker next() {
        return this.selector.select(this.workers);
    }

    @Override
    public Promise stop() {
        for (TaskWorker worker : this.workers)  {
            worker.active = false;
            worker.interrupt();
        }
        return this.termination;
    }

    @Override
    public Promise terminate() {
        for (TaskWorker worker : this.workers)  {
            synchronized (worker.mutex) {
                worker.active = false;
                worker.interrupt();
            }
        }
        return this.termination;
    }

    @Override
    public Promise submit(@NonNull Runnable task) {
        return this.next().submit(task);
    }

    @Override
    public <R> Future<R> submit(@NonNull Supplier<R> task) {
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

    protected class TaskWorker extends Thread implements Worker {
        protected final Object mutex = new Object[0];
        protected volatile Runnable task = null;
        protected volatile boolean active = true;

        @Override
        public void run() {
            Runnable task;
            PUnsafe.monitorEnter(this.mutex);
            try {
                while (this.active) {
                    while (this.active && (task = PUnsafe.getAndSetObject(this, TASK_OFFSET, null)) != null) {
                        this.mutex.notify();
                        PUnsafe.monitorExit(this.mutex);
                        try {
                            task.run();
                        } catch (Exception e) {
                            new RuntimeException(e).printStackTrace();
                        }
                        PUnsafe.monitorEnter(this.mutex);
                    }
                    try {
                        this.mutex.wait();
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                if (PUnsafe.getAndAddInt(FixedBlockingPool.this, ACTIVE_OFFSET, -1) == 1) {
                    FixedBlockingPool.this.termination.completeSuccessfully();
                }
            } finally {
                PUnsafe.monitorExit(this.mutex);
            }
        }

        @Override
        public WorkerPool pool() {
            return FixedBlockingPool.this;
        }

        @Override
        public Promise submit(@NonNull Runnable task) {
            PUnsafe.monitorEnter(this.mutex);
            try {
                if (this.active)    {
                    Promise promise = this.newPromise();
                    Runnable realTask = () -> {
                        try {
                            task.run();
                            promise.completeSuccessfully();
                        } catch (Exception e)   {
                            promise.completeError(e);
                        }
                    };
                    while (!PUnsafe.compareAndSwapObject(this, TASK_OFFSET, null, realTask))    {
                        try {
                            this.mutex.wait();
                        } catch (InterruptedException e)    {
                            Thread.currentThread().interrupt();
                        }
                    }
                    this.mutex.notify();
                    return promise;
                }
            } finally {
                PUnsafe.monitorExit(this.mutex);
            }
            return DefaultWorkerPool.INSTANCE.submit(task);
        }

        @Override
        public <R> Future<R> submit(@NonNull Supplier<R> task) {
            PUnsafe.monitorEnter(this.mutex);
            try {
                if (this.active)    {
                    Future<R> future = this.newFuture();
                    Runnable realTask = () -> {
                        try {
                            future.completeSuccessfully(task.get());
                        } catch (Exception e)   {
                            future.completeError(e);
                        }
                    };
                    while (!PUnsafe.compareAndSwapObject(this, TASK_OFFSET, null, realTask))    {
                        try {
                            this.mutex.wait();
                        } catch (InterruptedException e)    {
                            Thread.currentThread().interrupt();
                        }
                    }
                    this.mutex.notify();
                    return future;
                }
            } finally {
                PUnsafe.monitorExit(this.mutex);
            }
            return DefaultWorkerPool.INSTANCE.submit(task);
        }

        @Override
        public <P> Promise submit(P arg, @NonNull Consumer<P> task) {
            PUnsafe.monitorEnter(this.mutex);
            try {
                if (this.active)    {
                    Promise promise = this.newPromise();
                    Runnable realTask = () -> {
                        try {
                            task.accept(arg);
                            promise.completeSuccessfully();
                        } catch (Exception e)   {
                            promise.completeError(e);
                        }
                    };
                    while (!PUnsafe.compareAndSwapObject(this, TASK_OFFSET, null, realTask))    {
                        try {
                            this.mutex.wait();
                        } catch (InterruptedException e)    {
                            Thread.currentThread().interrupt();
                        }
                    }
                    this.mutex.notify();
                    return promise;
                }
            } finally {
                PUnsafe.monitorExit(this.mutex);
            }
            return DefaultWorkerPool.INSTANCE.submit(arg, task);
        }

        @Override
        public <P, R> Future<R> submit(P arg, @NonNull Function<P, R> task) {
            PUnsafe.monitorEnter(this.mutex);
            try {
                if (this.active)    {
                    Future<R> future = this.newFuture();
                    Runnable realTask = () -> {
                        try {
                            future.completeSuccessfully(task.apply(arg));
                        } catch (Exception e)   {
                            future.completeError(e);
                        }
                    };
                    while (!PUnsafe.compareAndSwapObject(this, TASK_OFFSET, null, realTask))    {
                        try {
                            this.mutex.wait();
                        } catch (InterruptedException e)    {
                            Thread.currentThread().interrupt();
                        }
                    }
                    this.mutex.notify();
                    return future;
                }
            } finally {
                PUnsafe.monitorExit(this.mutex);
            }
            return DefaultWorkerPool.INSTANCE.submit(arg, task);
        }
    }
}