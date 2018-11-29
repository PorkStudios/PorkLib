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

package net.daporkchop.lib.network.protocol.netty.sctp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.handler.codec.sctp.SctpMessageCompletionHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.DisconnectPacket;
import net.daporkchop.lib.network.protocol.api.EndpointManager;
import net.daporkchop.lib.network.protocol.api.ProtocolManager;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * An implementation of {@link ProtocolManager} for the SCTP transport protocol.
 * <p>
 * SCTP provides an unlimited* number of independent reliable (and optionally ordered) channels. Unlike TCP,
 * which is stream-based, SCTP is message-based (like UDP) which gives better performance for the direct packet-based networking that
 * PorkLib network is designed for.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SctpProtocolManager implements ProtocolManager {
    public static final SctpProtocolManager INSTANCE = new SctpProtocolManager();

    @Override
    public EndpointManager.ServerEndpointManager createServerManager() {
        return new SctpServerManager();
    }

    @Override
    public EndpointManager.ClientEndpointManager createClientManager() {
        return new SctpClientManager();
    }

    private abstract static class SctpEndpointManager<E extends Endpoint> implements EndpointManager<E> {
        protected Channel channel;
        protected EventLoopGroup workerGroup;

        @Override
        public void close() {
            if (this.isClosed()) {
                throw new IllegalStateException("already closed!");
            }
            this.channel.flush();
            this.channel.close().syncUninterruptibly();
            this.workerGroup.shutdownGracefully();
        }

        @Override
        public boolean isRunning() {
            return this.channel != null && this.channel.isActive();
        }
    }

    private static class SctpServerManager extends SctpEndpointManager<Server> implements EndpointManager.ServerEndpointManager {
        protected EventLoopGroup bossGroup;
        private ChannelGroup channels;

        @Override
        public void start(@NonNull InetSocketAddress address, @NonNull Executor executor, @NonNull Server server) {
            this.bossGroup = new NioEventLoopGroup(0, executor);
            this.workerGroup = new NioEventLoopGroup(0, executor);
            this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(this.bossGroup, this.workerGroup);
                //TODO: channel
                bootstrap.channel(NioSctpServerChannel.class);
                bootstrap.childHandler(new SctpChannelInitializer(server, this.channels::add, this.channels::remove));
                bootstrap.childOption(SctpChannelOption.SCTP_NODELAY, true);

                this.channel = bootstrap.bind(address).syncUninterruptibly().channel();
            } catch (Throwable t) {
                this.workerGroup.shutdownGracefully();
                this.bossGroup.shutdownGracefully();
                this.channels.close();
                throw new RuntimeException(t);
            }
        }

        @Override
        public <C extends UserConnection> Collection<C> getConnections(Class<? extends UserProtocol<C>> protocolClass) {
            return null;
        }

        @Override
        public void broadcast(@NonNull Packet packet, boolean blocking, Void callback) {
            ChannelGroupFuture future = this.channels.writeAndFlush(packet);
            if (callback != null) {
                future.addListener(f -> callback.run());
            }
            if (blocking) {
                future.syncUninterruptibly();
            }
        }

        @Override
        public void close(String reason) {
            this.broadcast(new DisconnectPacket(reason), true, null);
            this.close();
        }

        @Override
        public void close() {
            super.close();
            this.bossGroup.shutdownGracefully();
        }
    }

    private static class SctpClientManager extends SctpEndpointManager<Client> implements EndpointManager.ClientEndpointManager {
        @Override
        public void start(@NonNull InetSocketAddress address, @NonNull Executor executor, @NonNull Client client) {
            this.workerGroup = new NioEventLoopGroup(0, executor);

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(this.workerGroup);
                //TODO: channel
                bootstrap.channel(NioSctpChannel.class);
                bootstrap.handler(new SctpChannelInitializer(client));
                bootstrap.option(SctpChannelOption.SCTP_NODELAY, true);

                this.channel = bootstrap.connect(address).syncUninterruptibly().channel();
            } catch (Throwable t) {
                this.workerGroup.shutdownGracefully();
                throw new RuntimeException(t);
            }
        }

        @Override
        public <C extends UserConnection> C getConnection(Class<? extends UserProtocol<C>> protocolClass) {
            return null;
        }

        @Override
        public void send(@NonNull Packet packet, boolean blocking, Void callback) {
            ChannelFuture future = this.channel.writeAndFlush(packet);
            if (callback != null) {
                future.addListener(f -> callback.run());
            }
            if (blocking) {
                future.syncUninterruptibly();
            }
        }
    }

    private static class SctpChannelInitializer extends ChannelInitializer<Channel> {
        @NonNull
        private final Endpoint endpoint;
        private final Consumer<Channel> registerHook;
        private final Consumer<Channel> unRegisterHook;

        private SctpChannelInitializer(@NonNull Endpoint endpoint) {
            this(endpoint, c -> {
            }, c -> {
            });
        }

        private SctpChannelInitializer(@NonNull Endpoint endpoint, @NonNull Consumer<Channel> registerHook, @NonNull Consumer<Channel> unRegisterHook) {
            this.endpoint = endpoint;
            this.registerHook = registerHook;
            this.unRegisterHook = unRegisterHook;
        }

        @Override
        protected void initChannel(Channel c) throws Exception {
            c.pipeline().addLast(new SctpMessageCompletionHandler());
            c.pipeline().addLast(new SctpPacketCodec(this.endpoint));
            c.pipeline().addLast(new SctpHandler(this.endpoint));
            this.registerHook.accept(c);

            //TODO: WrapperNioSocketChannel realConnection = (WrapperNioSocketChannel) c;
            //this.endpoint.getPacketRegistry().getProtocols().forEach(protocol -> realConnection.putUserConnection(protocol.getClass(), protocol.newConnection()));
            //realConnection.registerTheUnderlyingConnection();
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            this.unRegisterHook.accept(ctx.channel());
        }
    }
}
