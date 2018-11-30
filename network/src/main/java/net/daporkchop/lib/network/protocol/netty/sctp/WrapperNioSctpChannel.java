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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.netty.NettyConnection;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.primitive.map.IntegerObjectMap;
import net.daporkchop.lib.primitive.map.PorkMaps;
import net.daporkchop.lib.primitive.map.hashmap.IntegerObjectHashMap;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public class WrapperNioSctpChannel extends NioSctpChannel implements NettyConnection, Logging {
    @NonNull
    private final Endpoint endpoint;
    private final Map<Class<? extends UserProtocol>, UserConnection> connections = new IdentityHashMap<>();
    private final SctpChannel defaultChannel;

    final SparseBitSet channelIds = new SparseBitSet();
    final IntegerObjectMap<SctpChannel> channels = PorkMaps.synchronize(new IntegerObjectHashMap<>());

    public WrapperNioSctpChannel(@NonNull Client client)    {
        this.endpoint = client;
        this.defaultChannel = this.openChannel(Reliability.RELIABLE_ORDERED);
    }

    public WrapperNioSctpChannel(io.netty.channel.Channel parent, com.sun.nio.sctp.SctpChannel sctpChannel, @NonNull Server server) {
        super(parent, sctpChannel);
        this.endpoint = server;
        this.defaultChannel = this.openChannel(Reliability.RELIABLE_ORDERED);
    }

    @Override
    public SctpChannel openChannel(@NonNull Reliability reliability) {
        switch (reliability) {
            case RELIABLE:
            case RELIABLE_ORDERED: {
                int id;
                synchronized (this.channelIds) {
                    id = this.channelIds.nextClearBit(0);
                    this.channelIds.set(id);
                }
                SctpChannel channel = new SctpChannel(id, reliability, this);
                this.channels.put(id, channel);
                return channel;
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
