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

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.concurrent.compatibility.CompletableFutureAsPFuture;
import net.daporkchop.lib.concurrent.compatibility.NettyFutureAsCompletableFuture;
import net.daporkchop.lib.concurrent.compatibility.NettyFutureAsPFuture;
import net.daporkchop.lib.concurrent.future.runnable.ConsumerPFutureTask;
import net.daporkchop.lib.concurrent.future.runnable.FunctionPFutureTask;
import net.daporkchop.lib.concurrent.future.runnable.RunnablePFutureTask;
import net.daporkchop.lib.concurrent.future.runnable.RunnableWithResultPFutureTask;
import net.daporkchop.lib.concurrent.future.runnable.SupplierPFutureTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * Helpers for dealing with various implementations of a future value.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PFutures {
    protected final long DEFAULTPROMISE_EXECUTOR_OFFSET = pork_getOffset(DefaultPromise.class, "executor");

    protected final Method DEFAULTPROMISE_EXECUTOR;
    protected final Method COMPLETEFUTURE_EXECUTOR;

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

    public static <V> PFuture<V> wrap(@NonNull CompletableFuture<? extends V> toWrap) {
        return wrap((Object) toWrap);
    }

    public static <V> PFuture<V> wrap(@NonNull CompletionStage<? extends V> toWrap) {
        return wrap((Object) toWrap);
    }

    public static <V> PFuture<V> wrap(@NonNull java.util.concurrent.Future<? extends V> toWrap) {
        return wrap((Object) toWrap);
    }

    public static <V> PFuture<V> wrap(@NonNull Future<? extends V> toWrap) {
        return wrap((Object) toWrap);
    }

    public static <V> PFuture<V> wrap(@NonNull Object toWrap) {
        if (toWrap instanceof PFuture) {
            return uncheckedCast(toWrap);
        } else if (toWrap instanceof Future) {
            //TODO: cache instances of this...
            return new NettyFutureAsPFuture<>(uncheckedCast(toWrap));
        } else if (toWrap instanceof CompletableFuture) {
            if (toWrap instanceof NettyFutureAsCompletableFuture) {
                return wrap((Object) ((NettyFutureAsCompletableFuture) toWrap).delegate());
            } else {
                //TODO: cache instances of this...
                return new CompletableFutureAsPFuture<>(uncheckedCast(toWrap));
            }
        }
        throw new IllegalArgumentException(className(toWrap));
    }

    /**
     * Gets the {@link EventExecutor} used by a given {@link Future}.
     *
     * @param future the {@link Future} to get the {@link EventExecutor} for
     * @return the {@link EventExecutor} used by the given {@link Future}
     */
    public static EventExecutor executor(@NonNull Future<?> future) {
        try {
            if (future instanceof DefaultPromise) {
                return (EventExecutor) DEFAULTPROMISE_EXECUTOR.invoke(future, PorkUtil.EMPTY_OBJECT_ARRAY);
            } else if (future instanceof CompleteFuture) {
                return (EventExecutor) COMPLETEFUTURE_EXECUTOR.invoke(future, PorkUtil.EMPTY_OBJECT_ARRAY);
            } else {
                return null;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the given {@link Runnable} asynchronously using {@link ForkJoinPool#commonPool()}.
     *
     * @param action the {@link Runnable} to run
     * @return a {@link PFuture} which will be completed when the {@link Runnable} has finished execution
     */
    public static PFuture<Void> runAsync(@NonNull Runnable action) {
        return runAsync(action, PExecutors.FORKJOINPOOL);
    }

    /**
     * Executes the given {@link Runnable} on the given {@link Executor}.
     *
     * @param action   the {@link Runnable} to run
     * @param executor the {@link Executor} to run the action on
     * @return a {@link PFuture} which will be completed when the {@link Runnable} has finished execution
     */
    public static PFuture<Void> runAsync(@NonNull Runnable action, @NonNull Executor executor) {
        PRunnableFuture<Void> future = new RunnablePFutureTask(PExecutors.toNettyExecutor(executor), action);
        executor.execute(future);
        return future;
    }

    /**
     * Executes the given {@link Runnable} asynchronously using {@link ForkJoinPool#commonPool()}.
     *
     * @param action the {@link Runnable} to run
     * @param result the result to complete the returned future with upon completion of the {@link Runnable}
     * @return a {@link PFuture} which will be completed with the given result when the {@link Runnable} has been completed
     */
    public static <V> PFuture<V> runAsync(@NonNull Runnable action, V result) {
        return runAsync(action, result, PExecutors.FORKJOINPOOL);
    }

    /**
     * Executes the given {@link Runnable} on the given {@link Executor}.
     *
     * @param action   the {@link Runnable} to run
     * @param result   the result to complete the returned future with upon completion of the {@link Runnable}
     * @param executor the {@link Executor} to run the action on
     * @return a {@link PFuture} which will be completed with the given result when the {@link Runnable} has been completed
     */
    public static <V> PFuture<V> runAsync(@NonNull Runnable action, V result, @NonNull Executor executor) {
        PRunnableFuture<V> future = new RunnableWithResultPFutureTask<>(PExecutors.toNettyExecutor(executor), action, result);
        executor.execute(future);
        return future;
    }

    /**
     * Computes a value using the given {@link Supplier} asynchronously using {@link ForkJoinPool#commonPool()}.
     *
     * @param action the {@link Supplier} to use to compute the value
     * @return a {@link PFuture} which will be completed with the value returned by the {@link Supplier}
     */
    public static <V> PFuture<V> computeAsync(@NonNull Supplier<? extends V> action) {
        return computeAsync(action, PExecutors.FORKJOINPOOL);
    }

    /**
     * Computes a value using the given {@link Supplier} on the given {@link Executor}.
     *
     * @param action   the {@link Supplier} to use to compute the value
     * @param executor the {@link Executor} to run the action on
     * @return a {@link PFuture} which will be completed with the value returned by the {@link Supplier}
     */
    public static <V> PFuture<V> computeAsync(@NonNull Supplier<? extends V> action, @NonNull Executor executor) {
        PRunnableFuture<V> future = new SupplierPFutureTask<>(PExecutors.toNettyExecutor(executor), action);
        executor.execute(future);
        return future;
    }

    /**
     * Passes the given parameter to the given {@link Consumer} asynchronously using {@link ForkJoinPool#commonPool()}.
     *
     * @param action    the {@link Consumer} to pass the parameter to
     * @param parameter the parameter to pass to the {@link Consumer}
     * @return a {@link PFuture} which will be completed when the {@link Consumer} has finished execution
     */
    public static <P> PFuture<Void> acceptAsync(@NonNull Consumer<? super P> action, P parameter) {
        return acceptAsync(action, parameter, PExecutors.FORKJOINPOOL);
    }

    /**
     * Passes the given parameter to the given {@link Consumer} on the given {@link Executor}.
     *
     * @param action    the {@link Consumer} to pass the parameter to
     * @param parameter the parameter to pass to the {@link Consumer}
     * @param executor  the {@link Executor} to run the action on
     * @return a {@link PFuture} which will be completed when the {@link Consumer} has finished execution
     */
    public static <P> PFuture<Void> acceptAsync(@NonNull Consumer<? super P> action, P parameter, @NonNull Executor executor) {
        PRunnableFuture<Void> future = new ConsumerPFutureTask<>(PExecutors.toNettyExecutor(executor), action, parameter);
        executor.execute(future);
        return future;
    }

    /**
     * Computes a value using the given {@link Function} asynchronously using {@link ForkJoinPool#commonPool()}.
     *
     * @param action    the {@link Function} to use to compute the value
     * @param parameter the parameter to pass to the {@link Function}
     * @return a {@link PFuture} which will be completed with the value returned by the {@link Function}
     */
    public static <P, V> PFuture<V> applyAsync(@NonNull Function<? super P, ? extends V> action, P parameter) {
        return applyAsync(action, parameter, PExecutors.FORKJOINPOOL);
    }

    /**
     * Computes a value using the given {@link Function} on the given {@link Executor}.
     *
     * @param action    the {@link Function} to use to compute the value
     * @param parameter the parameter to pass to the {@link Function}
     * @param executor  the {@link Executor} to run the action on
     * @return a {@link PFuture} which will be completed with the value returned by the {@link Function}
     */
    public static <P, V> PFuture<V> applyAsync(@NonNull Function<? super P, ? extends V> action, P parameter, @NonNull Executor executor) {
        PRunnableFuture<V> future = new FunctionPFutureTask<>(PExecutors.toNettyExecutor(executor), action, parameter);
        executor.execute(future);
        return future;
    }
}
