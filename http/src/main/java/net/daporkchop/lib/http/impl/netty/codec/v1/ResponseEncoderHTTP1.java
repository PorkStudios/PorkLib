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

package net.daporkchop.lib.http.impl.netty.codec.v1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.daporkchop.lib.http.Response;
import net.daporkchop.lib.http.util.ConnectionState;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * Encodes HTTP/1.1 responses.
 *
 * @author DaPorkchop_
 */
public final class ResponseEncoderHTTP1 extends MessageToMessageEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, List<Object> out) throws Exception {
        ctx.channel().attr(KEY_STATE).set(ConnectionState.RESPONSE_HEADERS);
        out.add(Unpooled.wrappedBuffer(BYTES_HTTP1_1));
        out.add(response.status().encodedValue());

        //write headers
        //TODO: optimize this a lot!
        ByteBuf buf = ctx.alloc().ioBuffer();

        //temporary: implicitly add Content-Length header to all responses
        //TODO: remove this (or re-implement it in some better way)
        //TODO: make this work
        /*buf.writeBytes(BYTES_CRLF);
        buf.writeCharSequence("Content-Length", StandardCharsets.US_ASCII);
        buf.writeBytes(BYTES_HEADER_SEPARATOR);
        buf.writeCharSequence(String.valueOf(response.body().readableBytes()), StandardCharsets.US_ASCII);*/

        response.headers().forEach((name, value) -> {
            buf.writeBytes(BYTES_CRLF);
            buf.writeCharSequence(name, StandardCharsets.US_ASCII);
            buf.writeBytes(BYTES_HEADER_SEPARATOR);
            buf.writeCharSequence(value, StandardCharsets.US_ASCII);
        });

        out.add(buf.writeBytes(BYTES_2X_CRLF));

        ctx.channel().attr(KEY_STATE).set(ConnectionState.RESPONSE_BODY);
        //TODO: make this work
        //out.add(response.body());
    }
}
