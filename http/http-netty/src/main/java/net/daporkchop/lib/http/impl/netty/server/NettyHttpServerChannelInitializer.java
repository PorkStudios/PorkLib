/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.impl.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.http.impl.netty.server.codec.HttpServerEventHandler;
import net.daporkchop.lib.http.impl.netty.server.codec.HttpServerExceptionHandler;
import net.daporkchop.lib.http.impl.netty.server.codec.RequestHeaderDecoder;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ChannelHandler.Sharable
public final class NettyHttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    public static final NettyHttpServerChannelInitializer INSTANCE = new NettyHttpServerChannelInitializer();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        NettyHttpServer server = ch.attr(NettyHttpServer.ATTR_SERVER).get();
        server.channels.add(ch);

        ch.pipeline()
                .addLast("decode", new RequestHeaderDecoder())
                .addLast("handle", HttpServerEventHandler.INSTANCE)
                .addLast("exception", HttpServerExceptionHandler.INSTANCE);

        //server.logger.debug("Incoming connection from %s", ch.remoteAddress());
    }
}
