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

package net.daporkchop.lib.concurrent.lock.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.concurrent.lock.Latch;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.LinkedList;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class CountingLatch implements Latch {
    protected static final long LISTENERS_OFFSET = PUnsafe.pork_getOffset(CountingLatch.class, "listeners");
    protected static final long TICKETS_OFFSET = PUnsafe.pork_getOffset(CountingLatch.class, "tickets");
    
    protected final Object mutex = new Object[0];
    protected volatile Object listeners = null;
    @NonNull
    protected volatile int tickets;

    @Override
    public int tickets() {
        return this.tickets;
    }

    @Override
    public void release() {
        if (PUnsafe.getAndAddInt(this, TICKETS_OFFSET, -1) - 1 == 0)    {
            this.tickets = -1;
            this.fireListeners();
        }
    }

    @Override
    public void sync() {
        while (this.tickets > 0) {
            try {
                synchronized (this.mutex) {
                    this.mutex.wait();
                }
            } catch (InterruptedException e)    {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void syncInterruptably() throws InterruptedException {
        while (this.tickets > 0) {
            synchronized (this.mutex) {
                this.mutex.wait();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addListener(@NonNull Runnable callback) {
        Object prev, next;
        do {
            if ((prev = this.listeners) == null) {
                if (this.tickets <= 0) {
                    callback.run();
                    return;
                } else {
                    //first listener
                    next = callback;
                }
            } else if (prev instanceof List) {
                ((List) prev).add(callback);
                next = prev;
            } else {
                next = new LinkedList<>(); //TODO: pool these
                ((List) next).add(callback);
            }
        } while (!PUnsafe.compareAndSwapObject(this, LISTENERS_OFFSET, prev, next));
        return;
    }

    @SuppressWarnings("unchecked")
    protected void fireListeners() {
        Object obj = PUnsafe.pork_swapIfNonNull(this, LISTENERS_OFFSET, null);
        if (obj != null) {
            if (obj instanceof List) {
                for (Runnable listener : (List<Runnable>) obj) {
                    listener.run();
                }
            } else if (obj instanceof Runnable) {
                ((Runnable) obj).run();
            }
        }
    }
}
