/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.impl.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.server.HttpServer;
import net.daporkchop.lib.http.server.HttpServerBinding;
import net.daporkchop.lib.http.server.handle.NoopServerHandler;
import net.daporkchop.lib.http.server.handle.ServerHandler;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.pool.EventLoopGroupPool;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementation of {@link HttpServer} backed by Netty.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true, chain = true)
public final class NettyHttpServer implements HttpServer {
    public static final AttributeKey<NettyHttpServerBinding> ATTR_BINDING = AttributeKey.newInstance("porklib-http-server-binding");

    @Getter
    @Setter
    @NonNull
    protected ServerHandler handler = new NoopServerHandler();

    protected final EventLoopGroupPool loopPool;
    protected final EventLoopGroup     loop;

    protected final ChannelGroup channels;

    protected final ServerBootstrap bootstrap;

    @Getter
    protected final Promise<Void> closeFuture;

    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected volatile boolean closed = false;

    public NettyHttpServer(@NonNull EventLoopGroupPool loopPool) {
        this.loopPool = loopPool;
        this.loop = loopPool.get();
        this.channels = new DefaultChannelGroup(this.loop.next(), true);

        this.closeFuture = this.loop.next().newPromise();

        this.bootstrap = new ServerBootstrap()
                .group(this.loop, this.loop)
                .channelFactory(this.loopPool.transport().channelFactorySocketServer())
                .childHandler(new NettyHttpServerChannelInitializer(this));
    }

    @Override
    public Future<?> bind(@NonNull InetSocketAddress address) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            this.assertOpen();

            return this.bootstrap.clone()
                    .childAttr(ATTR_BINDING, new NettyHttpServerBinding(this, address))
                    .bind(address)
                    .addListener((ChannelFutureListener) future -> this.channels.add(future.channel()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Future<Void> close() {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            if (!this.closed) {
                this.closed = true;
                this.channels.close().addListener(future -> {
                    try {
                        this.handler.removed(this);
                    } catch (Exception e) {
                        if (!this.closeFuture.tryFailure(e)) {
                            throw new IllegalStateException();
                        }
                    } finally {
                        this.loopPool.release(this.loop);
                    }

                    if (future.isSuccess()) {
                        if (!this.closeFuture.trySuccess(null)) {
                            throw new IllegalStateException();
                        }
                    } else if (!this.closeFuture.tryFailure(future.cause())) {
                        throw new IllegalStateException();
                    }
                });
            }
        } finally {
            lock.unlock();
        }
        return this.closeFuture;
    }

    protected void assertOpen() {
        if (this.closed) {
            throw new IllegalStateException("Already closed!");
        }
    }
}
