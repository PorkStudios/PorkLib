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
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.pork.PorkConnection;
import net.daporkchop.lib.network.protocol.pork.PorkProtocol;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class NettyHandler extends ChannelInboundHandlerAdapter {
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
            throw new IllegalArgumentException(String.format("Expected %s, but got %s!", Packet.class.getCanonicalName(), msg.getClass().getCanonicalName()));
        }

        Packet packet = (Packet) msg;
        PacketRegistry registry = this.endpoint.getPacketRegistry();
        Class<? extends UserProtocol<UserConnection>> protocolClass = registry.getOwningProtocol(packet.getClass());
        if (protocolClass == null) {
            throw new IllegalArgumentException(String.format("Unregistered inbound packet: %s", packet.getClass().getCanonicalName()));
        }

        UserConnection connection = ((UnderlyingNetworkConnection) ctx.channel()).getUserConnection(protocolClass);
        System.out.printf("Handling %s...\n", packet.getClass().getCanonicalName());
        registry.getCodec(packet.getClass()).handle(packet, connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause != null) {
            cause.printStackTrace();
        }

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        PorkConnection porkConnection = realConnection.getUserConnection(PorkProtocol.class);
        porkConnection.setDisconnectReason(cause == null ? "Unknown exception" : String.format("%s: %s", cause.getClass().getCanonicalName(), cause.getMessage()));

        super.exceptionCaught(ctx, cause);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try {
            System.out.printf("[%s] New connection: %s\n", this.endpoint instanceof PorkServer ? "Server" : "Client", ctx.channel().localAddress().toString());
        } catch (NullPointerException e) {
            System.out.printf("[%s] new connection\n", this.endpoint instanceof PorkServer ? "Server" : "Client");
        }

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        this.endpoint.getPacketRegistry().getProtocols().stream()
                .map(userProtocol -> realConnection.getUserConnection((Class<UserProtocol<UserConnection>>) userProtocol.getClass()))
                .forEach(UserConnection::onConnect);

        super.channelRegistered(ctx);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        try {
            System.out.printf("[%s] Connection removed: %s\n", this.endpoint instanceof PorkServer ? "Server" : "Client", ctx.channel().remoteAddress().toString());
        } catch (NullPointerException e) {
            System.out.printf("[%s] connection removed\n", this.endpoint instanceof PorkServer ? "Server" : "Client");
        }

        UnderlyingNetworkConnection realConnection = (UnderlyingNetworkConnection) ctx.channel();
        String disconnectReason = realConnection.getUserConnection(PorkProtocol.class).getDisconnectReason();
        this.endpoint.getPacketRegistry().getProtocols().stream()
                .map(userProtocol -> realConnection.getUserConnection((Class<UserProtocol<UserConnection>>) userProtocol.getClass()))
                .forEach(c -> c.onDisconnect(disconnectReason));

        super.channelUnregistered(ctx);
    }
}
