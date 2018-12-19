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

package net.daporkchop.lib.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A server for HTTP requests
 *
 * @author DaPorkchop_
 */
public class HTTPServer implements Logging {
    final ChannelGroup channels;
    final Channel channel;
    final AtomicBoolean shutdownLock = new AtomicBoolean(false);

    public HTTPServer(@NonNull HTTPServerBuilder builder) {
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(builder.getGroup());
            bootstrap.channelFactory(() -> new HTTPServerSocketChannel(this));
            bootstrap.childHandler(new NettyChannelHandlerHTTP(this));
            bootstrap.option(ChannelOption.SO_BACKLOG, 256);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            this.channel = bootstrap.bind(builder.getListenAddress()).syncUninterruptibly().channel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down the server
     */
    public void shutdown() {
        if (this.shutdownLock.get()) {
            throw new IllegalStateException("Already shut down!");
        } else {
            synchronized (this.shutdownLock) {
                this.shutdownLock.set(true);
                this.channel.close().syncUninterruptibly();
                this.channels.close().syncUninterruptibly();
                this.channel.eventLoop().shutdownGracefully();
            }
        }
    }
}
