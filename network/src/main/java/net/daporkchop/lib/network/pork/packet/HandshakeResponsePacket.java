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

package net.daporkchop.lib.network.pork.packet;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.handler.DataPacketHandler;
import net.daporkchop.lib.network.util.Version;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class HandshakeResponsePacket {
    @NonNull
    public final Collection<Version> protocolVersions;

    public static class HandshakeResponseCodec implements DataPacketHandler<HandshakeResponsePacket> {
        @Override
        public void handle(@NonNull HandshakeResponsePacket packet, @NonNull UnderlyingNetworkConnection connection, int channelId) throws Exception {
            PacketRegistry registry = connection.getEndpoint().getPacketRegistry();
            if (registry.getProtocols().size() != packet.protocolVersions.size()) {
                connection.closeConnection("invalid protocol count");
                throw new IllegalStateException();
            }
            registry.getProtocols().stream().map(Version::new).forEach(protocolVersion -> {
                if (!packet.protocolVersions.contains(protocolVersion)) {
                    connection.closeConnection("invalid protocol version/name");
                    throw new IllegalStateException();
                }
            });
            connection.getControlChannel().send(new HandshakeCompletePacket());
        }

        @Override
        public void encode(@NonNull HandshakeResponsePacket packet, @NonNull DataOut out) throws Exception {
            out.writeVarInt(packet.protocolVersions.size(), true);
            for (Version version : packet.protocolVersions) {
                out.writeUTF(version.getName());
                out.writeVarInt(version.getVersion(), true);
            }
        }

        @Override
        public HandshakeResponsePacket decode(@NonNull DataIn in) throws Exception {
            Collection<Version> protocolVersions = new ArrayList<>();
            for (int i = in.readVarInt(true) - 1; i >= 0; i--) {
                protocolVersions.add(new Version(in.readUTF(), in.readVarInt(true)));
            }
            return new HandshakeResponsePacket(protocolVersions);
        }

        @Override
        public Class<HandshakeResponsePacket> getPacketClass() {
            return HandshakeResponsePacket.class;
        }
    }
}
