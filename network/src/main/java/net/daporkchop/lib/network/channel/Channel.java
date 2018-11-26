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

import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.util.reliability.Reliability;

/**
 * A channel opened over an existing {@link net.daporkchop.lib.network.conn.Connection}.
 * <p>
 * Channels are of no benefit over a TCP connection (actually there's slightly more overhead over TCP), however they are useful
 * over UDP-based protocols such as RakNet for e.g. having separate RELIABLE_ORDERED sequences that don't affect each other.
 *
 * @author DaPorkchop_
 */
public interface Channel {
    //
    //
    // Actual methods to implement
    //
    //

    /**
     * Send a {@link Packet} over this channel.
     *
     * @param packet      the packet to send
     * @param blocking    whether or not to block the invoking thread until the underlying network channel has been flushed
     * @param callback    a function to run after the underlying network channel has been flushed. if {@code null}, this
     *                    parameter is ignored.
     * @param reliability the desired channel reliability. depending on the underlying network protocol, this reliability may be
     *                    enforced for all packet, used merely as a default, or ignored entirely.
     */
    void send(@NonNull Packet packet, boolean blocking, Void callback, @NonNull Reliability reliability);

    /**
     * Get this channel's reliability.
     * <p>
     * Depending on the underlying network protocol, this may not be the same reliability
     * as was passed to {@link net.daporkchop.lib.network.conn.Connection#openChannel(Reliability)}
     *
     * @return the {@link Reliability} of this channel
     */
    Reliability getReliability();

    /**
     * Checks if this channel's reliability is enforced.
     * <p>
     * If the reliability is enforced, messages will only be able to be sent with this channel's reliability
     *
     * @return whether or not reliability is enforced
     * @see Channel#getReliability()
     */
    default boolean isReliabilityEnforced() {
        return false;
    }

    /**
     * Checks if this channel respects the reliability of packets.
     * <p>
     * If {@code false}, all packets will be sent with the reliability obtained from {@link Channel#getReliability()}, even
     * if the user requests something else.
     *
     * @return whether or not reliability is respected
     */
    default boolean isReliabilityRespected() {
        return true;
    }

    /**
     * Get the id of this channel
     *
     * @return this channel's id
     */
    int getId();

    /**
     * Gets a {@link UserConnection} from this channel's underlying connection.
     *
     * @param protocolClass the class of the protocol whose connection is to be returned
     * @param <C>           the type of the connection to get
     * @return the connection for the protocol which was given as a parameter, or null if the protocol isn't registered to this connection
     */
    <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass);

    //
    //
    // Convenience methods
    //
    //
    default void send(@NonNull Packet packet, boolean blocking, Void callback) {
        this.send(packet, blocking, callback, this.getReliability());
    }

    default void send(@NonNull Packet packet, boolean blocking, @NonNull Reliability reliability) {
        if (this.isReliabilityEnforced() && reliability != this.getReliability()) {
            throw new IllegalArgumentException(String.format("Invalid reliability: %s (channel setting: %s)", reliability.name(), this.getReliability().name()));
        }
        this.send(packet, blocking, null, reliability);
    }

    default void send(@NonNull Packet packet, Void callback, @NonNull Reliability reliability) {
        if (this.isReliabilityEnforced() && reliability != this.getReliability()) {
            throw new IllegalArgumentException(String.format("Invalid reliability: %s (channel setting: %s)", reliability.name(), this.getReliability().name()));
        }
        this.send(packet, false, callback, reliability);
    }

    default void send(@NonNull Packet packet, boolean blocking) {
        this.send(packet, blocking, null, this.getReliability());
    }

    default void send(@NonNull Packet packet, @NonNull Reliability reliability) {
        if (this.isReliabilityEnforced() && reliability != this.getReliability()) {
            throw new IllegalArgumentException(String.format("Invalid reliability: %s (channel setting: %s)", reliability.name(), this.getReliability().name()));
        }
        this.send(packet, false, null, reliability);
    }

    default void send(@NonNull Packet packet, Void callback) {
        this.send(packet, false, callback, this.getReliability());
    }

    default void send(@NonNull Packet packet) {
        this.send(packet, false, null, this.getReliability());
    }
}
