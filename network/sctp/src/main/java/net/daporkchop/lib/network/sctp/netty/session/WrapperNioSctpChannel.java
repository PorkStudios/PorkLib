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

package net.daporkchop.lib.network.sctp.netty.session;

import io.netty.channel.sctp.SctpMessage;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.sctp.endpoint.SCTPEndpoint;
import net.daporkchop.lib.network.sctp.pipeline.SCTPEdgeListener;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.PChannel;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.transport.WrappedPacket;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class WrapperNioSctpChannel<S extends AbstractUserSession<S>> extends NioSctpChannel implements NetSession<S> {
    protected final SCTPEndpoint<?, S, ?> endpoint;
    protected final Map<Integer, SCTPChannel<S>> channels = PorkUtil.newSoftCache();
    protected final S userSession;
    protected final Pipeline<S> dataPipeline;

    protected Reliability fallbackReliability = Reliability.RELIABLE_ORDERED;

    public WrapperNioSctpChannel(@NonNull SCTPEndpoint<?, S, ?> endpoint) {
        this.endpoint = endpoint;
        this.userSession = endpoint.protocol().sessionFactory().newSession();
        PUnsafe.putObject(this.userSession, ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);

        this.dataPipeline = new Pipeline<>(this.userSession, new SCTPEdgeListener<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends PEndpoint<E, S>> E endpoint() {
        return (E) this.endpoint;
    }

    @Override
    public PChannel<S> channel(int id) {
        synchronized (this.channels) {
            return this.channels.computeIfAbsent(id, i -> new SCTPChannel<>(this, i));
        }
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability) {
        reliability = this.reliability(reliability);
        this.write(reliability == Reliability.RELIABLE_ORDERED ? packet : new WrappedPacket<>(packet, 0, reliability));
        return this;
    }

    @Override
    public NetSession<S> sendFlush(@NonNull Object packet, Reliability reliability) {
        reliability = this.reliability(reliability);
        this.writeAndFlush(reliability == Reliability.RELIABLE_ORDERED ? packet : new WrappedPacket<>(packet, 0, reliability));
        return this;
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability, int channelId) {
        reliability = this.reliability(reliability);
        this.write(reliability == Reliability.RELIABLE_ORDERED && channelId == 0 ? packet : new WrappedPacket<>(packet, channelId, reliability));
        return this;
    }

    @Override
    public NetSession<S> sendFlush(@NonNull Object packet, Reliability reliability, int channelId) {
        reliability = this.reliability(reliability);
        this.write(reliability == Reliability.RELIABLE_ORDERED && channelId == 0 ? packet : new WrappedPacket<>(packet, channelId, reliability));
        return this;
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability) {
        reliability = this.reliability(reliability);
        return this.write(reliability == Reliability.RELIABLE_ORDERED ? packet : new WrappedPacket<>(packet, 0, reliability));
    }

    @Override
    public Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability) {
        reliability = this.reliability(reliability);
        return this.writeAndFlush(reliability == Reliability.RELIABLE_ORDERED ? packet : new WrappedPacket<>(packet, 0, reliability));
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability, int channelId) {
        reliability = this.reliability(reliability);
        return this.write(reliability == Reliability.RELIABLE_ORDERED && channelId == 0 ? packet : new WrappedPacket<>(packet, channelId, reliability));
    }

    @Override
    public Future<Void> sendFlushAsync(@NonNull Object packet, Reliability reliability, int channelId) {
        reliability = this.reliability(reliability);
        return this.write(reliability == Reliability.RELIABLE_ORDERED && channelId == 0 ? packet : new WrappedPacket<>(packet, channelId, reliability));
    }

    public Reliability reliability(Reliability reliability) {
        if (reliability == null || (reliability != Reliability.RELIABLE_ORDERED && reliability != Reliability.RELIABLE)) {
            return this.fallbackReliability;
        } else {
            return reliability;
        }
    }

    @Override
    public DataOut writer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NetSession<S> flushBuffer() {
        this.flush();
        return this;
    }

    @Override
    public NetSession<S> fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        if (reliability != Reliability.RELIABLE_ORDERED && reliability != Reliability.RELIABLE) {
            throw new IllegalArgumentException(reliability.name());
        }
        this.fallbackReliability = reliability;
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
}
