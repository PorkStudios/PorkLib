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
import net.daporkchop.lib.concurrent.future.PCompletable;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A very simple implementation of {@link PCompletable}, with everything synchronized on a single mutex.
 * <p>
 * I could probably make this faster and lighter by using Unsafe atomic swaps later...
 *
 * @author DaPorkchop_
 */
public class PCompletableImpl implements PCompletable {
    protected final Object mutex = new Object[0];
    protected volatile Throwable t;
    protected volatile List<BiConsumer<Void, Throwable>> listeners;
    protected volatile boolean complete = false;

    @Override
    public void sync() {
        synchronized (this.mutex) {
            while (!this.complete) {
                try {
                    this.mutex.wait();
                } catch (InterruptedException e) {
                }
            }

            if (this.t != null) {
                PUnsafe.throwException(this.t);
            }
        }
    }

    @Override
    public void syncInterruptably() throws InterruptedException {
        synchronized (this.mutex) {
            if (!this.complete) {
                this.mutex.wait();
            }

            if (this.t != null) {
                PUnsafe.throwException(this.t);
            }
        }
    }

    @Override
    public void sync(long time) {
        synchronized (this.mutex) {
            if (!this.complete) {
                try {
                    this.mutex.wait(time);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (this.complete && this.t != null) {
                PUnsafe.throwException(this.t);
            }
        }
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void complete() {
        synchronized (this.mutex) {
            if (this.complete) {
                throw new IllegalStateException("Already completed!");
            } else {
                this.complete = true;
                this.mutex.notifyAll();

                if (this.listeners != null) {
                    for (BiConsumer<Void, Throwable> callback : this.listeners) {
                        callback.accept(null, null);
                    }
                    this.listeners = null;
                }
            }
        }
    }

    @Override
    public void tryComplete() {
        synchronized (this.mutex) {
            if (!this.complete) {
                this.complete = true;
                this.mutex.notifyAll();

                if (this.listeners != null) {
                    for (BiConsumer<Void, Throwable> callback : this.listeners) {
                        callback.accept(null, null);
                    }
                    this.listeners = null;
                }
            }
        }
    }

    @Override
    public void completeExceptionally(@NonNull Throwable t) {
        synchronized (this.mutex) {
            if (this.complete) {
                throw new IllegalStateException("Already completed!");
            } else {
                this.t = t;
                this.complete = true;
                this.mutex.notifyAll();

                if (this.listeners != null) {
                    for (BiConsumer<Void, Throwable> callback : this.listeners) {
                        callback.accept(null, t);
                    }
                    this.listeners = null;
                }
            }
        }
    }

    @Override
    public void tryCompleteExceptionally(@NonNull Throwable t) {
        synchronized (this.mutex) {
            if (!this.complete) {
                this.t = t;
                this.complete = true;
                this.mutex.notifyAll();

                if (this.listeners != null) {
                    for (BiConsumer<Void, Throwable> callback : this.listeners) {
                        callback.accept(null, t);
                    }
                    this.listeners = null;
                }
            }
        }
    }

    @Override
    public void addListener(@NonNull BiConsumer<Void, Throwable> callback) {
        synchronized (this.mutex) {
            if (this.complete) {
                callback.accept(null, this.t);
            } else if (this.listeners == null) {
                this.listeners = new LinkedList<>(); //smallest footprint
            }
            this.listeners.add(callback);
        }
    }
}
