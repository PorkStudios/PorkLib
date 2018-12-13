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

import com.zaxxer.sparsebits.SparseBitSet;
import io.netty.channel.sctp.nio.NioSctpChannel;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.OpenChannelPacket;
import net.daporkchop.lib.network.protocol.netty.NettyConnection;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.primitive.map.IntegerObjectMap;
import net.daporkchop.lib.primitive.map.PorkMaps;
import net.daporkchop.lib.primitive.map.array.IntegerObjectArrayMap;
import net.daporkchop.lib.primitive.map.hashmap.IntegerObjectHashMap;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 */
@Getter
public class WrapperNioSctpChannel extends NioSctpChannel implements NettyConnection, Logging {
    final SparseBitSet channelIds = new SparseBitSet();
    final IntegerObjectMap<SctpChannel> channels = PorkMaps.synchronize(new IntegerObjectHashMap<>(), new ReentrantLock());
    @NonNull
    private final Endpoint endpoint;
    private final Map<Class<? extends UserProtocol>, UserConnection> connections = new IdentityHashMap<>();
    private final SctpChannel defaultChannel;
    private final SctpChannel controlChannel;

    public WrapperNioSctpChannel(@NonNull Client client) {
        this.endpoint = client;
        this.controlChannel = this.openChannel(Reliability.RELIABLE_ORDERED, ID_CONTROL_CHANNEL, false);
        this.defaultChannel = this.openChannel(Reliability.RELIABLE_ORDERED, ID_DEFAULT_CHANNEL, false);
    }

    public WrapperNioSctpChannel(io.netty.channel.Channel parent, com.sun.nio.sctp.SctpChannel sctpChannel, @NonNull Server server) {
        super(parent, sctpChannel);
        this.endpoint = server;
        this.controlChannel = this.openChannel(Reliability.RELIABLE_ORDERED, ID_CONTROL_CHANNEL, false);
        this.defaultChannel = this.openChannel(Reliability.RELIABLE_ORDERED, ID_DEFAULT_CHANNEL, false);
    }

    @Override
    public SctpChannel openChannel(@NonNull Reliability reliability) {
        synchronized (this.channelIds) {
            return this.openChannel(reliability, this.channelIds.nextClearBit(0), true);
        }
    }

    @Override
    public SctpChannel openChannel(@NonNull Reliability reliability, int requestedId, boolean notifyRemote) {
        switch (reliability) {
            case RELIABLE:
            case RELIABLE_ORDERED: {
                try {
                    synchronized (this.channelIds) {
                        if (this.channelIds.get(requestedId)) {
                            throw this.exception("channel id ${0} already taken!", requestedId);
                        }
                        this.channelIds.set(requestedId);
                    }
                    SctpChannel channel = new SctpChannel(requestedId, reliability, this);
                    this.channels.put(requestedId, channel);
                    return channel;
                } catch (Exception e) {
                    notifyRemote = false;
                    throw e;
                } finally {
                    if (notifyRemote && requestedId > 1) {
                        this.controlChannel.send(new OpenChannelPacket(reliability, requestedId), true);
                    }
                }
            }
            default:
                throw new IllegalArgumentException(this.format("SCTP only supports RELIABLE and RELIABLE_ORDERED, but ${0} was given!", reliability.name()));
        }
    }

    @Override
    public Channel getOpenChannel(int id) {
        return this.channels.get(id);
    }

    @Override
    public void disconnectAtNetworkLevel() {
        this.channels.values().forEach(channel -> channel.closed = true);
        this.channels.clear();
        NettyConnection.super.disconnectAtNetworkLevel();
    }
}
