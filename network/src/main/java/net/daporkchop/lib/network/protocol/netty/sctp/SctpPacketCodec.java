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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.Endpoint;

import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SctpPacketCodec extends MessageToMessageCodec<SctpMessage, SctpPacketWrapper> implements Logging {
    @NonNull
    private final Endpoint endpoint;

    @Override
    protected void encode(@NonNull ChannelHandlerContext ctx, @NonNull SctpPacketWrapper msg, @NonNull List<Object> out) throws Exception {
        out.add(new SctpMessage(msg.getId(), msg.getChannel(), !msg.isOrdered(), msg.getData()));
    }

    @Override
    protected void decode(@NonNull ChannelHandlerContext ctx, @NonNull SctpMessage msg, @NonNull List<Object> out) throws Exception {
        if (false) {
            logger.debug("Received packet: ${0}", this.toHex(msg.content()));
            logger.debug("plain          : ${0}", this.toString(msg.content()));
        }
        out.add(new SctpPacketWrapper(msg.content(), msg.streamIdentifier(), msg.protocolIdentifier(), !msg.isUnordered()));
    }

    private String toHex(@NonNull ByteBuf buf) {
        char[] hex = "0123456789abcdef".toCharArray();
        char[] chars = new char[buf.readableBytes() << 1];
        int j = buf.readerIndex();
        for (int i = buf.readableBytes() - 1; i >= 0; i--) {
            chars[(i << 1)] = hex[buf.getByte(i + j) & 0xF];
            chars[(i << 1) + 1] = hex[(buf.getByte(i + j) >>> 1) & 0xF];
        }
        return PorkUtil.wrap(chars);
    }

    private String toString(@NonNull ByteBuf buf) {
        byte[] b = new byte[buf.readableBytes()];
        int j = buf.readerIndex();
        for (int i = buf.readableBytes() - 1; i >= 0; i--) {
            b[i] = buf.getByte(i + j);
        }
        return new String(b).replace("\n", "\\n");
    }
}
