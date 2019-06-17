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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.netty.NettyByteBufOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.endpoint.PEndpoint;
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

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true, chain = true)
public class TCPNioSocket<S extends AbstractUserSession<S>> extends NioSocketChannel implements TCPSession<S> {
    protected final TCPEndpoint<?, S, ?> endpoint;
    protected final S userSession;
    protected final boolean incoming;
    protected final Framer<S> framer;
    protected final Promise<Void> connectFuture;
    protected final InetSocketAddress address;

    public TCPNioSocket(@NonNull TCPEndpoint<?, S, ?> endpoint, @NonNull InetSocketAddress address) {
        this.incoming = false;
        this.endpoint = endpoint;
        this.address = address;
        this.userSession = endpoint.sessionFactory().newSession();
        PUnsafe.putObject(this.userSession, ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);

        @SuppressWarnings("unchecked")
        Framer<S> framer = (Framer<S>) this.endpoint.transportEngine().framerFactory().newFramer();
        (this.framer = framer).init(this.userSession);

        this.connectFuture = this.newPromise();
        this.closeFuture().addListener(v -> this.onClosed());
    }

    public TCPNioSocket(@NonNull TCPEndpoint<?, S, ?> endpoint, Channel parent, SocketChannel socket) {
        super(parent, socket);

        this.incoming = true;
        this.address = null;
        this.endpoint = endpoint;
        this.userSession = endpoint.sessionFactory().newSession();
        PUnsafe.putObject(this.userSession, ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);

        @SuppressWarnings("unchecked")
        Framer<S> framer = (Framer<S>) this.endpoint.transportEngine().framerFactory().newFramer();
        (this.framer = framer).init(this.userSession);

        this.connectFuture = this.newPromise();
        this.closeFuture().addListener(v -> this.onClosed());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends PEndpoint<E, S>> E endpoint() {
        return (E) this.endpoint;
    }

    @Override
    public Future<Void> send(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        if (channel != 0)    {
            message = ChanneledPacket.getInstance(message, channel);
        }
        if ((flags & SendFlags.ASYNC) != 0) {
            Object screwJava = message; //reeeeee
            return this.eventLoop().submit(
                    () -> this.send(screwJava, 0, null, null, flags & ~(SendFlags.ASYNC | SendFlags.SYNC)),
                    null
            );
        } else {
            ChannelFuture future = (flags & SendFlags.FLUSH) != 0 ? this.writeAndFlush(message) : this.write(message);
            if ((flags & SendFlags.SYNC) != 0) {
                future.syncUninterruptibly();
            }
            return future;
        }
    }

    @Override
    public DataOut writer() {
        return new NettyByteBufOut(this.alloc().ioBuffer()) {
            @Override
            public void close() throws IOException {
                if (this.buf.writerIndex() == 0) {
                    this.buf.release();
                } else {
                    TCPNioSocket.this.write(this.buf);
                }
            }
        };
    }

    @Override
    public NetSession<S> flushBuffer() {
        this.flush();
        return this;
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
    public boolean isClosed() {
        return !this.isOpen();
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
    public Future<Void> closeAsync() {
        if (this.endpoint.type() == EndpointType.CLIENT) {
            return this.endpoint.closeAsync();
        } else {
            return this.close();
        }
    }

    @Override
    public void onOpened(boolean incoming) {
        try {
            TCPSession.super.onOpened(incoming);
        } finally {
            this.connectFuture.trySuccess(null);
        }
    }

    @Override
    public void onException(@NonNull Exception e) {
        try {
            TCPSession.super.onException(e);
        } finally {
            this.connectFuture.tryFailure(e);
        }
    }
}
