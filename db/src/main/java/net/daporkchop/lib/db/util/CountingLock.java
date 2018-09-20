/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.util;

import net.daporkchop.lib.db.util.exception.WrappedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * A simple, lightweight implementation of a lock which counts the number of threads currently using/waiting on it
 *
 * @author DaPorkchop_
 */
public class CountingLock implements Lock {
    private final AtomicInteger using = new AtomicInteger(0);
    private volatile boolean locked;

    @Override
    public void lock() {
        this.using.incrementAndGet();
        synchronized (this) {
            while (this.locked) {
                try {
                    this.wait(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new WrappedException(e);
                }
            }
            this.locked = true;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.using.incrementAndGet();
        synchronized (this) {
            while (this.locked) {
                this.wait(1L);
            }
            this.locked = true;
        }
    }

    @Override
    public boolean tryLock() {
        if (this.locked) {
            return false;
        } else {
            this.using.incrementAndGet();
            return this.locked = true;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (this.locked) {
            synchronized (this) {
                this.wait(unit.toMillis(time));
            }
        }
        return this.tryLock();
    }

    @Override
    public void unlock() {
        if (!this.locked) {
            throw new IllegalStateException("Not locked!");
        }
        this.locked = false;
        synchronized (this) {
            this.notify();
        }
        this.using.decrementAndGet();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public int getCurrentlyUsing() {
        return this.using.get();
    }
}
