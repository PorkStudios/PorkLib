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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.pork.PorkConnection;
import net.daporkchop.lib.network.protocol.pork.PorkProtocol;
import net.daporkchop.lib.network.protocol.pork.packet.HandshakeInitPacket;
import net.daporkchop.lib.network.util.ConnectionState;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class NettyHandler extends ChannelInboundHandlerAdapter implements Logging {
    @NonNull
    private final Endpoint endpoint;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //TODO: send keepalives
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, @NonNull Object msg) throws Exception {
        if (!(msg instanceof Packet)) {
            throw new IllegalArgumentException(this.format("Expected ${0}, but got ${1}!", Packet.class, msg.getClass()));
        }

        Packet packet = (Packet) msg;
        PacketRegistry registry = this.endpoint.getPacketRegistry();
        Class<? extends UserProtocol<UserConnection>> protocolClass = registry.getOwningProtocol(packet.getClass());
        if (protocolClass == null) {
            throw new IllegalArgumentException(this.format("Unregistered inbound packet: ${0}", packet.getClass()));
        }

        UserConnection connection = ((UnderlyingNetworkConnection) ctx.channel()).getUserConnection(protocolClass);
        logger.debug("Handling ${0}...", packet.getClass());
        registry.getCodec(packet.getClass()).handle(packet, connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause != null) {
            logger.error(cause);
        }

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        PorkConnection porkConnection = realConnection.getUserConnection(PorkProtocol.class);
        porkConnection.setDisconnectReason(cause == null ? "Unknown exception" : this.format("${0}: ${1}", cause.getClass(), cause.getMessage()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("[${0}] New connection: ${1}", this.endpoint.getName(), ctx.channel().remoteAddress());

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        /*this.endpoint.getPacketRegistry().getProtocols().stream()
                .map(userProtocol -> realConnection.getUserConnection((Class<UserProtocol<UserConnection>>) userProtocol.getClass()))
                .forEach(UserConnection::onConnect);*/
        if (false && this.endpoint instanceof PorkServer) {
            realConnection.send(new HandshakeInitPacket(
                    ((PorkServer) this.endpoint).getCryptographySettings(),
                    ((PorkServer) this.endpoint).getCompression()
            ));
            PorkConnection connection = realConnection.getUserConnection(PorkProtocol.class);
            connection.setState(ConnectionState.getNext(connection.getState()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("[${0}] Connection ${1} removed", this.endpoint.getName(), ctx.channel().remoteAddress());

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        String disconnectReason = realConnection.getUserConnection(PorkProtocol.class).getDisconnectReason();
        this.endpoint.getPacketRegistry().getProtocols().stream()
                .map(userProtocol -> realConnection.getUserConnection((Class<UserProtocol<UserConnection>>) userProtocol.getClass()))
                .forEach(c -> c.onDisconnect(disconnectReason));
    }
}
