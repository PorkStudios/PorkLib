/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.server.handle;

import lombok.NonNull;
import net.daporkchop.lib.http.header.map.HeaderMap;
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
     * Handles an incoming request.
     * <p>
     * This is the first event that is fired when handling a request. It will be notified as soon as the request's query line
     * (containing HTTP method, version and path) is available.
     *
     * @param query the request's query line
     * @throws Exception if an exception occurs while handling the request
     */
    void handle(@NonNull Query query) throws Exception;

    /**
     * Handles an incoming request.
     *
     * @param query    the client's query
     * @param headers  the headers sent with the request
     * @param response a {@link ResponseBuilder} for sending a response
     * @throws Exception if an exception occurs while handling the request
     */
    void handle(@NonNull Query query, @NonNull HeaderMap headers, @NonNull ResponseBuilder response) throws Exception;
}
