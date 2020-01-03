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
    Future<HttpServerBinding> bind(@NonNull InetSocketAddress address);

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
