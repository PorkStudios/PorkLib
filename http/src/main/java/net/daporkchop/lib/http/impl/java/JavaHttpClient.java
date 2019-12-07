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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.selection.SelectionPool;
import net.daporkchop.lib.http.HttpClient;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.request.RequestBuilder;
import net.daporkchop.lib.http.util.Constants;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * A very simple implementation of {@link HttpClient}, using {@link java.net.URL}'s built-in support to act as a simple HTTP client.
 * <p>
 * Each request will be executed on a separate thread, and a shared {@link EventExecutor} is used for invoking callbacks. By default the
 * {@link EventExecutor} is set to {@link ImmediateEventExecutor#INSTANCE}, meaning that the request is entirely executed on the connection-
 * specific thread.
 *
 * @author DaPorkchop_
 */
//TODO: a builder class might be better suited here than all these constructors
@Accessors(fluent = true)
public final class JavaHttpClient implements HttpClient {
    protected static final Set<HttpMethod> SUPPORTED_METHODS = Collections.unmodifiableSet(EnumSet.of(
            HttpMethod.GET,
            HttpMethod.POST
    ));

    protected volatile ThreadFactory factory;
    protected final    EventExecutor executor;
    protected final    Promise<Void> closeFuture;

    protected final SelectionPool<String> userAgentSelectionPool;

    public JavaHttpClient(@NonNull ThreadFactory factory, @NonNull EventExecutor executor, @NonNull SelectionPool<String> userAgentSelectionPool) {
        this.factory = factory;
        this.executor = executor;
        this.closeFuture = executor.newPromise();
        this.userAgentSelectionPool = userAgentSelectionPool;
    }

    public JavaHttpClient(@NonNull EventExecutor executor) {
        this(Thread::new, executor, Constants.DEFAULT_USER_AGENT_SELECTION_POOL);
    }

    public JavaHttpClient(@NonNull ThreadFactory factory) {
        this(factory, ImmediateEventExecutor.INSTANCE, Constants.DEFAULT_USER_AGENT_SELECTION_POOL);
    }

    public JavaHttpClient(@NonNull EventExecutor executor, @NonNull SelectionPool<String> userAgentSelectionPool) {
        this(Thread::new, executor, userAgentSelectionPool);
    }

    public JavaHttpClient(@NonNull ThreadFactory factory, @NonNull SelectionPool<String> userAgentSelectionPool) {
        this(factory, ImmediateEventExecutor.INSTANCE, userAgentSelectionPool);
    }

    public JavaHttpClient(@NonNull SelectionPool<String> userAgentSelectionPool) {
        this(Thread::new, ImmediateEventExecutor.INSTANCE, userAgentSelectionPool);
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
        //TODO: do this lol
        this.closeFuture.setSuccess(null);
        return this.closeFuture;
    }

    @Override
    public Future<Void> closeFuture() {
        return this.closeFuture;
    }
}
