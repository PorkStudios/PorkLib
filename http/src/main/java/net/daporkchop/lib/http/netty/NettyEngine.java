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

package net.daporkchop.lib.http.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.HttpEngine;
import net.daporkchop.lib.http.client.HttpClient;
import net.daporkchop.lib.http.server.HttpServer;

import java.util.concurrent.Executor;

/**
 * The official implementation of {@link HttpEngine}, built with the Netty library.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class NettyEngine implements HttpEngine {
    protected final EventLoopGroup group;
    protected final ChannelFactory<Channel> clientChannelFactory;
    protected final ChannelFactory<ServerChannel> serverChannelFactory;
    protected final Future<NettyEngine> closeFuture;
    protected final boolean autoClose;

    /**
     * Constructs a new {@link NettyEngine} with a specific number of threads using a specific {@link Executor}.
     *
     * @param threads   the number of threads to use
     * @param executor  the executor that will run the threads
     * @param autoClose whether or not to automatically close the engine when all clients and servers are closed
     */
    public NettyEngine(int threads, @NonNull Executor executor, boolean autoClose) {
        this(Epoll.isAvailable() ? new EpollEventLoopGroup(threads, executor) : new NioEventLoopGroup(threads, executor), autoClose);

        //automatically shut down group when closed
        this.closeFuture.addListener((FutureListener<NettyEngine>) f -> f.get().group.shutdownGracefully());
    }

    /**
     * Constructs a new {@link NettyEngine} with a specific {@link EventLoopGroup}.
     * <p>
     * Do not use directly unless you know what you're doing!
     *
     * @param group     the {@link EventLoopGroup} to use
     * @param autoClose whether or not to automatically close the engine when all clients and servers are closed
     */
    public NettyEngine(@NonNull EventLoopGroup group, boolean autoClose) {
        if (group instanceof EpollEventLoopGroup) {
            this.clientChannelFactory = EpollSocketChannel::new;
            this.serverChannelFactory = EpollServerSocketChannel::new;
        } else if (group instanceof NioEventLoopGroup) {
            this.clientChannelFactory = NioSocketChannel::new;
            this.serverChannelFactory = NioServerSocketChannel::new;
        } else {
            throw new IllegalArgumentException(String.format("Invalid event loop group: %s", PorkUtil.className(group)));
        }
        this.group = group;
        this.autoClose = autoClose;
        this.closeFuture = group.next().newPromise();
    }

    @Override
    public Future<HttpClient> client() {
        return null;
    }

    @Override
    public Future<HttpServer> server() {
        return null;
    }

    @Override
    public Future<Void> close() {
        return null;
    }
}
