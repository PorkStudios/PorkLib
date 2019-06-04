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

package net.daporkchop.lib.network.transport;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Used for actually constructing connections, channels etc.
 *
 * @author DaPorkchop_
 */
public interface TransportEngine {
    //endpoint creation methods

    /**
     * Creates a new client that will connect to the given address.
     *
     * @param builder the builder to use
     * @return the newly created client
     */
    <S extends AbstractUserSession<S>> PClient<S> createClient(@NonNull ClientBuilder<S> builder);

    /**
     * Creates a new multi-client.
     * @return the newly created multi-client
     */
    //<S extends AbstractUserSession<S>> PMultiClient<S> createMultiClient();

    /**
     * Creates a new server that will bind to the given address.
     *
     * @param builder the builder to use
     * @return the newly created server
     */
    <S extends AbstractUserSession<S>> PServer<S> createServer(@NonNull ServerBuilder<S> builder);

    /**
     * Creates a new multi endpoint that will bind to the given address.
     * @param bindAddress the address that the new multi endpoint will be bound to
     * @return the newly created multi endpoint
     */
    //<S extends AbstractUserSession<S>> PMulti<S> createMulti(@NonNull InetSocketAddress bindAddress);

    /**
     * Creates a new p2p endpoint that will bind to the given address.
     * @param bindAddress the address that the new p2p endpoint will be bound to
     * @return the newly created p2p endpoint
     */
    //<S extends AbstractUserSession<S>> Pp2pEndpoint<S> createP2P(@NonNull InetSocketAddress bindAddress);

    //reliability methods

    /**
     * Gets all reliability levels supported by this engine.
     *
     * @return all reliability levels supported by this engine
     */
    Collection<Reliability> supportedReliabilities();

    /**
     * Checks whether or not a specific reliability level is supported by this engine.
     *
     * @param reliability the reliability level to check
     * @return whether or not the given reliability level is supported by this engine
     */
    default boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return this.supportedReliabilities().contains(reliability);
    }

    default boolean isBinary(@NonNull Object msg) {
        return msg instanceof byte[] || msg instanceof ByteBuffer;
    }

    /**
     * Attempts to wrap a message into a {@link DataIn} for binary reading.
     * @param msg the message to wrap
     * @return the wrapped message, or {@code null} if the message does not contain binary data
     */
    default DataIn attemptRead(@NonNull Object msg) {
        DataIn in = null;
        if (msg instanceof byte[])  {
            in = DataIn.wrap(ByteBuffer.wrap((byte[]) msg));
        } else if (msg instanceof ByteBuffer)   {
            in = DataIn.wrap((ByteBuffer) msg);
        }
        return in;
    }
}
