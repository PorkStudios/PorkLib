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

package net.daporkchop.lib.concurrent.tasks.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.PCollections;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.ReturnableFuture;
import net.daporkchop.lib.concurrent.future.impl.SimpleFuture;
import net.daporkchop.lib.concurrent.future.impl.SimpleReturnableFuture;
import net.daporkchop.lib.concurrent.synchronization.NotificationQueue;
import net.daporkchop.lib.concurrent.synchronization.impl.JavaNotificationQueue;
import net.daporkchop.lib.concurrent.tasks.TaskExecutor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A simple implementation of a {@link TaskExecutor} that uses a pool of worker threads to execute tasks.
 *
 * @author DaPorkchop_
 */
@ToString(exclude = {
        "threadFactory",
        "corePool",
        "supplementaryPool",
        "tasks"
})
public class SimpleThreadPoolTaskExecutor implements TaskExecutor {
    protected static final long CLOSINGDOWN_OFFSET = PUnsafe.pork_getOffset(SimpleThreadPoolTaskExecutor.class, "closingDown");
    protected static final long TASKS_OFFSET = PUnsafe.pork_getOffset(SimpleThreadPoolTaskExecutor.class, "tasks");

    public static Builder builder() {
        return new Builder();
    }

    protected static String getDefaultThreadName(int threadNum) {
        return String.format("SimpleThreadPoolTaskExecutor worker #%d", threadNum);
    }

    protected final BiFunction<Integer, Runnable, Thread> threadFactory;
    protected final int corePoolSize;
    protected final int maxPoolSize;
    protected final int supplementaryPoolSize;
    protected final long threadKeepAliveTime;
    protected final Collection<Thread> corePool;
    protected final Collection<Thread> supplementaryPool;
    protected volatile Deque<Tuple<Runnable, Future>> tasks = new ConcurrentLinkedDeque<>();
    protected final NotificationQueue notificationQueue = new JavaNotificationQueue();
    protected final AtomicInteger activeTasks = new AtomicInteger();
    protected final ReadWriteLock closingLock = new ReentrantReadWriteLock();
    protected volatile int closingDown = 0;

    public SimpleThreadPoolTaskExecutor(@NonNull BiFunction<Integer, Runnable, Thread> threadFactory, int corePoolSize, int maxPoolSize, long threadKeepAliveTime) {
        if (corePoolSize < 0 || maxPoolSize < 0) {
            throw new IllegalArgumentException(String.format("corePoolSize(%d) and maxPoolSize(%d) must be >= 0!", corePoolSize, maxPoolSize));
        } else if (corePoolSize > maxPoolSize) {
            throw new IllegalArgumentException(String.format("corePoolSize(%d) must be <= maxPoolSize(%d)!", corePoolSize, maxPoolSize));
        }

        this.threadFactory = threadFactory;

        this.corePool = new ArrayList<>(this.corePoolSize = corePoolSize);
        this.supplementaryPool = new ArrayList<>(this.supplementaryPoolSize = ((this.maxPoolSize = maxPoolSize) - corePoolSize));

        this.threadKeepAliveTime = threadKeepAliveTime;

        //init core pool
        for (int i = 0; i < corePoolSize; i++) {
            Thread t = threadFactory.apply(i, () -> this.workerHeartbeat(true));
            this.corePool.add(t);
            t.start();
        }
    }

    @Override
    public Future submit(@NonNull Runnable task) {
        this.closingLock.readLock().lock();
        try {
            if (this.closingDown != 0) {
                throw new IllegalStateException("Executor stopped!");
            }
            Future future = new SimpleFuture();
            this.tasks.addLast(new Tuple<>(() -> {
                try {
                    task.run();
                    future.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }, future));
            this.considerNewWorkers();
            return future;
        } finally {
            this.closingLock.readLock().unlock();
        }
    }

    @Override
    public <T> ReturnableFuture<T> submit(@NonNull Supplier<T> task) {
        this.closingLock.readLock().lock();
        try {
            if (this.closingDown != 0) {
                throw new IllegalStateException("Executor stopped!");
            }
            ReturnableFuture<T> future = new SimpleReturnableFuture<>();
            this.tasks.addLast(new Tuple<>(() -> {
                try {
                    future.complete(task.get());
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }, future));
            this.considerNewWorkers();
            return future;
        } finally {
            this.closingLock.readLock().unlock();
        }
    }

    protected void considerNewWorkers() {
        int active = this.activeTasks.incrementAndGet();
        if (active > this.corePoolSize + this.supplementaryPool.size()) {
            synchronized (this.supplementaryPool) {
                if (this.corePoolSize + this.supplementaryPool.size() < this.maxPoolSize) {
                    System.out.println("Creating new worker!");
                    Thread t = this.threadFactory.apply(this.corePoolSize + this.supplementaryPool.size() - 1, () -> this.workerHeartbeat(false));
                    this.supplementaryPool.add(t);
                    t.start();
                }
            }
        } else {
            this.notificationQueue.signal();
        }
    }

    @Override
    public void close() {
        this.doStop(false);
    }

    @Override
    public void cancel() {
        this.doStop(true);
    }

    protected void doStop(boolean cancel) {
        this.closingLock.writeLock().lock();
        try {
            if (!PUnsafe.compareAndSwapInt(this, CLOSINGDOWN_OFFSET, 0, 1)) {
                throw new IllegalStateException("Already stopped!");
            }
            if (cancel) {
                InterruptedException e = new InterruptedException();
                for (Tuple<Runnable, Future> tuple : this.tasks) {
                    tuple.getB().completeExceptionally(e);
                }
                this.tasks = PCollections.emptyDeque();
            }
            this.notificationQueue.signalAll();
        } finally {
            this.closingLock.writeLock().unlock();
        }
    }

    protected void workerHeartbeat(boolean core) {
        boolean hasRun, firstRun = true;
        int closingDownOld;
        do {
            hasRun = false;
            closingDownOld = this.closingDown;
            if (firstRun)   {
                firstRun = false;
            } else {
                this.notificationQueue.await(this.threadKeepAliveTime);
            }
            {
                Tuple<Runnable, Future> task;
                while ((task = this.tasks.pollFirst()) != null) { //keep running tasks until the work queue is empty
                    hasRun = true; //mark this thread as having run to reset keepalive timer
                    task.getA().run();
                    this.activeTasks.decrementAndGet();
                }
            }
        } while ((core || hasRun) && closingDownOld == 0); //core threads stay alive even if they don't do anything
        //also we check if this executor was closing down before we tried to run tasks

        Collection<Thread> currentThreadGroup = core ? this.corePool : this.supplementaryPool;
        synchronized (currentThreadGroup) {
            currentThreadGroup.remove(Thread.currentThread());
        }

        if (!core)  {
            System.out.println("Worker closing!");
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        @NonNull
        protected BiFunction<Integer, Runnable, Thread> threadFactory = (id, runnable) -> {
            Thread t = new Thread(runnable);
            t.setName(SimpleThreadPoolTaskExecutor.getDefaultThreadName(id));
            return t;
        };
        protected int corePoolSize = 0;
        protected int maxPoolSize = 1;
        protected long threadKeepAliveTime = TimeUnit.MINUTES.toMillis(1);

        public SimpleThreadPoolTaskExecutor build() {
            return new SimpleThreadPoolTaskExecutor(this.threadFactory, this.corePoolSize, this.maxPoolSize, this.threadKeepAliveTime);
        }
    }
}
