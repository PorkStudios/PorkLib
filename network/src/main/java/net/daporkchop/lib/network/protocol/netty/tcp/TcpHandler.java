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

/**
 * Handles events on a connection managed by {@link TcpProtocolManager}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class TcpHandler extends ChannelInboundHandlerAdapter implements Logging {
    @NonNull
    private final Endpoint endpoint;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.trace("[%s] Channel inactive: %s", this.endpoint.getName(), ctx.channel().remoteAddress());
        //TODO: send keepalives
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, @NonNull Object msg) throws Exception {
        try {
            if (!(msg instanceof TcpPacketWrapper)) {
                logger.error("Expected %s, but got %s!", TcpPacketWrapper.class, msg.getClass());
                throw new IllegalArgumentException(String.format("Expected %s, but got %s!", TcpPacketWrapper.class, msg.getClass()));
            }

            TcpPacketWrapper packet = (TcpPacketWrapper) msg;
            //logger.debug("Received message!");
            UnderlyingNetworkConnection connection = (UnderlyingNetworkConnection) ctx.channel();
            this.endpoint.getPacketRegistry().getHandler(packet.getId()).handle(packet.getData(), connection, packet.getChannel());
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
        if (cause != null && realConnection.getEndpoint() instanceof PorkClient) {
            realConnection.<PorkClient>getEndpoint().postConnectCallback(cause);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("[%s] New connection: %s", this.endpoint.getName(), ctx.channel().remoteAddress());

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        if (this.endpoint.isServer()) {
            realConnection.getControlChannel().send(new HandshakeInitPacket(), () -> logger.debug("Sent handshake init!"));
        } else if (false && this.endpoint instanceof PorkClient) {
            ((PorkClient) this.endpoint).postConnectCallback(null);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("[%s] Connection %s removed", this.endpoint.getName(), ctx.channel().remoteAddress());

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        String disconnectReason = realConnection.getUserConnection(PorkProtocol.class).getDisconnectReason();
        realConnection.getConnections().forEach((protocolClass, c) -> c.onDisconnect(disconnectReason));
    }
}
