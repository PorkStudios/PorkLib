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

package net.daporkchop.lib.concurrent.future.group;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A {@link net.daporkchop.lib.concurrent.PFuture} which aggregates a number of futures into a single result.
 *
 * @author DaPorkchop_
 */
public class CollectAllToListFuture<V> extends DefaultPFuture<List<V>> implements GenericFutureListener<Future<V>> {
    protected final Set<Future<V>> allWaiting;
    protected final List<V> results;
    protected Throwable failureCause;

    public CollectAllToListFuture(@NonNull EventExecutor executor, @NonNull Collection<Future<V>> waiting) {
        super(executor);
        checkArg(!waiting.isEmpty(), "waiting may not be empty!");

        this.allWaiting = new HashSet<>(waiting);
        this.results = new ArrayList<>(this.allWaiting.size());

        waiting.forEach(f -> f.addListener(this));
    }

    @Override
    public synchronized void operationComplete(Future<V> future) throws Exception {
        checkState(this.allWaiting.remove(future), "given future was not registered to this group!");

        if (future.isSuccess()) {
            this.results.add(future.getNow());
        } else {
            if (this.failureCause == null) {
                this.failureCause = new ExecutionException(null);
            }
            this.failureCause.addSuppressed(future.cause());
        }

        if (this.allWaiting.isEmpty()) {
            if (this.failureCause == null)  {
                this.setSuccess(this.results);
            } else {
                this.setFailure(this.failureCause);
            }
        }
    }
}
