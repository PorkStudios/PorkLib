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

package net.daporkchop.lib.network.protocol.netty.tcp;

import com.zaxxer.sparsebits.SparseBitSet;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.OpenChannelPacket;
import net.daporkchop.lib.network.protocol.netty.NettyConnection;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.primitive.map.IntegerObjectMap;
import net.daporkchop.lib.primitive.map.PorkMaps;
import net.daporkchop.lib.primitive.map.array.IntegerObjectArrayMap;
import net.daporkchop.lib.primitive.map.hashmap.IntegerObjectHashMap;

import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A wrapper on top of {@link NioSocketChannel} that allows to store extra data (i.e. implement {@link UnderlyingNetworkConnection})
 *
 * @author DaPorkchop_
 */
@Getter
public class WrapperNioSocketChannel extends NioSocketChannel implements NettyConnection, Logging {
    private final Map<Class<? extends UserProtocol>, UserConnection> connections = new IdentityHashMap<>();
    final IntegerObjectMap<TcpChannel> channels = PorkMaps.synchronize(new IntegerObjectHashMap<>());
    final SparseBitSet channelIds = new SparseBitSet();
    @NonNull
    private final Endpoint endpoint;
    private final TcpChannel controlChannel;
    private final TcpChannel defaultChannel;

    public WrapperNioSocketChannel(@NonNull Endpoint endpoint)  {
        this.endpoint = endpoint;

        this.controlChannel = (TcpChannel) this.openChannel(Reliability.RELIABLE_ORDERED, 0, false);
        this.defaultChannel = (TcpChannel) this.openChannel(Reliability.RELIABLE_ORDERED, 1, false);
    }

    public WrapperNioSocketChannel(io.netty.channel.Channel parent, SocketChannel socket, @NonNull Endpoint endpoint) {
        super(parent, socket);
        this.endpoint = endpoint;

        this.controlChannel = (TcpChannel) this.openChannel(Reliability.RELIABLE_ORDERED, 0, false);
        this.defaultChannel = (TcpChannel) this.openChannel(Reliability.RELIABLE_ORDERED, 1, false);
    }

    //
    //
    // Connection implementations
    //
    //

    @Override
    public Channel openChannel(Reliability reliability) {
        synchronized (this.channelIds)  {
            return this.openChannel(reliability, this.channelIds.nextClearBit(0), true);
        }
    }

    @Override
    public Channel getOpenChannel(int id) {
        return this.channels.get(id);
    }

    @Override
    public Channel openChannel(Reliability reliability, int requestedId, boolean notifyRemote) {
        try {
            synchronized (this.channelIds) {
                if (this.channelIds.get(requestedId)) {
                    throw this.exception("Channel id ${0} already taken!", requestedId);
                } else {
                    this.channelIds.set(requestedId);
                    TcpChannel channel = new TcpChannel(this, requestedId);
                    this.channels.put(requestedId, channel);
                    return channel;
                }
            }
        } finally {
            if (notifyRemote && requestedId > 1)    {
                this.controlChannel.send(new OpenChannelPacket(Reliability.RELIABLE_ORDERED, requestedId), true);
            }
        }
    }
}
