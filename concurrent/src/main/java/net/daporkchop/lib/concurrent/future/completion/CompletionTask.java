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

package net.daporkchop.lib.concurrent.future.completion;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;

import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * An abstract representation of a task which will be executed upon completion of another {@link Future}.
 *
 * @author DaPorkchop_
 */
public abstract class CompletionTask<V, R> extends DefaultPFuture<R> implements GenericFutureListener<Future<V>>, Runnable {
    protected static final long DEPENDS_OFFSET = pork_getOffset(CompletionTask.class, "depends");

    protected volatile Future<V> depends;
    protected final    boolean   fork;

    public CompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> depends, boolean fork) {
        super(executor);

        this.depends = depends;
        this.fork = fork;

        depends.addListener(this);
    }

    @Override
    public void operationComplete(Future<V> future) throws Exception {
        Future<V> depends = this.depends;
        if (depends == null) {
            throw new IllegalStateException("already run!");
        } else if (depends != future) {
            throw new IllegalArgumentException("wrong future?!?");
        }

        if (this.fork) {
            this.executor().submit(this);
        } else {
            this.run();
        }
    }

    @Override
    public void run() {
        Future<V> depends = pork_swapIfNonNull(this, DEPENDS_OFFSET, null);
        try {
            if (depends == null) {
                throw new IllegalStateException("already run!");
            } else if (!depends.isDone()) {
                throw new IllegalStateException("not done?!?");
            } else if (depends.isSuccess()) {
                this.trySuccess(this.computeResult(depends.getNow()));
            } else {
                if (depends.isCancelled()) {
                    this.cancel(true);
                } else {
                    this.tryFailure(depends.cause());
                }
            }
        } catch (Throwable e) {
            this.tryFailure(e);
            throwException(e);
        }
    }

    protected abstract R computeResult(V value) throws Exception;

    @Override
    public CompletionTask<V, R> setFailure(Throwable cause) {
        super.setFailure(cause);
        this.onFailure(cause);
        return this;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        if (super.tryFailure(cause))    {
            this.onFailure(cause);
            return true;
        } else {
            return false;
        }
    }

    protected abstract void onFailure(Throwable cause);
}
