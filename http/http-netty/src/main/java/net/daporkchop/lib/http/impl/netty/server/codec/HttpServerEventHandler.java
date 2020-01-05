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

package net.daporkchop.lib.http.impl.netty.server.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.http.impl.netty.server.NettyHttpServer;
import net.daporkchop.lib.http.impl.netty.server.NettyResponseBuilder;
import net.daporkchop.lib.http.impl.netty.util.ParsedIncomingHttpRequest;
import net.daporkchop.lib.http.util.exception.GenericHttpException;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ChannelHandler.Sharable
public final class HttpServerEventHandler extends ChannelDuplexHandler {
    public static final HttpServerEventHandler INSTANCE = new HttpServerEventHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ParsedIncomingHttpRequest)) {
            super.channelRead(ctx, msg);
        }

        ParsedIncomingHttpRequest request = (ParsedIncomingHttpRequest) msg;
        NettyHttpServer server = ctx.channel().attr(NettyHttpServer.ATTR_SERVER).get();
        NettyResponseBuilder responseBuilder = new NettyResponseBuilder();

        server.handler().handle(request.query(), request.headers(), responseBuilder);

        //TODO: something
        throw GenericHttpException.Internal_Server_Error;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        NettyHttpServer server = ctx.channel().attr(NettyHttpServer.ATTR_SERVER).get();
        server.logger().debug("Connection from %s closed", ctx.channel().remoteAddress());

        super.channelUnregistered(ctx);
    }
}
