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

package net.daporkchop.lib.http.client;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;

/**
 * Handles events that are fired while processing a {@link ClientHttpSession}.
 * <p>
 * If an exception is thrown by any method in a handler, the session will be closed regardless of connection
 * state and marked as failed with the given exception.
 *
 * @author DaPorkchop_
 */
public interface ClientHttpHandler {
    /**
     * Fired when this handler is added to the given session.
     *
     * @param session the {@link ClientHttpSession} that this handler was added to
     */
    void added(@NonNull ClientHttpSession session) throws Exception;

    /**
     * Fired when this handler is removed from the given session.
     *
     * @param session the {@link ClientHttpSession} that this handler was removed from
     */
    void removed(@NonNull ClientHttpSession session) throws Exception;

    /**
     * Fired when the HTTP headers have been completely sent to the remote server.
     *
     * @param session the {@link ClientHttpSession} which is active
     */
    void headersWritten(@NonNull ClientHttpSession session) throws Exception;

    /**
     * Fired when the HTTP request's body has been completely sent to the remote server.
     * <p>
     * This handler will only be fired for HTTP methods that have a request body (such as POST).
     *
     * @param session the {@link ClientHttpSession} which is active
     */
    void bodyWritten(@NonNull ClientHttpSession session) throws Exception;

    /**
     * Fired when the HTTP headers have been completely received from the remote server.
     *
     * @param session the {@link ClientHttpSession} which is active
     */
    void headersRead(@NonNull ClientHttpSession session) throws Exception;

    /**
     * Fired multiple times whenever data could be received from the remote server.
     *
     * @param session the {@link ClientHttpSession} which is active
     * @param data    a {@link ByteBuf} containing the data received from the server. This is not guaranteed to be
     *                any specific size, although it will never be empty.
     */
    void bodyRead(@NonNull ClientHttpSession session, @NonNull ByteBuf data) throws Exception;

    /**
     * Fired when the given {@link ClientHttpSession} has been closed.
     *
     * @param session the {@link ClientHttpSession} which was closed
     */
    void closed(@NonNull ClientHttpSession session) throws Exception;
}
