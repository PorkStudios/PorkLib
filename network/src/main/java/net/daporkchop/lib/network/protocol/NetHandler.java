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

package net.daporkchop.lib.network.protocol;

import net.daporkchop.lib.network.endpoint.AbstractSession;

/**
 * Handles events for endpoints that aren't necasarily session-specific or affect
 * connections themselves
 *
 * @author DaPorkchop_
 */
public interface NetHandler<S extends AbstractSession> {
    /**
     * Invoked when a connection is completed (i.e. connection established and handshake complete)
     *
     * @param server  whether or not this event was invoked on the server side
     * @param session the session that has finished connecting
     */
    default void onConnect(boolean server, S session) {
    }

    /**
     * Invoked after a connection has been closed.
     *
     * @param server  whether or not this event was invoked on the server
     * @param session the session that disconnected
     * @param reason  the reason for disconnecting. if null, this means that the connection was
     *                closed at a network level (i.e. not safely with a {@link net.daporkchop.lib.network.protocol.encapsulated.packet.DisconnectPacket}
     */
    default void onDisconnect(boolean server, S session, String reason) {
    }

    /**
     * A handler that does nothing
     */
    final class NoopHandler<S extends AbstractSession> implements NetHandler<S> {
    }
}
