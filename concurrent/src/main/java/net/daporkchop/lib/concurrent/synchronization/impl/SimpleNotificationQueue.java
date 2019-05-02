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

package net.daporkchop.lib.concurrent.synchronization.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.daporkchop.lib.concurrent.synchronization.NotificationQueue;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A simple implementation of a {@link NotificationQueue}
 *
 * @author DaPorkchop_
 */
//TODO: i don't think this is actually safe at all
public class SimpleNotificationQueue implements NotificationQueue {
    protected static final long QUEUE_OFFSET = PUnsafe.pork_getOffset(SimpleNotificationQueue.class, "queue");

    protected final Deque<Entry> queue = new ConcurrentLinkedDeque<>();

    @Override
    public void awaitNanos(long maxWaitNanos) {
        Entry entry = new Entry(Thread.currentThread(), maxWaitNanos < 0L ? -1L : System.nanoTime() + maxWaitNanos);
        this.queue.addLast(entry);
        if (maxWaitNanos < 0L) {
            do {
                PUnsafe.park(false, Long.MAX_VALUE);
            } while (!entry.done);
        } else {
            PUnsafe.park(false, maxWaitNanos);
            entry.done = true;
        }
        this.queue.remove(entry);
    }

    @Override
    public void signal() {
        Entry entry;
        while ((entry = this.queue.pollFirst()) != null)  {
            if (entry.endTime < System.nanoTime())   {
                entry.done = true;
                entry.thread.interrupt();
                return;
            }
        }
    }

    @Override
    public void signalAll() {
        for (Entry entry : PUnsafe.<Deque<Entry>>pork_swapObject(this, QUEUE_OFFSET, new ConcurrentLinkedDeque<>()))    {
            if (entry.endTime < System.nanoTime())   {
                entry.done = true;
                entry.thread.interrupt();
            }
        }
    }

    @RequiredArgsConstructor
    @ToString
    protected static class Entry    {
        @NonNull
        protected final Thread thread;
        protected final long endTime;
        protected volatile boolean done = false;
    }
}
