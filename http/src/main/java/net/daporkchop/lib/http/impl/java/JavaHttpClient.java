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

package net.daporkchop.lib.http.impl.java;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.selection.SelectionPool;
import net.daporkchop.lib.http.HttpClient;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.request.RequestBuilder;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A very simple implementation of {@link HttpClient}, using {@link java.net.URL}'s built-in support to act as a simple HTTP client.
 * <p>
 * Each request will be executed on a separate thread, and a shared {@link EventExecutorGroup} is used for invoking callbacks. By default the
 * {@link EventExecutorGroup} is set to {@link ImmediateEventExecutor#INSTANCE}, meaning that the request is entirely executed on the connection's
 * thread.
 *
 * @author DaPorkchop_
 */
@Setter
@Accessors(fluent = true)
public class JavaHttpClient implements HttpClient {
    protected static final Set<HttpMethod> SUPPORTED_METHODS = Collections.unmodifiableSet(EnumSet.of(
            HttpMethod.GET,
            HttpMethod.POST
    ));

    protected final ThreadFactory      threadFactory;
    protected final EventExecutorGroup group;
    protected final Promise<Void>      closeFuture;

    @NonNull
    protected volatile SelectionPool<String> userAgents;

    protected final Map<JavaRequest, Object> activeRequests = new ConcurrentHashMap<>();
    protected final ReadWriteLock            requestsLock   = new ReentrantReadWriteLock();

    public JavaHttpClient(@NonNull JavaHttpClientBuilder builder) {
        this.threadFactory = builder.threadFactory;
        this.group = builder.group;
        this.closeFuture = this.group.next().newPromise();
        this.userAgents = builder.userAgents;
    }

    @Override
    public Set<HttpMethod> supportedMethods() {
        return SUPPORTED_METHODS;
    }

    @Override
    public RequestBuilder<Void> request() {
        return new JavaRequestBuilder<>(this);
    }

    @Override
    public Future<Void> close() {
        this.requestsLock.writeLock().lock();
        try {
            this.closeFuture.setSuccess(null);
            this.activeRequests.forEach((request, _val) -> {
                //cancel futures
                request.headers.cancel(true);
                request.body.cancel(true);
            });
        } finally {
            this.requestsLock.writeLock().unlock();
        }
        return this.closeFuture;
    }

    @Override
    public Future<Void> closeFuture() {
        return this.closeFuture;
    }

    protected boolean addRequest(@NonNull JavaRequest request) {
        this.requestsLock.readLock().lock();
        try {
            if (this.closeFuture.isDone()) {
                request.headers.cancel(true);
                request.body.cancel(true);
                return false;
            } else if (this.activeRequests.putIfAbsent(request, this) != null) {
                throw new IllegalStateException("Request already added!?!");
            } else {
                return true;
            }
        } finally {
            this.requestsLock.readLock().unlock();
        }
    }

    protected void removeRequest(@NonNull JavaRequest request) {
        this.requestsLock.readLock().lock();
        try {
            if (!this.activeRequests.remove(request, this)) {
                throw new IllegalStateException("Request already removed!?!");
            }
        } finally {
            this.requestsLock.readLock().unlock();
        }
    }

    protected <V> JavaRequest<V> buildRequest(@NonNull JavaRequestBuilder<V> builder) {
        return new DefaultJavaRequest<>(builder);
    }
}
