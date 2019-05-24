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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Completable;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.pool.DynamicPool;
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class ThreadPerTaskPool implements DynamicPool {
    protected static final long PROMISE_OFFSET = PUnsafe.pork_getOffset(ThreadPerTaskPool.class, "terminationPromise");
    protected static final long ACTIVE_OFFSET = PUnsafe.pork_getOffset(ThreadPerTaskPool.class, "active");

    protected volatile Promise terminationPromise = null;
    protected volatile int active = 0;

    @Override
    public int activeWorkers() {
        return this.active;
    }

    @Override
    public Worker next() {
        return this;
    }

    @Override
    public Promise stop() {
        return this.terminate();
    }

    @Override
    public Promise terminate() {
        PUnsafe.compareAndSwapObject(this, PROMISE_OFFSET, null, this.newPromise());
        return this.terminationPromise;
    }

    @Override
    public Promise submit(@NonNull Runnable task) {
        if (this.terminationPromise != null)    {
            return DefaultWorkerPool.INSTANCE.submit(task);
        } else {
            Promise promise = this.newPromise();
            new TaskWorker().func(task).completable(promise).start();
            return promise;
        }
    }

    @Override
    public <R> Future<R> submit(@NonNull Supplier<R> task) {
        if (this.terminationPromise != null)    {
            return DefaultWorkerPool.INSTANCE.submit(task);
        } else {
            Future<R> future = this.newFuture();
            new TaskWorker().func(task).completable(future).start();
            return future;
        }
    }

    @Override
    public <P> Promise submit(P arg, @NonNull Consumer<P> task) {
        if (this.terminationPromise != null)    {
            return DefaultWorkerPool.INSTANCE.submit(arg, task);
        } else {
            Promise promise = this.newPromise();
            new TaskWorker().func(task).arg(arg).completable(promise).start();
            return promise;
        }
    }

    @Override
    public <P, R> Future<R> submit(P arg, @NonNull Function<P, R> task) {
        if (this.terminationPromise != null)    {
            return DefaultWorkerPool.INSTANCE.submit(arg, task);
        } else {
            Future<R> future = this.newFuture();
            new TaskWorker().func(task).arg(arg).completable(future).start();
            return future;
        }
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    protected class TaskWorker extends Thread   {
        protected Object arg;
        protected Object func;
        protected Completable completable;

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            PUnsafe.getAndAddInt(ThreadPerTaskPool.this, ACTIVE_OFFSET, 1);
            try {
                if (this.arg == null) {
                    if (this.func instanceof Runnable)  {
                        ((Runnable) this.func).run();
                        ((Promise) this.completable).completeSuccessfully();
                    } else {
                        ((Future) this.completable).completeSuccessfully(((Supplier) this.func).get());
                    }
                } else {
                    if (this.func instanceof Consumer)  {
                        ((Consumer) this.func).accept(this.arg);
                        ((Promise) this.completable).completeSuccessfully();
                    } else {
                        ((Future) this.completable).completeSuccessfully(((Function) this.func).apply(this.arg));
                    }
                }
            } catch (Exception e)   {
                this.completable.completeError(e);
            } finally {
                if (PUnsafe.getAndAddInt(ThreadPerTaskPool.this, ACTIVE_OFFSET, -1) == 1 && ThreadPerTaskPool.this.terminationPromise != null)  {
                    ThreadPerTaskPool.this.terminationPromise.completeSuccessfully();
                }
            }
        }
    }
}
