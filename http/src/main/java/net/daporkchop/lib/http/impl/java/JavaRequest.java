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

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.header.HeaderImpl;
import net.daporkchop.lib.http.header.map.HeaderSnapshot;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.DelegatingResponseBodyImpl;
import net.daporkchop.lib.http.response.ResponseBody;
import net.daporkchop.lib.http.response.ResponseHeaders;
import net.daporkchop.lib.http.response.ResponseHeadersImpl;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.StringJoiner;

/**
 * Shared implementation of {@link Request} for {@link JavaHttpClient}.
 *
 * @author DaPorkchop_
 */
public class JavaRequest<V> implements Request<V>, Runnable {
    protected final JavaHttpClient        client;
    protected final Thread                thread;
    protected final JavaRequestBuilder<V> builder;
    protected       HttpURLConnection     connection;

    protected final Promise<ResponseHeaders> headers;
    protected final Promise<ResponseBody<V>> body;

    public JavaRequest(@NonNull JavaRequestBuilder<V> builder) {
        this.client = builder.client;
        this.builder = builder;
        this.thread = this.client.factory.newThread(this);

        this.headers = this.client.executor.newPromise();
        this.body = this.client.executor.newPromise();

        this.body.addListener(f -> {
            if (!this.headers.isDone()) {
                this.headers.setFailure(f.isSuccess() ? new IllegalStateException("Complete future was successful, but response future was never set!") : f.cause());
            }
        });

        this.thread.start();
    }

    @Override
    public Future<ResponseHeaders> headersFuture() {
        return this.headers;
    }

    @Override
    public Future<ResponseBody<V>> bodyFuture() {
        return this.body;
    }

    @Override
    public Future<ResponseBody<V>> close() {
        this.connection.disconnect();
        return this.body;
    }

    @Override
    public void run() {
        if (Thread.currentThread() != this.thread) throw new IllegalStateException("Invoked from illegal thread!");

        try {
            URL url = this.builder.url;
            do {
                this.connection = (HttpURLConnection) url.openConnection();
                //set request headers
                this.builder.prepareHeaders(this.connection::setRequestProperty);
                this.connection.connect();

                ResponseHeadersImpl response = new ResponseHeadersImpl(
                        StatusCode.of(this.connection.getResponseCode(), this.connection.getResponseMessage()),
                        new HeaderSnapshot(this.connection.getHeaderFields().entrySet().stream()
                                .map(entry -> {
                                    String key = entry.getKey();
                                    List<String> value = entry.getValue();
                                    if (key == null || value.isEmpty()) {
                                        return null;
                                    } else if (value.size() == 1) {
                                        return new HeaderImpl(key, value.get(0));
                                    } else {
                                        return new HeaderImpl(key, value.stream().collect(() -> new StringJoiner(","), StringJoiner::add, StringJoiner::merge).toString());
                                    }
                                })));

                if (this.builder.followRedirects && response.isRedirect()) {
                    url = new URL(response.redirectLocation());
                    this.connection.disconnect();
                    continue;
                }

                this.headers.setSuccess(response);
                this.body.setSuccess(new DelegatingResponseBodyImpl<>(response, this.implReceiveBody(this.connection.getInputStream())));
            } while (!this.body.isDone());
        } catch (Exception e) {
            this.body.setFailure(e);
        } finally {
            this.connection.disconnect();
        }
    }

    protected V implReceiveBody(@NonNull InputStream bodyIn) throws Exception {
        ResponseAggregator<Object, V> aggregator = this.builder.aggregator;
        Object temp = aggregator.init(this.headers.getNow(), this);
        try {
            byte[] buf = new byte[4096];
            for (int i; (i = bodyIn.read(buf)) > 0; ) {
                temp = aggregator.add(temp, Unpooled.wrappedBuffer(buf, 0, i), this);
            }
            return aggregator.doFinal(temp, this);
        } finally {
            aggregator.deinit(temp, this);
        }
    }
}
