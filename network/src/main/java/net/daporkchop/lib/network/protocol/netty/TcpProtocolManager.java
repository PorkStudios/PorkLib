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
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.protocol.EndpointManager;
import net.daporkchop.lib.network.protocol.ProtocolManager;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TcpProtocolManager implements ProtocolManager {
    public static final TcpProtocolManager INSTANCE = new TcpProtocolManager();

    @Override
    public <C extends UserConnection> EndpointManager.ServerEndpointManager<C> createServerManager() {
        return new NettyServerManager<>();
    }

    @Override
    public <C extends UserConnection> EndpointManager.ClientEndpointManager<C> createClientManager() {
        return new NettyClientManager<>();
    }

    private static abstract class NettyEndpointManager<C extends UserConnection> implements EndpointManager<C> {
        protected Channel channel;
        protected EventLoopGroup workerGroup;

        @Override
        public void close() {
            if (this.isClosed()) {
                throw new IllegalStateException("already closed!");
            }
            this.channel.close().syncUninterruptibly();
            this.workerGroup.shutdownGracefully();
        }

        @Override
        public boolean isRunning() {
            return this.channel != null && this.channel.isActive();
        }
    }

    private static class NettyServerManager<C extends UserConnection> extends NettyEndpointManager<C> implements EndpointManager.ServerEndpointManager<C> {
        protected EventLoopGroup bossGroup;
        private ChannelGroup channels;

        @Override
        @SuppressWarnings("unchecked")
        public void start(InetSocketAddress address, Executor executor, Endpoint<C> endpoint) {
            this.bossGroup = new NioEventLoopGroup(0, executor);
            this.workerGroup = new NioEventLoopGroup(0, executor);
            this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            ChannelHandler groupAdder = new ChannelInboundHandlerAdapter()  {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    NettyServerManager.this.channels.add(ctx.channel());
                }
            };

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(this.bossGroup, this.workerGroup);
                bootstrap.channel(NioServerSocketChannel.class); //TODO: make use of this for fast data access?
                bootstrap.childHandler(new NettyChannelInitializer<>(endpoint, p -> p.addLast(groupAdder)));
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
        public Collection<C> getConnections() {
            return null;
        }

        @Override
        public void broadcast(Packet packet) {
            this.channels.write(packet);
        }
    }

    private static class NettyClientManager<C extends UserConnection> extends NettyEndpointManager<C> implements EndpointManager.ClientEndpointManager<C> {
        @Override
        @SuppressWarnings("unchecked")
        public void start(InetSocketAddress address, Executor executor, Endpoint<C> endpoint) {
            this.workerGroup = new NioEventLoopGroup(0, executor);

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(this.workerGroup);
                bootstrap.channel(NioSocketChannel.class); //TODO: make use of this for fast data access?
                bootstrap.handler(new NettyChannelInitializer<>(endpoint));
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

                this.channel = bootstrap.connect(address).syncUninterruptibly().channel();
            } catch (Throwable t) {
                this.workerGroup.shutdownGracefully();
                throw new RuntimeException(t);
            }
        }

        @Override
        public C getConnection() {
            return null;
        }

        @Override
        public void send(Packet packet) {
            this.channel.write(packet);
        }
    }

    private static class NettyChannelInitializer<C extends UserConnection> extends ChannelInitializer<SocketChannel> {
        @NonNull
        private final Endpoint<C> endpoint;
        private final Collection<Consumer<ChannelPipeline>> populators;

        @SafeVarargs
        public NettyChannelInitializer(@NonNull Endpoint<C> endpoint, @NonNull Consumer<ChannelPipeline>... populators) {
            this.endpoint = endpoint;
            this.populators = Arrays.asList(populators);
            this.populators.forEach(c -> {
                if (c == null) {
                    throw new NullPointerException();
                }
            });
        }

        @Override
        protected void initChannel(SocketChannel c) throws Exception {
            c.pipeline().addLast(new LengthFieldPrepender(3));
            c.pipeline().addLast(new LengthFieldBasedFrameDecoder(0xFFFFFF, 0, 3, 0, 3));
            c.pipeline().addLast(new NettyPacketEncoder());
            c.pipeline().addLast(new NettyPacketDecoder());
            c.pipeline().addLast(new PorkReceiveHandler<>(this.endpoint));
            this.populators.forEach(populator -> populator.accept(c.pipeline()));
        }
    }
}
