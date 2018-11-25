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

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.SocketUtils;
import lombok.*;
import net.daporkchop.lib.network.endpoint.Endpoint;

import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class WrapperNioServerSocketChannel extends NioServerSocketChannel/* implements UnderlyingNetworkConnection*/ {
    @NonNull
    private final Endpoint endpoint;

    /*@Override
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
        if (blocking)   {
            future.syncUninterruptibly();
        }
    }*/

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(this.javaChannel());

        try {
            if (ch != null) {
                WrapperNioSocketChannel wrapper = new WrapperNioSocketChannel(this, ch, this.endpoint);
                buf.add(wrapper);
                return 1;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Failed to create a new channel from an accepted socket.");

            try {
                ch.close();
            } catch (Throwable t2) {
                t2.printStackTrace();
                System.err.println("Failed to close a socket.");
            }
        }

        return 0;
    }
}
