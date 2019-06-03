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
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.util.CloseableFuture;

import java.io.IOException;

/**
 * A session represents a single connection between two endpoints.
 *
 * @author DaPorkchop_
 */
public interface PSession<Impl extends PSession<Impl, S>, S extends AbstractUserSession<S>> extends CloseableFuture, Reliable<Impl> {
    /**
     * @return the local endpoint associated with this session
     */
    <E extends PEndpoint<E, S>> E endpoint();

    /**
     * @return this session's pipeline
     */
    Pipeline<S> dataPipeline();

    /**
     * Gets an open channel on this session with a given id.
     *
     * @param id the id of the channel to get
     * @return a channel with the given id
     */
    PChannel<S> channel(int id);

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
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
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
     *
     * @param packet the packet to be sent
     * @return this session
     */
    default Impl send(@NonNull Object packet) {
        return this.send(packet, this.fallbackReliability());
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet, Reliability reliability) {
        return this.send(packet, reliability).flushBuffer();
    }

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet the packet to be sent
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet) {
        return this.sendFlush(packet, this.fallbackReliability());
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel   the id of the channel that the packet will be sent on
     * @return this session
     */
    Impl send(@NonNull Object packet, Reliability reliability, int channel);

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     *
     * @param packet    the packet to be sent
     * @param channel the id of the channel that the packet will be sent on
     * @return this session
     */
    default Impl send(@NonNull Object packet, int channel) {
        return this.send(packet, this.fallbackReliability(), channel);
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel.
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel   the id of the channel that the packet will be sent on
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet, Reliability reliability, int channel)  {
        return this.send(packet, reliability, channel).flushBuffer();
    }

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet    the packet to be sent
     * @param channel the id of the channel that the packet will be sent on
     * @return this session
     */
    default Impl sendFlush(@NonNull Object packet, int channel) {
        return this.sendFlush(packet, this.fallbackReliability(), channel);
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
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
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet the packet to be sent
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendAsync(@NonNull Object packet) {
        return this.sendAsync(packet, this.fallbackReliability());
    }

    /**
     * Sends a single packet to the remote endpoint.
     * <p>
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability)    {
        return this.sendAsync(packet, reliability).addListener(v -> this.flushBuffer());
    }

    /**
     * Sends a single packet to the remote endpoint, using this session's default reliability level.
     * <p>
     * All packets sent using these methods will be sent on channel 0, which may not be closed.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet the packet to be sent
     * @return a future which may be used to track the packet as it is sent
     */
    default Future<Void> sendFlushAsync(@NonNull Object packet) {
        return this.sendFlushAsync(packet, this.fallbackReliability());
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
     * @param channel   the id of the channel that the packet will be sent on
     * @return a future which may be used to track the packet as it is sent
     */
    Future<Void> sendAsync(@NonNull Object packet, Reliability reliability, int channel);

    /**
     * Sends a single packet to the remote endpoint over a specific channel, using this channel's fallback reliability
     * level.
     * <p>
     * This method is non-blocking, and returns a future that may be used to track the packet as it is sent.
     *
     * @param packet    the packet to be sent
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
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet      the packet to be sent
     * @param reliability the reliability that the packet is to be sent with. If {@code null} or unsupported by this
     *                    session's transport protocol, this session's fallback reliability level will be
     *                    used (see {@link #fallbackReliability()})
     * @param channel   the id of the channel that the packet will be sent on
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
     *
     * The send buffer will also be flushed after this operation.
     *
     * @param packet    the packet to be sent
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
     * Closes this session, blocking until it is closed.
     */
    @Override
    void closeNow();
}
