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

package net.daporkchop.lib.network.packet.handler;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.netty.NettyByteBufUtil;
import net.daporkchop.lib.network.channel.ChannelImplementation;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.packet.handler.codec.Codec;

/**
 * A {@link MessageHandler} that decodes messages into packets before handling them
 *
 * @author DaPorkchop_
 */
public interface PacketHandler<P> extends MessageHandler, Codec<P> {
    @Override
    default void handle(@NonNull ByteBuf msg, @NonNull UnderlyingNetworkConnection connection, int channelId) throws Exception {
        Logging.logger.debug("Handling message on channel ${0}...", channelId);
        this.handle(this.decode(msg), connection, channelId);
    }

    /**
     * Handle a packet
     *
     * @param packet     the packet that was received
     * @param connection the connection that the packet was received on
     * @param channelId  the reliability that the packet was sent with
     * @throws Exception if an exception occurs
     */
    void handle(@NonNull P packet, @NonNull UnderlyingNetworkConnection connection, int channelId) throws Exception;

    @Override
    void encode(@NonNull P packet, @NonNull ByteBuf buf) throws Exception;

    @Override
    P decode(@NonNull ByteBuf buf) throws Exception;

    /**
     * Gets the packet class
     *
     * @return the class of the packet
     */
    Class<P> getPacketClass();
}
