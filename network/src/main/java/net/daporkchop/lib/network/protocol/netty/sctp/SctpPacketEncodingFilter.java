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

package net.daporkchop.lib.network.protocol.netty.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.handler.PacketHandler;
import net.daporkchop.lib.network.util.NetworkConstants;

import java.util.List;

/**
 * Encodes unencoded packets
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SctpPacketEncodingFilter extends MessageToMessageEncoder<UnencodedSctpPacket> implements Logging {
    @NonNull
    private final PacketRegistry registry;

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(@NonNull ChannelHandlerContext ctx, @NonNull UnencodedSctpPacket msg, List<Object> out) throws Exception {
        try {
            ByteBuf buf = ctx.alloc().ioBuffer();
            PacketHandler handler = (PacketHandler) this.registry.getHandler(msg.getId());
            handler.encode(msg.getMessage(), buf);
            out.add(new SctpPacketWrapper(buf, msg.getChannel(), msg.getId(), msg.isOrdered()));
            if (NetworkConstants.DEBUG_REF_COUNT) {
                logger.debug("Encoded packet with ${0} references!", buf.refCnt());
            }
        } catch (Exception e) {
            Logging.logger.error(e);
            throw e;
        }
    }
}
