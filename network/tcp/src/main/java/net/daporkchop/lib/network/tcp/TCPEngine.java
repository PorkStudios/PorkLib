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

package net.daporkchop.lib.network.tcp;

import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PMulti;
import net.daporkchop.lib.network.endpoint.PMultiClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.Pp2pEndpoint;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.tcp.endpoint.TCPClient;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.netty.NettyEngine;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * An implementation of {@link TransportEngine} for the TCP/IP transport protocol.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class TCPEngine extends NettyEngine {
    public static final ChannelOption<Boolean> USE_SSL = ChannelOption.valueOf("PORKLIB_TCP_USE_SSL");

    protected static final Collection<Reliability> RELIABILITIES = Collections.singleton(Reliability.RELIABLE_ORDERED);

    @SuppressWarnings("unchecked")
    public static <B extends Builder<B>> B builder() {
        return (B) new Builder<B>();
    }

    @SuppressWarnings("unchecked")
    public static <B extends Builder<B>> B builder(@NonNull Framer framer) {
        return (B) new Builder<B>(framer);
    }

    public static TCPEngine of(@NonNull Framer framer) {
        return new TCPEngine(framer);
    }

    protected final Framer framer;

    protected TCPEngine(@NonNull Builder<?> builder) {
        this(builder.clientOptions(), builder.serverOptions(), builder.framer());
    }

    protected TCPEngine(@NonNull Framer framer) {
        this.framer = framer;
    }

    protected TCPEngine(@NonNull Map<ChannelOption, Object> clientOptions, @NonNull Map<ChannelOption, Object> serverOptions, @NonNull Framer framer) {
        super(
                Collections.unmodifiableMap(clientOptions),
                Collections.unmodifiableMap(serverOptions)
        );

        this.framer = framer;
    }

    @Override
    public PClient createClient(@NonNull ClientBuilder builder) {
        return new TCPClient(builder);
    }

    @Override
    public PMultiClient createMultiClient() {
        return null;
    }

    @Override
    public PServer createServer(@NonNull InetSocketAddress bindAddress) {
        return null;
    }

    @Override
    public PMulti createMulti(@NonNull InetSocketAddress bindAddress) {
        return null;
    }

    @Override
    public Pp2pEndpoint createP2P(@NonNull InetSocketAddress bindAddress) {
        return null;
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return RELIABILITIES;
    }

    @Override
    public boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return reliability == Reliability.RELIABLE_ORDERED;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class Builder<Impl extends Builder<Impl>> extends NettyEngine.Builder<Impl, TCPEngine> {
        @NonNull
        protected Framer framer;

        @SuppressWarnings("unchecked")
        public Impl framer(@NonNull Framer framer) {
            this.framer = framer;
            return (Impl) this;
        }

        @SuppressWarnings("unchecked")
        public Impl useSSL() {
            return this.option(TCPEngine.USE_SSL, true);
        }

        @Override
        public TCPEngine build() {
            if (this.framer == null) {
                this.framer = new Framer.DefaultFramer();
            }
            return new TCPEngine(this);
        }
    }
}
