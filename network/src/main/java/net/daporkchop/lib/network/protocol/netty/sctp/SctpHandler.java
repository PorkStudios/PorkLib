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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.pork.PorkConnection;
import net.daporkchop.lib.network.pork.PorkProtocol;
import net.daporkchop.lib.network.pork.packet.HandshakeInitPacket;
import net.daporkchop.lib.network.util.NetworkConstants;

/**
 * Handles events on a connection managed by {@link SctpProtocolManager}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SctpHandler extends ChannelInboundHandlerAdapter implements Logging {
    @NonNull
    private final Endpoint endpoint;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("[${0}] New connection: ${1}", this.endpoint.getName(), ctx.channel().remoteAddress());

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        if (this.endpoint.isServer()) {
            realConnection.getControlChannel().send(new HandshakeInitPacket(), () -> logger.debug("sent handshake init!"));
        }

        super.channelRegistered(ctx);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("[${0}] Connection ${1} removed", this.endpoint.getName(), ctx.channel().remoteAddress());

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        String disconnectReason = realConnection.getUserConnection(PorkProtocol.class).getDisconnectReason();
        realConnection.getConnections().forEach((protocolClass, c) -> c.onDisconnect(disconnectReason));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //TODO: send keepalives
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //logger.debug("Received ${0}", msg.getClass());
        try {
            if (!(msg instanceof SctpPacketWrapper)) {
                logger.error("Expected ${0}, but got ${1}!", SctpPacketWrapper.class, msg.getClass());
                throw new IllegalArgumentException(String.format("Expected %s, but got %s!", SctpPacketWrapper.class, msg.getClass()));
            }

            SctpPacketWrapper packet = (SctpPacketWrapper) msg;
            //logger.debug("Received message!");
            UnderlyingNetworkConnection connection = (UnderlyingNetworkConnection) ctx.channel();
            if (NetworkConstants.DEBUG_REF_COUNT) {
                int oldRefCount = packet.getData().refCnt();
                this.endpoint.getPacketRegistry().getHandler(packet.getId()).handle(packet.getData(), connection, packet.getChannel());
                logger.debug("Received packet with ${0} references! (pre-handle: ${1})", packet.getData().refCnt(), oldRefCount);
            } else {
                this.endpoint.getPacketRegistry().getHandler(packet.getId()).handle(packet.getData(), connection, packet.getChannel());
            }
            packet.getData().release();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause != null) {
            logger.error(cause);
        }

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        PorkConnection porkConnection = realConnection.getUserConnection(PorkProtocol.class);
        porkConnection.setDisconnectReason(cause == null ? "Unknown exception" : String.format("%s: %s", cause.getClass(), cause.getMessage()));
        if (cause != null && this.endpoint instanceof PorkClient) {
            ((PorkClient) this.endpoint).postConnectCallback(cause);
        }
    }
}
