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

package net.daporkchop.lib.network.channel;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.api.EndpointManager;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Like {@link Channel}, but for a server
 *
 * @author DaPorkchop_
 */
public interface ServerChannel {
    /**
     * Gets a list of all currently open connections to this server
     *
     * @param protocolClass the class of the protocol whose connections are to be returned
     * @param <C>           the connection type
     * @return all currently open connections to this server
     */
    default <C extends UserConnection> Collection<C> getConnections(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return this.getUnderlyingNetworkConnections().stream()
                .map(connection -> connection.getUserConnection(protocolClass))
                .collect(Collectors.toList());
    }

    /**
     * Get a list of all currently open connections
     *
     * @return a list of all currently open connections
     */
    Collection<UnderlyingNetworkConnection> getUnderlyingNetworkConnections();

    /**
     * Broadcasts a packet to all connections
     *
     * @param message   the packet to send
     * @param blocking whether or not the send operation will be blocking. this will block until the packet has been sent to ALL clients.
     */
    default void broadcast(@NonNull Object message, boolean blocking) {
        this.getUnderlyingNetworkConnectionsAsStream().forEach(connection -> connection.send(message, blocking));
    }

    /**
     * Broadcasts a raw packet to all connections on the default channel
     *
     * @param data          the data to send
     * @param id            the id of the packet
     * @param protocolClass the class of the protocol that the packet should be sent on
     * @param <C>           the protocol's connection type
     */
    default <C extends UserConnection> void broadcast(@NonNull ByteBuf data, short id, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.getUnderlyingNetworkConnectionsAsStream()
                .map(connection -> connection.getUserConnection(protocolClass).getDefaultChannel())
                .forEach(channel -> channel.send(data, id));
    }

    /**
     * Closes the endpoint, removing all connected sessions
     *
     * @param reason the reason for disconnecting
     * @see EndpointManager#close()
     */
    void close(String reason);

    /**
     * Gets this server channel's server
     *
     * @param <S> convenience cast
     * @return this server channel's server
     */
    <S extends Server> S getServer();

    //
    //
    // Convenience methods
    //
    //
    default void broadcast(@NonNull Object... packets) {
        for (Object packet : packets) {
            this.broadcast(packet);
        }
    }

    default void broadcastBlocking(@NonNull Object... packets) {
        for (Object packet : packets) {
            this.broadcastBlocking(packet);
        }
    }

    default void broadcast(@NonNull Object packet) {
        this.broadcast(packet, false);
    }

    default void broadcastBlocking(@NonNull Object packet) {
        this.broadcast(packet, true);
    }

    default Stream<UnderlyingNetworkConnection> getUnderlyingNetworkConnectionsAsStream() {
        return this.getUnderlyingNetworkConnections().stream();
    }
}
