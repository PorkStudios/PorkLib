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

package net.daporkchop.lib.network.tcp;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.netty.NettyByteBufOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.PChannel;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.tcp.pipeline.TCPEdgeListener;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.tcp.endpoint.TCPEndpoint;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class WrapperNioSocketChannel<S extends AbstractUserSession<S>> extends NioSocketChannel implements NetSession<S> {
    protected final TCPEndpoint<?, S, ?> endpoint;
    protected final DummyTCPChannel<S> defaultChannel = new DummyTCPChannel<>(this, 0);
    protected final Map<Integer, DummyTCPChannel<S>> channels = PorkUtil.newSoftCache();
    protected final S userSession;
    protected final Pipeline<S> dataPipeline;

    public WrapperNioSocketChannel(@NonNull TCPEndpoint<?, S, ?> endpoint) {
        this.endpoint = endpoint;
        this.userSession = endpoint.protocol().sessionFactory().newSession();
        PUnsafe.putObject(this, ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);

        this.dataPipeline = new Pipeline<>(this.userSession, new TCPEdgeListener<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends PEndpoint<E, S>> E endpoint() {
        return (E) this.endpoint;
    }

    @Override
    public PChannel<S> channel(int id) {
        if (id == 0) {
            return this.defaultChannel;
        } else {
            synchronized (this.channels) {
                return this.channels.computeIfAbsent(id, i -> new DummyTCPChannel<>(this, i));
            }
        }
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability) {
        this.write(new ChanneledPacket<>(packet, 0));
        return this;
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability, int channelId) {
        if (!this.channels.containsKey(channelId)) {
            throw new IllegalArgumentException(String.format("Unknown channel id: %d", channelId));
        }
        this.write(new ChanneledPacket<>(packet, channelId));
        return this;
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability) {
        return this.write(new ChanneledPacket<>(packet, 0));
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability, int channelId) {
        if (!this.channels.containsKey(channelId)) {
            throw new IllegalArgumentException(String.format("Unknown channel id: %d", channelId));
        }
        return this.write(new ChanneledPacket<>(packet, channelId));
    }

    @Override
    public DataOut writer() {
        return new NettyByteBufOut(this.alloc().ioBuffer())    {
            @Override
            public void flush() throws IOException {
                WrapperNioSocketChannel.this.write(this.buf);
                this.buf(WrapperNioSocketChannel.this.alloc().ioBuffer());
            }

            @Override
            public void close() throws IOException {
                WrapperNioSocketChannel.this.write(this.buf);
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
        this.close().syncUninterruptibly();
    }

    @Override
    public Future<Void> closeAsync() {
        return this.close();
    }
}
