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

package net.daporkchop.lib.network.raknet.impl;

import com.nukkitx.network.raknet.EncapsulatedPacket;
import com.nukkitx.network.raknet.RakNetPriority;
import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.network.raknet.RakNetSessionListener;
import com.nukkitx.network.raknet.RakNetState;
import com.nukkitx.network.util.DisconnectReason;
import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.netty.NettyByteBufOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.protocol.DataProtocol;
import net.daporkchop.lib.network.raknet.RakNetEngine;
import net.daporkchop.lib.network.raknet.endpoint.RakNetEndpoint;
import net.daporkchop.lib.network.raknet.pipeline.RakNetDataCodec;
import net.daporkchop.lib.network.raknet.pipeline.RakNetEdgeListener;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.PChannel;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true, chain = true)
public class PRakNetSession<S extends AbstractUserSession<S>> implements RakNetSessionListener, NetSession<S> {
    protected final RakNetEndpoint<?, S, ?> endpoint;
    protected final RakNetSession delegate;
    protected final S userSession;
    protected final Pipeline<S> dataPipeline;
    protected final EventExecutor group;
    protected final Promise<Void> connectFuture;
    protected final Promise<Void> disconnectFuture;

    protected final DummyRakNetChannel<S> defaultChannel = new DummyRakNetChannel<>(this, 0);
    protected final Map<Integer, DummyRakNetChannel<S>> channels = PorkUtil.newSoftCache();

    @Setter
    @NonNull
    protected Reliability fallbackReliability = Reliability.RELIABLE_ORDERED;

    public PRakNetSession(@NonNull RakNetEndpoint<?, S, ?> endpoint, @NonNull RakNetSession delegate) {
        this.endpoint = endpoint;
        this.delegate = delegate;
        this.group = endpoint.group().next();
        this.connectFuture = this.group.newPromise();
        this.disconnectFuture = this.group.newPromise();

        this.userSession = endpoint.protocol().sessionFactory().newSession();
        PUnsafe.putObject(this.userSession, ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);

        this.dataPipeline = new Pipeline<>(
                this.userSession,
                new RakNetEdgeListener<>(),
                (sendQueue, session, msg, reliability, channel) -> {
                    if (msg instanceof ByteBuf) {
                        this.delegate.send((ByteBuf) msg, RakNetPriority.MEDIUM, RakNetEngine.toRakNet(reliability), channel);
                    } else {
                        throw new IllegalArgumentException(String.format("Invalid packet type: \"%s\"!", msg == null ? "null" : msg.getClass().getCanonicalName()));
                    }
                }
        );
    }

    @Override
    public void onSessionChangeState(RakNetState state) {
        if (state == RakNetState.CONNECTED) {
            if (this.endpoint.protocol() instanceof DataProtocol) {
                this.dataPipeline.addLast("protocol", new RakNetDataCodec<>((DataProtocol<S>) this.endpoint.protocol(), this.delegate));
            }

            this.dataPipeline.fireOpened();
            this.connectFuture.setSuccess(null);
        }
    }

    @Override
    public void onDisconnect(DisconnectReason reason) {
        this.dataPipeline.fireClosed();
    }

    @Override
    public void onEncapsulated(EncapsulatedPacket packet) {
        this.dataPipeline.fireReceived(packet.getBuffer(), packet.getOrderingChannel() & 0xFFFF);
    }

    @Override
    public void onDirect(ByteBuf buf) {
        //TODO: figure out when this is called
    }

    @Override
    public PChannel<S> channel(int id) {
        if (id == 0) {
            return this.defaultChannel;
        } else {
            synchronized (this.channels) {
                return this.channels.computeIfAbsent(id, i -> new DummyRakNetChannel<>(this, i));
            }
        }
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability) {
        this.dataPipeline.fireSending(packet, reliability, 0, null);
        return this;
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability, int channel) {
        this.dataPipeline.fireSending(packet, reliability, channel, null);
        return this;
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability) {
        return this.group.submit(() -> {
            this.dataPipeline.fireSending(packet, reliability, 0, null);
            return null;
        });
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return this.group.submit(() -> {
            this.dataPipeline.fireSending(packet, reliability, channel, null);
            return null;
        });
    }

    @Override
    public DataOut writer() {
        return new NettyByteBufOut(this.delegate.allocateBuffer(32)) {
            @Override
            public void close() throws IOException {
                if (this.buf.writerIndex() == 0) {
                    this.buf.release();
                } else {
                    PRakNetSession.this.sendAsync(this.buf);
                }
            }
        };
    }

    @Override
    public NetSession<S> flushBuffer() {
        return this;
    }

    @Override
    public void closeNow() {
        this.delegate.disconnect();
    }

    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Override
    public Future<Void> closeAsync() {
        this.group.submit(() -> {
            this.delegate.disconnect();
            this.disconnectFuture.trySuccess(null);
            return null;
        });
        return this.disconnectFuture;
    }

    @Override
    public TransportEngine transportEngine() {
        return this.endpoint.transportEngine();
    }

    //RakNet doesn't need to be flushed

    @Override
    public NetSession<S> sendFlush(@NonNull Object packet, Reliability reliability) {
        return this.send(packet, reliability);
    }

    @Override
    public NetSession<S> sendFlush(@NonNull Object packet, Reliability reliability, int channel) {
        return this.send(packet, reliability, channel);
    }

    @Override
    public Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability) {
        return this.sendAsync(packet, reliability);
    }

    @Override
    public Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return this.sendAsync(packet, reliability, channel);
    }
}
