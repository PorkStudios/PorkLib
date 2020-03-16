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

package net.daporkchop.lib.concurrent.compatibility;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.concurrent.PFuture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Wraps a Netty {@link Future} into a {@link PFuture}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class NettyFutureAsPFuture<V> implements PFuture<V> {
    protected static final Method DEFAULTPROMISE_EXECUTOR;
    protected static final Method COMPLETEFUTURE_EXECUTOR;

    static {
        try {
            DEFAULTPROMISE_EXECUTOR = DefaultPromise.class.getDeclaredMethod("executor");
            DEFAULTPROMISE_EXECUTOR.setAccessible(true);

            COMPLETEFUTURE_EXECUTOR = CompleteFuture.class.getDeclaredMethod("executor");
            COMPLETEFUTURE_EXECUTOR.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final Future<V>     delegate;
    protected final EventExecutor executor;

    protected NettyFutureAsCompletableFuture<V> completableFuture;

    public NettyFutureAsPFuture(@NonNull Future<V> delegate) {
        this.delegate = delegate;

        try {
            if (delegate instanceof DefaultPromise) {
                this.executor = (EventExecutor) DEFAULTPROMISE_EXECUTOR.invoke(delegate, PorkUtil.EMPTY_OBJECT_ARRAY);
            } else if (delegate instanceof CompleteFuture) {
                this.executor = (EventExecutor) COMPLETEFUTURE_EXECUTOR.invoke(delegate, PorkUtil.EMPTY_OBJECT_ARRAY);
            } else {
                throw new IllegalArgumentException(PorkUtil.className(delegate));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSuccess() {
        return this.delegate.isSuccess();
    }

    @Override
    public boolean isCancellable() {
        return this.delegate.isCancellable();
    }

    @Override
    public Throwable cause() {
        return this.delegate.cause();
    }

    @Override
    public PFuture<V> addListener(@NonNull GenericFutureListener<? extends Future<? super V>> listener) {
        this.delegate.addListener(listener);
        return this;
    }

    @Override
    public PFuture<V> addListeners(@NonNull GenericFutureListener<? extends Future<? super V>>... listeners) {
        this.delegate.addListeners(listeners);
        return this;
    }

    @Override
    public PFuture<V> removeListener(@NonNull GenericFutureListener<? extends Future<? super V>> listener) {
        this.delegate.removeListener(listener);
        return this;
    }

    @Override
    public PFuture<V> removeListeners(@NonNull GenericFutureListener<? extends Future<? super V>>... listeners) {
        this.delegate.removeListeners(listeners);
        return this;
    }

    @Override
    public PFuture<V> sync() throws InterruptedException {
        this.delegate.sync();
        return this;
    }

    @Override
    public PFuture<V> syncUninterruptibly() {
        this.delegate.syncUninterruptibly();
        return this;
    }

    @Override
    public PFuture<V> await() throws InterruptedException {
        this.delegate.await();
        return this;
    }

    @Override
    public PFuture<V> awaitUninterruptibly() {
        this.delegate.awaitUninterruptibly();
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.await(timeout, unit);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.delegate.await(timeoutMillis);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return this.delegate.awaitUninterruptibly(timeout, unit);
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return this.delegate.awaitUninterruptibly(timeoutMillis);
    }

    @Override
    public V getNow() {
        return this.delegate.getNow();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.delegate.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.get(timeout, unit);
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        NettyFutureAsCompletableFuture<V> completableFuture = this.completableFuture;
        if (completableFuture == null) {
            synchronized (this) {
                if ((completableFuture = this.completableFuture) == null) {
                    this.completableFuture = completableFuture = new NettyFutureAsCompletableFuture<>(this);
                }
            }
        }
        return completableFuture;
    }
}
