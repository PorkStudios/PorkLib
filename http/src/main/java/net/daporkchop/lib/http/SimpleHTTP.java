/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.apache.ApacheHttpTransport;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.FastByteArrayOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class SimpleHTTP {
    private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
    private static final HttpRequestFactory REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                HTTP_TRANSPORT.shutdown();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public static String getString(@NonNull String url, Object... params) throws IOException {
        return new String(get(url, params));
    }

    public static String getString(@NonNull URL url, Object... params) throws IOException {
        return new String(get(url, params));
    }

    public static byte[] get(@NonNull String url, Object... params) throws IOException {
        return get(new URL(url), params);
    }

    public static byte[] get(@NonNull URL url, Object... params) throws IOException {
        HttpRequest request = REQUEST_FACTORY.buildGetRequest(new GenericUrl(url));
        handleParameters(request.getHeaders(), params);
        return fetchResponseAsBytes(request);
    }

    public static String postUrlEncodedAsString(@NonNull String url, @NonNull String content, Object... params) throws IOException {
        return postAsString(new URL(url), content.getBytes(UTF8.utf8), "application/x-www-form-urlencoded", params);
    }

    public static String postJsonAsString(@NonNull String url, @NonNull String json, Object... params) throws IOException {
        return postAsString(new URL(url), json.getBytes(UTF8.utf8), "application/json", params);
    }

    public static String postAsString(@NonNull String url, @NonNull byte[] content, @NonNull String contentType, Object... params) throws IOException {
        return postAsString(new URL(url), new ByteArrayContent(contentType, content), params);
    }

    public static String postAsString(@NonNull String url, @NonNull InputStream content, @NonNull String contentType, Object... params) throws IOException {
        return postAsString(new URL(url), new InputStreamContent(contentType, content), params);
    }

    public static String postAsString(@NonNull String url, HttpContent content, Object... params) throws IOException {
        return new String(post(new URL(url), content, params));
    }

    public static byte[] postUrlEncoded(@NonNull String url, @NonNull String content, Object... params) throws IOException {
        return post(new URL(url), content.getBytes(UTF8.utf8), "application/x-www-form-urlencoded", params);
    }

    public static byte[] postJson(@NonNull String url, @NonNull String json, Object... params) throws IOException {
        return post(new URL(url), json.getBytes(UTF8.utf8), "application/json", params);
    }

    public static byte[] post(@NonNull String url, @NonNull byte[] content, @NonNull String contentType, Object... params) throws IOException {
        return post(new URL(url), new ByteArrayContent(contentType, content), params);
    }

    public static byte[] post(@NonNull String url, @NonNull InputStream content, @NonNull String contentType, Object... params) throws IOException {
        return post(new URL(url), new InputStreamContent(contentType, content), params);
    }

    public static byte[] post(@NonNull String url, HttpContent content, Object... params) throws IOException {
        return post(new URL(url), content, params);
    }

    public static String postUrlEncodedAsString(@NonNull URL url, @NonNull String content, Object... params) throws IOException {
        return postAsString(url, content.getBytes(UTF8.utf8), "application/x-www-form-urlencoded", params);
    }

    public static String postJsonAsString(@NonNull URL url, @NonNull String json, Object... params) throws IOException {
        return postAsString(url, json.getBytes(UTF8.utf8), "application/json", params);
    }

    public static String postAsString(@NonNull URL url, @NonNull byte[] content, @NonNull String contentType, Object... params) throws IOException {
        return postAsString(url, new ByteArrayContent(contentType, content), params);
    }

    public static String postAsString(@NonNull URL url, @NonNull InputStream content, @NonNull String contentType, Object... params) throws IOException {
        return postAsString(url, new InputStreamContent(contentType, content), params);
    }

    public static String postAsString(@NonNull URL url, HttpContent content, Object... params) throws IOException {
        return new String(post(url, content, params));
    }

    public static byte[] postUrlEncoded(@NonNull URL url, @NonNull String content, Object... params) throws IOException {
        return post(url, content.getBytes(UTF8.utf8), "application/x-www-form-urlencoded", params);
    }

    public static byte[] postJson(@NonNull URL url, @NonNull String json, Object... params) throws IOException {
        return post(url, json.getBytes(UTF8.utf8), "application/json", params);
    }

    public static byte[] post(@NonNull URL url, @NonNull byte[] content, @NonNull String contentType, Object... params) throws IOException {
        return post(url, new ByteArrayContent(contentType, content), params);
    }

    public static byte[] post(@NonNull URL url, @NonNull InputStream content, @NonNull String contentType, Object... params) throws IOException {
        return post(url, new InputStreamContent(contentType, content), params);
    }

    public static byte[] post(@NonNull URL url, HttpContent content, Object... params) throws IOException {
        HttpRequest request = REQUEST_FACTORY.buildPostRequest(new GenericUrl(url), content);
        handleParameters(request.getHeaders(), params);
        return fetchResponseAsBytes(request);
    }

    private static void handleParameters(@NonNull HttpHeaders headers, @NonNull Object... params) {
        if (params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                Object o = params[i];
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry) o).getKey();
                    Object v = ((Map.Entry) o).getValue();
                    if (!(k instanceof String)) {
                        throw new IllegalArgumentException(String.format("Expected java.lang.String as key, but found %s", k == null ? "null" : k.getClass().getCanonicalName()));
                    }
                    headers.put((String) k, v);
                } else if (o instanceof String) {
                    if (params.length - 1 < i + 1) {
                        throw new IllegalStateException("Not enough parameters!");
                    }
                    Object v = params[++i];
                    if (v instanceof String) {
                        v = Arrays.asList((String) v);
                    }
                    headers.put((String) o, v);
                } else {
                    throw new IllegalArgumentException(String.format("Don't know how to handle parameter %s", o == null ? "null" : o.getClass().getCanonicalName()));
                }
            }
        }
    }

    private static byte[] fetchResponseAsBytes(@NonNull HttpRequest request) throws IOException {
        HttpResponse response = request.execute();
        int length;
        {
            Long lengthObj = response.getHeaders().getContentLength();
            length = lengthObj == null ? -1 : (int) (long) lengthObj;
        }
        ByteArrayOutputStream baos = length == -1 ? new ByteArrayOutputStream() : new FastByteArrayOutputStream(length);
        try (InputStream is = response.getContent()) {
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
        }
        response.disconnect();
        return baos.toByteArray();
    }
}
