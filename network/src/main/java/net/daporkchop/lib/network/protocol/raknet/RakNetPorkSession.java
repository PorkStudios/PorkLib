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

package net.daporkchop.lib.network.protocol.raknet;

import com.nukkitx.network.NetworkSession;
import com.nukkitx.network.raknet.datagram.RakNetReliability;
import com.nukkitx.network.raknet.session.RakNetSession;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.PorkProtocol;
import net.daporkchop.lib.network.pork.packet.DisconnectPacket;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.network.util.reliability.ReliabilityMap;
import net.daporkchop.lib.primitive.map.IntegerObjectMap;
import net.daporkchop.lib.primitive.map.PorkMaps;
import net.daporkchop.lib.primitive.map.hashmap.IntegerObjectHashMap;

import java.net.InetSocketAddress;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A session for RakNet connections
 *
 * @author DaPorkchop_
 */
@Getter
public class RakNetPorkSession implements NetworkSession<RakNetSession>, UnderlyingNetworkConnection, RakNetConstants, Logging {
    static final int CHANNEL_ID_CONTROL = 0;
    static final int CHANNEL_ID_DEFAULT = 1;

    final BitSet channelIds = new BitSet(MAX_CHANNELS);
    final IntegerObjectMap<PorkRakNetChannel> channels = PorkMaps.synchronize(new IntegerObjectHashMap<>(), this);
    private final RakNetSession connection;
    private final Endpoint endpoint;
    private final Channel defaultChannel;
    private final Channel controlChannel;
    private final Map<Class<? extends UserProtocol>, UserConnection> connections = new IdentityHashMap<>();

    public RakNetPorkSession(@NonNull RakNetSession connection, @NonNull Endpoint endpoint) {
        this.connection = connection;
        this.endpoint = endpoint;
        this.controlChannel = this.openChannel(Reliability.RELIABLE_ORDERED, CHANNEL_ID_CONTROL);
        this.defaultChannel = this.openChannel(Reliability.RELIABLE_ORDERED, CHANNEL_ID_DEFAULT);
    }

    @Override
    public void disconnect() {
        this.closeConnection(null);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void closeConnection(String reason) {
        //actually set the disconnect reason here
        this.getUserConnection(PorkProtocol.class).setDisconnectReason(reason);
        this.send(new DisconnectPacket(reason));
        this.disconnectAtNetworkLevel();
    }

    @Override
    public boolean isConnected() {
        return !this.connection.isClosed();
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.connection.getRemoteAddress().orElseThrow(IllegalStateException::new);
    }

    @Override
    public PorkRakNetChannel openChannel(@NonNull Reliability r) {
        synchronized (this) {
            return this.openChannel(r, this.channelIds.nextClearBit(0));
        }
    }

    @Override
    public PorkRakNetChannel openChannel(@NonNull Reliability r, int requestedId) {
        synchronized (this) {
            RakNetReliability reliability = ReliabilityMap.RAKNET.get(r);
            if (requestedId >= MAX_CHANNELS) {
                throw new IllegalStateException(this.format("Too many channels open! (max: ${0})", MAX_CHANNELS));
            }
            this.channelIds.set(requestedId);
            PorkRakNetChannel channel = new PorkRakNetChannel(requestedId, reliability, this);
            this.channels.put(requestedId, channel);
            return channel;
        }
    }

    @Override
    public Channel getOpenChannel(int id) {
        return this.channels.get(id);
    }

    @Override
    public void disconnectAtNetworkLevel() {
        this.connection.close();
    }
}
