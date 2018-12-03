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

package net.daporkchop.lib.network.protocol.api;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.ChannelImplementation;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;

import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes packets ye dummy
 * <p>
 * Hah! You came here looking for a useful javadoc, but all you got was this LOUSY insult!
 *
 * @author DaPorkchop_
 */
public interface PacketDecoder extends Logging {
    <E extends Endpoint> E getEndpoint();

    default Packet getPacket(@NonNull ChannelImplementation channel, @NonNull InputStream stream) throws IOException {
        return this.getPacket(channel, stream, true);
    }

    default Packet getPacket(@NonNull ChannelImplementation channel, @NonNull InputStream stream, boolean allowEncryption) throws IOException {
        // DataIn in = DataIn.wrap(((UnderlyingNetworkConnection) ctx.channel()).getUserConnection(PorkProtocol.class).getPacketReprocessor().wrap(NettyByteBufUtil.wrapIn(buf)))
        try (DataIn in = DataIn.wrap(channel.getPacketReprocessor().wrap(stream, allowEncryption))) {
            int id = in.readVarInt(true);
            Codec<? extends Packet, ? extends UserConnection> codec = this.getEndpoint().getPacketRegistry().getCodec(id);
            if (codec == null) {
                throw this.exception("Received unknown packet id ${0}", id);
            }
            Packet packet = codec.createInstance();
            packet.read(in);
            return packet;
        } catch (Exception e) {
            logger.error(e);
            if (this.getEndpoint() instanceof PorkClient) {
                ((PorkClient) this.getEndpoint()).postConnectCallback(e);
            }
            channel.getConnection().closeConnection(e.toString());
            throw e;
        }
    }
}
