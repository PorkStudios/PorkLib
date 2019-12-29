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

package net.daporkchop.lib.http.impl.java;

import io.netty.util.concurrent.EventExecutor;
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
import net.daporkchop.lib.http.util.Constants;

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
//TODO: a builder class might be better suited here than all these constructors
@Setter
@Accessors(fluent = true, chain = true)
public final class JavaHttpClient implements HttpClient {
    protected static final Set<HttpMethod> SUPPORTED_METHODS = Collections.unmodifiableSet(EnumSet.of(
            HttpMethod.GET,
            HttpMethod.POST
    ));

    @NonNull
    protected volatile ThreadFactory      factory;
    protected final    EventExecutorGroup group;
    protected final    Promise<Void>      closeFuture;

    @NonNull
    protected volatile SelectionPool<String> userAgents;

    protected final Map<JavaRequest, Object> activeRequests = new ConcurrentHashMap<>();
    protected final ReadWriteLock requestsLock = new ReentrantReadWriteLock();

    public JavaHttpClient(@NonNull ThreadFactory factory, @NonNull EventExecutorGroup group, @NonNull SelectionPool<String> userAgents) {
        this.factory = factory;
        this.group = group;
        this.closeFuture = group.next().newPromise();
        this.userAgents = userAgents;
    }

    public JavaHttpClient(@NonNull EventExecutor group) {
        this(Thread::new, group, Constants.DEFAULT_USER_AGENT_SELECTION_POOL);
    }

    public JavaHttpClient(@NonNull ThreadFactory factory) {
        this(factory, ImmediateEventExecutor.INSTANCE, Constants.DEFAULT_USER_AGENT_SELECTION_POOL);
    }

    public JavaHttpClient(@NonNull EventExecutor group, @NonNull SelectionPool<String> userAgents) {
        this(Thread::new, group, userAgents);
    }

    public JavaHttpClient(@NonNull ThreadFactory factory, @NonNull SelectionPool<String> userAgents) {
        this(factory, ImmediateEventExecutor.INSTANCE, userAgents);
    }

    public JavaHttpClient(@NonNull SelectionPool<String> userAgents) {
        this(Thread::new, ImmediateEventExecutor.INSTANCE, userAgents);
    }

    public JavaHttpClient() {
        this(Thread::new, ImmediateEventExecutor.INSTANCE, Constants.DEFAULT_USER_AGENT_SELECTION_POOL);
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

    boolean addRequest(@NonNull JavaRequest request)   {
        this.requestsLock.readLock().lock();
        try {
            if (this.closeFuture.isDone())  {
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

    void removeRequest(@NonNull JavaRequest request)    {
        this.requestsLock.readLock().lock();
        try {
            if (!this.activeRequests.remove(request, this)) {
                throw new IllegalStateException("Request already removed!?!");
            }
        } finally {
            this.requestsLock.readLock().unlock();
        }
    }
}
