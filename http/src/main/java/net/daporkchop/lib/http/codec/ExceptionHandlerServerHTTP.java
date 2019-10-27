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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.Response;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.HTTPException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Handles exceptions on an HTTP server.
 *
 * @author DaPorkchop_
 */
public final class ExceptionHandlerServerHTTP extends ChannelInboundHandlerAdapter {
    private static final byte[] BYTES_HEADER_BEGIN = "<html><head><title>".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_HEADER_END = "</title></head><body><h1>".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_TITLE_END = "</h1>".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_TEXT_BEGIN = "<p>".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_TEXT_END = "</p>".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_FOOTER_BEGIN = "<hr><address>PorkLib v0.4.0-SNAPSHOT at ".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_FOOTER_PORT = " port ".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BYTES_FOOTER_END = "</address></body></html>".getBytes(StandardCharsets.US_ASCII);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //TODO: do something else if the connection has already started transmitting a response
        for (Throwable next = cause; next != null; next = next.getCause())  {
            if (next instanceof HTTPException)  {
                cause = next;
                break;
            }
        }
        HTTPException http = cause instanceof HTTPException ? (HTTPException) cause : null;

        StatusCode status = http != null ? http.status() : StatusCodes.Internal_Server_Error;

        ByteBuf body = ctx.alloc().ioBuffer();

        body.writeBytes(BYTES_HEADER_BEGIN);
        body.writeCharSequence(Integer.toUnsignedString(status.code()), StandardCharsets.UTF_8);
        body.writeByte((byte) ' ');
        body.writeCharSequence(status.msg(), StandardCharsets.UTF_8);
        body.writeBytes(BYTES_HEADER_END);
        body.writeCharSequence(status.msg(), StandardCharsets.UTF_8);
        body.writeBytes(BYTES_TITLE_END);

        if (status.errorMessage() != null)  {
            body.writeBytes(BYTES_TEXT_BEGIN);
            body.writeCharSequence(status.errorMessage(), StandardCharsets.UTF_8);
            body.writeBytes(BYTES_TEXT_END);
        }

        body.writeBytes(BYTES_FOOTER_BEGIN);
        {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().localAddress();
            body.writeCharSequence(address.getHostString(), StandardCharsets.UTF_8);
            body.writeBytes(BYTES_FOOTER_PORT);
            body.writeCharSequence(Integer.toUnsignedString(address.getPort()), StandardCharsets.UTF_8);
        }
        body.writeBytes(BYTES_FOOTER_END);

        Response response = new Response.Simple(status, body, Collections.singletonMap("Content-Type", "text/html; charset=utf-8"));
        ctx.writeAndFlush(response);
        ctx.close();
    }
}
