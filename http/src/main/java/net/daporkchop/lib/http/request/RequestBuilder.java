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

package net.daporkchop.lib.http.request;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.http.HttpClient;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.entity.FileHttpEntity;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderMaps;
import net.daporkchop.lib.http.message.MessageBuilder;
import net.daporkchop.lib.http.request.auth.Authentication;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;
import net.daporkchop.lib.http.response.aggregate.ToByteArrayAggregator;
import net.daporkchop.lib.http.response.aggregate.ToByteBufAggregator;
import net.daporkchop.lib.http.response.aggregate.ToFileAggregator;
import net.daporkchop.lib.http.response.aggregate.ToStringAggregator;
import net.daporkchop.lib.http.server.ResponseBuilder;
import net.daporkchop.lib.http.util.ProgressHandler;

import java.io.File;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Used for configuring an HTTP request prior to sending.
 * <p>
 * Assume that all implementations will cause undefined behavior when used concurrently.
 *
 * @author DaPorkchop_
 */
public interface RequestBuilder<V> extends MessageBuilder<RequestBuilder<V>> {
    /**
     * @return the {@link HttpClient} instance that will issue the request
     */
    HttpClient client();

    /**
     * Configures this {@link RequestBuilder} to send the request to the given URL.
     *
     * @param url the URL that the request should be sent to
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> url(@NonNull String url);

    /**
     * Configures the {@link HttpMethod} that the request will be sent using.
     * <p>
     * Defaults to {@link HttpMethod#GET}.
     * <p>
     * If the new {@link HttpMethod} does not allow sending a request body, the value of {@link #body(HttpEntity)} will be cleared.
     *
     * @param method the new {@link HttpMethod} to use
     * @return this {@link RequestBuilder} instance
     * @throws IllegalArgumentException if the {@link HttpClient} does not support the given {@link HttpMethod}
     */
    RequestBuilder<V> method(@NonNull HttpMethod method) throws IllegalArgumentException;

    /**
     * Configures the {@link HttpEntity} that will be sent with the request.
     * <p>
     * For HTTP methods that require a body, this value must be set.
     *
     * @param body the body of the request
     * @return this {@link RequestBuilder} instance
     * @throws IllegalStateException if the currently selected {@link HttpMethod} does not allow sending a request body
     */
    @Override
    RequestBuilder<V> body(@NonNull HttpEntity body) throws IllegalStateException;

    /**
     * Configures this {@link RequestBuilder} to use the given {@link ResponseAggregator}.
     *
     * @param aggregator the new {@link ResponseAggregator} to use
     * @param <V_NEW>    the new return value type
     * @return this {@link RequestBuilder} instance
     */
    <V_NEW> RequestBuilder<V_NEW> aggregator(@NonNull ResponseAggregator<?, V_NEW> aggregator);

    /**
     * Configures this {@link RequestBuilder} to aggregate data into a {@link String}.
     *
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<String> aggregateToString() {
        return this.aggregator(new ToStringAggregator());
    }

    /**
     * Configures this {@link RequestBuilder} to aggregate data into a {@code byte[]}.
     *
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<byte[]> aggregateToByteArray() {
        return this.aggregator(new ToByteArrayAggregator());
    }

    /**
     * Configures this {@link RequestBuilder} to aggregate data into a {@link ByteBuf}.
     *
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<ByteBuf> aggregateToByteBuf() {
        return this.aggregator(new ToByteBufAggregator());
    }

    /**
     * Configures this {@link RequestBuilder} to write received data to the given {@link File}.
     *
     * @param file the {@link File} to write received data to
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<File> downloadToFile(@NonNull File file) {
        return this.aggregator(new ToFileAggregator(file));
    }

    /**
     * Configures this {@link RequestBuilder} to write received data to the given {@link File}.
     *
     * @param file           the {@link File} to write received data to
     * @param allowOverwrite whether or not to overwrite existing data if the given file already exists
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<File> downloadToFile(@NonNull File file, boolean allowOverwrite) {
        return this.aggregator(new ToFileAggregator(file, allowOverwrite));
    }

    /**
     * Configures this {@link RequestBuilder} to use the given {@link ProgressHandler}.
     *
     * @param handler the {@link ProgressHandler} to use. If {@code null} (default), none will be used.
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> progressHandler(ProgressHandler handler);

    /**
     * Configures this {@link RequestBuilder} to use the given {@link Proxy}.
     *
     * @param proxy the {@link Proxy} to use. If {@code null} (default), none will be used.
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> proxy(Proxy proxy);

    /**
     * Configures this {@link RequestBuilder} to follow redirects silently.
     *
     * @param silentlyFollowRedirects whether or not this request will follow redirects silently
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> followRedirects(boolean silentlyFollowRedirects);

    /**
     * Sets a number of specific headers to be sent with the request.
     * <p>
     * If a header with the a given key already exists, it will be silently replaced by the new value.
     *
     * @param headers the headers to set
     * @return this {@link RequestBuilder} instance
     * @see #putHeader(Header)
     */
    default RequestBuilder<V> putHeaders(@NonNull Header... headers) {
        for (Header header : headers) {
            if (header == null) {
                throw new NullPointerException("headers");
            }
        }
        for (Header header : headers) {
            this.putHeader(header);
        }
        return this;
    }

    /**
     * Adds a number of specific headers to be sent with the request.
     * <p>
     * If a header with the a given key already exists, it will be silently replaced by the new value.
     *
     * @param headers the headers to set
     * @return this {@link RequestBuilder} instance
     * @see #addHeader(String, String) (Header)
     */
    default RequestBuilder<V> addHeaders(@NonNull Header... headers) {
        for (Header header : headers) {
            if (header == null) {
                throw new NullPointerException("headers");
            }
        }
        for (Header header : headers) {
            this.addHeader(header);
        }
        return this;
    }

    /**
     * Sets the "User-Agent" header to be sent with the request.
     *
     * @param userAgent the new user agent
     * @return this {@link RequestBuilder} instance
     */
    default RequestBuilder<V> userAgent(@NonNull String userAgent) {
        return this.putHeader("user-agent", userAgent);
    }

    /**
     * Sets the HTTP authentication method to be used by the request.
     * <p>
     * Defaults to {@link Authentication#none()}.
     *
     * @param authentication the new HTTP authentication method to use
     * @return this {@link RequestBuilder} instance
     * @see Authentication#basic(String, String)
     */
    RequestBuilder<V> authentication(@NonNull Authentication authentication);

    /**
     * Sets the maximum number of bytes the server may send as a body before aborting the request with an exception.
     * <p>
     * Defaults to {@code -1L}.
     *
     * @param maxLength the maximum size of the body. If less than {@code 0L}, no limit will be enforced.
     * @return this {@link RequestBuilder} instance
     */
    RequestBuilder<V> maxLength(long maxLength);

    /**
     * Initiates the HTTP request using the configured settings.
     * <p>
     * Once this method has been called, this {@link RequestBuilder} instance should be discarded and any attempts
     * to use it will result in undefined behavior.
     *
     * @return an {@link Request} constructed using the configured settings
     */
    Request<V> send();
}
