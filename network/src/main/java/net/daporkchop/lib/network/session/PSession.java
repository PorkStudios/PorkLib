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
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.Reliability;
import net.daporkchop.lib.network.util.SendFlags;
import net.daporkchop.lib.network.util.TransportEngineHolder;

import java.io.IOException;
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
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and no additional
     * flags.
     *
     * @see #send(Object, int, Reliability, Priority, int)
     */
    default Future<Void> send(@NonNull Object message)  {
        return this.send(message, 0, this.fallbackReliability(), Priority.NORMAL, 0);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#FLUSH} flag.
     *
     * @see #send(Object, int, Reliability, Priority, int)
     */
    default Future<Void> sendFlush(@NonNull Object message)  {
        return this.send(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#SYNC} flag.
     *
     * @see #send(Object, int, Reliability, Priority, int)
     */
    default Future<Void> sendNow(@NonNull Object message)  {
        return this.send(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.SYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * {@link SendFlags#ASYNC} flag.
     *
     * @see #send(Object, int, Reliability, Priority, int)
     */
    default Future<Void> sendAsync(@NonNull Object message)  {
        return this.send(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.ASYNC);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#SYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #send(Object, int, Reliability, Priority, int)
     */
    default Future<Void> sendFlushNow(@NonNull Object message)  {
        return this.send(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.SYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint on channel 0, using this session's fallback reliability level, {@link Priority#NORMAL}, and the
     * following flags:
     * - {@link SendFlags#ASYNC}
     * - {@link SendFlags#FLUSH}
     *
     * @see #send(Object, int, Reliability, Priority, int)
     */
    default Future<Void> sendFlushAsync(@NonNull Object message)  {
        return this.send(message, 0, this.fallbackReliability(), Priority.NORMAL, SendFlags.ASYNC | SendFlags.FLUSH);
    }

    /**
     * Sends a message to the remote endpoint.
     *
     * @param message     the message to be sent
     * @param channel     the id of the channel to send the message on
     * @param reliability the reliability that the message is to be sent with. Some transport engines may ignore this
     * @param priority    the priority that the message is to be sent with. Some transport engines may ignore this
     * @param flags       additional flags that the message is to be sent with. Valid are any fields from {@link net.daporkchop.lib.network.util.SendFlags}, and
     *                    flags may also be ORed together
     * @return a {@link Future} that may be used to monitor the message as it is sent. Depending on the flags that are set (or if none are set), this may return {@code null}
     */
    Future<Void> send(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags);

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
