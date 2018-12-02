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

package net.daporkchop.lib.network.protocol.netty.tcp;

import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.netty.NettyConnection;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A wrapper on top of {@link NioSocketChannel} that allows to store extra data (i.e. implement {@link UnderlyingNetworkConnection})
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class WrapperNioSocketChannel extends NioSocketChannel implements NettyConnection {
    private final Map<Class<? extends UserProtocol>, UserConnection> connections = new IdentityHashMap<>();
    @NonNull
    private final Endpoint endpoint;
    private final TcpChannel channel = new TcpChannel(this);

    public WrapperNioSocketChannel(SelectorProvider provider, @NonNull Endpoint endpoint) {
        super(provider);
        this.endpoint = endpoint;
    }

    public WrapperNioSocketChannel(SocketChannel socket, @NonNull Endpoint endpoint) {
        super(socket);
        this.endpoint = endpoint;
    }

    public WrapperNioSocketChannel(io.netty.channel.Channel parent, SocketChannel socket, @NonNull Endpoint endpoint) {
        super(parent, socket);
        this.endpoint = endpoint;
    }

    //
    //
    // Connection implementations
    //
    //

    @Override
    public Channel openChannel(Reliability reliability) {
        return this.channel;
    }

    @Override
    public Channel getOpenChannel(int id) {
        return id == 0 ? this.channel : null;
    }

    @Override
    public Channel openChannel(Reliability reliability, int requestedId) {
        return this.channel;
    }

    //minor optimization (removes overhead of calling getOpenChannel())

    @Override
    public void send(@NonNull Packet packet, boolean blocking, Void callback) {
        this.channel.send(packet, blocking, callback);
    }

    @Override
    public Channel getDefaultChannel() {
        return this.channel;
    }

    @Override
    public Channel getControlChannel() {
        return this.channel;
    }
}
