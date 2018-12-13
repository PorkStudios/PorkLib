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

package net.daporkchop.lib.network.protocol.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.builder.AbstractBuilder;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.DisconnectPacket;
import net.daporkchop.lib.network.protocol.api.EndpointManager;
import net.daporkchop.lib.network.protocol.api.ProtocolManager;
import net.daporkchop.lib.network.protocol.netty.NettyServerChannel;

import java.util.function.Consumer;

/**
 * An implementation of {@link ProtocolManager} for the TCP transport protocol.
 * <p>
 * TCP provides a single, bi-directional {@link net.daporkchop.lib.network.util.reliability.Reliability#RELIABLE_ORDERED} channel.
 *
 * @author DaPorkchop_
 */
//TODO: abstract out netty protocol manager stuff
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TcpProtocolManager implements ProtocolManager {
    public static final TcpProtocolManager INSTANCE = new TcpProtocolManager();

    @Override
    public EndpointManager.ServerEndpointManager createServerManager() {
        return new TcpServerManager();
    }

    @Override
    public EndpointManager.ClientEndpointManager createClientManager() {
        return new TcpClientManager();
    }

    @Override
    public boolean areEncryptionSettingsRespected() {
        return true;
    }

    @Override
    public boolean areCompressionSettingsRespected() {
        return true;
    }

    private abstract static class TcpEndpointManager<E extends Endpoint, B extends AbstractBuilder<E, B>> implements EndpointManager<E, B> {
        protected Channel channel;
        protected EventLoopGroup workerGroup;

        @Override
        public void close() {
            if (this.isClosed()) {
                throw new IllegalStateException("already closed!");
            }
            this.channel.flush();
            this.channel.close().syncUninterruptibly();
        }

        @Override
        public boolean isRunning() {
            return this.channel != null && this.channel.isActive();
        }
    }

    private static class TcpServerManager extends TcpEndpointManager<Server, ServerBuilder> implements EndpointManager.ServerEndpointManager {
        private ChannelGroup channels;
        @Getter
        private TcpServerChannel channel;

        @Override
        @SuppressWarnings("unchecked")
        public void start(@NonNull ServerBuilder builder, @NonNull Server server) {
            this.workerGroup = builder.getEventGroup();
            this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(this.workerGroup);
                bootstrap.channelFactory(() -> new WrapperNioServerSocketChannel(server));
                bootstrap.childHandler(new TcpChannelInitializer(server, this.channels::add, this.channels::remove));
                bootstrap.option(ChannelOption.SO_BACKLOG, 256);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

                super.channel = bootstrap.bind(builder.getAddress()).syncUninterruptibly().channel();
                this.channel = new TcpServerChannel(this.channels, server);
            } catch (Throwable t) {
                this.channels.close();
                throw new RuntimeException(t);
            }
        }

        @Override
        public void close() {
            this.channels.close();
            super.close();
        }

        @Override
        public void close(String reason) {
            this.channel.broadcast(new DisconnectPacket(reason), false);
            this.close();
        }

        private class TcpServerChannel extends NettyServerChannel {
            private TcpServerChannel(ChannelGroup channels, Server server) {
                super(channels, server);
            }

            @Override
            public void close(String reason) {
                TcpServerManager.this.close(reason);
            }

            @Override
            public void broadcast(@NonNull Object message, boolean blocking) {
                int id = this.server.getPacketRegistry().getId(message.getClass());
                super.broadcast(new UnencodedTcpPacket(message, UnderlyingNetworkConnection.ID_DEFAULT_CHANNEL, id), blocking);
            }

            @Override
            public <C extends UserConnection> void broadcast(@NonNull ByteBuf data, short id, @NonNull Class<? extends UserProtocol<C>> protocolClass) {
                TcpPacketWrapper wrapper = new TcpPacketWrapper(
                        data,
                        UnderlyingNetworkConnection.ID_DEFAULT_CHANNEL,
                        PacketRegistry.combine(this.server.getPacketRegistry().getProtocolId(protocolClass), id)
                );
                this.channels.writeAndFlush(wrapper);
            }
        }
    }

    private static class TcpClientManager extends TcpEndpointManager<Client, ClientBuilder> implements EndpointManager.ClientEndpointManager {
        @Override
        @SuppressWarnings("unchecked")
        public void start(@NonNull ClientBuilder builder, @NonNull Client client) {
            this.workerGroup = builder.getEventGroup();

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(this.workerGroup);
                bootstrap.channelFactory(() -> new WrapperNioSocketChannel(client));
                bootstrap.handler(new TcpChannelInitializer(client));
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.TCP_NODELAY, true);

                this.channel = bootstrap.connect(builder.getAddress()).syncUninterruptibly().channel();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
            return ((WrapperNioSocketChannel) this.channel).getUserConnection(protocolClass);
        }

        @Override
        public void send(@NonNull Object message, boolean blocking, Void callback) {
            ((WrapperNioSocketChannel) this.channel).send(message, blocking, callback);
        }
    }

    private static class TcpChannelInitializer extends ChannelInitializer<Channel> {
        @NonNull
        private final Endpoint endpoint;
        private final Consumer<Channel> registerHook;
        private final Consumer<Channel> unRegisterHook;

        private TcpChannelInitializer(@NonNull Endpoint endpoint) {
            this(endpoint, c -> {
            }, c -> {
            });
        }

        private TcpChannelInitializer(@NonNull Endpoint endpoint, @NonNull Consumer<Channel> registerHook, @NonNull Consumer<Channel> unRegisterHook) {
            this.endpoint = endpoint;
            this.registerHook = registerHook;
            this.unRegisterHook = unRegisterHook;
        }

        @Override
        protected void initChannel(Channel c) throws Exception {
            //c.pipeline().addLast(new LengthFieldPrepender(4));
            c.pipeline().addLast(new LengthFieldBasedFrameDecoder(0x7FFFFFFF, 0, 4, 0, 4));
            c.pipeline().addLast(new TcpPacketCodec(this.endpoint));
            c.pipeline().addLast(new TcpPacketEncodingFilter(this.endpoint.getPacketRegistry()));
            c.pipeline().addLast(new TcpHandler(this.endpoint));
            this.registerHook.accept(c);

            WrapperNioSocketChannel realConnection = (WrapperNioSocketChannel) c;
            this.endpoint.getPacketRegistry().getProtocols().forEach(protocol -> realConnection.putUserConnection(protocol.getClass(), protocol.newConnection()));
            realConnection.registerTheUnderlyingConnection();
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            this.unRegisterHook.accept(ctx.channel());
        }
    }
}
