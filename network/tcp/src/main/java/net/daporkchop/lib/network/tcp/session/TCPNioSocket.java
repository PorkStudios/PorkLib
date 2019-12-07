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

package net.daporkchop.lib.network.tcp.session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.netty.NettyByteBufOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.netty.util.future.NettyChannelPromise;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.frame.Framer;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.network.tcp.endpoint.TCPEndpoint;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.util.SendFlags;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true, chain = true)
public class TCPNioSocket<S extends AbstractUserSession<S>> extends NioSocketChannel implements TCPSession<S> {
    protected static SocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openSocketChannel();
        } catch (IOException e) {
            throw new ChannelException("Failed to open a socket.", e);
        }
    }

    protected final TCPEndpoint<?, S, ?, ?> endpoint;
    protected final S userSession;
    protected final boolean incoming;
    protected final Framer<S> framer;
    protected final AtomicBoolean closed = new AtomicBoolean(false);
    protected final NettyChannelPromise closePromise = new NettyChannelPromise(this);
    protected final InetSocketAddress address;

    public TCPNioSocket(@NonNull TCPEndpoint<?, S, ?, ?> endpoint, @NonNull InetSocketAddress address) {
        this(endpoint, address, null, null);
    }

    public TCPNioSocket(@NonNull TCPEndpoint<?, S, ?, ?> endpoint, Channel parent, SocketChannel socket) {
        this(endpoint, null, parent, socket == null ? newSocket(SelectorProvider.provider()) : socket);
    }

    protected TCPNioSocket(@NonNull TCPEndpoint<?, S, ?, ?> endpoint, InetSocketAddress address, Channel parent, SocketChannel socket) {
        super(parent, socket == null ? newSocket(SelectorProvider.provider()) : socket);

        this.incoming = address == null;
        this.address = address;
        this.endpoint = endpoint;
        this.userSession = endpoint.sessionFactory().newSession();
        PUnsafe.putObject(this.userSession, ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);

        @SuppressWarnings("unchecked")
        Framer<S> framer = (Framer<S>) this.endpoint.transportEngine().framerFactory().newFramer();
        (this.framer = framer).init(this.userSession);

        this.closeFuture().addListener(v -> this.onClosed());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends PEndpoint<E, S>> E endpoint() {
        return (E) this.endpoint;
    }

    @Override
    public NettyChannelPromise send(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        if (channel != 0)    {
            message = ChanneledPacket.getInstance(message, channel);
        }
        NettyChannelPromise future = new NettyChannelPromise(this, this.eventLoop());
        if ((flags & SendFlags.ASYNC) != 0) {
            Object screwJava = message; //reeeeee
            this.eventLoop().submit(
                    () -> this.doSend(screwJava, flags & ~(SendFlags.ASYNC | SendFlags.SYNC), future),
                    null
            );
            return future;
        } else {
            return this.doSend(message, flags, future);
        }
    }

    protected NettyChannelPromise doSend(@NonNull Object message, int flags, @NonNull NettyChannelPromise future) {
        if ((flags & SendFlags.FLUSH) != 0) {
            this.writeAndFlush(message, future);
        } else {
            this.write(message, future);
        }
        return (flags & SendFlags.SYNC) != 0 ? future.syncUninterruptibly() : future;
    }

    @Override
    public DataOut writer() {
        return new NettyByteBufOut(this.alloc().ioBuffer()) {
            @Override
            protected boolean handleClose(@NonNull ByteBuf buf) throws IOException {
                if (this.buf.isReadable()) {
                    TCPNioSocket.this.write(this.buf);
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    @Override
    public Reliability fallbackReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    public NetSession<S> fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        if (reliability != Reliability.RELIABLE_ORDERED) {
            throw new IllegalArgumentException(reliability.name());
        }
        return this;
    }

    @Override
    public TransportEngine transportEngine() {
        return this.endpoint.transportEngine();
    }

    @Override
    public S userSession() {
        return this.userSession;
    }

    @Override
    public void closeNow() {
        this.closeAsync().syncUninterruptibly();
    }

    @Override
    public Promise closeAsync() {
        if (this.endpoint.type() == EndpointType.CLIENT) {
            return this.endpoint.closeAsync();
        } else {
            if (this.closed.compareAndSet(false, true)) {
                return (Promise) super.close(this.closePromise);
            } else {
                return this.closePromise;
            }
        }
    }

    @Override
    public Promise closePromise() {
        return this.closePromise;
    }

    @Override
    public void flushBuffer() {
        this.flush();
    }
}
