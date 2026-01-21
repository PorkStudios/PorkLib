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
import lombok.NonNull;

import java.util.function.Function;

/**
 * An {@link EitherBiCompletionTask} which will execute a {@link Function}.
 *
 * @author DaPorkchop_
 */
public class EitherBiFunctionCompletionTask<V, R> extends EitherBiCompletionTask<V, R> {
    protected Function<? super V, ? extends R> action;

    public EitherBiFunctionCompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> primary, @NonNull Future<V> secondary, boolean fork, @NonNull Function<? super V, ? extends R> action) {
        super(executor, primary, secondary, fork);

        this.action = action;
    }

    @Override
    protected R computeResult(V v) throws Exception {
        try {
            return this.action.apply(v);
        } finally {
            this.action = null;
        }
    }

    @Override
    protected void onFailure(Throwable cause) {
        this.action = null;
    }
}
