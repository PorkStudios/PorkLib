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
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.packet.handler.DataPacketHandler;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class CloseChannelPacket {
    public final int channelId;

    public static class CloseChannelCodec implements DataPacketHandler<CloseChannelPacket>, Logging {
        @Override
        public void handle(@NonNull CloseChannelPacket packet, @NonNull UnderlyingNetworkConnection connection, int channelId) throws Exception {
            Channel theChannel = connection.getOpenChannel(packet.channelId);
            if (theChannel == null) {
                throw this.exception("Invalid channel: ${0}", packet.channelId);
            } else {
                theChannel.close();
            }
        }

        @Override
        public void encode(@NonNull CloseChannelPacket packet, @NonNull DataOut out) throws Exception {
            out.writeVarInt(packet.channelId, true);
        }

        @Override
        public CloseChannelPacket decode(@NonNull DataIn in) throws Exception {
            return new CloseChannelPacket(
                    in.readVarInt(true)
            );
        }

        @Override
        public Class<CloseChannelPacket> getPacketClass() {
            return CloseChannelPacket.class;
        }
    }
}
