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

package net.daporkchop.lib.concurrent.future.impl;

import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * A basic implementation of a {@link Future}.
 *
 * @author DaPorkchop_
 */
public class SimpleFuture implements Future {
    //protected static final long COMPLETE_OFFSET = PUnsafe.pork_getOffset(SimpleFuture.class, "complete");
    //protected static final long EXCEPTION_OFFSET = PUnsafe.pork_getOffset(SimpleFuture.class, "exception");

    protected final Object mutex = new Object[0];
    protected volatile Exception exception = null;
    protected volatile boolean complete = false;
    //protected volatile int complete = 0; //int instead of boolean because of atomics and x86 being dumb, there shouldn't really be any perforamce cost

    @Override
    public void syncInterruptably() throws InterruptedException {
        if (!this.complete) { //check before locking mutex as well to avoid the expensive operation of obtaining a lock if already complete
            synchronized (this.mutex) {
                if (!this.complete) {
                    this.mutex.wait();
                }
            }
        }
        if (this.exception != null) {
            PUnsafe.throwException(this.exception);
        }
    }

    @Override
    public boolean isCompleted() {
        return this.complete;
    }

    @Override
    public void complete() {
        this.doComplete(null);
    }

    @Override
    public void completeExceptionally(@NonNull Exception e) {
        this.doComplete(e);
    }

    protected void doComplete(Exception e) {
        synchronized (this.mutex) {
            /*if (PUnsafe.compareAndSwapObject(this, EXCEPTION_OFFSET, null, e) && PUnsafe.compareAndSwapInt(this, COMPLETE_OFFSET, 0, 1)) { //set exception first just to be sure, this isn't a very good check tho
                this.mutex.notifyAll();
            }*/
            if (this.complete) {
                throw new IllegalStateException("Already complete!");
            } else {
                this.exception = e; //set exception first, just in case
                this.complete = true;
                this.mutex.notifyAll();
            }
        }
    }
}
