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

package net.daporkchop.lib.concurrent.future.task;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author DaPorkchop_
 */
public class ScheduledFutureWrapper<V> extends BasePromiseTask<V> implements RunnableScheduledFuture<V>, ScheduledFuture<V> {
    private final RunnableScheduledFuture<V> delegate;

    public ScheduledFutureWrapper(EventExecutor executor, Runnable runnable, V result, @NonNull RunnableScheduledFuture<V> delegate) {
        super(executor, runnable, result);

        this.delegate = delegate;
    }

    public ScheduledFutureWrapper(EventExecutor executor, Callable<V> callable, @NonNull RunnableScheduledFuture<V> delegate) {
        super(executor, callable);

        this.delegate = delegate;
    }

    @Override
    public void run() {
        if (!this.isPeriodic()) {
            super.run();
        } else if (!this.isDone()) {
            try {
                // Its a periodic task so we need to ignore the return value
                this.task.call();
            } catch (Throwable cause) {
                if (!this.tryFailureInternal(cause)) {
                    new RuntimeException("Failure during execution of task", cause).printStackTrace(System.err);
                }
            }
        }
    }

    @Override
    public boolean isPeriodic() {
        return this.delegate.isPeriodic();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return this.delegate.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        return this.delegate.compareTo(o);
    }
}
