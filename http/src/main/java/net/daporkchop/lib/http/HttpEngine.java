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

import io.netty.util.concurrent.Future;
import net.daporkchop.lib.http.client.HttpClient;
import net.daporkchop.lib.http.server.HttpServer;

/**
 * An engine for (implementation of) the HTTP protocol.
 * <p>
 * Provides methods for constructing instances of {@link HttpClient} and {@link HttpServer}.
 * <p>
 * Keeping the same instance of an engine around may provide additional benefits, such as being able to share
 * resources (potentially even between {@link HttpServer} and {@link HttpClient} instances).
 *
 * @author DaPorkchop_
 */
public interface HttpEngine {
    /**
     * Creates a new {@link HttpClient} instance backed by this engine.
     */
    Future<HttpClient> client();

    /**
     * Creates a new {@link HttpServer} instance backed by this engine.
     */
    Future<HttpServer> server();

    /**
     * Closes this engine.
     * <p>
     * Closing an engine will also result in all {@link HttpClient} and {@link HttpServer} instances created by
     * this engine being closed.
     */
    Future<Void> close();

    /**
     * @return a {@link Future} that will be notified when this {@link HttpEngine} is closed
     */
    Future<Void> closeFuture();
}
