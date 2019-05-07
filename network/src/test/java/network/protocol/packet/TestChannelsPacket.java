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

package network.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.packet.handler.PacketHandler;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class TestChannelsPacket {
    public final int theIdOfTheChannelThatThePacketWasSupposedToBeSentOn; //man i love making variable names

    public static class TestChannelsHandler implements PacketHandler<TestChannelsPacket> {
        @Override
        public void handle(@NonNull TestChannelsPacket packet, @NonNull UnderlyingNetworkConnection connection, int channelId) throws Exception {
            if (channelId == packet.theIdOfTheChannelThatThePacketWasSupposedToBeSentOn) {
                Logging.logger.info("Received packet on correct channel: %d", channelId);
            } else {
                Logging.logger.info("Received packet on incorrect channel: %d", channelId);
                throw new IllegalStateException("wrong id!");
            }
        }

        @Override
        public void encode(@NonNull TestChannelsPacket packet, @NonNull ByteBuf buf) throws Exception {
            buf.writeInt(packet.theIdOfTheChannelThatThePacketWasSupposedToBeSentOn);
        }

        @Override
        public TestChannelsPacket decode(@NonNull ByteBuf buf) throws Exception {
            return new TestChannelsPacket(
                    buf.readInt()
            );
        }

        @Override
        public Class<TestChannelsPacket> getPacketClass() {
            return TestChannelsPacket.class;
        }
    }
}
