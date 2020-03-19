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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.HttpClient;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.header.DefaultMutableHeaderMap;
import net.daporkchop.lib.http.header.HeaderMap;
import net.daporkchop.lib.http.header.HeaderMaps;
import net.daporkchop.lib.http.header.MutableHeaderMap;
import net.daporkchop.lib.http.impl.java.JavaHttpClient;
import net.daporkchop.lib.http.request.auth.Authentication;
import net.daporkchop.lib.http.message.body.BodyAggregator;
import net.daporkchop.lib.http.util.ProgressHandler;

import java.net.Proxy;

/**
 * Implementation of some of the most common methods of {@link RequestBuilder}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public abstract class AbstractRequestBuilder<V, C extends HttpClient> implements RequestBuilder<V> {
    protected long maxLength = -1L;

    @NonNull
    protected final JavaHttpClient client;

    @Setter(AccessLevel.NONE)
    protected HttpMethod method = HttpMethod.GET;

    @Setter(AccessLevel.NONE)
    protected HttpEntity body;

    @Setter(AccessLevel.NONE)
    protected BodyAggregator<Object, V> aggregator;

    protected ProgressHandler progressHandler;

    protected Proxy proxy;

    @NonNull
    @Getter
    protected HeaderMap headers = HeaderMaps.empty();

    @NonNull
    protected Authentication authentication = Authentication.none();

    protected boolean followRedirects = false;

    @Override
    public RequestBuilder<V> method(@NonNull HttpMethod method) throws IllegalArgumentException {
        if (!this.client.supportedMethods().contains(method)) {
            throw new IllegalArgumentException(String.format("HTTP method %s is not supported by \"%s\"!", method, PorkUtil.className(this.client)));
        } else if (!method.hasRequestBody()) {
            this.body = null;
        }
        this.method = method;
        return this;
    }

    @Override
    public RequestBuilder<V> body(@NonNull HttpEntity body) throws IllegalStateException {
        if (this.method.hasRequestBody()) {
            this.body = body;
            return this;
        } else {
            throw new IllegalStateException(String.format("HTTP method %s does not support a request body!", this.method));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V_NEW> RequestBuilder<V_NEW> aggregator(@NonNull BodyAggregator<?, V_NEW> aggregator) {
        AbstractRequestBuilder<V_NEW, C> _this = (AbstractRequestBuilder<V_NEW, C>) this;
        _this.aggregator = (BodyAggregator<Object, V_NEW>) aggregator;
        return _this;
    }

    @Override
    public RequestBuilder<V> addHeader(@NonNull String key, @NonNull String value) {
        this.internalHeaderMap().add(key, value);
        return this;
    }

    @Override
    public RequestBuilder<V> putHeader(@NonNull String key, @NonNull String value) {
        this.internalHeaderMap().put(key, value);
        return this;
    }

    protected MutableHeaderMap internalHeaderMap() {
        HeaderMap headers = this.headers;
        MutableHeaderMap mutableHeaders;
        if (headers instanceof MutableHeaderMap) {
            mutableHeaders = (MutableHeaderMap) headers;
        } else if (headers == HeaderMaps.empty()) {
            mutableHeaders = new DefaultMutableHeaderMap();
        } else {
            mutableHeaders = new DefaultMutableHeaderMap(headers);
        }
        return (MutableHeaderMap) (this.headers = mutableHeaders);
    }

    protected void assertConfigured() {
        if (this.aggregator == null) {
            throw new IllegalStateException("aggregator isn't set!");
        } else if (this.method.hasRequestBody() && this.body == null) {
            throw new IllegalStateException("body isn't set!");
        }
    }

    /**
     * Internal method only: makes a copy of the locally stored {@link HeaderMap} and prepares it to be sent.
     */
    protected HeaderMap _prepareHeaders() {
        MutableHeaderMap headers = this.internalHeaderMap();
        headers.forEach((key, value) -> {
            if (HeaderMaps.isReserved(key)) {
                throw new IllegalArgumentException(String.format("Header key \"%s\" is reserved, but was manually set!", key));
            }
        });

        this.authentication.rewrite(headers);

        return headers;
    }
}
