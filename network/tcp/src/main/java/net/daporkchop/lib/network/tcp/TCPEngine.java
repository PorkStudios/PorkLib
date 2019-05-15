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
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.tcp.endpoint.TCPClient;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.netty.NettyEngine;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * An implementation of {@link TransportEngine} for the TCP/IP transport protocol.
 *
 * Default pipeline layout:
 * "tcp_framer" =>
 * "tcp_handler" => {@link net.daporkchop.lib.network.tcp.netty.TCPHandler}
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class TCPEngine extends NettyEngine {
    protected static final Collection<Reliability> RELIABILITIES = Collections.singleton(Reliability.RELIABLE_ORDERED);

    @SuppressWarnings("unchecked")
    public static <B extends Builder<B>> B builder() {
        return (B) new Builder<B>();
    }

    protected TCPEngine(@NonNull Builder<?> builder) {
        super(
                builder.clientOptions(),
                builder.serverOptions(),
                builder.group(),
                builder.autoShutdownGroup()
        );
    }

    protected TCPEngine(@NonNull Map<ChannelOption, Object> clientOptions, @NonNull Map<ChannelOption, Object> serverOptions, EventLoopGroup group, boolean autoShutdownGroup) {
        super(
                Collections.unmodifiableMap(clientOptions),
                Collections.unmodifiableMap(serverOptions),
                group,
                group == null || autoShutdownGroup
        );
    }

    @Override
    public <S extends AbstractUserSession<S>> PClient<S> createClient(@NonNull ClientBuilder<S> builder) {
        return new TCPClient<>(builder);
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
        @Override
        protected TCPEngine doBuild() {
            return new TCPEngine(this);
        }
    }
}
