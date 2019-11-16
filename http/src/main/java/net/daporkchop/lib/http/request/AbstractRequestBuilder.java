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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.header.map.HeaderMaps;
import net.daporkchop.lib.http.header.map.MutableHeaderMap;
import net.daporkchop.lib.http.header.map.MutableHeaderMapImpl;
import net.daporkchop.lib.http.request.auth.Authentication;
import net.daporkchop.lib.http.response.aggregate.ResponseAggregator;

/**
 * Implementation of some of the most common methods of {@link RequestBuilder}.
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public abstract class AbstractRequestBuilder<V> implements RequestBuilder<V> {
    protected long maxLength = -1L;

    @Setter(AccessLevel.NONE)
    protected ResponseAggregator<Object, V> aggregator;

    @NonNull
    @Getter(AccessLevel.NONE)
    protected HeaderMap      headers        = HeaderMaps.empty();

    @NonNull
    protected Authentication authentication = Authentication.none();

    protected boolean        followRedirects = false;

    @Override
    @SuppressWarnings("unchecked")
    public <V_NEW> RequestBuilder<V_NEW> aggregator(@NonNull ResponseAggregator<?, V_NEW> aggregator) {
        AbstractRequestBuilder<V_NEW> _this = (AbstractRequestBuilder<V_NEW>) this;
        _this.aggregator = (ResponseAggregator<Object, V_NEW>) aggregator;
        return _this;
    }

    @Override
    public RequestBuilder<V> header(@NonNull String key, @NonNull String value) {
        HeaderMap headers = this.headers;
        MutableHeaderMap mutableHeaders;
        if (headers instanceof MutableHeaderMap) {
            mutableHeaders = (MutableHeaderMap) headers;
        } else if (headers == HeaderMaps.empty()) {
            mutableHeaders = new MutableHeaderMapImpl();
        } else {
            mutableHeaders = headers.mutableCopy();
        }
        mutableHeaders.put(key, value);
        this.headers = mutableHeaders;
        return this;
    }
}
