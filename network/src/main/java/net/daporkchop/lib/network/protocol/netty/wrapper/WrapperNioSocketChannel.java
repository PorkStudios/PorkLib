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

package net.daporkchop.lib.network.protocol.netty.wrapper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.pork.packet.DisconnectPacket;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class WrapperNioSocketChannel extends NioSocketChannel implements UnderlyingNetworkConnection {
    private final Map<Class<? extends UserProtocol>, UserConnection> connections = new IdentityHashMap<>();
    @NonNull
    private final Endpoint endpoint;

    public WrapperNioSocketChannel(SelectorProvider provider, @NonNull Endpoint endpoint) {
        super(provider);
        this.endpoint = endpoint;
    }

    public WrapperNioSocketChannel(SocketChannel socket, @NonNull Endpoint endpoint) {
        super(socket);
        this.endpoint = endpoint;
    }

    public WrapperNioSocketChannel(Channel parent, SocketChannel socket, @NonNull Endpoint endpoint) {
        super(parent, socket);
        this.endpoint = endpoint;
    }

    // Connection implementations
    @Override
    public void closeConnection(String reason) {
        super.writeAndFlush(new DisconnectPacket(reason));
        super.close();
    }

    @Override
    public boolean isConnected() {
        return super.isActive();
    }

    @Override
    public void send(@NonNull Packet packet, boolean blocking) {
        ChannelFuture future = super.write(packet);
        if (blocking) {
            future.syncUninterruptibly();
        }
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.remoteAddress();
    }

    // UnderlyingNetworkConnection implementations
    @Override
    public void disconnectAtNetworkLevel() {
        this.close();//.syncUninterruptibly();
    }
}
