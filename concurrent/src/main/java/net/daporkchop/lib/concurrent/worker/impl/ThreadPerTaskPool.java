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

package net.daporkchop.lib.concurrent.worker.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.concurrent.future.Completable;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.WorkerPool;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public class ThreadPerTaskPool implements WorkerPool {
    protected static final long ACTIVE_OFFSET = PUnsafe.pork_getOffset(ThreadPerTaskPool.class, "active");

    protected volatile boolean terminated = false;

    protected volatile int active = 0;

    @Override
    public Worker next() {
        return this;
    }

    @Override
    public Promise stop() {
        return this.terminate();
    }

    @Override
    public Promise terminate() {
        this.terminated = true;
        return null;
    }

    @Override
    public Promise submit(@NonNull Runnable task) {
        if (this.terminated)    {
            task.run();
            //TODO
        } else {
        }
    }

    @Override
    public <R> Future<R> submit(@NonNull Callable<R> task) {
        return null;
    }

    @Override
    public <P> Promise submit(P arg, @NonNull Consumer<P> task) {
        return null;
    }

    @Override
    public <P, R> Future<R> submit(P arg, @NonNull Function<P, R> task) {
        return null;
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    protected class Worker extends Thread   {
        final Object arg;
        final Object func;
        final Completable completable;

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            PUnsafe.getAndAddInt(ThreadPerTaskPool.class, ACTIVE_OFFSET, 1);
            try {
                if (this.arg == null) {
                    if (this.func instanceof Runnable)  {
                        ((Runnable) this.func).run();
                        ((Promise) this.completable).completeSuccessfully();
                    } else {
                        ((Future) this.completable).completeSuccessfully(((Callable) this.func).call());
                    }
                } else {
                    if (this.func instanceof Consumer)  {
                        ((Consumer) this.func).accept(this.arg);
                        ((Promise) this.completable).completeSuccessfully();
                    } else {
                        ((Future) this.completable).completeSuccessfully(((Function) this.func).apply(this.arg));
                    }
                }
            } catch (Exception e)   {
                this.completable.completeError(e);
            } finally {
                PUnsafe.getAndAddInt(ThreadPerTaskPool.class, ACTIVE_OFFSET, -1);
            }
        }
    }
}
