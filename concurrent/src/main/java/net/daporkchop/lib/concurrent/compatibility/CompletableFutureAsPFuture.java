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

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.throwing.ERunnable;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.concurrent.PExecutors;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.reflection.lambda.LambdaBuilder;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Wraps a Java {@link CompletableFuture} into a {@link PFuture}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class CompletableFutureAsPFuture<V> implements PFuture<V> {
    protected static final long     RESULT_OFFSET   = PUnsafe.pork_getOffset(CompletableFuture.class, "result");
    protected static final Class<?> ALTRESULT_CLASS = PorkUtil.classForName("java.util.concurrent.CompletableFuture$AltResult");
    protected static final long     EX_OFFSET       = PUnsafe.pork_getOffset(ALTRESULT_CLASS, "ex");

    protected static final WaitingGet WAITING_GET = LambdaBuilder.of(WaitingGet.class)
            .param().setType(CompletableFuture.class).build()
            .param().setType(boolean.class).build()
            .returnType().setType(Object.class).build()
            .setInterfaceName("apply")
            .setMethodName("waitingGet")
            .build();

    protected static final TimedGet TIMED_GET = LambdaBuilder.of(TimedGet.class)
            .param().setType(CompletableFuture.class).build()
            .param().setType(boolean.class).build()
            .returnType().setType(Object.class).build()
            .setInterfaceName("apply")
            .setMethodName("timedGet")
            .build();

    @NonNull
    protected final CompletableFuture<V> delegate;

    @Override
    public EventExecutor executor() {
        return PExecutors.toNettyExecutor(ForkJoinPool.commonPool());
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        return this.delegate;
    }

    // java future methods

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

    // netty future methods

    @Override
    public boolean isSuccess() {
        Object result = PUnsafe.getObjectVolatile(this.delegate, RESULT_OFFSET);
        return result != null && result.getClass() != ALTRESULT_CLASS;
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public Throwable cause() {
        Object result = PUnsafe.getObjectVolatile(this.delegate, RESULT_OFFSET);
        return result != null && result.getClass() == ALTRESULT_CLASS ? PUnsafe.getObject(result, EX_OFFSET) : null;
    }

    @Override
    public V getNow() {
        Object result = PUnsafe.getObjectVolatile(this.delegate, RESULT_OFFSET);
        return result == null || result.getClass() != ALTRESULT_CLASS ? uncheckedCast(result) : null;
    }

    @Override
    public PFuture<V> addListener(@NonNull GenericFutureListener<? extends Future<? super V>> listener) {
        this.delegate.thenRun(new ListenerWrapper(listener));
        return this;
    }

    @Override
    public PFuture<V> addListeners(@NonNull GenericFutureListener<? extends Future<? super V>>... listeners) {
        for (GenericFutureListener<? extends Future<? super V>> listener : listeners) {
            this.addListener(listener);
        }
        return this;
    }

    @Override
    public PFuture<V> removeListener(@NonNull GenericFutureListener<? extends Future<? super V>> listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PFuture<V> removeListeners(@NonNull GenericFutureListener<? extends Future<? super V>>... listeners) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PFuture<V> sync() throws InterruptedException {
        this.await();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public PFuture<V> syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public PFuture<V> await() throws InterruptedException {
        if (!this.isDone()) {
            WAITING_GET.apply(this.delegate, true);
        }
        return this;
    }

    @Override
    public PFuture<V> awaitUninterruptibly() {
        if (!this.isDone()) {
            WAITING_GET.apply(this.delegate, false);
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (!this.isDone()) {
            try {
                Object o = TIMED_GET.apply(this.delegate, unit.toNanos(timeout));
                if (o == null) {
                    throw new InterruptedException();
                } else {
                    return true;
                }
            } catch (TimeoutException e) {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        long end = System.nanoTime() + unit.toNanos(timeout);
        boolean interrupted = Thread.interrupted();
        try {
            do {
                Object o = TIMED_GET.apply(this.delegate, end - System.nanoTime());
                if (o == null || (interrupted |= Thread.interrupted())) {
                    interrupted = true;
                } else {
                    return true;
                }
            } while (System.nanoTime() < end);
        } catch (TimeoutException e) {
            //ignore
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return this.awaitUninterruptibly(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    protected void rethrowIfFailed() {
        Throwable cause = this.cause();
        if (cause != null) {
            PUnsafe.throwException(cause);
        }
    }

    @RequiredArgsConstructor
    protected class ListenerWrapper implements ERunnable {
        @NonNull
        protected final GenericFutureListener<? extends Future<? super V>> listener;

        @Override
        public void runThrowing() throws Exception {
            this.listener.operationComplete(uncheckedCast(CompletableFutureAsPFuture.this));
        }
    }

    @FunctionalInterface
    protected interface WaitingGet {
        Object apply(CompletableFuture arg, boolean val);
    }

    @FunctionalInterface
    protected interface TimedGet {
        Object apply(CompletableFuture arg, long val) throws TimeoutException;
    }
}
