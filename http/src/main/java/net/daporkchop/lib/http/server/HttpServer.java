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

package net.daporkchop.lib.http.server;

import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.http.server.handle.ServerHandler;

import java.net.InetSocketAddress;

/**
 * A representation of an HTTP server.
 *
 * @author DaPorkchop_
 */
public interface HttpServer {
    /**
     * @return the {@link ServerHandler} currently in use
     */
    ServerHandler handler();

    /**
     * Sets the {@link ServerHandler} used by this server.
     *
     * @param handler the new {@link ServerHandler} to use
     * @return this {@link HttpServer} instance
     */
    HttpServer handler(@NonNull ServerHandler handler);

    /**
     * Binds this {@link HttpServer} to a local address to accept incoming connections.
     *
     * @param address the local address to bind to
     * @return a {@link Future} that will be notified once the bind operation is complete
     */
    Future<?> bind(@NonNull InetSocketAddress address);

    /**
     * Closes this {@link HttpServer}, disconnecting all connections, releasing any allocated resources and preventing further requests from being accepted.
     *
     * @return a {@link Future} which will be notified when the close operation has been completed
     */
    Future<Void> close();

    /**
     * @return a {@link Future} which will be notified when this {@link HttpServer} instance has been closed
     */
    Future<Void> closeFuture();
}
