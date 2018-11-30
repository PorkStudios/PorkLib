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

import io.netty.channel.group.ChannelGroup;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.channel.ServerChannel;
import net.daporkchop.lib.network.conn.UnderlyingNetworkConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.Packet;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A base implementation of {@link ServerChannel} for Netty-based protocol managers
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class NettyServerChannel implements ServerChannel {
    @NonNull
    private final ChannelGroup channels;
    @NonNull
    private final Server server;

    @Override
    public void broadcast(@NonNull Packet packet, boolean blocking) {
        if (blocking)   {
            this.channels.writeAndFlush(packet).syncUninterruptibly();
        } else {
            this.channels.writeAndFlush(packet);
        }
    }

    @Override
    public Collection<UnderlyingNetworkConnection> getUnderlyingNetworkConnections() {
        return this.channels.stream().map(UnderlyingNetworkConnection.class::cast).collect(Collectors.toList());
    }

    @Override
    public Stream<UnderlyingNetworkConnection> getUnderlyingNetworkConnectionsAsStream() {
        return this.channels.stream().map(UnderlyingNetworkConnection.class::cast);
    }
}
