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
     * Closes this {@link HttpClient}, disconnecting any active requests and releasing any resources allocated
     * by it and preventing any new connections from being sent.
     *
     * @return a {@link Future} which will be notified when the close operation has been completed
     */
    Future<Void> close();

    /**
     * @return a {@link Future} which will be notified when this {@link HttpClient} instance has been closed
     */
    Future<Void> closeFuture();
}
