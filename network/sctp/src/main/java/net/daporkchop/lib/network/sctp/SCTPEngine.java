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

package net.daporkchop.lib.network.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.sctp.SctpMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.netty.NettyEngine;
import net.daporkchop.lib.network.sctp.endpoint.SCTPClient;
import net.daporkchop.lib.network.sctp.endpoint.SCTPServer;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.transport.TransportEngine;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

/**
 * An implementation of {@link TransportEngine} for the SCTP/IP transport protocol.
 * <p>
 * Default pipeline layout:
 * "sctp_packer" => {@link net.daporkchop.lib.network.sctp.pipeline.SCTPPacker}
 * "protocol"    => {@link net.daporkchop.lib.network.netty.pipeline.NettyDataCodec} (only if endpoint protocol is {@link net.daporkchop.lib.network.protocol.DataProtocol})
 *
 * @author DaPorkchop_
 */
public class SCTPEngine extends NettyEngine {
    protected static final Collection<Reliability> RELIABILITIES = EnumSet.of(Reliability.RELIABLE, Reliability.RELIABLE_ORDERED);

    @SuppressWarnings("unchecked")
    public static <B extends Builder<B>> B builder() {
        return (B) new Builder<B>();
    }

    public static SCTPEngine defaultInstance() {
        return new SCTPEngine(
                Collections.emptyMap(),
                Collections.emptyMap(),
                null,
                true
        );
    }

    protected SCTPEngine(@NonNull Builder<?> builder) {
        super(
                builder.clientOptions(),
                builder.serverOptions(),
                builder.group(),
                builder.autoShutdownGroup()
        );
    }

    protected SCTPEngine(@NonNull Map<ChannelOption, Object> clientOptions, @NonNull Map<ChannelOption, Object> serverOptions, EventLoopGroup group, boolean autoShutdownGroup) {
        super(
                Collections.unmodifiableMap(clientOptions),
                Collections.unmodifiableMap(serverOptions),
                group,
                group == null || autoShutdownGroup
        );
    }

    @Override
    public <S extends AbstractUserSession<S>> PClient<S> createClient(@NonNull ClientBuilder<S> builder) {
        return new SCTPClient<>(builder);
    }

    @Override
    public <S extends AbstractUserSession<S>> PServer<S> createServer(@NonNull ServerBuilder<S> builder) {
        return new SCTPServer<>(builder);
    }

    @Override
    public boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return reliability == Reliability.RELIABLE || reliability == Reliability.RELIABLE_ORDERED;
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return RELIABILITIES;
    }

    @Override
    public boolean isBinary(@NonNull Object object) {
        return object instanceof ByteBuf || object instanceof SctpMessage;
    }

    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class Builder<Impl extends Builder<Impl>> extends NettyEngine.Builder<Impl, SCTPEngine> {
        @Override
        protected SCTPEngine doBuild() {
            return new SCTPEngine(this);
        }
    }
}
