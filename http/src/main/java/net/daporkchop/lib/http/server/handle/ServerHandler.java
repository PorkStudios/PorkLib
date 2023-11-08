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

package net.daporkchop.lib.http.server.handle;

import lombok.NonNull;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.message.Message;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.server.HttpServer;
import net.daporkchop.lib.http.server.ResponseBuilder;

/**
 * Handles events on a HTTP server.
 *
 * @author DaPorkchop_
 */
public interface ServerHandler {
    /**
     * Fired when this handler is set as the handler for a {@link HttpServer}.
     *
     * @param server the {@link HttpServer} that this handler is now the handler of
     */
    default void added(@NonNull HttpServer server) {
    }

    /**
     * Fired when this handler is no longer the handler for a {@link HttpServer}.
     *
     * @param server the {@link HttpServer} that this handler was removed from
     */
    default void removed(@NonNull HttpServer server) {
    }

    /**
     * @return the maximum allowed size (in bytes) of a request body before the request will be rejected
     */
    int maxBodySize();

    /**
     * Handles an incoming request's query.
     * <p>
     * This is the first event that is fired when handling a request. It will be notified as soon as the request's query line
     * (containing HTTP method, version and path) is available.
     *
     * @param query the request's query line
     * @throws Exception if an exception occurs while handling the request
     */
    default void handleQuery(@NonNull Query query) throws Exception {
    }

    /**
     * Handles an incoming request's headers.
     *
     * @param query   the client's query
     * @param headers the headers sent with the request
     * @throws Exception if an exception occurs while handling the request
     */
    default void handleHeaders(@NonNull Query query, @NonNull HeaderMap headers) throws Exception {
    }

    /**
     * Handles an incoming request.
     *
     * @param query    the client's query
     * @param message  a {@link Message} containing the request's headers and body
     * @param response a {@link ResponseBuilder} for sending a response
     * @throws Exception if an exception occurs while handling the request
     */
    void handle(@NonNull Query query, @NonNull Message message, @NonNull ResponseBuilder response) throws Exception;
}
