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
import net.daporkchop.lib.network.packet.handler.DataPacketHandler;
import net.daporkchop.lib.network.util.reliability.Reliability;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class OpenChannelPacket {
    @NonNull
    public final Reliability reliability;
    public final int channelId;

    public static class OpenChannelCodec implements DataPacketHandler<OpenChannelPacket> {
        @Override
        public void handle(@NonNull OpenChannelPacket packet, @NonNull UnderlyingNetworkConnection connection, int channelId) throws Exception {
            connection.openChannel(packet.reliability, packet.channelId);
        }

        @Override
        public void encode(@NonNull OpenChannelPacket packet, @NonNull DataOut out) throws Exception {
            out.writeEnum(packet.reliability);
            out.writeVarInt(packet.channelId, true);
        }

        @Override
        public OpenChannelPacket decode(@NonNull DataIn in) throws Exception {
            return new OpenChannelPacket(
                    in.readEnum(Reliability::valueOf),
                    in.readVarInt(true)
            );
        }

        @Override
        public Class<OpenChannelPacket> getPacketClass() {
            return OpenChannelPacket.class;
        }
    }
}
