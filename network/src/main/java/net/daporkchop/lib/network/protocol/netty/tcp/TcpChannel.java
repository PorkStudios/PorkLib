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

package net.daporkchop.lib.network.protocol.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.CloseChannelPacket;
import net.daporkchop.lib.network.protocol.netty.NettyChannel;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.Collection;
import java.util.Collections;

/**
 * A simple implementation of {@link Channel} for a TCP session.
 * <p>
 * As TCP only supports full ordering and reliability, packets sent through this channel will not respect the reliability setting
 * given by the user.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class TcpChannel extends NettyChannel implements Logging {
    private static final Collection<Reliability> RELIABLE_ORDERED_ONLY = Collections.singleton(Reliability.RELIABLE_ORDERED);

    @NonNull
    private final WrapperNioSocketChannel channel;
    private final int id;
    volatile boolean closed;

    @Override
    public void send(@NonNull Object message, boolean blocking, Void callback, Reliability reliability) {
        if (this.closed) {
            throw new IllegalStateException("channel closed!");
        } else {
            //logger.debug("Writing ${0} (${1}blocking)...", message.getClass(), blocking ? "" : "non-");
            int id = this.channel.getEndpoint().getPacketRegistry().getId(message.getClass());
            ChannelFuture future = this.channel.writeAndFlush(new UnencodedTcpPacket(message, this.id, id));
            if (callback != null) {
                future.addListener(f -> callback.run());
            }
            if (blocking) {
                future.syncUninterruptibly();
            }
        }
    }

    @Override
    public void send(@NonNull ByteBuf data, int id, boolean blocking, Void callback, Reliability reliability) {
        if (this.closed) {
            throw new IllegalStateException("channel closed!");
        } else {
            ChannelFuture future = this.channel.writeAndFlush(new TcpPacketWrapper(data, this.id, id));
            if (callback != null) {
                future.addListener(f -> callback.run());
            }
            if (blocking) {
                future.syncUninterruptibly();
            }
        }
    }

    @Override
    public Reliability getReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    public boolean isReliabilityRespected() {
        return false;
    }

    @Override
    public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return this.channel.getUserConnection(protocolClass);
    }

    @Override
    public synchronized void close(boolean notifyRemote) {
        if (this.isDefaultChannel()) {
            throw new IllegalStateException("Cannot close default channel!");
        } else if (this.isControlChannel()) {
            throw new IllegalStateException("Cannot close control channel!");
        } else {
            if (this.closed) {
                throw new IllegalArgumentException("already closed!");
            } else {
                synchronized (this.channel.channelIds) {
                    this.channel.channels.remove(this.id);
                    this.channel.channelIds.clear(this.id);
                }
                this.closed = true;
                if (notifyRemote) {
                    this.channel.getControlChannel().send(new CloseChannelPacket(this.id), true);
                }
            }
        }
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return RELIABLE_ORDERED_ONLY;
    }

    @Override
    public UnderlyingNetworkConnection getConnection() {
        return this.channel;
    }
}
