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

package net.daporkchop.lib.network.conn;

import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.net.InetSocketAddress;

/**
 * A connection between two {@link Endpoint}s
 * <p>
 * To be more precise, a connection between exactly one {@link net.daporkchop.lib.network.endpoint.server.Server} and
 * one {@link net.daporkchop.lib.network.endpoint.client.Client}. This applies even in p2p scenarios.
 *
 * @author DaPorkchop_
 */
public interface Connection {
    //
    //
    // Actual methods to implement
    //
    //

    /**
     * Get the local endpoint for this connection
     *
     * @param <E> convenience cast to a desired {@link Endpoint} type
     * @return the local endpoint for this connection
     */
    <E extends Endpoint> E getEndpoint();

    /**
     * Close this connection
     *
     * @param reason the reason for closing
     */
    void closeConnection(String reason);

    /**
     * Checks if the connection is open
     *
     * @return whether or not the connection is open
     */
    boolean isConnected();

    /**
     * Send a packet to the remote endpoint
     *
     * @param message  the packet to send
     * @param blocking whether or not to block the invoking thread until the underlying network channel has been flushed
     * @param callback a function to run after the underlying network channel has been flushed. if {@code null}, this
     *                 parameter is ignored.
     */
    void send(@NonNull Object message, boolean blocking, Void callback);

    /**
     * Get the network address of the remote endpoint
     *
     * @return the network address of the remote endpoint
     */
    InetSocketAddress getAddress();

    /**
     * Open a new {@link Channel} on this connection
     *
     * @param reliability the desired channel reliability. depending on the underlying network protocol, this reliability may be
     *                    enforced for all packets, used merely as a default, or ignored entirely.
     * @return a new {@link Channel}
     */
    Channel openChannel(@NonNull Reliability reliability);

    /**
     * Gets a currently open {@link Channel} on this connection
     *
     * @param id the id of the desired {@link Channel}
     * @return the channel with the given id, or {@code null} if no such channel was found.
     * @see Channel#getId()
     */
    Channel getOpenChannel(int id);

    /**
     * Gets this connection's default channel.
     * <p>
     * For most implementations, this will return the same channel that PorkLib network uses for sending control messages, and
     * should therefore be avoided in applications where performance is critical.
     *
     * @return this connection's default channel
     */
    default Channel getDefaultChannel() {
        return this.getOpenChannel(0);
    }

    /**
     * Gets this connection's control channel.
     * <p>
     * !!! WARNING !!!
     * This is the channel that PorkLib network sends internal packets on! Do not use unless you know what you're doing!
     *
     * @return this connection's control channel
     */
    default Channel getControlChannel() {
        return this.getDefaultChannel();
    }

    //
    //
    // Convenience methods
    //
    //
    default void send(@NonNull Object... messages) {
        for (Object message : messages) {
            this.send(message);
        }
    }

    default void send(@NonNull Object message) {
        this.send(message, false, null);
    }

    default void send(@NonNull Object message, Void callback) {
        this.send(message, false, callback);
    }

    default void sendBlocking(@NonNull Object message) {
        this.send(message, true, null);
    }

    default void sendBlocking(@NonNull Object message, Void callback) {
        this.send(message, true, callback);
    }

    default void send(@NonNull Object message, boolean blocking) {
        this.send(message, blocking, null);
    }

    default void closeConnection() {
        this.closeConnection(null);
    }

    default Channel openChannel() {
        return this.openChannel(Reliability.RELIABLE_ORDERED);
    }
}
