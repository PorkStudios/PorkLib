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
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * A {@link CompletionTask} which awaits the completion of two separate {@link java.util.concurrent.CompletionStage}s.
 *
 * @author DaPorkchop_
 */
public abstract class BothBiCompletionTask<V, U, R> extends DefaultPFuture<R> implements GenericFutureListener<Future<?>> {
    protected static final long PRIMARY_OFFSET   = pork_getOffset(BothBiCompletionTask.class, "primary");
    protected static final long SECONDARY_OFFSET = pork_getOffset(BothBiCompletionTask.class, "secondary");

    protected volatile Future<V> primary;
    protected volatile Future<U> secondary;

    protected final boolean fork;

    public BothBiCompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> primary, @NonNull Future<U> secondary, boolean fork) {
        super(executor);

        if (primary == secondary) {
            throw new IllegalStateException("primary may not be the same as secondary!");
        }

        this.primary = primary;
        this.secondary = secondary;
        this.fork = fork;

        primary.addListener(uncheckedCast(primary));
        secondary.addListener(uncheckedCast(secondary));
    }

    @Override
    public void operationComplete(Future future) throws Exception {
        if (future != this.primary && future != this.secondary) {
            throw new IllegalArgumentException();
        } else if (future.isSuccess()) {
            if (this.primary.isSuccess() && this.secondary.isSuccess() && compareAndSwapInt(this, RUN_OFFSET, 0, 1)) {
                try {
                    this.setSuccess(this.computeResult(this.primary.getNow(), this.secondary.getNow()));
                } catch (Exception e) {
                    this.tryFailure(e);
                    throw e;
                } finally {
                    this.primary = null;
                    this.secondary = null;
                }
            }
        } else {
            if (future.isCancelled()) {
                this.cancel(true);
            } else {
                this.tryFailure(future.cause());
            }
            this.handleFailure(future.cause());
        }
    }

    protected abstract R computeResult(V v1, U v2) throws Exception;

    @Override
    protected final R computeResult(V value) throws Exception {
        //no-op
        return null;
    }

    @Override
    protected void handleFailure(@NonNull Throwable cause) {
        this.primary = null;
        this.secondary = null;
    }
}
