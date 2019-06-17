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

package net.daporkchop.lib.network.tcp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.network.netty.NettyHandler;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.network.tcp.session.TCPNioSocket;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class TCPHandler<S extends AbstractUserSession<S>> extends NettyHandler<S, TCPNioSocket<S>> {
    public TCPHandler(TCPNioSocket<S> session) {
        super(session);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        this.session.logger().debug("Received message @ %d bytes...", buf.readableBytes());
        this.session.framer().received(this.session.userSession(), buf, (bb, channelId, protocolId) -> {
            PacketMetadata metadata = PacketMetadata.instance(Reliability.RELIABLE_ORDERED, channelId, protocolId, true);
            try (DataIn in = NettyUtil.wrapIn(bb)) {
                this.session.onReceive(in, metadata); //TODO: recycle this lambda
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                metadata.release();
            }
        });
        buf.release();
    }
}
