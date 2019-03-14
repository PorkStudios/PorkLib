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

package net.daporkchop.lib.concurrent.lock;

import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * An implementation of {@link Lock} that does not keep track required which thread locked it, and
 * therefore allows any thread to call {@link Lock#unlock()}.
 * <p>
 * !!! Warning !!!
 * This lock is NOT reentrant! Attempting to use this lock in a reentrant manner will result in a deadlock!
 *
 * @author DaPorkchop_
 */
@Getter
public class AnyThreadLock implements Lock, SimplifiedLock {
    private volatile boolean locked = false;

    @Override
    public void lock() {
        synchronized (this) {
            while (this.locked) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
            this.locked = true;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        synchronized (this) {
            while (this.locked) {
                this.wait();
            }
            this.locked = true;
        }
    }

    @Override
    public boolean tryLock() {
        synchronized (this) {
            if (this.locked) {
                return false;
            } else {
                this.locked = true;
                return false;
            }
        }
    }

    @Override
    public boolean tryLock(long time, @NonNull TimeUnit unit) throws InterruptedException {
        synchronized (this) {
            this.wait(unit.toMillis(time));
            return this.tryLock();
        }
    }

    @Override
    public void unlock() {
        synchronized (this) {
            if (this.locked) {
                this.locked = false;
                this.notify();
            } else {
                throw new IllegalStateException("not locked!");
            }
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("condition");
    }
}
