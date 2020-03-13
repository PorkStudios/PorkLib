/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.concurrent.future.completion.either;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;
import net.daporkchop.lib.concurrent.future.completion.CompletionTask;

import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * A {@link CompletionTask} which awaits the completion of either one of two separate {@link Future}s.
 *
 * @author DaPorkchop_
 */
public abstract class EitherBiCompletionTask<V, R> extends DefaultPFuture<R> implements GenericFutureListener<Future<V>>, Runnable {
    protected static final long RUN_OFFSET = pork_getOffset(EitherBiCompletionTask.class, "run");

    protected volatile Future<V> primary;
    protected volatile Future<V> secondary;

    protected volatile int run = 0;

    protected final boolean fork;

    public EitherBiCompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> primary, @NonNull Future<V> secondary, boolean fork) {
        super(executor);

        if (primary == secondary) {
            throw new IllegalStateException("primary may not be the same as secondary!");
        }

        this.primary = primary;
        this.secondary = secondary;
        this.fork = fork;

        primary.addListener(this);
        secondary.addListener(this);
    }

    @Override
    public void operationComplete(Future<V> future) throws Exception {
        Future<V> primary = this.primary;
        Future<V> secondary = this.secondary;
        if (primary == null || secondary == null) {
            throw new IllegalStateException("already run!");
        } else if (primary != future && secondary != future) {
            throw new IllegalArgumentException("wrong future?!?");
        } else if (primary.isSuccess() || secondary.isSuccess() || (primary.isDone() && secondary.isDone())) {
            //if either one was successful, or both have completed (unsuccessfully), run
            if (this.fork) {
                this.executor().submit(this);
            } else {
                this.run();
            }
        }
    }

    @Override
    public void run() {
        Future<V> primary = this.primary;
        Future<V> secondary = this.secondary;
        try {
            if (primary == null || secondary == null) {
                throw new IllegalStateException("already run!");
            } else if (!primary.isDone() && !secondary.isDone()) {
                throw new IllegalStateException("not done?!?");
            } else if (primary.isSuccess()) {
                if (compareAndSwapInt(this, RUN_OFFSET, 0, 1))  {
                    this.trySuccess(this.computeResult(primary.getNow()));
                }
            } else if (secondary.isSuccess()) {
                if (compareAndSwapInt(this, RUN_OFFSET, 0, 1))  {
                    this.trySuccess(this.computeResult(secondary.getNow()));
                }
            } else if (primary.isDone() && secondary.isDone()) {
                //if we get here and both futures have completed, we know that both futures completed unsuccessfully
                this.tryFailure(primary.cause());
            } else {
                throw new IllegalStateException("what");
            }
        } catch (Throwable e) {
            this.tryFailure(e);
            throwException(e);
        } finally {
            if (this.run != 0) {
                this.primary = null;
                this.secondary = null;
            }
        }
    }

    protected abstract R computeResult(V v) throws Exception;

    @Override
    public EitherBiCompletionTask<V, R> setFailure(Throwable cause) {
        super.setFailure(cause);
        this.onFailure(cause);
        return this;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        if (super.tryFailure(cause)) {
            this.onFailure(cause);
            return true;
        } else {
            return false;
        }
    }

    protected abstract void onFailure(Throwable cause);
}
