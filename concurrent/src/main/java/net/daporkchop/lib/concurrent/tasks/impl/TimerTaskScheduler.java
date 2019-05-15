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

package net.daporkchop.lib.concurrent.tasks.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.PCompletable;
import net.daporkchop.lib.concurrent.future.PFuture;
import net.daporkchop.lib.concurrent.future.impl.PCompletableImpl;
import net.daporkchop.lib.concurrent.future.impl.PFutureImpl;
import net.daporkchop.lib.concurrent.tasks.TaskScheduler;
import net.daporkchop.lib.math.primitive.PMath;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class TimerTaskScheduler implements TaskScheduler {
    protected final Object mutex = new Object[0];

    @NonNull
    protected Timer timer;

    public TimerTaskScheduler() {
        this(new Timer());
    }

    @Override
    public PCompletable schedule(@NonNull Runnable task, long time) {
        synchronized (this.mutex) {
            PCompletable future = new PCompletableImpl();
            this.timer.schedule(new TimerTask() {
                volatile boolean started = false;

                @Override
                public void run() {
                    this.started = true;
                    try {
                        task.run();
                        future.complete();
                    } catch (Exception e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public boolean cancel() {
                    if (super.cancel()) {
                        if (!this.started) {
                            future.completeExceptionally(new CancellationException());
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }, PMath.max(System.currentTimeMillis() - time, 0L));
            return future;
        }
    }

    @Override
    public <T> PFuture<T> schedule(@NonNull Supplier<T> task, long time) {
        synchronized (this.mutex) {
            PFuture<T> future = new PFutureImpl<>();
            this.timer.schedule(new TimerTask() {
                volatile boolean started = false;

                @Override
                public void run() {
                    this.started = true;
                    try {
                        future.complete(task.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public boolean cancel() {
                    if (super.cancel()) {
                        if (!this.started) {
                            future.completeExceptionally(new CancellationException());
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }, PMath.max(System.currentTimeMillis() - time, 0L));
            return future;
        }
    }

    @Override
    public void scheduleRepeating(@NonNull BooleanSupplier task, long startTime, long interval) {
        synchronized (this.mutex) {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!task.getAsBoolean()) {
                        this.cancel();
                    }
                }
            }, PMath.max(System.currentTimeMillis() - startTime, 0L), interval);
        }
    }

    @Override
    public void close() {
        synchronized (this.mutex) {
            this.timer = null;
        }
    }

    @Override
    public void cancel() {
        synchronized (this.mutex) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }
}
