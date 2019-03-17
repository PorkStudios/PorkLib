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

package net.daporkchop.lib.collections.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.collections.util.exception.ConcurrentException;
import net.daporkchop.lib.collections.util.exception.IterationCompleteException;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PConstants;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.Supplier;

/**
 * Various methods for helping with executing concurrent tasks
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConcurrencyHelper {
    public static void runConcurrent(int end, @NonNull IntConsumer executor)  {
        runConcurrent(0, end, executor);
    }

    public static void runConcurrent(int start, int end, @NonNull IntConsumer executor)  {
        if (start > end) {
            throw new IllegalArgumentException("start must be lower than end!");
        } else if (start == end) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(start);
        runConcurrent(() -> {
            int i = counter.getAndIncrement();
            if (i < end) {
                return i;
            } else {
                throw new IterationCompleteException();
            }
        }, executor::accept);
    }
    public static void runConcurrent(long end, @NonNull LongConsumer executor)  {
        runConcurrent(0L, end, executor);
    }

    public static void runConcurrent(long start, long end, @NonNull LongConsumer executor)  {
        if (start > end) {
            throw new IllegalArgumentException("start must be lower than end!");
        } else if (start == end) {
            return;
        }

        AtomicLong counter = new AtomicLong(start);
        runConcurrent(() -> {
            long l = counter.getAndIncrement();
            if (l < end) {
                return l;
            } else {
                throw new IterationCompleteException();
            }
        }, executor::accept);
    }

    public static <T> void runConcurrent(int end, @NonNull IntFunction<T> valueSupplier, @NonNull Consumer<T> executor) {
        runConcurrent(0, end, valueSupplier, executor);
    }

    public static <T> void runConcurrent(int start, int end, @NonNull IntFunction<T> valueSupplier, @NonNull Consumer<T> executor) {
        if (start > end) {
            throw new IllegalArgumentException("start must be lower than end!");
        } else if (start == end) {
            return;
        }
        AtomicInteger counter = new AtomicInteger(start);
        runConcurrent(() -> {
            int i = counter.getAndIncrement();
            if (i < end) {
                return valueSupplier.apply(i);
            } else {
                throw new IterationCompleteException();
            }
        }, executor);
    }

    public static <T> void runConcurrent(long end, @NonNull LongFunction<T> valueSupplier, @NonNull Consumer<T> executor) {
        runConcurrent(0L, end, valueSupplier, executor);
    }

    public static <T> void runConcurrent(long start, long end, @NonNull LongFunction<T> valueSupplier, @NonNull Consumer<T> executor) {
        if (start > end) {
            throw new IllegalArgumentException("start must be lower than end!");
        } else if (start == end) {
            return;
        }
        AtomicLong counter = new AtomicLong(start);
        runConcurrent(() -> {
            long l = counter.getAndIncrement();
            if (l < end) {
                return valueSupplier.apply(l);
            } else {
                throw new IterationCompleteException();
            }
        }, executor);
    }

    public static <T> void runConcurrent(@NonNull LongFunction<T> valueSupplier, @NonNull Consumer<T> executor) {
        AtomicLong counter = new AtomicLong(0L);
        runConcurrent(() -> valueSupplier.apply(counter.getAndIncrement()), executor);
    }

    public static <T> void runConcurrent(@NonNull Supplier<T> valueSupplier, @NonNull Consumer<T> executor) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        AtomicInteger waitingCounter = new AtomicInteger(PConstants.CPU_COUNT); //number of threads waiting for a task
        Worker<T>[] workers = PArrays.<Worker<T>>filled(waitingCounter.get(), Worker[]::new, () -> new Worker<>(waitingCounter, executor, lock, condition));
        ForkJoinTask[] tasks = new ForkJoinTask[waitingCounter.get()];
        try {
            while (true) {
                do {
                    for (int i = workers.length - 1; i >= 0; i--) {
                        Worker<T> worker = workers[i];
                        if (!worker.running.get()) {
                            ForkJoinTask task = tasks[i];
                            if (task != null && task.isCompletedAbnormally())   {
                                Throwable t = task.getException();
                                if (t instanceof Exception) {
                                    throw new ConcurrentException(String.format("Exception in worker thread #%d", i), t);
                                } else {
                                    throw PConstants.p_exception(t);
                                }
                            }
                            synchronized (worker) {
                                worker.running.set(true);
                                tasks[i] = pool.submit(worker.setValue(valueSupplier.get()));
                            }
                            waitingCounter.decrementAndGet();
                        }
                    }
                } while (waitingCounter.get() > 0);
                lock.lock();
                condition.await(5L, TimeUnit.MILLISECONDS);
                lock.unlock();
            }
        } catch (IterationCompleteException e) {
            //supplier has reached the end
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        while (waitingCounter.get() != workers.length) {
            try {
                lock.lock();
                condition.await(5L, TimeUnit.MILLISECONDS);
                lock.unlock();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    @Accessors(chain = true)
    protected static class Worker<T> implements Runnable {
        @NonNull
        protected final AtomicInteger waitingCounter;
        @NonNull
        protected final Consumer<T> executor;
        @NonNull
        protected final Lock lock;
        @NonNull
        protected final Condition condition;
        protected final AtomicBoolean running = new AtomicBoolean(false);
        protected T value;

        @Override
        public void run() {
            try {
                synchronized (this) {
                    if (!this.running.get()) {
                        throw new IllegalStateException("Not running!");
                    }
                    this.executor.accept(this.value);
                }
            } finally {
                if (!this.running.getAndSet(false)) {
                    throw new IllegalStateException("Not running!");
                }
                this.waitingCounter.incrementAndGet();
                this.lock.lock();
                this.condition.signal();
                this.lock.unlock();
            }
        }
    }
}
