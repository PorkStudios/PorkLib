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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.util.reliability.Reliability;

/**
 * A simple implementation of {@link Channel} for a TCP session.
 * <p>
 * As TCP only supports full ordering and reliability, packets sent through this channel will not respect the reliability setting
 * given by the user.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class TcpChannel implements Channel {
    @NonNull
    private final WrapperNioSocketChannel realChannel;

    @Override
    public void send(@NonNull Packet packet, boolean blocking, Void callback, Reliability reliability) {
        this.realChannel.send(packet, blocking, callback);
    }

    @Override
    public Reliability getReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    public boolean isReliabilityRespected() {
        return false;
    }

    @Override
    public int getId() {
        return 0; //TCP only has one channel
    }

    @Override
    public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return this.realChannel.getUserConnection(protocolClass);
    }
}
