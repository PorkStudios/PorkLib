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

package net.daporkchop.lib.network.protocol.netty.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.protocol.api.PacketEncoder;

import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SctpPacketCodec extends MessageToMessageCodec<SctpMessage, SctpPacketWrapper> implements PacketEncoder {
    @NonNull
    private final Endpoint endpoint;

    @Override
    protected void encode(ChannelHandlerContext ctx, SctpPacketWrapper msg, List<Object> out) throws Exception {
        //logger.debug("[${0} Packet codec] Encoding packet ${1}", this.endpoint.getName(), msg.getClass());
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(256, Integer.MAX_VALUE);
        try (DataOut dataOut = NettyByteBufUtil.wrapOut(buf))   {
            dataOut.writeUTF(msg.getPacket().getClass().getCanonicalName());
        }
        out.add(new SctpMessage(0, msg.getChannel(), !msg.isOrdered(), buf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, SctpMessage msg, List<Object> out) throws Exception {
        try (DataIn in = NettyByteBufUtil.wrapIn(msg.content()))    {
            out.add(in.readUTF());
        }
    }
}
