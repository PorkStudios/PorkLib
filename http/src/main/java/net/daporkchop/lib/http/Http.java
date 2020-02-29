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

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.threadfactory.ThreadFactoryBuilder;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.impl.java.JavaHttpClientBuilder;

import java.nio.charset.StandardCharsets;

/**
 * Helper class for sending simple HTTP requests with a single method call.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Http {
    public final HttpClient CLIENT = new JavaHttpClientBuilder()
            .threadFactory(new ThreadFactoryBuilder().name("PorkLib HTTP Worker Thread #%d").formatId().collapsingId().build())
            .build();

    public String getString(@NonNull String url, Header... headers) {
        return CLIENT.request(url)
                .followRedirects(true)
                .putHeaders(headers)
                .aggregateToString()
                .send()
                .syncBodyAndGet().body();
    }

    public byte[] get(@NonNull String url, Header... headers) {
        return CLIENT.request(url)
                .followRedirects(true)
                .putHeaders(headers)
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().body();
    }

    public String postUrlEncodedString(@NonNull String url, @NonNull String content, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .putHeaders(headers)
                .body(ContentType.parse("application/x-www-form-urlencoded; charset=UTF-8"), content.getBytes(StandardCharsets.UTF_8))
                .aggregateToString()
                .send()
                .syncBodyAndGet().body();
    }

    public String postJsonString(@NonNull String url, @NonNull String json, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .putHeaders(headers)
                .bodyJson(json)
                .aggregateToString()
                .send()
                .syncBodyAndGet().body();
    }

    public String postString(@NonNull String url, @NonNull byte[] content, @NonNull String contentType, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .putHeaders(headers)
                .body(contentType, content)
                .aggregateToString()
                .send()
                .syncBodyAndGet().body();
    }

    public byte[] postUrlEncoded(@NonNull String url, @NonNull String content, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .putHeaders(headers)
                .body(ContentType.parse("application/x-www-form-urlencoded; charset=UTF-8"), content.getBytes(StandardCharsets.UTF_8))
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().body();
    }

    public byte[] postJson(@NonNull String url, @NonNull String json, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .putHeaders(headers)
                .bodyJson(json)
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().body();
    }

    public byte[] post(@NonNull String url, @NonNull byte[] content, @NonNull String contentType, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .putHeaders(headers)
                .body(contentType, content)
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().body();
    }
}
