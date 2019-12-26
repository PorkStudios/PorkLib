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
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderMaps;
import net.daporkchop.lib.http.header.map.MutableHeaderMap;
import net.daporkchop.lib.http.header.map.MutableHeaderMapImpl;
import net.daporkchop.lib.http.impl.java.JavaHttpClient;
import net.daporkchop.lib.http.request.auth.Authentication;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;
import net.daporkchop.lib.http.util.ProgressHandler;

import java.util.List;

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
    protected ResponseAggregator<Object, V> aggregator;

    @Setter(AccessLevel.NONE)
    protected ProgressHandler progressHandler;

    @NonNull
    @Getter(AccessLevel.NONE)
    protected HeaderMap      headers        = HeaderMaps.empty();

    @NonNull
    protected Authentication authentication = Authentication.none();

    protected boolean        followRedirects = false;

    @Override
    public RequestBuilder<V> method(@NonNull HttpMethod method) throws IllegalArgumentException {
        if (!this.client.supportedMethods().contains(method))   {
            throw new IllegalArgumentException(String.format("HTTP method %s is not supported by \"%s\"!", method, PorkUtil.className(this.client)));
        } else if (!method.hasRequestBody())  {
            this.body = null;
        }
        this.method = method;
        return this;
    }

    @Override
    public RequestBuilder<V> body(@NonNull HttpEntity body) throws IllegalStateException {
        if (this.method.hasRequestBody())  {
            this.body = body;
            return this;
        } else {
            throw new IllegalStateException(String.format("HTTP method %s does not support a request body!", this.method));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V_NEW> RequestBuilder<V_NEW> aggregator(@NonNull ResponseAggregator<?, V_NEW> aggregator) {
        AbstractRequestBuilder<V_NEW, C> _this = (AbstractRequestBuilder<V_NEW, C>) this;
        _this.aggregator = (ResponseAggregator<Object, V_NEW>) aggregator;
        return _this;
    }

    @Override
    public RequestBuilder<V> progressHandler(ProgressHandler handler) {
        this.progressHandler = handler;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RequestBuilder<V> header(@NonNull Header header) {
        HeaderMap headers = this.headers;
        MutableHeaderMap mutableHeaders;
        if (headers instanceof MutableHeaderMap) {
            mutableHeaders = (MutableHeaderMap) headers;
        } else if (headers == HeaderMaps.empty()) {
            mutableHeaders = new MutableHeaderMapImpl();
        } else {
            mutableHeaders = headers.mutableCopy();
        }
        mutableHeaders.put(header);
        this.headers = mutableHeaders;
        return this;
    }

    protected void assertConfigured()   {
        if (this.aggregator == null)    {
            throw new IllegalStateException("aggregator isn't set!");
        } else if (this.method.hasRequestBody() && this.body == null)  {
            throw new IllegalStateException("body isn't set!");
        }
    }

    /**
     * Internal method only: makes a copy of the locally stored {@link HeaderMap} and prepares it to be sent.
     */
    protected HeaderMap _prepareHeaders() {
        MutableHeaderMap headers = this.headers.mutableCopy();
        headers.forEach((key, value) -> {
            if (HeaderMaps.isReserved(key)) {
                throw new IllegalArgumentException(String.format("Header key \"%s\" is reserved, but was manually set!", key));
            }
        });

        this.authentication.rewrite(headers);

        return headers;
    }
}
