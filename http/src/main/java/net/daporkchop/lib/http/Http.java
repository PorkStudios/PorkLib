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

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.impl.java.JavaHttpClient;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper class for sending simple HTTP requests with a single method call.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Http {
    public final HttpClient CLIENT = new JavaHttpClient(runnable -> new Thread(runnable, String.format("http-client-worker-%x", System.nanoTime() * ThreadLocalRandom.current().nextLong())));

    public String getString(@NonNull String url, Header... headers) {
        return CLIENT.request(url)
                .followRedirects(true)
                .headers(headers)
                .aggregateToString()
                .send()
                .syncBodyAndGet().value();
    }

    public byte[] get(@NonNull String url, Header... headers) {
        return CLIENT.request(url)
                .followRedirects(true)
                .headers(headers)
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().value();
    }

    public String postUrlEncodedString(@NonNull String url, @NonNull String content, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .headers(headers)
                .body(ContentType.parse("application/x-www-form-urlencoded; charset=UTF-8"), content.getBytes(StandardCharsets.UTF_8))
                .aggregateToString()
                .send()
                .syncBodyAndGet().value();
    }

    public String postJsonString(@NonNull String url, @NonNull String json, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .headers(headers)
                .bodyJson(json)
                .aggregateToString()
                .send()
                .syncBodyAndGet().value();
    }

    public String postString(@NonNull String url, @NonNull byte[] content, @NonNull String contentType, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .headers(headers)
                .body(contentType, content)
                .aggregateToString()
                .send()
                .syncBodyAndGet().value();
    }

    public byte[] postUrlEncoded(@NonNull String url, @NonNull String content, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .headers(headers)
                .body(ContentType.parse("application/x-www-form-urlencoded; charset=UTF-8"), content.getBytes(StandardCharsets.UTF_8))
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().value();
    }

    public byte[] postJson(@NonNull String url, @NonNull String json, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .headers(headers)
                .bodyJson(json)
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().value();
    }

    public byte[] post(@NonNull String url, @NonNull byte[] content, @NonNull String contentType, Header... headers) {
        return CLIENT.request(HttpMethod.POST, url)
                .followRedirects(true)
                .headers(headers)
                .body(contentType, content)
                .aggregateToByteArray()
                .send()
                .syncBodyAndGet().value();
    }
}
