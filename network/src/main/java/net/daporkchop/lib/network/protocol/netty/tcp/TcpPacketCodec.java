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

package net.daporkchop.lib.network.protocol.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.Endpoint;

import java.util.List;

/**
 * Prefixes outgoing messages
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class TcpPacketCodec extends MessageToMessageCodec<ByteBuf, TcpPacketWrapper> implements Logging {
    @NonNull
    private final Endpoint endpoint;

    @Override
    protected void encode(@NonNull ChannelHandlerContext ctx, @NonNull TcpPacketWrapper msg, @NonNull List<Object> out) throws Exception {
        try {
            out.add(ctx.alloc().buffer(12).writeInt(msg.getData().readableBytes() + 8).writeInt(msg.getChannel()).writeInt(msg.getId()));
            out.add(msg.getData().retain());
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    protected void decode(@NonNull ChannelHandlerContext ctx, @NonNull ByteBuf in, @NonNull List<Object> out) throws Exception {
        try {
            int channelId = in.readInt();
            int packetId = in.readInt();
            out.add(new TcpPacketWrapper(in.retain(), channelId, packetId));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }
}
