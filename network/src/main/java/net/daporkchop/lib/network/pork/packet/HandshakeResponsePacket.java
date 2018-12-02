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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.pork.PorkConnection;
import net.daporkchop.lib.network.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@AllArgsConstructor
public class HandshakeResponsePacket implements Packet {
    public Collection<Version> protocolVersions;

    @Override
    public void read(@NonNull DataIn in) throws IOException {
        this.protocolVersions = new ArrayList<>();
        for (int i = in.readVarInt(true) - 1; i >= 0; i--) {
            this.protocolVersions.add(new Version(in.readUTF(), in.readVarInt(true)));
        }
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeVarInt(this.protocolVersions.size(), true);
        for (Version version : this.protocolVersions) {
            out.writeUTF(version.getName());
            out.writeVarInt(version.getVersion(), true);
        }
    }

    public static class HandshakeResponseCodec implements Codec<HandshakeResponsePacket, PorkConnection> {
        @Override
        public void handle(@NonNull HandshakeResponsePacket packet, @NonNull Channel channel, @NonNull PorkConnection connection) {
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
            channel.send(new HandshakeCompletePacket());
        }

        @Override
        public HandshakeResponsePacket createInstance() {
            return new HandshakeResponsePacket();
        }
    }
}
