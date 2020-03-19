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

package net.daporkchop.lib.http.impl.netty.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.http.header.HeaderMap;
import net.daporkchop.lib.http.message.MessageImpl;
import net.daporkchop.lib.http.util.StatusCodes;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class RequestBodyHandler extends ChannelInboundHandlerAdapter {
    protected       ByteBuf buf;
    @NonNull
    protected final HeaderMap headers;
    protected final int     contentLength;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            super.channelRead(ctx, msg);
            return;
        }

        final ByteBuf buf = this.buf;
        try {
            ByteBuf tmp = (ByteBuf) msg;
            if (tmp.readableBytes() > buf.writableBytes())  {
                throw StatusCodes.PAYLOAD_TOO_LARGE.exception();
            }
            buf.writeBytes((ByteBuf) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }

        if (!buf.isWritable())  {
            //body has been read completely
            ctx.fireChannelRead(new MessageImpl(this.headers, buf));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.buf = ctx.alloc().ioBuffer(this.contentLength, this.contentLength);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.buf.release();
        this.buf = null;
    }
}
