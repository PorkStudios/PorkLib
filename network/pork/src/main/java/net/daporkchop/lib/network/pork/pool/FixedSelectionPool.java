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

package net.daporkchop.lib.network.pork.pool;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.lock.Latch;
import net.daporkchop.lib.concurrent.lock.impl.CountingLatch;
import net.daporkchop.lib.network.pork.SelectionHandler;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link SelectionPool} with a fixed thread count.
 *
 * @author DaPorkchop_
 */
public class FixedSelectionPool extends AbstractSelectionPool {
    protected static final long OPEN_OFFSET = PUnsafe.pork_getOffset(FixedSelectionPool.class, "open");

    protected final Latch latch;
    protected final Worker[] workers;
    protected final Balancer balancer;
    protected volatile int open = 1;

    public FixedSelectionPool(int workers)  {
        this(workers, Executors.defaultThreadFactory(), new RoundRobinBalancer());
    }

    public FixedSelectionPool(int workers, @NonNull Balancer balancer)  {
        this(workers, Executors.defaultThreadFactory(), balancer);
    }

    public FixedSelectionPool(int workers, @NonNull ThreadFactory factory)  {
        this(workers, factory, new RoundRobinBalancer());
    }

    public FixedSelectionPool(int workers, @NonNull ThreadFactory factory, @NonNull Balancer balancer)  {
        if (workers <= 0)   {
            throw new IllegalArgumentException("Must have at least 1 worker!");
        }
        this.latch = new CountingLatch(workers);
        this.workers = new Worker[workers];
        this.balancer = balancer;
        for (int i = workers - 1; i >= 0; i--)  {
            this.workers[i] = new Worker(factory);
        }
    }

    @Override
    public void register(@NonNull SelectableChannel channel, int interestOps, @NonNull SelectionHandler handler) {
        Selector selector = this.workers[this.balancer.balance(this.workers.length)].selector;
        try {
            channel.configureBlocking(false);
            channel.register(selector, interestOps, handler);
        } catch (IOException e)  {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Promise closeAsync() {
        if (PUnsafe.compareAndSwapInt(this, OPEN_OFFSET, 1, 0)) {
            for (Worker worker : this.workers)  {
                worker.selector.wakeup();
            }
        }
        return this.closePromise;
    }

    protected class Worker implements Runnable  {
        protected final Selector selector;
        protected final Thread thread;

        public Worker(@NonNull ThreadFactory factory) {
            try {
                this.selector = Selector.open();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            (this.thread = factory.newThread(this)).start();
        }

        @Override
        public void run() {
            try {
                while (FixedSelectionPool.this.open != 0) {
                    this.selector.select();
                    for (Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator(); iter.hasNext();)    {
                        SelectionKey key = iter.next();
                        SelectionHandler handler = (SelectionHandler) key.attachment();
                        try {
                            handler.handle(key.readyOps());
                        } catch (Exception e)   {
                            if (key.attachment() instanceof SelectionHandler)   {
                                try {
                                    ((SelectionHandler) key.attachment()).handleException(e);
                                    continue;
                                } catch (Exception e1)  {
                                    new RuntimeException(e1).printStackTrace();
                                }
                            }
                            new RuntimeException(e).printStackTrace();
                        } finally {
                            iter.remove();
                        }
                    }
                }
                this.selector.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                FixedSelectionPool.this.latch.release();
            }
        }
    }

    @FunctionalInterface
    public interface Balancer   {
        int balance(int workerCount);
    }

    public static final class RoundRobinBalancer  implements Balancer  {
        protected int i = 0;

        @Override
        public synchronized int balance(int workerCount) {
            return this.i++ % workerCount;
        }
    }

    @RequiredArgsConstructor
    public static final class RandomBalancer  implements Balancer  {
        @NonNull
        protected final Random r;

        public RandomBalancer() {
            this(new Random());
        }

        @Override
        public synchronized int balance(int workerCount) {
            return this.r.nextInt(workerCount);
        }
    }
}
