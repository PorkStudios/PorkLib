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
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.NettyByteBufUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.protocol.pork.PorkProtocol;

import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class NettyPacketDecoder extends ByteToMessageDecoder implements Logging {
    @NonNull
    private final Endpoint endpoint;

    private static void readRemainingBuffer(@NonNull ByteBuf buf) {
        while (buf.isReadable()) {
            buf.readByte();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        int size = buf.readableBytes();
        try (DataIn in = DataIn.wrap(((UnderlyingNetworkConnection) ctx.channel()).getUserConnection(PorkProtocol.class).getPacketReprocessor().wrap(NettyByteBufUtil.wrapIn(buf)))) {
            int id = in.readVarInt(true);
            Codec<? extends Packet, ? extends UserConnection> codec = this.endpoint.getPacketRegistry().getCodec(id);
            if (codec == null) {
                throw this.exception("Received unknown packet id ${0}", id);
            }
            Packet packet = codec.createInstance();
            packet.read(in);
            logger.debug("[${0}] Read packet: ${1} (${2} bytes)", this.endpoint.getName(), packet.getClass(), size - buf.readableBytes());
            list.add(packet);
        } catch (Exception e) {
            logger.error(e);
            if (this.endpoint instanceof PorkClient) {
                ((PorkClient) this.endpoint).postConnectCallback(e);
            }
            ((UnderlyingNetworkConnection) ctx.channel()).closeConnection();
            throw e;
        }
        readRemainingBuffer(buf);
    }
}
