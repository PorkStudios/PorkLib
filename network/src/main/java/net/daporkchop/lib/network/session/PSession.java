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

package net.daporkchop.lib.network.session;

import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.util.CloseableFuture;
import net.daporkchop.lib.network.util.Reliability;
import net.daporkchop.lib.network.util.TransportEngineHolder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collection;

/**
 * A session represents a single connection between two endpoints.
 *
 * @author DaPorkchop_
 */
public interface PSession<Impl extends PSession<Impl, S>, S extends AbstractUserSession<S>> extends CloseableFuture, TransportEngineHolder {
    /**
     * @return the local endpoint associated with this session
     */
    <E extends PEndpoint<E, S>> E endpoint();

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return this session
     */
    Impl send(@NonNull Object packet, Reliability reliability);

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     *
     * @param packet the packet to be sent
     * @return this session
     */
    default Impl send(@NonNull Object packet) {
        return this.send(packet, this.fallbackReliability(), 0);
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet, Reliability reliability) {
        return this.send(packet, reliability, 0).flushBuffer();
    }

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet the packet to be sent
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet) {
        return this.sendFlush(packet, this.fallbackReliability(), 0);
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel     the id of the channel that the packet will be sent on
     * @return this session
     */
    Impl send(@NonNull Object packet, Reliability reliability, int channel);

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     *
     * @param packet  the packet to be sent
     * @param channel the id of the channel that the packet will be sent on
     * @return this session
     */
    default Impl send(@NonNull Object packet, int channel) {
        return this.send(packet, this.fallbackReliability(), channel);
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel     the id of the channel that the packet will be sent on
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet, Reliability reliability, int channel) {
        return this.send(packet, reliability, channel).flushBuffer();
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet  the packet to be sent
     * @param channel the id of the channel that the packet will be sent on
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet, int channel) {
        return this.sendFlush(packet, this.fallbackReliability(), channel);
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return a future which may be used to track the packet as it is sent
     */
    Future<Void> sendAsync(@NonNull Object packet, Reliability reliability);

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet the packet to be sent
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendAsync(@NonNull Object packet) {
        return this.sendAsync(packet, this.fallbackReliability(), 0);
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability) {
        return this.sendAsync(packet, reliability, 0).addListener(v -> this.flushBuffer());
    }

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * All packets sent using these methods will be sent on channel 0.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet the packet to be sent
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFlushAsync(@NonNull Object packet) {
        return this.sendFlushAsync(packet, this.fallbackReliability(), 0);
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel     the id of the channel that the packet will be sent on
     * @return a future which may be used to track the packet as it is sent
     */
    Future<Void> sendAsync(@NonNull Object packet, Reliability reliability, int channel);

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet  the packet to be sent
     * @param channel the id of the channel that the packet will be sent on
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendAsync(@NonNull Object packet, int channel) {
        return this.sendAsync(packet, this.fallbackReliability(), channel);
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel     the id of the channel that the packet will be sent on
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return this.sendAsync(packet, reliability, channel).addListener(v -> this.flushBuffer());
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     * <p>
     * The send buffer will also be flushed after this operation.
     *
     * @param packet  the packet to be sent
     * @param channel the id of the channel that the packet will be sent on
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFlushAsync(@NonNull Object packet, int channel) {
        return this.sendFlushAsync(packet, this.fallbackReliability(), channel);
    }

    /**
     * Gets a {@link DataOut} which may be used to write raw binary data to the session.
     * <p>
     * The data written to the output stream returned by this method will be buffered until the stream is
     * closed or flushed using {@link DataOut#close()}, respectively.
     *
     * @return a {@link DataOut} for writing raw binary to the session
     */
    DataOut writer();

    /**
     * Writes raw binary data to the session.
     *
     * @param callback a function that will actually write data
     * @return this session
     */
    @SuppressWarnings("unchecked")
    default Impl write(@NonNull IOConsumer<DataOut> callback) {
        try (DataOut out = this.writer()) {
            callback.acceptThrowing(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (Impl) this;
    }

    /**
     * Flushes the send buffer for this session.
     * <p>
     * Note that depending on the transport engine, this method may do nothing.
     *
     * @return this session
     */
    Impl flushBuffer();

    /**
     * Gets this channel's fallback reliability level. Packets that are sent without having a specific reliability
     * defined will be sent using this reliability.
     *
     * @return this channel's fallback reliability level
     */
    Reliability fallbackReliability();

    /**
     * Gets this channel's fallback reliability level. Packets that are sent without having a specific reliability
     * defined will be sent using this reliability.
     *
     * @param reliability the new fallback reliability level to use
     * @return this channel's fallback reliability level
     * @throws IllegalArgumentException if the given reliability level is not supported by this channel
     */
    Impl fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException;

    /**
     * @see TransportEngine#supportedReliabilities()
     */
    default Collection<Reliability> supportedReliabilities() {
        return this.transportEngine().supportedReliabilities();
    }

    /**
     * @see TransportEngine#isReliabilitySupported(Reliability)
     */
    default boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return this.transportEngine().isReliabilitySupported(reliability);
    }

    /**
     * @return the {@link Logger} used by this session
     */
    default Logger logger() {
        return this.endpoint().logger();
    }

    /**
     * @return the {@link SocketAddress} of the local endpoint
     */
    SocketAddress localAddress();

    /**
     * @return the {@link SocketAddress} of the remote endpoint
     */
    SocketAddress remoteAddress();

    /**
     * Closes this session, blocking until it is closed.
     */
    @Override
    void closeNow();
}
