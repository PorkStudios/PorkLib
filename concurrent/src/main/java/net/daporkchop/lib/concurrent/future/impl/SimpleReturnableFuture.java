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
import net.daporkchop.lib.concurrent.future.ReturnableFuture;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * A basic implementation of a {@link ReturnableFuture}.
 *
 * @author DaPorkchop_
 */
public class SimpleReturnableFuture<V> implements ReturnableFuture<V> {
    protected final Object mutex = new Object[0];
    protected volatile V value = null;
    protected volatile Exception exception = null;
    protected volatile boolean complete = false;

    @Override
    public V getInterruptably() throws InterruptedException {
        if (!this.complete) {
            synchronized (this.mutex)   {
                if (!this.complete) {
                    this.mutex.wait();
                }
            }
        }
        if (this.exception != null) {
            PUnsafe.throwException(this.exception);
        }
        return this.value;
    }

    @Override
    public boolean isCompleted() {
        return this.complete;
    }

    @Override
    public void complete(V value) {
        this.doComplete(value, null);
    }

    @Override
    public void completeExceptionally(@NonNull Exception e) {
        this.doComplete(null, e);
    }

    protected void doComplete(V value, Exception e) {
        synchronized (this.mutex) {
            if (this.complete) {
                throw new IllegalStateException("Already complete!");
            } else {
                this.value = value;
                this.exception = e; //set exception and value first, just in case
                this.complete = true;
                this.mutex.notifyAll();
            }
        }
    }
}
