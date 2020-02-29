/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
