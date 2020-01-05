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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.daporkchop.lib.http.header.map.ArrayHeaderMap;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.GenericHttpException;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public final class RequestHeaderDecoder extends ChannelInboundHandlerAdapter {
    protected ByteBuf        buf;
    protected Query          query;
    protected ArrayHeaderMap headers;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            super.channelRead(ctx, msg);
            return;
        }

        final ByteBuf buf = this.buf;
        try {
            buf.writeBytes((ByteBuf) msg);
        } catch (IndexOutOfBoundsException e) {
            throw GenericHttpException.Bad_Request;
        } finally {
            ReferenceCountUtil.release(msg);
        }

        throw new GenericHttpException(StatusCodes.Im_A_Teapot);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (this.buf != null) {
            throw new IllegalStateException("buffer already set?!?");
        }

        this.buf = ctx.alloc().ioBuffer(MAX_REQUEST_SIZE, MAX_REQUEST_SIZE);

        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.buf == null) {
            throw new IllegalStateException("buffer already released?!?");
        }

        this.buf.release();
        this.buf = null;

        super.handlerRemoved(ctx);
    }
}
