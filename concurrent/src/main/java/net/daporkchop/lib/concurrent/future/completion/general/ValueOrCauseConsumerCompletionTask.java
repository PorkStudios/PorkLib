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

package net.daporkchop.lib.concurrent.future.completion.general;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.compatibility.NettyFutureAsPFuture;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;
import net.daporkchop.lib.concurrent.future.completion.CompletionTask;

import java.util.function.BiConsumer;

import static net.daporkchop.lib.common.util.PorkUtil.uncheckedCast;
import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * A {@link CompletionTask} which will pass both the value and failure cause to the given {@link BiConsumer}, where at least
 * one of the two parameters is guaranteed to be null (as a future cannot fail and return a value at once).
 *
 * @author DaPorkchop_
 */
public class ValueOrCauseConsumerCompletionTask<V> extends DefaultPFuture<V> implements GenericFutureListener<Future<V>>, Runnable {
    protected BiConsumer<? super V, ? super Throwable> action;

    protected volatile Future<V> depends;

    protected final boolean fork;

    public ValueOrCauseConsumerCompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> depends, boolean fork, @NonNull BiConsumer<? super V, ? super Throwable> action) {
        super(executor);

        this.action = action;
        this.depends = depends instanceof NettyFutureAsPFuture ? uncheckedCast(((NettyFutureAsPFuture) depends).delegate()) : depends;
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
        Future<V> depends = this.depends;
        try {
            if (depends == null) {
                throw new IllegalStateException("already run!");
            } else if (!depends.isDone()) {
                throw new IllegalStateException("not done?!?");
            } else if (depends.isSuccess()) {
                this.action.accept(depends.getNow(), null);
            } else if (depends.isCancelled()) {
                this.cancel(true);
            } else {
                this.action.accept(null, depends.cause());
            }
            this.trySuccess(depends.getNow());
        } catch (Throwable e) {
            this.tryFailure(e);
            throwException(e);
        } finally {
            this.action = null;
            this.depends = null;
        }
    }
}
