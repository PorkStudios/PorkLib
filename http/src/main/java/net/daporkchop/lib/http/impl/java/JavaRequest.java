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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderSnapshot;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.DelegatingResponseBodyImpl;
import net.daporkchop.lib.http.response.ResponseBody;
import net.daporkchop.lib.http.response.ResponseHeaders;
import net.daporkchop.lib.http.response.ResponseHeadersImpl;
import net.daporkchop.lib.http.response.aggregate.NotifyingAggregator;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;
import net.daporkchop.lib.http.util.Constants;
import net.daporkchop.lib.http.util.exception.ResponseTooLargeException;
import net.daporkchop.lib.unsafe.PUnsafe;
import sun.net.www.http.HttpClient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;

/**
 * Base implementation of {@link Request} for {@link JavaHttpClient}.
 *
 * @author DaPorkchop_
 */
public abstract class JavaRequest<V> implements Request<V>, Runnable {
    static {
        PUnsafe.ensureClassInitialized(HttpClient.class);
        PUnsafe.pork_getStaticField(HttpClient.class, "keepAliveProp").setBoolean(false);
    }

    protected final JavaHttpClient        client;
    protected final JavaRequestBuilder<V> builder;
    protected       Thread                thread;
    protected       HttpURLConnection     connection;

    protected final Promise<ResponseHeaders<V>> headers;
    protected final Promise<ResponseBody<V>>    body;

    public JavaRequest(@NonNull JavaRequestBuilder<V> builder) {
        this.client = builder.client();
        this.builder = builder;

        EventExecutor executor = this.client.group.next();
        this.headers = executor.newPromise();
        this.body = executor.newPromise();

        this.body.addListener(f -> {
            if (!this.headers.isDone()) {
                this.headers.setFailure(f.isSuccess() ? new IllegalStateException("Complete future was successful, but response future was never set!") : f.cause());
            }
        });
    }

    @Override
    public Future<ResponseHeaders<V>> headersFuture() {
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
        if (this.thread == null) {
            throw new IllegalStateException("Thread is not set?!?");
        } else if (Thread.currentThread() != this.thread) {
            throw new IllegalStateException(String.format("Invoked from illegal thread \"%s\", expected: \"%s\"", Thread.currentThread().getName(), this.thread.getName()));
        }

        if (!this.client.addRequest(this)) {
            //return immediately if client is closed (addRequest will cancel the futures for us)
            return;
        }

        try {
            final HttpMethod method = this.builder.method();

            URL url = this.builder.url;
            do {
                this.connection = (HttpURLConnection) (this.builder.proxy() == null ? url.openConnection() : url.openConnection(this.builder.proxy()));

                //set method
                this.connection.setRequestMethod(method.name());
                this.connection.setDoOutput(method.hasRequestBody());
                this.connection.setDoInput(method.hasResponseBody());

                //set request headers
                HeaderMap headers = this.builder._prepareHeaders();
                //add all headers as properties (we don't need to use set since HeaderMap already guarantees distinct keys, so using setRequestProperty would only cause needless string comparisons)
                headers.forEach(header -> this.connection.setRequestProperty(header.key(), header.value())); //if it's a list all the values will be joined together

                if (!headers.hasKey("user-agent")) {
                    this.connection.setRequestProperty("User-Agent", this.client.userAgents.any());
                }

                if (method.hasRequestBody()) {
                    HttpEntity entity = this.builder.body();
                    TransferSession session = entity.newSession();
                    try {
                        TransferEncoding encoding = session.transferEncoding();
                        long length = session.length();

                        this.connection.setRequestProperty("Content-Encoding", entity.encoding().name());
                        this.connection.setRequestProperty("Content-Type", entity.type().formatted());

                        if (encoding != StandardTransferEncoding.chunked) {
                            this.connection.setRequestProperty("Content-Length", String.valueOf(length));
                        }

                        if (length < 0L) {
                            if (encoding != StandardTransferEncoding.chunked) {
                                throw new IllegalStateException(String.format("Chunked transport required, but found \"%s\"!", encoding.name()));
                            } else {
                                //enable chunked transport
                                this.connection.setChunkedStreamingMode(0);
                            }
                        } else {
                            if (encoding == StandardTransferEncoding.chunked) {
                                //user is requesting chunked transport anyway for some reason or another
                                this.connection.setChunkedStreamingMode(0);
                            } else {
                                //set fixed length output
                                this.connection.setFixedLengthStreamingMode(length);
                            }
                        }

                        //send body
                        if (session.hasByteBuf()) {
                            ByteBuf buf = session.getByteBuf();
                            try {
                                buf.readBytes(this.connection.getOutputStream(), buf.readableBytes());
                            } finally {
                                buf.release();
                            }
                        } else {
                            try (WritableByteChannel out = Channels.newChannel(this.connection.getOutputStream())) {
                                long transferred = session.transferAllBlocking(session.position(), out);
                                if (transferred < 0L) {
                                    throw new IllegalStateException(String.format("Transferred %d bytes (negative?!?)", transferred));
                                } else if (length >= 0L && transferred != length) {
                                    throw new IllegalStateException(String.format("Transferred %d bytes (expected: %d)", transferred, length));
                                }
                            }
                        }
                    } finally {
                        session.release();
                    }
                }

                //TODO: this no work correct
                ResponseHeadersImpl<V> responseHeaders = new ResponseHeadersImpl<>(
                        this,
                        StatusCode.of(this.connection.getResponseCode(), this.connection.getResponseMessage()),
                        new HeaderSnapshot(this.connection.getHeaderFields().entrySet().stream()
                                .map(entry -> {
                                    String key = entry.getKey();
                                    List<String> value = entry.getValue();
                                    return (key == null || value.isEmpty()) ? null : Header.of(key, value);
                                })));

                if (this.builder.followRedirects() && responseHeaders.isRedirect()) {
                    url = Constants.encodeUrl(responseHeaders.redirectLocation());
                    this.connection.disconnect();
                    continue;
                }

                if (!this.headers.trySuccess(responseHeaders) && !this.headers.isCancelled()) {
                    //what?
                    throw new IllegalStateException("Unable to mark headers as received!");
                } else if (this.headers.isCancelled() || this.body.isCancelled()) {
                    //client has been closed, exit silently
                    return;
                }

                V bodyValue = this.implReceiveBody(this.connection.getInputStream(), responseHeaders);
                if (!this.body.trySuccess(new DelegatingResponseBodyImpl<>(responseHeaders, bodyValue)) && !this.body.isCancelled()) {
                    //what?
                    throw new IllegalStateException("Unable to mark body as received!");
                }
                //if body is cancelled this will simply exit the loop normally
            } while (!this.body.isDone());
        } catch (Exception e) {
            this.body.setFailure(e);
        } finally {
            this.client.removeRequest(this);
            if (this.connection != null) {
                this.connection.disconnect();
            }
            if (!this.body.isDone()) {
                if (this.headers.isDone()) {
                    if (this.headers.isCancelled()) {
                        this.body.cancel(true);
                    } else if (!this.headers.isSuccess()) {
                        this.body.setFailure(this.headers.cause());
                    } else {
                        this.body.setFailure(new IllegalStateException("Body was never marked as complete?!?"));
                    }
                } else {
                    IllegalStateException e = new IllegalStateException("Exited with no headers being completed?!?");
                    this.body.setFailure(e);
                    throw e;
                }
            }
        }
    }

    protected V implReceiveBody(@NonNull InputStream bodyIn, @NonNull ResponseHeaders headers) throws Exception {
        long maxLength = this.builder.maxLength();
        long readBytes = headers.contentLength();
        if (readBytes >= 0L) {
            if (maxLength >= 0L && readBytes > maxLength) {
                throw new ResponseTooLargeException(readBytes, maxLength);
            } else {
                //set max length to content-length, even if max length is not defined
                //we don't want to read a request body larger than the content-length since that'd indicate a broken request
                maxLength = readBytes;
            }
        }
        if (maxLength < 0L) {
            maxLength = -1L;
        }
        readBytes = 0L;

        ResponseAggregator<Object, V> aggregator = this.builder.aggregator();
        if (this.builder.progressHandler() != null) {
            aggregator = new NotifyingAggregator<>(aggregator, this.builder.progressHandler());
        }
        Object temp = aggregator.init(this.headers.getNow(), this);
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] buf = handle.get();
            for (int i; !this.body.isDone() && (i = bodyIn.read(buf)) > 0; ) {
                if (maxLength >= 0L && (readBytes += i) > maxLength) {
                    //if max length is set and we've read more data than it, throw exception
                    throw new ResponseTooLargeException(readBytes, maxLength);
                }
                temp = aggregator.add(temp, Unpooled.wrappedBuffer(buf, 0, i), this);
            }
            return this.body.isDone() ? null : aggregator.doFinal(temp, this); //don't call doFinal if the request is cancelled
        } finally {
            aggregator.deinit(temp, this);
        }
    }
}
