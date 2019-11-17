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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.SingletonHeaderImpl;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderSnapshot;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.DelegatingResponseBodyImpl;
import net.daporkchop.lib.http.response.ResponseBody;
import net.daporkchop.lib.http.response.ResponseHeaders;
import net.daporkchop.lib.http.response.ResponseHeadersImpl;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;
import net.daporkchop.lib.http.util.exception.ResponseTooLargeException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.StringJoiner;

/**
 * Shared implementation of {@link Request} for {@link JavaHttpClient}.
 *
 * @author DaPorkchop_
 */
public final class JavaRequest<V> implements Request<V>, Runnable {
    protected final JavaHttpClient        client;
    protected final Thread                thread;
    protected final JavaRequestBuilder<V> builder;
    protected       HttpURLConnection     connection;

    protected final Promise<ResponseHeaders> headers;
    protected final Promise<ResponseBody<V>> body;

    public JavaRequest(@NonNull JavaRequestBuilder<V> builder) {
        this.client = builder.client();
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
            final HttpMethod method = this.builder.method();

            URL url = this.builder.url;
            do {
                this.connection = (HttpURLConnection) url.openConnection();

                //set method
                this.connection.setRequestMethod(method.name());
                this.connection.setDoOutput(method.hasRequestBody());
                this.connection.setDoInput(method.hasResponseBody());

                //set request headers
                {
                    HeaderMap headers = this.builder._prepareHeaders();
                    //add all headers as properties (we don't need to use set since HeaderMap already guarantees distinct keys, so using setRequestProperty would only cause needless string comparisons)
                    headers.forEach(header -> this.connection.addRequestProperty(header.key(), header.value())); //if it's a list all the values will be joined together

                    if (!headers.hasKey("user-agent")) this.connection.setRequestProperty("user-agent", this.client.userAgentPool.any());

                    if (method.hasRequestBody())    {
                        HttpEntity entity = this.builder.body();

                        this.connection.setRequestProperty("content-encoding", entity.encoding().name());
                        this.connection.setRequestProperty("content-type", entity.type().formatted());

                        if (entity.transferEncoding() != StandardTransferEncoding.chunked) {
                            this.connection.setRequestProperty("content-length", String.valueOf(entity.length()));
                        }
                    }
                }

                if (method.hasRequestBody())    {
                    HttpEntity entity = this.builder.body();
                    TransferEncoding encoding = entity.transferEncoding();
                    long length = entity.length();

                    if (length < 0L)  {
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
                    try (TransferSession session = entity.newSession();
                         OutputStream out = this.connection.getOutputStream())  {
                        long transferred = session.transferAllBlocking(out);
                        if (transferred < 0L) {
                            throw new IllegalStateException(String.format("Transferred %d bytes (negative?!?)", transferred));
                        } else if (length >= 0L && transferred != length)  {
                            throw new IllegalStateException(String.format("Transferred %d bytes (expected: %d)", transferred, length));
                        }
                    }
                }

                ResponseHeadersImpl headers = new ResponseHeadersImpl(
                        StatusCode.of(this.connection.getResponseCode(), this.connection.getResponseMessage()),
                        new HeaderSnapshot(this.connection.getHeaderFields().entrySet().stream()
                                .map(entry -> {
                                    String key = entry.getKey();
                                    List<String> value = entry.getValue();
                                    return (key == null || value.isEmpty()) ? null : Header.of(key, value);
                                })));

                if (this.builder.followRedirects() && headers.isRedirect()) {
                    url = new URL(headers.redirectLocation());
                    this.connection.disconnect();
                    continue;
                }

                this.headers.setSuccess(headers);
                this.body.setSuccess(new DelegatingResponseBodyImpl<>(headers, this.implReceiveBody(this.connection.getInputStream(), headers)));
            } while (!this.body.isDone());
        } catch (Exception e) {
            this.body.setFailure(e);
        } finally {
            if (this.connection != null)    {
                this.connection.disconnect();
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
        readBytes = 0L;

        ResponseAggregator<Object, V> aggregator = this.builder.aggregator();
        Object temp = aggregator.init(this.headers.getNow(), this);
        try {
            byte[] buf = new byte[4096];
            for (int i; (i = bodyIn.read(buf)) > 0; ) {
                if (maxLength >= 0L && (readBytes += i) > maxLength) {
                    //if max length is set and we've read more data than it, throw exception
                    throw new ResponseTooLargeException(readBytes, maxLength);
                }
                temp = aggregator.add(temp, Unpooled.wrappedBuffer(buf, 0, i), this);
            }
            return aggregator.doFinal(temp, this);
        } finally {
            aggregator.deinit(temp, this);
        }
    }
}
