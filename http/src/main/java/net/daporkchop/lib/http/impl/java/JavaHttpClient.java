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
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.HttpClient;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.request.RequestBuilder;

import java.util.concurrent.ThreadFactory;

/**
 * A very simple implementation of {@link HttpClient}, using {@link java.net.URL}'s built-in support to act as a simple HTTP client.
 * <p>
 * Each request will be executed on a separate thread, and a shared {@link EventExecutor} is used for invoking callbacks.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class JavaHttpClient implements HttpClient {
    @NonNull
    protected volatile ThreadFactory factory;
    @NonNull
    protected final EventExecutor executor;
    protected final Promise<Void> closeFuture = this.executor.newPromise();

    public JavaHttpClient(@NonNull EventExecutor executor) {
        this(Thread::new, executor);
    }

    public JavaHttpClient(@NonNull ThreadFactory factory) {
        this(factory, GlobalEventExecutor.INSTANCE);
    }

    public JavaHttpClient() {
        this(Thread::new, GlobalEventExecutor.INSTANCE);
    }

    @Override
    public RequestBuilder<Void, Request<Void>> request() {

        return null;
    }

    @Override
    public Future<Void> close() {
        return this.closeFuture;
    }

    @Override
    public Future<Void> closeFuture() {
        return this.closeFuture;
    }
}
