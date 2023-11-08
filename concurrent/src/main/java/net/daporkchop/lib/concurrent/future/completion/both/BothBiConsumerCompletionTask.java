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

package net.daporkchop.lib.concurrent.future.completion.both;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import lombok.NonNull;

import java.util.function.BiConsumer;

/**
 * A {@link BothBiCompletionTask} which will execute a {@link BiConsumer}.
 *
 * @author DaPorkchop_
 */
public class BothBiConsumerCompletionTask<V, U> extends BothBiCompletionTask<V, U, Void> {
    protected BiConsumer<? super V, ? super U> action;

    public BothBiConsumerCompletionTask(@NonNull EventExecutor executor, @NonNull Future<V> primary, @NonNull Future<U> secondary, boolean fork, @NonNull BiConsumer<? super V, ? super U> action) {
        super(executor, primary, secondary, fork);

        this.action = action;
    }

    @Override
    protected Void computeResult(V v1, U v2) throws Exception {
        try {
            this.action.accept(v1, v2);
            return null;
        } finally {
            this.action = null;
        }
    }

    @Override
    protected void onFailure(Throwable cause) {
        this.action = null;
    }
}
