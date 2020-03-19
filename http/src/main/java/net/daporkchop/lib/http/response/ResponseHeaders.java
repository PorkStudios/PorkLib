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

package net.daporkchop.lib.http.response;

import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.header.HeaderMap;
import net.daporkchop.lib.http.request.Request;

/**
 * The server's response to an HTTP request.
 *
 * @author DaPorkchop_
 */
public interface ResponseHeaders<V> {
    /**
     * @return the {@link Request} that these headers are a response to
     */
    Request<V> request();

    /**
     * @return the {@link StatusCode} that the server responded with
     */
    StatusCode status();

    /**
     * @return a {@link HeaderMap} containing the headers that the server responded with
     */
    HeaderMap headers();

    /**
     * Gets the length of the response's body's content (in bytes).
     * <p>
     * If the body's length is not known (e.g. Transfer-Encoding is "chunked"), this will return {@code -1L}.
     *
     * @return the length of the response's body's content (in bytes)
     */
    default long contentLength() {
        String length = this.headers().getValue("content-length");
        return length == null ? -1L : Long.parseLong(length);
    }

    /**
     * @return whether or not the server's response is a redirect
     */
    default boolean isRedirect() {
        int code = this.status().code();
        return code == 301 || code == 302; //TODO: 3xx status codes, not just 301 and 302
    }

    /**
     * Gets the location that this request is being redirected to.
     * <p>
     * If the server's response does not indicate a redirect, this method returns {@code null}.
     *
     * @return the location that this request is being redirected to
     */
    default String redirectLocation() {
        int code = this.status().code();
        switch (code) {
            case 301:
            case 302:
                return this.headers().getValue("location");
            default:
                return null;
        }
    }
}
