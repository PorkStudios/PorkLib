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

package net.daporkchop.lib.network.endpoint.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.Transport;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.packet.PacketProtocol;
import net.daporkchop.lib.network.protocol.netty.NettyPacketDecoder;
import net.daporkchop.lib.network.protocol.netty.NettyPacketEncoder;
import net.daporkchop.lib.network.protocol.netty.PorkReceiveHandler;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
@Getter
public class PorkServer<C extends UserConnection> implements Endpoint<C> {
    private final PacketProtocol<C> protocol;
    private final CryptographySettings cryptographySettings;
    private final Collection<C> connections = new ArrayDeque<>();
    private final Transport transport;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ChannelFuture channelFuture;

    public PorkServer(@NonNull ServerBuilder<C> builder)    {
        this.protocol = builder.getProtocol();
        this.cryptographySettings = builder.getCryptographySettings();
        this.transport = builder.getTransport();

        //this.bossGroup = new EpollEventLoopGroup()
        this.bossGroup = new NioEventLoopGroup(0, builder.getExecutor());
        this.workerGroup = new NioEventLoopGroup(0, builder.getExecutor());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(this.bossGroup, this.workerGroup);
            bootstrap.channel(NioServerSocketChannel.class); //TODO: make use of this for fast data access?
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel c) throws Exception {
                    c.pipeline().addLast(new LengthFieldPrepender(3));
                    c.pipeline().addLast(new LengthFieldBasedFrameDecoder(0xFFFFFF, 0, 3, 0, 3));
                    c.pipeline().addLast(new NettyPacketEncoder());
                    c.pipeline().addLast(new NettyPacketDecoder());
                    c.pipeline().addLast(new PorkReceiveHandler<>(PorkServer.this));
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 256);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            this.channelFuture = bootstrap.bind(builder.getAddress()).syncUninterruptibly();
        } catch (Exception e)   {
            this.workerGroup.shutdownGracefully();
            this.bossGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

    @Override
    public EndpointType getType() {
        return EndpointType.SERVER;
    }

    @Override
    public Collection<C> getConnections() {
        return this.connections;
    }

    @Override
    public void close(String reason) {
        synchronized (this) {
            if (!this.isRunning())  {
                throw new IllegalStateException("Already closed!");
            }
            this.connections.forEach(c -> c.close(reason));

            this.channelFuture.channel().close();//.syncUninterruptibly();
            this.workerGroup.shutdownGracefully();//.syncUninterruptibly();
            this.bossGroup.shutdownGracefully();//.syncUninterruptibly();
        }
    }

    @Override
    public boolean isRunning() {
        return this.channelFuture.channel().isActive();
    }
}
