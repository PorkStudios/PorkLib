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

package net.daporkchop.lib.network.protocol.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class NettyPacketEncoder extends MessageToByteEncoder<Packet> {
    @NonNull
    private final Endpoint endpoint;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf buf) throws Exception {
        //System.out.printf("Size pre-send: %d\n", buf.writerIndex());
        try (DataOut out = NettyByteBufUtil.wrapOut(buf)) {
            System.out.printf("[%s] Writing %s...\n", this.endpoint.getName(), packet.getClass().getCanonicalName());
            int id = this.endpoint.getPacketRegistry().getId(packet.getClass());
            if (id == -1)   {
                throw new IllegalArgumentException(String.format("Unregistered outbound packet: %s", packet.getClass().getCanonicalName()));
            }
            out.writeVarInt(id, true);
            packet.write(out);
        } catch (Exception e)   {
            e.printStackTrace();
            throw e;
        }
        //System.out.printf("Size post-send: %d\n", buf.writerIndex());
    }
}
