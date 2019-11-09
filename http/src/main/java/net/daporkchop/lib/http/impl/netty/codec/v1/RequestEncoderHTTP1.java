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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.daporkchop.lib.http.Request;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * Encodes requests for HTTP/1.1.
 *
 * @author DaPorkchop_
 */
public final class RequestEncoderHTTP1 extends MessageToMessageEncoder<Request> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Request request, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().ioBuffer();

        //request line
        buf.writeBytes(request.type().asciiName());
        buf.writeByte(' ');
        buf.writeCharSequence(request.query(), StandardCharsets.US_ASCII);
        buf.writeByte(' ');
        buf.writeBytes(BYTES_HTTP1_1);

        request.headers().forEach((name, value) -> {
            buf.writeBytes(BYTES_CRLF);
            buf.writeCharSequence(name, StandardCharsets.US_ASCII);
            buf.writeBytes(BYTES_HEADER_SEPARATOR);
            buf.writeCharSequence(value, StandardCharsets.US_ASCII);
        });

        buf.writeBytes(BYTES_2X_CRLF);
        out.add(buf);
    }
}
