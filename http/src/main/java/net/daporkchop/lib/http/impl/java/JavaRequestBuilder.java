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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderMaps;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.request.RequestBuilder;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class JavaRequestBuilder<V> implements RequestBuilder<V> {
    @NonNull
    protected final JavaHttpClient client;

    protected URL url;

    protected ResponseAggregator<Object, V> aggregator;

    @Setter
    protected HeaderMap headers = HeaderMaps.empty();

    @Setter
    protected boolean silentlyFollowRedirects = false;

    @Override
    public RequestBuilder<V> url(@NonNull String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e)   {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V_NEW> RequestBuilder<V_NEW> aggregator(@NonNull ResponseAggregator<?, V_NEW> aggregator) {
        JavaRequestBuilder<V_NEW> _this = (JavaRequestBuilder<V_NEW>) this;
        _this.aggregator = (ResponseAggregator<Object, V_NEW>) aggregator;
        return _this;
    }

    @Override
    public Request<V> send() {
        return new JavaRequest<>(this);
    }
}
