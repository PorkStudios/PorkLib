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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderMaps;
import net.daporkchop.lib.http.header.map.MutableHeaderMap;
import net.daporkchop.lib.http.header.map.MutableHeaderMapImpl;
import net.daporkchop.lib.http.request.AbstractRequestBuilder;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.request.RequestBuilder;
import net.daporkchop.lib.http.request.auth.Authentication;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;
import net.daporkchop.lib.http.util.Constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public final class JavaRequestBuilder<V> extends AbstractRequestBuilder<V, JavaHttpClient> {
    protected URL url;

    public JavaRequestBuilder(@NonNull JavaHttpClient client) {
        super(client);
    }

    @Override
    public RequestBuilder<V> url(@NonNull String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    @Override
    public Request<V> send() {
        this.assertConfigured();
        return new JavaRequest<>(this);
    }

    @Override
    protected void assertConfigured() {
        super.assertConfigured();

        if (this.url == null)   {
            throw new IllegalStateException("url isn't set!");
        }
    }

    /**
     * Internal method only: makes a copy of the locally stored {@link HeaderMap} and prepares it to be sent.
     */
    //TODO: abstract this method out into AbstractRequestBuilder
    protected void prepareHeaders(@NonNull BiConsumer<String, String> addCallback) {
        MutableHeaderMap headers = this.headers.mutableCopy();
        headers.forEach((key, value) -> {
            if (HeaderMaps.isReserved(key)) {
                throw new IllegalArgumentException(String.format("Header key \"%s\" is reserved, but was manually set!", key));
            }
        });

        this.authentication.rewrite(headers);

        headers.forEach(addCallback);

        if (!headers.hasKey("user-agent"))  { //only add user-agent header if it's not already set
            addCallback.accept("User-Agent", this.client.userAgentPool.any());
        }
    }
}
