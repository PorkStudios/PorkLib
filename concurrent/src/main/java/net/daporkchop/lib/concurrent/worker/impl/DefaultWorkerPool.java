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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.function.throwing.ERunnable;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.CappedSizePool;
import net.daporkchop.lib.concurrent.worker.TimeoutPool;
import net.daporkchop.lib.concurrent.worker.Worker;
import net.daporkchop.lib.concurrent.worker.WorkerPool;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A very simple {@link WorkerPool} implementation. Simply hands tasks over to {@link java.util.concurrent.ForkJoinPool}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultWorkerPool implements WorkerPool {
    public static final DefaultWorkerPool INSTANCE = new DefaultWorkerPool();

    protected final ForkJoinPool delegate = ForkJoinPool.commonPool();

    @Override
    public Worker next() {
        return this;
    }

    @Override
    public Promise stop() {
        throw new UnsupportedOperationException("Cannot stop default pool!");
    }

    @Override
    public Promise terminate() {
        throw new UnsupportedOperationException("Cannot terminate default pool!");
    }

    @Override
    public Promise submit(@NonNull Runnable task) {
        Promise promise = this.newPromise();
        this.delegate.submit(() -> {
            try {
                task.run();
                promise.completeSuccessfully();
            } catch (Exception e)   {
                promise.completeError(e);
            }
        });
        return promise;
    }

    @Override
    public <R> Future<R> submit(@NonNull Callable<R> task) {
        Future<R> future = this.newFuture();
        this.delegate.submit(() -> {
            try {
                future.completeSuccessfully(task.call());
            } catch (Exception e)   {
                future.completeError(e);
            }
        });
        return future;
    }

    @Override
    public <P> Promise submit(P arg, @NonNull Consumer<P> task) {
        Promise promise = this.newPromise();
        this.delegate.submit(() -> {
            try {
                task.accept(arg);
                promise.completeSuccessfully();
            } catch (Exception e)   {
                promise.completeError(e);
            }
        });
        return promise;
    }

    @Override
    public <P, R> Future<R> submit(P arg, @NonNull Function<P, R> task) {
        Future<R> future = this.newFuture();
        this.delegate.submit(() -> {
            try {
                future.completeSuccessfully(task.apply(arg));
            } catch (Exception e)   {
                future.completeError(e);
            }
        });
        return future;
    }
}
