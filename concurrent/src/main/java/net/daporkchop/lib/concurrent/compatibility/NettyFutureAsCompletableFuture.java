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

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.concurrent.PFutures;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Wraps a Netty {@link Future} into a {@link CompletableFuture}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class NettyFutureAsCompletableFuture<V> extends CompletableFuture<V> implements GenericFutureListener<Future<V>> {
    protected final Future<V> delegate;
    protected final Executor  executor;

    public NettyFutureAsCompletableFuture(@NonNull Future<V> delegate) {
        this(delegate, PorkUtil.fallbackIfNull(PFutures.executor(delegate), ForkJoinPool.commonPool()));
    }

    public NettyFutureAsCompletableFuture(@NonNull Future<V> delegate, @NonNull Executor executor) {
        this.delegate = delegate;
        this.executor = executor;

        if (delegate.isDone()) {
            this.operationComplete(delegate);
        } else {
            delegate.addListener(this);
        }
    }

    @Override
    public void operationComplete(@NonNull Future<V> future) {
        if (future != this.delegate) {
            throw new IllegalArgumentException("wrong future?!?");
        } else if (this.delegate.isSuccess()) {
            this.complete(this.delegate.getNow());
        } else if (this.delegate.isCancelled()) {
            this.cancel(true);
        } else {
            this.completeExceptionally(this.delegate.cause());
        }
    }

    @Override
    public <U> CompletableFuture<U> thenApplyAsync(Function<? super V, ? extends U> fn) {
        return this.thenApplyAsync(fn, this.executor);
    }

    @Override
    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super V> action) {
        return this.thenAcceptAsync(action, this.executor);
    }

    @Override
    public CompletableFuture<Void> thenRunAsync(Runnable action) {
        return this.thenRunAsync(action, this.executor);
    }

    @Override
    public <U, V1> CompletableFuture<V1> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super V, ? super U, ? extends V1> fn) {
        return this.thenCombineAsync(other, fn, this.executor);
    }

    @Override
    public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super V, ? super U> action) {
        return this.thenAcceptBothAsync(other, action, this.executor);
    }

    @Override
    public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return this.runAfterBothAsync(other, action, this.executor);
    }

    @Override
    public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends V> other, Function<? super V, U> fn) {
        return this.applyToEitherAsync(other, fn, this.executor);
    }

    @Override
    public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends V> other, Consumer<? super V> action) {
        return this.acceptEitherAsync(other, action, this.executor);
    }

    @Override
    public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return this.runAfterEitherAsync(other, action, this.executor);
    }

    @Override
    public <U> CompletableFuture<U> thenComposeAsync(Function<? super V, ? extends CompletionStage<U>> fn) {
        return this.thenComposeAsync(fn, this.executor);
    }

    @Override
    public CompletableFuture<V> whenCompleteAsync(BiConsumer<? super V, ? super Throwable> action) {
        return this.whenCompleteAsync(action, this.executor);
    }

    @Override
    public <U> CompletableFuture<U> handleAsync(BiFunction<? super V, Throwable, ? extends U> fn) {
        return this.handleAsync(fn, this.executor);
    }
}
