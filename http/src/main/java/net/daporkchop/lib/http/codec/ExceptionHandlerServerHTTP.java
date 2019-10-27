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

package net.daporkchop.lib.http.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.daporkchop.lib.http.Response;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.HTTPException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Handles exceptions on an HTTP server.
 *
 * @author DaPorkchop_
 */
public final class ExceptionHandlerServerHTTP extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        //TODO: do something else if the connection has already started transmitting a response
        StatusCode status = cause instanceof HTTPException ? ((HTTPException) cause).status() : StatusCodes.Internal_Server_Error;
        ByteBuf body = Unpooled.wrappedBuffer(String.format(
                "<html><head><title>%1$d %2$s</title></head><body><h1>%2$s</h1><p>Placeholder error message</p><hr><address>PorkLib</address></body></html>",
                status.code(),
                status.name()
        ).getBytes(StandardCharsets.US_ASCII));
        Response response = new Response.Simple(status, body, Collections.emptyMap());
        ctx.writeAndFlush(response);
        ctx.close();
    }
}
