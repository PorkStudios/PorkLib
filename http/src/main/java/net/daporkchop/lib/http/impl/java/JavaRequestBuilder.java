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

import lombok.NonNull;
import net.daporkchop.lib.http.header.HeaderMap;
import net.daporkchop.lib.http.request.AbstractRequestBuilder;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.request.RequestBuilder;
import net.daporkchop.lib.http.util.Constants;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of {@link RequestBuilder} for {@link JavaHttpClient}.
 *
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
            this.url = Constants.encodeUrl(url);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    @Override
    public Request<V> send() {
        this.assertConfigured();
        return this.client.buildRequest(this);
    }

    @Override
    protected void assertConfigured() {
        super.assertConfigured();

        if (this.url == null) {
            throw new IllegalStateException("url isn't set!");
        }
    }

    @Override
    protected HeaderMap _prepareHeaders() {
        return super._prepareHeaders();
    }
}
