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
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.conn.Connection;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.Collection;

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
     * Send a packet over this channel.
     *
     * @param message     the packet to send
     * @param blocking    whether or not to block the invoking thread until the underlying network channel has been flushed
     * @param callback    a function to run after the underlying network channel has been flushed. if {@code null}, this
     *                    parameter is ignored.
     * @param reliability the desired channel reliability. depending on the underlying network protocol, this reliability may be
     *                    enforced for all packet, used merely as a default, or ignored entirely.
     */
    void send(@NonNull Object message, boolean blocking, Void callback, @NonNull Reliability reliability);

    /**
     * Sends a raw packet over this channel
     *
     * @param data        the data to send. this buffer will not be released by this, even though the reference count may change it
     *                    will be set to the original state after the packet is written.
     * @param id          the full id of the packet. See {@link net.daporkchop.lib.network.packet.PacketRegistry#combine(short, short)}
     * @param blocking    whether or not to block the invoking thread until the underlying network channel has been flushed
     * @param callback    a function to run after the underlying network channel has been flushed. if {@code null}, this
     *                    parameter is ignored.
     * @param reliability the desired channel reliability. depending on the underlying network protocol, this reliability may be
     *                    enforced for all packet, used merely as a default, or ignored entirely.
     */
    void send(@NonNull ByteBuf data, int id, boolean blocking, Void callback, @NonNull Reliability reliability);

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
     * <p>
     * For implementations that only support a few reliability modes (e.g. SCTP), the reliability will only be respected if it
     * is supported.
     *
     * @return whether or not reliability is respected
     * @see #supportedReliabilities()
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

    /**
     * Closes this channel.
     * <p>
     * This will only close the channel, not the connection.
     * <p>
     * To close the actual connection, use {@link Connection#closeConnection()} (a dummy connection can be obtained from
     * {@link #getConnection(Class)} with {@link net.daporkchop.lib.network.pork.PorkProtocol}.class as a parameter)
     */
    default void close() {
        this.close(true);
    }

    void close(boolean notifyRemote);

    /**
     * Checks if this is the default channel
     *
     * @return whether or not this is the default channel
     */
    default boolean isDefaultChannel() {
        return this.getConnection().getDefaultChannel() == this;
    }

    /**
     * Checks if this is the control channel
     *
     * @return whether or not this is the control channel
     */
    default boolean isControlChannel() {
        return this.getConnection().getControlChannel() == this;
    }

    /**
     * Gets a list of all {@link Reliability} modes supported by this channel
     *
     * @return all reliabilities supported by this channel
     */
    Collection<Reliability> supportedReliabilities();

    /**
     * Gets this channel's endpoint
     *
     * @param <E> convenience cast
     * @return this channel's endpoint
     */
    default <E extends Endpoint> E getEndpoint() {
        return this.getConnection().getEndpoint();
    }

    /**
     * Get this channel's connection
     *
     * @return this channel's connection
     */
    UnderlyingNetworkConnection getConnection();

    //
    //
    // Convenience methods
    //
    //

    // message send methods

    default void send(@NonNull Object message, boolean blocking, Void callback) {
        this.send(message, blocking, callback, this.getReliability());
    }

    default void send(@NonNull Object message, boolean blocking, @NonNull Reliability reliability) {
        if (this.isReliabilityEnforced() && reliability != this.getReliability()) {
            throw new IllegalArgumentException(String.format("Invalid reliability: %s (channel setting: %s)", reliability.name(), this.getReliability().name()));
        }
        this.send(message, blocking, null, reliability);
    }

    default void send(@NonNull Object message, Void callback, @NonNull Reliability reliability) {
        if (this.isReliabilityEnforced() && reliability != this.getReliability()) {
            throw new IllegalArgumentException(String.format("Invalid reliability: %s (channel setting: %s)", reliability.name(), this.getReliability().name()));
        }
        this.send(message, false, callback, reliability);
    }

    default void send(@NonNull Object message, boolean blocking) {
        this.send(message, blocking, null, this.getReliability());
    }

    default void send(@NonNull Object message, @NonNull Reliability reliability) {
        if (this.isReliabilityEnforced() && reliability != this.getReliability()) {
            throw new IllegalArgumentException(String.format("Invalid reliability: %s (channel setting: %s)", reliability.name(), this.getReliability().name()));
        }
        this.send(message, false, null, reliability);
    }

    default void send(@NonNull Object message, Void callback) {
        this.send(message, false, callback, this.getReliability());
    }

    default void send(@NonNull Object message) {
        this.send(message, false, null, this.getReliability());
    }

    //raw packet w. protocol send methods

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, boolean blocking, Void callback, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), blocking, callback, this.getReliability());
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, boolean blocking, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), blocking, null, this.getReliability());
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, Void callback, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), false, callback, this.getReliability());
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, @NonNull Reliability reliability, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), false, null, reliability);
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, boolean blocking, @NonNull Reliability reliability, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), blocking, null, reliability);
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, Void callback, @NonNull Reliability reliability, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), false, callback, reliability);
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), false, null, this.getReliability());
    }

    default <C extends UserConnection> void send(@NonNull ByteBuf data, short id, boolean blocking, Void callback, @NonNull Reliability reliability, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
        this.send(data, PacketRegistry.combine(this.getEndpoint().getPacketRegistry().getProtocolId(protocolClass), id), blocking, callback, reliability);
    }

    //raw packet w/o. protocol send methods

    default void send(@NonNull ByteBuf data, int id, boolean blocking, Void callback) {
        this.send(data, id, blocking, callback, this.getReliability());
    }

    default void send(@NonNull ByteBuf data, int id, boolean blocking) {
        this.send(data, id, blocking, null, this.getReliability());
    }

    default void send(@NonNull ByteBuf data, int id, Void callback) {
        this.send(data, id, false, callback, this.getReliability());
    }

    default void send(@NonNull ByteBuf data, int id, @NonNull Reliability reliability) {
        this.send(data, id, false, null, reliability);
    }

    default void send(@NonNull ByteBuf data, int id, boolean blocking, @NonNull Reliability reliability) {
        this.send(data, id, blocking, null, reliability);
    }

    default void send(@NonNull ByteBuf data, int id, Void callback, @NonNull Reliability reliability) {
        this.send(data, id, false, callback, reliability);
    }

    default void send(@NonNull ByteBuf data, int id) {
        this.send(data, id, false, null, this.getReliability());
    }

    //other methods

    default boolean isDefault() {
        return this.getId() == 0;
    }
}
