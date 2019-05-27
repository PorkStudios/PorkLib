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

package net.daporkchop.lib.concurrent.worker.group;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.Task;
import net.daporkchop.lib.concurrent.worker.WorkerGroup;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class FixedQueuedGroup implements WorkerGroup {
    protected static final long ALIVE_OFFSET = PUnsafe.pork_getOffset(FixedQueuedGroup.class, "alive");
    protected static final long OPEN_OFFSET = PUnsafe.pork_getOffset(FixedQueuedGroup.class, "open");

    protected final Thread[] workers;
    protected final BlockingQueue<Runnable> queue;
    @Getter
    protected final Promise closePromise = DefaultGroup.INSTANCE.newPromise();
    protected volatile int alive;
    protected volatile int open = 1;

    public FixedQueuedGroup(int workers)    {
        this(workers, Executors.defaultThreadFactory(), new LinkedBlockingQueue<>());
    }

    public FixedQueuedGroup(int workers, @NonNull ThreadFactory factory)    {
        this(workers, factory, new LinkedBlockingQueue<>());
    }
    public FixedQueuedGroup(int workers, @NonNull BlockingQueue<Runnable> queue)    {
        this(workers, Executors.defaultThreadFactory(), queue);
    }

    public FixedQueuedGroup(int workers, @NonNull ThreadFactory factory, @NonNull BlockingQueue<Runnable> queue)    {
        if (workers < 1)    {
            throw new IllegalArgumentException("Must have at least 1 worker!");
        }
        this.workers = new Thread[this.alive = workers];
        this.queue = queue;

        Runnable func = () -> {
            try {
                Runnable task;
                while (this.open != 0)   {
                    try {
                        if ((task = this.queue.poll(500L, TimeUnit.MILLISECONDS)) != null)   {
                            try {
                                task.run();
                            } catch (Exception e)   {
                                new RuntimeException(e).printStackTrace();
                            } finally {
                                task = null; //allow task to be garbage-collected
                            }
                        }
                    } catch (InterruptedException e)    {
                        //accept interrupts from close
                    }
                }
            } catch (Exception e)   {
                this.closePromise.tryCompleteError(e);
                throw new RuntimeException(e);
            } finally {
                if (PUnsafe.getAndAddInt(this, ALIVE_OFFSET, -1) == 1)  {
                    //decremented from 1 to 0
                    this.closePromise.tryCompleteSuccessfully();
                }
            }
        };
        for (int i = 0; i < workers; i++)   {
            this.workers[i] = factory.newThread(func);
        }
        for (int i = 0; i < workers; i++)   {
            this.workers[i].start();
        }
    }

    @Override
    public Promise closeAsync() {
        if (PUnsafe.compareAndSwapInt(this, OPEN_OFFSET, 1, 0)) {
            for (Thread t : this.workers)   {
                t.interrupt(); //interrupt all workers to notify them that they shouldn't poll the queue any more
            }
            for (Runnable r : this.queue)   {
                if (r instanceof Task)  {
                    ((Task) r).cancel(); //cancel all tasks
                }
            }
            this.queue.clear();
        }
        return this.closePromise;
    }

    @Override
    public void submitFast(@NonNull Runnable task) {
        if (this.open != 0) {
            this.queue.add(task);
        }
    }
}
