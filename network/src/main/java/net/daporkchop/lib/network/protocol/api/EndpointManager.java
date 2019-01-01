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

package net.daporkchop.lib.network.protocol.api;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.channel.ServerChannel;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.builder.AbstractBuilder;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.UserProtocol;

import java.util.stream.Stream;

/**
 * Manages a specific endpoint
 *
 * @author DaPorkchop_
 * @see ProtocolManager
 */
public interface EndpointManager<E extends Endpoint, B extends AbstractBuilder<E, B>> {
    /**
     * Closes the endpoint, removing all connected sessions
     */
    void close();

    /**
     * Checks if this endpoint is running
     *
     * @return whether or not this endpoint is running
     */
    boolean isRunning();

    default boolean isClosed() {
        return !this.isRunning();
    }

    /**
     * Starts this endpoint
     *
     * @param builder  the {@link AbstractBuilder} the builder used to initiate the endpoint
     * @param endpoint the actual endpoint object
     */
    void start(@NonNull B builder, @NonNull E endpoint);

    /**
     * Manages a server
     */
    interface ServerEndpointManager extends EndpointManager<Server, ServerBuilder> {
        /**
         * Gets a list of all currently open connections to this server
         *
         * @param protocolClass the class of the protocol whose connections are to be returned
         * @param <C>           the connection type
         * @return all currently open connections to this server
         */
        default <C extends UserConnection> Stream<C> getConnections(Class<? extends UserProtocol<C>> protocolClass) {
            return this.getChannel().getUnderlyingNetworkConnectionsAsStream().map(connection -> connection.getUserConnection(protocolClass));
        }

        /**
         * Send a message to all connected clients on the default channel
         *
         * @param message  the packet to send
         * @param blocking whether or not this method will block the invoking thread until the packet has been flushed
         */
        default void broadcast(@NonNull Object message, boolean blocking) {
            this.getChannel().broadcast(message, blocking);
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
            this.getChannel().broadcast(data, id, protocolClass);
        }

        /**
         * Closes the endpoint, removing all connected sessions
         *
         * @param reason the reason for disconnecting
         * @see EndpointManager#close()
         */
        default void close(String reason) {
            this.getChannel().close(reason);
        }

        @Override
        default void close() {
            this.getChannel().close(null);
        }

        /**
         * Gets this server's channel
         *
         * @return this server's channel
         */
        ServerChannel getChannel();
    }

    /**
     * Manages a client
     */
    interface ClientEndpointManager extends EndpointManager<Client, ClientBuilder> {
        /**
         * Gets the client's connection
         *
         * @param protocolClass the class of the protocol whose connection is to be returned
         * @param <C>           the connection type
         * @return the client's connection
         */
        <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass);

        /**
         * Send a packet to the server on the default channel
         *
         * @param message  the packet to send
         * @param blocking whether or not this method will block the invoking thread until the packet has been flushed
         * @param callback a function to run once the packet has been flushed
         */
        void send(@NonNull Object message, boolean blocking, Void callback);
    }
}
