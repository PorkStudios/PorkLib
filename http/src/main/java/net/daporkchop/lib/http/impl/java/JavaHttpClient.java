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
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.NonNull;
import net.daporkchop.lib.http.HttpClient;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.request.RequestBuilder;

import java.util.concurrent.ThreadFactory;

/**
 * A very simple implementation of {@link HttpClient}, using {@link java.net.URL}'s built-in support to act
 * as an HTTP client.
 * <p>
 * Each request will be executed on a separate thread.
 *
 * @author DaPorkchop_
 */
public class JavaHttpClient implements HttpClient {
    protected final EventExecutor executor;

    public JavaHttpClient() {
        this(Thread::new);
    }

    public JavaHttpClient(@NonNull ThreadFactory threadFactory) {
        //TODO: this.executor = new ThreadPerTaskExecutor(threadFactory);
    }

    @Override
    public RequestBuilder<Void, Request<Void>> request() {
        return null;
    }

    @Override
    public Future<Void> close() {
        return null;
    }

    @Override
    public Future<Void> closeFuture() {
        return null;
    }
}
