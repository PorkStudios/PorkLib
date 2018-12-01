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

package net.daporkchop.lib.network.protocol.netty.sctp;

import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.netty.NettyChannel;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.Arrays;
import java.util.Collection;

/**
 * An implementation of {@link Channel} for SCTP.
 * <p>
 * !!!IMPORTANT!!!
 * SCTP as a protocol only supports the following {@link Reliability} modes:
 * - {@link Reliability#RELIABLE}
 * - {@link Reliability#RELIABLE_ORDERED}
 * <p>
 * All other reliability modes will be silently overridden with the channel's default reliability.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SctpChannel extends NettyChannel implements Logging {
    static final int FLAG_DEFAULT = 0;
    static final int FLAG_CONTROL = 1;

    private static final Collection<Reliability> RELIABLE_ONLY = Arrays.asList(
            Reliability.RELIABLE,
            Reliability.RELIABLE_ORDERED
    );

    private final int id;
    @NonNull
    private final Reliability reliability;
    @NonNull
    private final WrapperNioSctpChannel channel;

    volatile boolean closed;

    @Override
    public void send(@NonNull Packet packet, boolean blocking, Void callback, Reliability reliability) {
        if (this.closed) {
            throw new IllegalStateException("channel closed!");
        }
        //logger.debug("[${0}] Sending packet: ${1}", this.channel.getEndpoint().getName(), packet.getClass());
        ChannelFuture future;
        if (reliability == null) {
            reliability = this.reliability;
        }
        switch (reliability) {
            case RELIABLE: {
                future = this.channel.writeAndFlush(new SctpPacketWrapper(packet, this.id, false));
            }
            break;
            case RELIABLE_ORDERED: {
                future = this.channel.writeAndFlush(new SctpPacketWrapper(packet, this.id, true));
            }
            break;
            default: {
                //silently override unsupported reliabilities with channel default
                this.send(packet, blocking, callback, this.reliability);
                return;
            }
        }
        if (callback != null) {
            future.addListener(f -> callback.run());
        }
        if (blocking) {
            future.syncUninterruptibly();
        }
    }

    @Override
    public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return this.channel.getUserConnection(protocolClass);
    }

    @Override
    public synchronized void close() {
        if (!this.closed) {
            throw new IllegalArgumentException("already closed!");
        }
        if (this.isDefaultChannel())    {
            this.channel.close();
        }
        this.closed = true;
        synchronized (this.channel.channelIds) {
            this.channel.channelIds.clear(this.id);
        }
        this.channel.channels.remove(this.id, this);
        //TODO: notify remote end of channel close
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return RELIABLE_ONLY;
    }

    @Override
    public UnderlyingNetworkConnection getConnection() {
        return this.channel;
    }

    @Override
    public boolean isDefaultChannel() {
        return this.id == WrapperNioSctpChannel.CHANNEL_ID_DEFAULT;
    }

    @Override
    public boolean isControlChannel() {
        return this.id == WrapperNioSctpChannel.CHANNEL_ID_CONTROL;
    }
}
