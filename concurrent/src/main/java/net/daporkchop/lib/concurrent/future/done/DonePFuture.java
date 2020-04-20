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

package net.daporkchop.lib.concurrent.future.done;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.compatibility.NettyFutureAsCompletableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link PFuture} that has already been completed.
 *
 * @author DaPorkchop_
 */
public abstract class DonePFuture<V> extends CompleteFuture<V> implements PFuture<V> {
    protected NettyFutureAsCompletableFuture<V> completableFuture;

    public DonePFuture(@NonNull EventExecutor executor) {
        super(executor);
    }

    @Override
    public EventExecutor executor() {
        return super.executor();
    }

    @Override
    public DonePFuture<V> await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public DonePFuture<V> awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public DonePFuture<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public DonePFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        super.addListeners(listeners);
        return this;
    }

    @Override
    public DonePFuture<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public DonePFuture<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
        super.removeListeners(listeners);
        return this;
    }

    @Override
    public DonePFuture<V> sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public DonePFuture<V> syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        NettyFutureAsCompletableFuture<V> completableFuture = this.completableFuture;
        if (completableFuture == null)  {
            synchronized (this) {
                if ((completableFuture = this.completableFuture) == null)   {
                    this.completableFuture = completableFuture = new NettyFutureAsCompletableFuture<>(this);
                }
            }
        }
        return completableFuture;
    }
}
