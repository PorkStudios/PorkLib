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

package net.daporkchop.lib.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.DefaultPFuture;
import net.daporkchop.lib.concurrent.future.completion.ConsumerCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.FunctionCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.RunnableCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.both.BothBiConsumerCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.both.BothBiFunctionCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.both.BothBiRunnableCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.either.EitherBiConsumerCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.either.EitherBiFunctionCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.either.EitherBiRunnableCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.general.ComposeCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.general.ExceptionalCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.general.ValueOrCauseConsumerCompletionTask;
import net.daporkchop.lib.concurrent.future.completion.general.ValueOrCauseFunctionCompletionTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A combination of Netty's {@link Future} with Java 8's {@link CompletionStage}.
 * <p>
 * Allows futures to be used in a monadic style while still retaining compatibility with Netty APIs.
 *
 * @author DaPorkchop_
 */
public interface PFuture<V> extends Future<V>, CompletionStage<V> {

    //
    //
    //new methods
    //
    //

    /**
     * Gets the {@link ExecutorService} that handles asynchronous execution for this {@link PFuture}.
     *
     * @return the {@link ExecutorService} that handles asynchronous execution for this {@link PFuture}
     */
    EventExecutor executor();

    /**
     * Gets a new {@link PFuture} using the same executor as this one.
     *
     * @param <U> the new {@link PFuture}'s value type
     * @return the new {@link PFuture}
     */
    default <U> PFuture<U> split() {
        return new DefaultPFuture<>(this.executor());
    }

    /**
     * @see CompletableFuture#join()
     */
    default V join()    {
        return this.syncUninterruptibly().getNow();
    }

    //
    //
    //Future methods
    //
    //

    @Override
    boolean isSuccess();

    @Override
    boolean isCancellable();

    @Override
    Throwable cause();

    @Override
    PFuture<V> addListener(@NonNull GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    PFuture<V> addListeners(@NonNull GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    PFuture<V> removeListener(@NonNull GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    PFuture<V> removeListeners(@NonNull GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    PFuture<V> sync() throws InterruptedException;

    @Override
    PFuture<V> syncUninterruptibly();

    @Override
    PFuture<V> await() throws InterruptedException;

    @Override
    PFuture<V> awaitUninterruptibly();

    @Override
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    @Override
    boolean await(long timeoutMillis) throws InterruptedException;

    @Override
    boolean awaitUninterruptibly(long timeout, TimeUnit unit);

    @Override
    boolean awaitUninterruptibly(long timeoutMillis);

    @Override
    V getNow();

    //
    //
    // CompletionStage methods
    //
    //

    // apply

    @Override
    default <U> PFuture<U> thenApply(@NonNull Function<? super V, ? extends U> fn) {
        return new FunctionCompletionTask<>(this.executor(), this, false, fn);
    }

    @Override
    default <U> PFuture<U> thenApplyAsync(@NonNull Function<? super V, ? extends U> fn) {
        return this.thenApplyAsync(fn, this.executor());
    }

    @Override
    default <U> PFuture<U> thenApplyAsync(@NonNull Function<? super V, ? extends U> fn, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new FunctionCompletionTask<>(eventExecutor, this, true, fn);
    }

    // accept

    @Override
    default PFuture<Void> thenAccept(@NonNull Consumer<? super V> action) {
        return new ConsumerCompletionTask<>(this.executor(), this, false, action);
    }

    @Override
    default PFuture<Void> thenAcceptAsync(@NonNull Consumer<? super V> action) {
        return this.thenAcceptAsync(action, this.executor());
    }

    @Override
    default PFuture<Void> thenAcceptAsync(@NonNull Consumer<? super V> action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new ConsumerCompletionTask<>(eventExecutor, this, true, action);
    }

    // run

    @Override
    default PFuture<Void> thenRun(@NonNull Runnable action) {
        return new RunnableCompletionTask<>(this.executor(), this, false, action);
    }

    @Override
    default PFuture<Void> thenRunAsync(@NonNull Runnable action) {
        return this.thenRunAsync(action, this.executor());
    }

    @Override
    default PFuture<Void> thenRunAsync(@NonNull Runnable action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new RunnableCompletionTask<>(eventExecutor, this, true, action);
    }

    // apply both

    @Override
    default <U, V1> PFuture<V1> thenCombine(@NonNull CompletionStage<? extends U> other, @NonNull BiFunction<? super V, ? super U, ? extends V1> fn) {
        return new BothBiFunctionCompletionTask<>(this.executor(), this, PFutures.wrap(other), false, fn);
    }

    @Override
    default <U, V1> PFuture<V1> thenCombineAsync(@NonNull CompletionStage<? extends U> other, @NonNull BiFunction<? super V, ? super U, ? extends V1> fn) {
        return this.thenCombineAsync(other, fn, this.executor());
    }

    @Override
    default <U, V1> PFuture<V1> thenCombineAsync(@NonNull CompletionStage<? extends U> other, @NonNull BiFunction<? super V, ? super U, ? extends V1> fn, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new BothBiFunctionCompletionTask<>(eventExecutor, this, PFutures.wrap(other), true, fn);
    }

    // accept both

    @Override
    default <U> PFuture<Void> thenAcceptBoth(@NonNull CompletionStage<? extends U> other, @NonNull BiConsumer<? super V, ? super U> action) {
        return new BothBiConsumerCompletionTask<>(this.executor(), this, PFutures.wrap(other), false, action);
    }

    @Override
    default <U> PFuture<Void> thenAcceptBothAsync(@NonNull CompletionStage<? extends U> other, @NonNull BiConsumer<? super V, ? super U> action) {
        return this.thenAcceptBothAsync(other, action, this.executor());
    }

    @Override
    default <U> PFuture<Void> thenAcceptBothAsync(@NonNull CompletionStage<? extends U> other, @NonNull BiConsumer<? super V, ? super U> action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new BothBiConsumerCompletionTask<>(eventExecutor, this, PFutures.wrap(other), true, action);
    }

    // run both

    @Override
    default PFuture<Void> runAfterBoth(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return new BothBiRunnableCompletionTask<>(this.executor(), this, PFutures.wrap(other), false, action);
    }

    @Override
    default PFuture<Void> runAfterBothAsync(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return this.runAfterBothAsync(other, action, this.executor());
    }

    @Override
    default PFuture<Void> runAfterBothAsync(@NonNull CompletionStage<?> other, @NonNull Runnable action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new BothBiRunnableCompletionTask<>(eventExecutor, this, PFutures.wrap(other), true, action);
    }

    // apply either

    @Override
    default <U> PFuture<U> applyToEither(@NonNull CompletionStage<? extends V> other, @NonNull Function<? super V, U> fn) {
        return new EitherBiFunctionCompletionTask<>(this.executor(), this, PFutures.wrap(other), false, fn);
    }

    @Override
    default <U> PFuture<U> applyToEitherAsync(@NonNull CompletionStage<? extends V> other, @NonNull Function<? super V, U> fn) {
        return this.applyToEitherAsync(other, fn, this.executor());
    }

    @Override
    default <U> PFuture<U> applyToEitherAsync(@NonNull CompletionStage<? extends V> other, @NonNull Function<? super V, U> fn, @NonNull Executor executor) {
        return new EitherBiFunctionCompletionTask<>(this.executor(), this, PFutures.wrap(other), true, fn);
    }

    // accept either

    @Override
    default PFuture<Void> acceptEither(@NonNull CompletionStage<? extends V> other, @NonNull Consumer<? super V> action) {
        return new EitherBiConsumerCompletionTask<>(this.executor(), this, PFutures.wrap(other), false, action);
    }

    @Override
    default PFuture<Void> acceptEitherAsync(@NonNull CompletionStage<? extends V> other, @NonNull Consumer<? super V> action) {
        return this.acceptEitherAsync(other, action, this.executor());
    }

    @Override
    default PFuture<Void> acceptEitherAsync(@NonNull CompletionStage<? extends V> other, @NonNull Consumer<? super V> action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new EitherBiConsumerCompletionTask<>(eventExecutor, this, PFutures.wrap(other), true, action);
    }

    // run either

    @Override
    default PFuture<Void> runAfterEither(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return new EitherBiRunnableCompletionTask(this.executor(), this, PFutures.wrap(other), false, action);
    }

    @Override
    default PFuture<Void> runAfterEitherAsync(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return this.runAfterEitherAsync(other, action, this.executor());
    }

    @Override
    default PFuture<Void> runAfterEitherAsync(@NonNull CompletionStage<?> other, @NonNull Runnable action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new EitherBiRunnableCompletionTask(eventExecutor, this, PFutures.wrap(other), true, action);
    }

    // compose

    @Override
    default <U> PFuture<U> thenCompose(@NonNull Function<? super V, ? extends CompletionStage<U>> action) {
        return new ComposeCompletionTask<>(this.executor(), this, false, action);
    }

    @Override
    default <U> PFuture<U> thenComposeAsync(@NonNull Function<? super V, ? extends CompletionStage<U>> action) {
        return this.thenComposeAsync(action, this.executor());
    }

    @Override
    default <U> PFuture<U> thenComposeAsync(@NonNull Function<? super V, ? extends CompletionStage<U>> action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new ComposeCompletionTask<>(eventExecutor, this, true, action);
    }

    // general listeners

    @Override
    default PFuture<V> exceptionally(@NonNull Function<Throwable, ? extends V> fn) {
        return new ExceptionalCompletionTask<>(this.executor(), this, false, fn);
    }

    @Override
    default PFuture<V> whenComplete(@NonNull BiConsumer<? super V, ? super Throwable> action) {
        return new ValueOrCauseConsumerCompletionTask<>(this.executor(), this, false, action);
    }

    @Override
    default PFuture<V> whenCompleteAsync(@NonNull BiConsumer<? super V, ? super Throwable> action) {
        return this.whenCompleteAsync(action, this.executor());
    }

    @Override
    default PFuture<V> whenCompleteAsync(@NonNull BiConsumer<? super V, ? super Throwable> action, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new ValueOrCauseConsumerCompletionTask<>(eventExecutor, this, true, action);
    }

    @Override
    default <U> PFuture<U> handle(@NonNull BiFunction<? super V, Throwable, ? extends U> fn) {
        return new ValueOrCauseFunctionCompletionTask<>(this.executor(), this, false, fn);
    }

    @Override
    default <U> PFuture<U> handleAsync(@NonNull BiFunction<? super V, Throwable, ? extends U> fn) {
        return this.handleAsync(fn, this.executor());
    }

    @Override
    default <U> PFuture<U> handleAsync(@NonNull BiFunction<? super V, Throwable, ? extends U> fn, @NonNull Executor executor) {
        EventExecutor eventExecutor = PExecutors.toNettyExecutor(executor);
        return new ValueOrCauseFunctionCompletionTask<>(eventExecutor, this, true, fn);
    }

    @Override
    CompletableFuture<V> toCompletableFuture();
}
