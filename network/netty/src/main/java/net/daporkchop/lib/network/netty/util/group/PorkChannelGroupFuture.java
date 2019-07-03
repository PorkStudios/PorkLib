/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.network.netty.util.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroupException;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.util.exception.AlreadyCompleteException;
import net.daporkchop.lib.network.netty.session.NettySession;
import net.daporkchop.lib.network.netty.util.future.NettyChannelPromise;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class PorkChannelGroupFuture<S extends AbstractUserSession<S>> extends DefaultPromise<Void> implements Promise {
    @Getter
    protected final PorkChannelGroup<S> group;
    protected final Map<NettySession<S>, ChannelFuture> futures;
    protected int successCount;
    protected int failureCount;

    public PorkChannelGroupFuture(@NonNull PorkChannelGroup<S> group, @NonNull Map<NettySession<S>, ChannelFuture> futures, EventExecutor executor) {
        super(executor);
        this.group = group;
        this.futures = Collections.unmodifiableMap(futures);

        GenericFutureListener<Future<Void>> childListener = future -> {
            boolean success = future.isSuccess();
            boolean callSetDone;
            synchronized (PorkChannelGroupFuture.this) {
                if (success) {
                    PorkChannelGroupFuture.this.successCount++;
                } else {
                    PorkChannelGroupFuture.this.failureCount++;
                }

                callSetDone = PorkChannelGroupFuture.this.successCount + PorkChannelGroupFuture.this.failureCount == PorkChannelGroupFuture.this.futures.size();
                assert PorkChannelGroupFuture.this.successCount + PorkChannelGroupFuture.this.failureCount <= PorkChannelGroupFuture.this.futures.size();
            }

            if (callSetDone) {
                if (PorkChannelGroupFuture.this.failureCount > 0) {
                    List<Map.Entry<Channel, Throwable>> failed = new ArrayList<>(PorkChannelGroupFuture.this.failureCount);
                    for (ChannelFuture f : PorkChannelGroupFuture.this.futures.values()) {
                        if (!f.isSuccess()) {
                            failed.add(new DefaultEntry<>(f.channel(), f.cause()));
                        }
                    }
                    setFailure0(new ChannelGroupException(failed));
                } else {
                    setSuccess0();
                }
            }
        };

        for (ChannelFuture f : this.futures.values()) {
            f.addListener(childListener);
        }

        // Done on arrival?
        if (this.futures.isEmpty()) {
            setSuccess0();
        }
    }

    public ChannelFuture find(@NonNull NettySession<S> session) {
        return this.futures.get(session);
    }

    public Iterator<ChannelFuture> iterator() {
        return this.futures.values().iterator();
    }

    public synchronized boolean isPartialSuccess() {
        return this.successCount != 0 && this.successCount != this.futures.size();
    }

    public synchronized boolean isPartialFailure() {
        return this.failureCount != 0 && this.failureCount != this.futures.size();
    }

    @Override
    public PorkChannelGroupFuture<S> addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.addListener(listener);
        return this;
    }

    @Override
    public PorkChannelGroupFuture<S> addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
        super.addListeners(listeners);
        return this;
    }

    @Override
    public PorkChannelGroupFuture<S> removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public PorkChannelGroupFuture<S> removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
        super.removeListeners(listeners);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PorkChannelGroupFuture<S> addListener(@NonNull Consumer<Promise> callback) {
        return this.addListener((GenericFutureListener<? extends Future<? super Void>>) f -> callback.accept((PorkChannelGroupFuture<S>) f));
    }

    @Override
    public PorkChannelGroupFuture<S> await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public PorkChannelGroupFuture<S> awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public PorkChannelGroupFuture<S> syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public PorkChannelGroupFuture<S> sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelGroupException cause() {
        return (ChannelGroupException) super.cause();
    }

    @Override
    public ChannelGroupException getError() {
        return this.cause();
    }

    private void setSuccess0() {
        super.setSuccess(null);
    }

    private void setFailure0(ChannelGroupException cause) {
        super.setFailure(cause);
    }

    @Override
    public PorkChannelGroupFuture<S> setSuccess(Void result) {
        throw new IllegalStateException();
    }

    @Override
    public void completeSuccessfully() throws AlreadyCompleteException {
        throw new IllegalStateException();
    }

    @Override
    public boolean trySuccess(Void result) {
        throw new IllegalStateException();
    }

    @Override
    public PorkChannelGroupFuture<S> setFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        throw new IllegalStateException();
    }

    @Override
    public void completeError(@NonNull Exception error) throws AlreadyCompleteException {
        throw new IllegalStateException();
    }

    @Override
    public void cancel() throws AlreadyCompleteException {
        throw new IllegalStateException();
    }

    @Override
    protected void checkDeadLock() {
        EventExecutor e = executor();
        if (e != null && e != ImmediateEventExecutor.INSTANCE && e.inEventLoop()) {
            throw new BlockingOperationException();
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = false)
    protected static class DefaultEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private final V value;

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("read-only");
        }
    }
}
