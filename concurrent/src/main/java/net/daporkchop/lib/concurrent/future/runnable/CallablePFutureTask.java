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

package net.daporkchop.lib.concurrent.future.runnable;

import io.netty.util.concurrent.EventExecutor;
import lombok.NonNull;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A {@link net.daporkchop.lib.concurrent.PFuture} which, when run, will be completed with the result of a {@link Callable} task.
 *
 * @author DaPorkchop_
 */
public class CallablePFutureTask<V> extends AbstractRunnablePFuture<V> {
    protected Callable<? extends V> action;

    public CallablePFutureTask(@NonNull EventExecutor executor, @NonNull Callable<? extends V> action) {
        super(executor);

        this.action = action;
    }

    @Override
    protected V run0() throws Exception {
        return this.action.call();
    }

    @Override
    protected void cleanup() {
        this.action = null;
    }
}
