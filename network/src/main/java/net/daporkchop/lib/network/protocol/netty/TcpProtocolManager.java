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

package net.daporkchop.lib.network.protocol.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.EndpointManager;
import net.daporkchop.lib.network.protocol.ProtocolManager;
import net.daporkchop.lib.network.protocol.netty.wrapper.WrapperNioServerSocketChannel;
import net.daporkchop.lib.network.protocol.netty.wrapper.WrapperNioSocketChannel;
import net.daporkchop.lib.network.protocol.pork.packet.DisconnectPacket;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TcpProtocolManager implements ProtocolManager {
    public static final TcpProtocolManager INSTANCE = new TcpProtocolManager();

    @Override
    public EndpointManager.ServerEndpointManager createServerManager() {
        return new NettyServerManager();
    }

    @Override
    public EndpointManager.ClientEndpointManager createClientManager() {
        return new NettyClientManager();
    }

    private static abstract class NettyEndpointManager implements EndpointManager {
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

    private static class NettyServerManager extends NettyEndpointManager implements EndpointManager.ServerEndpointManager {
        protected EventLoopGroup bossGroup;
        private ChannelGroup channels;

        @Override
        @SuppressWarnings("unchecked")
        public void start(@NonNull InetSocketAddress address, @NonNull Executor executor, @NonNull Endpoint endpoint) {
            this.bossGroup = new NioEventLoopGroup(0, executor);
            this.workerGroup = new NioEventLoopGroup(0, executor);
            this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            ChannelHandler groupAdder = new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    NettyServerManager.this.channels.add(ctx.channel());
                }
            };

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(this.bossGroup, this.workerGroup);
                //bootstrap.channel(NioServerSocketChannel.class);
                //bootstrap.channel(WrapperNioServerSocketChannel.class);
                bootstrap.channelFactory(() -> new WrapperNioServerSocketChannel(endpoint));
                bootstrap.childHandler(new NettyChannelInitializer(endpoint, p -> p.addLast(groupAdder)));
                bootstrap.option(ChannelOption.SO_BACKLOG, 256);
                bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

                this.channel = bootstrap.bind(address).syncUninterruptibly().channel();
            } catch (Throwable t) {
                this.workerGroup.shutdownGracefully();
                this.bossGroup.shutdownGracefully();
                this.channels.close();
                throw new RuntimeException(t);
            }
        }

        @Override
        public void close() {
            super.close();
            this.bossGroup.shutdownGracefully();
            this.channels.close();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends UserConnection> Collection<C> getConnections(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
            return this.channels.stream()
                    .map(WrapperNioSocketChannel.class::cast)
                    .map(realConnection -> realConnection.getUserConnection(protocolClass))
                    .collect(Collectors.toCollection(ArrayDeque::new));
        }

        @Override
        public void broadcast(@NonNull Packet packet) {
            this.channels.writeAndFlush(packet);
        }

        @Override
        public void close(String reason) {
            this.broadcast(new DisconnectPacket(reason));
            this.close();
        }
    }

    private static class NettyClientManager extends NettyEndpointManager implements EndpointManager.ClientEndpointManager {
        @Override
        @SuppressWarnings("unchecked")
        public void start(@NonNull InetSocketAddress address, @NonNull Executor executor, @NonNull Endpoint endpoint) {
            this.workerGroup = new NioEventLoopGroup(0, executor);

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(this.workerGroup);
                //bootstrap.channel(NioSocketChannel.class);
                //bootstrap.channel(WrapperNioSocketChannel.class);
                bootstrap.channelFactory(() -> new WrapperNioSocketChannel(endpoint));
                bootstrap.handler(new NettyChannelInitializer(endpoint));
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

                this.channel = bootstrap.connect(address).syncUninterruptibly().channel();
            } catch (Throwable t) {
                this.workerGroup.shutdownGracefully();
                throw new RuntimeException(t);
            }
        }

        @Override
        public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
            return ((WrapperNioSocketChannel) this.channel).getUserConnection(protocolClass);
        }

        @Override
        public void send(@NonNull Packet packet) {
            this.channel.writeAndFlush(packet);
        }
    }

    private static class NettyChannelInitializer extends ChannelInitializer<Channel> {
        @NonNull
        private final Endpoint endpoint;
        private final Collection<Consumer<ChannelPipeline>> populators;

        @SafeVarargs
        private NettyChannelInitializer(@NonNull Endpoint endpoint, @NonNull Consumer<ChannelPipeline>... populators) {
            this.endpoint = endpoint;
            this.populators = Arrays.asList(populators);
            this.populators.forEach(c -> {
                if (c == null) {
                    throw new NullPointerException();
                }
            });
        }

        @Override
        protected void initChannel(Channel c) throws Exception {
            c.pipeline().addLast(new LengthFieldPrepender(3));
            c.pipeline().addLast(new LengthFieldBasedFrameDecoder(0xFFFFFF, 0, 3, 0, 3));
            c.pipeline().addLast(new NettyPacketEncoder(this.endpoint));
            c.pipeline().addLast(new NettyPacketDecoder(this.endpoint));
            c.pipeline().addLast(new NettyHandler(this.endpoint));
            this.populators.forEach(populator -> populator.accept(c.pipeline()));

            WrapperNioSocketChannel realConnection = (WrapperNioSocketChannel) c;
            this.endpoint.getPacketRegistry().getProtocols().forEach(protocol -> realConnection.putUserConnection(protocol.getClass(), protocol.newConnection()));
            realConnection.registerTheUnderlyingConnection();
        }
    }
}
