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

import net.daporkchop.lib.concurrent.synchronization.NotificationQueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple implementation of a {@link NotificationQueue} using built-in Java locks.
 * <p>
 * This isn't as fast as it possibly could be (?) due to having to obtain a lock before executing other operations, but is probably
 * safer than using a {@link SimpleNotificationQueue}.
 *
 * @author DaPorkchop_
 */
public class JavaNotificationQueue implements NotificationQueue {
    protected final Lock lock = new ReentrantLock();
    protected final Condition condition = this.lock.newCondition();

    @Override
    public void awaitNanos(long maxWaitNanos) {
        this.lock.lock();
        try {
            if (maxWaitNanos < 0L) {
                this.condition.await();
            } else {
                this.condition.awaitNanos(maxWaitNanos);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void signal() {
        this.lock.lock();
        try {
            this.condition.signal();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void signalAll() {
        this.lock.lock();
        try {
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }
}
