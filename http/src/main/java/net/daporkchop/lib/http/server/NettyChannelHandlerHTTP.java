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

package net.daporkchop.lib.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.http.HTTPVersion;
import net.daporkchop.lib.http.Request;
import net.daporkchop.lib.http.RequestMethod;
import net.daporkchop.lib.http.parameter.ParameterRegistry;
import net.daporkchop.lib.http.parameter.Parameters;
import net.daporkchop.lib.http.server.handler.Response;
import net.daporkchop.lib.logging.Logging;

import java.util.LinkedList;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class NettyChannelHandlerHTTP extends ChannelInboundHandlerAdapter implements Logging {
    @NonNull
    private final HTTPServer server;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("Incoming connection: ${0}", ctx.channel().remoteAddress());
        this.server.channels.add(ctx.channel());

        SslHandler sslHandler = this.server.sslHandlerSupplier.apply(ctx.channel());
        if (sslHandler != null) {
            ctx.channel().pipeline().addFirst("ssl", sslHandler);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.trace("Connection closed: ${0}", ctx.channel().remoteAddress());
        this.server.channels.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        //logger.debug("Received message: ${0}", ((ByteBuf) msg).toString(UTF8.utf8));
        RequestReader reader = new RequestReader((ByteBuf) msg);
        RequestMethod method = RequestMethod.valueOf(reader.readUntilSpace());
        String path = reader.readUntilSpace();
        //logger.debug("Skipped ${0} bytes!", );
        reader.skipUntil('\r');
        reader.skip(1);
        Parameters parameters;
        {
            List<String> params = new LinkedList<>();
            while (reader.next() != '\r') {
                //logger.debug("Next: ${0}", reader.next());
                params.add(reader.readUntil('\r'));
                reader.skip(1);
            }
            parameters = new Parameters(params, ParameterRegistry.def());
        }
        try (Response response = new Response(channel)) {
            this.server.getHandler(path).handle(new Request(HTTPVersion.V1_1, method, parameters, path, null), response);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause != null) {
            logger.error(cause);
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
