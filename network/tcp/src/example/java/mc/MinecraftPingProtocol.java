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

package mc;

import lombok.NonNull;
import mc.packet.HandshakePacket;
import mc.packet.PingPacket;
import mc.packet.PongPacket;
import mc.packet.ResponsePacket;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.session.pipeline.Pipeline;

/**
 * @author DaPorkchop_
 */
public class MinecraftPingProtocol extends PacketProtocol<MCSession> implements Logging {
    @Override
    protected void registerPackets(@NonNull Registerer registerer) {
        registerer.outbound(0x00, HandshakePacket.class)
                .outbound(0x01, PingPacket.class)
                .inbound(0x00, ResponsePacket.class)
                .inbound(0x01, PongPacket.class);
    }

    @Override
    public void initPipeline(@NonNull Pipeline<MCSession> pipeline, @NonNull MCSession session) {
        super.initPipeline(pipeline, session);

        pipeline.replace("tcp_framer", new MinecraftPacketFramer());
    }

    @Override
    public MCSession newSession() {
        return new MCSession();
    }
}
