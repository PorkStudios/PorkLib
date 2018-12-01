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

import com.nukkitx.network.raknet.datagram.RakNetReliability;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.network.util.reliability.ReliabilityMap;

import java.util.Collection;

/**
 * An implementation of {@link Channel} for RakNet connections
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class PorkRakNetChannel implements Channel {
    private final int id;
    private final RakNetReliability reliability;
    @NonNull
    private final RakNetPorkSession session;
    private volatile boolean closed;

    private void send(@NonNull Packet packet, boolean blocking, Void callback, RakNetReliability reliability)    {
        //TODO: use correct channel and reliability!
        this.session.getConnection().sendPacket(new RakNetPacketWrapper(packet, this.session));
    }

    @Override
    public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return this.session.getUserConnection(protocolClass);
    }

    @Override
    public void close() {
        synchronized (this.session) {
            if (!this.isDefault()) {
                this.closed = false;
                this.session.channels.remove(this.id);
                this.session.channelIds.clear(this.id);
            }
        }
    }

    public Reliability getReliability() {
        return ReliabilityMap.RAKNET.get(this.reliability);
    }

    //OVERRIDES

    @Override
    public void send(@NonNull Packet packet, boolean blocking, Void callback, Reliability reliability) {
        this.send(packet, blocking, callback, ReliabilityMap.RAKNET.get(reliability));
    }

    @Override
    public void send(@NonNull Packet packet, boolean blocking, Void callback) {
        this.send(packet, blocking, callback, this.reliability);
    }

    @Override
    public void send(@NonNull Packet packet, boolean blocking) {
        this.send(packet, blocking, null, this.reliability);
    }

    @Override
    public void send(@NonNull Packet packet, Void callback) {
        this.send(packet, false, callback, this.reliability);
    }

    @Override
    public void send(@NonNull Packet packet) {
        this.send(packet, false, null, this.reliability);
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return Reliability.ALL;
    }

    @Override
    public boolean isEncrypted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startEncryption(CryptographySettings cryptographySettings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnderlyingNetworkConnection getConnection() {
        throw new UnsupportedOperationException();
    }
}
