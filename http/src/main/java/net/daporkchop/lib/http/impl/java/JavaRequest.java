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

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.header.DefaultHeaderMap;
import net.daporkchop.lib.http.header.HeaderImpl;
import net.daporkchop.lib.http.header.HeaderMap;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.Response;
import net.daporkchop.lib.http.response.ResponseImpl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Shared implementation of {@link Request} for {@link JavaHttpClient}.
 *
 * @author DaPorkchop_
 */
public abstract class JavaRequest<V, R extends JavaRequest<V, R>> implements Request<V>, Runnable {
    protected final JavaHttpClient client;
    protected final Thread thread;
    protected final HttpURLConnection connection;

    protected final Promise<Response> response;
    protected final Promise<V> complete;

    public JavaRequest(@NonNull JavaHttpClient client, @NonNull JavaRequestBuilder<V, R> builder) throws IOException {
        this.client = client;
        this.thread = client.factory.newThread(this);

        this.response = client.executor.newPromise();
        this.complete = client.executor.newPromise();

        this.complete.addListener(f -> {
            if (!this.response.isDone()) {
                this.response.setFailure(f.isSuccess() ? new IllegalStateException("Complete future was successful, but response future was never set!") : f.cause());
            }
        });

        try {
            //this doesn't actually initiate the connection, it only creates the instance
            this.connection = (HttpURLConnection) builder.url.openConnection();
        } catch (IOException e) {
            this.complete.tryFailure(e);
            throw e;
        }

        this.thread.start();
    }

    @Override
    public Future<Response> response() {
        return this.response;
    }

    @Override
    public Future<V> complete() {
        return this.complete;
    }

    @Override
    public Future<V> close() {
        this.connection.disconnect();
        return this.complete;
    }

    @Override
    public void run() {
        if (Thread.currentThread() != this.thread) throw new IllegalStateException("Invoked from illegal thread!");

        try {
            this.connection.connect();

            {
                StatusCode status = StatusCode.of(this.connection.getResponseCode(), this.connection.getResponseMessage());
                Map<String, List<String>> internalHeadersMap = this.connection.getHeaderFields();
                HeaderMap headers = new DefaultHeaderMap(internalHeadersMap.entrySet().stream()
                        .map(entry -> new HeaderImpl(
                                entry.getKey(),
                                entry.getValue().size() == 1
                                        ? entry.getValue().get(0)
                                        : entry.getValue().stream().collect(() -> new StringJoiner(","), StringJoiner::add, StringJoiner::merge).toString()
                        )));
                this.response.setSuccess(new ResponseImpl(status, headers));
            }
        } catch (IOException e) {
            this.complete.setFailure(e);
        }
    }
}
