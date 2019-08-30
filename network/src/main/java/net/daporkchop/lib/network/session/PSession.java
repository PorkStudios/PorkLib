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

import lombok.NonNull;
import net.daporkchop.lib.binary.io.OldDataOut;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.concurrent.CloseableFuture;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.network.util.TransportEngineHolder;
import net.daporkchop.lib.network.util.group.Sender;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;

/**
 * A session represents a single connection between two endpoints.
 *
 * @author DaPorkchop_
 */
public interface PSession<Impl extends PSession<Impl, S>, S extends AbstractUserSession<S>> extends Sender<Impl>, CloseableFuture, TransportEngineHolder {
    /**
     * @return the local endpoint associated with this session
     */
    <E extends PEndpoint<E, S>> E endpoint();

    /**
     * Gets a {@link OldDataOut} which may be used to write raw binary data to the session.
     * <p>
     * The data written to the output stream returned by this method will be buffered until the stream is
     * closed or flushed using {@link OldDataOut#close()}, respectively.
     *
     * @return a {@link OldDataOut} for writing raw binary to the session
     */
    OldDataOut writer();

    /**
     * Writes raw binary data to the session.
     *
     * @param callback a function that will actually write data
     * @return this session
     */
    @SuppressWarnings("unchecked")
    default Impl write(@NonNull IOConsumer<OldDataOut> callback) {
        try (OldDataOut out = this.writer()) {
            callback.acceptThrowing(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (Impl) this;
    }

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
