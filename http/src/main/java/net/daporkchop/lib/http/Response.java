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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.common.header.HeaderMap;

/**
 * An HTTP response.
 *
 * @author DaPorkchop_
 */
//TODO: where is the body?
public interface Response {
    /**
     * @return the status of the HTTP response
     */
    StatusCode status();

    /**
     * @return the headers attached to the HTTP response
     */
    HeaderMap headers();

    /**
     * Checks if this response is a redirect.
     *
     * @return whether or not this response is a redirect
     */
    default boolean isRedirect() {
        int code = this.status().code();
        //return code >= 300 && code < 400; // 3xx
        return code == 301 || code == 302;
    }

    /**
     * Gets the URL that the request is being redirected to.
     * <p>
     * If this response does not indicate a redirect (see {@link #isRedirect()}), this method will return {@code null}.
     * <p>
     * If this response does indicate a redirect, but no next URL is given, this method will throw {@link IllegalStateException}.
     *
     * @return the URL that the request is being redirected to
     */
    default String redirectUrl() {
        switch (this.status().code()) {
            case 301:
            case 302: {
                String location = this.headers().getValue("Location");
                if (location == null) {
                    throw new IllegalStateException("No location given!");
                } else {
                    return location;
                }
            }
            default:
                return null;
        }
    }

    /**
     * A simple implementation of {@link Response}.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class Default implements Response {
        @NonNull
        protected final StatusCode status;
        @NonNull
        protected final HeaderMap  headers;
    }
}
