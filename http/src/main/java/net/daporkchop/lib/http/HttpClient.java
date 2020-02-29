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

package net.daporkchop.lib.http;

import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.http.request.RequestBuilder;

import java.util.Set;

/**
 * A representation of an HTTP client.
 *
 * @author DaPorkchop_
 */
public interface HttpClient {
    /**
     * @return a {@link Set} containing all {@link HttpMethod}s supported by this client
     */
    Set<HttpMethod> supportedMethods();

    /**
     * Creates a new, blank {@link RequestBuilder} instance to make a new HTTP request.
     *
     * @return a new {@link RequestBuilder} instance
     */
    RequestBuilder<Void> request();

    /**
     * Creates a new {@link RequestBuilder} instance to make a new HTTP request, pre-configured with the given URL.
     *
     * @param url the URL that the request will be sent to
     * @return a new {@link RequestBuilder} instance
     */
    default RequestBuilder<Void> request(@NonNull String url) {
        return this.request().url(url);
    }

    /**
     * Creates a new {@link RequestBuilder} instance to make a new HTTP request, pre-configured with the given {@link HttpMethod}.
     *
     * @param method the {@link HttpMethod} that the request will be sent using
     * @return a new {@link RequestBuilder} instance
     */
    default RequestBuilder<Void> request(@NonNull HttpMethod method) {
        return this.request().method(method);
    }

    /**
     * Creates a new {@link RequestBuilder} instance to make a new HTTP request, pre-configured with the given {@link HttpMethod} and URL.
     *
     * @param method the {@link HttpMethod} that the request will be sent using
     * @param url the URL that the request will be sent to
     * @return a new {@link RequestBuilder} instance
     */
    default RequestBuilder<Void> request(@NonNull HttpMethod method, @NonNull String url) {
        return this.request().method(method).url(url);
    }

    /**
     * Closes this {@link HttpClient}, disconnecting any active requests, releasing any allocated resources and preventing any new connections from
     * being sent.
     *
     * @return a {@link Future} which will be notified when the close operation has been completed
     */
    Future<Void> close();

    /**
     * @return a {@link Future} which will be notified when this {@link HttpClient} instance has been closed
     */
    Future<Void> closeFuture();
}
