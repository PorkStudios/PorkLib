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

import com.nukkitx.network.raknet.CustomRakNetPacket;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.conn.Connection;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.protocol.api.PacketEncoder;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A wrapper for {@link com.nukkitx.network.raknet.RakNetPacket} to make this work with PorkLib
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@RequiredArgsConstructor
public class RakNetPacketWrapper implements CustomRakNetPacket<RakNetPorkSession>, PacketEncoder {
    @NonNull
    public Packet packet;
    @NonNull
    public UnderlyingNetworkConnection connection;
    private ByteBuf buf;
    private long ok;

    @Override
    public <E extends Endpoint> E getEndpoint() {
        return this.connection.getEndpoint();
    }

    @Override
    public void encode(@NonNull ByteBuf buffer) {
        buffer.writeLong(ThreadLocalRandom.current().nextLong());
        /*try {
            this.writePacket(this.connection, this.packet, NettyByteBufUtil.wrapOut(buffer));
        } catch (IOException e) {
            throw new RuntimeException("this isn't even possible...", e);
        }*/
    }

    @Override
    public void decode(@NonNull ByteBuf buffer) {
        /*while (buffer.readableBytes() != 0) {
            buffer.readByte();
        }*/
        //this.buf = buffer.copy();
        this.ok = buffer.readLong();
    }

    @Override
    public void handle(@NonNull RakNetPorkSession session) throws Exception {
        logger.debug("Received RakNet packet: ${0}", this.ok);
    }
}
