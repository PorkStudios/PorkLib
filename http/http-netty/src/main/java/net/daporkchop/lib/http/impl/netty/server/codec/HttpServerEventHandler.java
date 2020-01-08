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
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.binary.oio.appendable.ASCIIByteBufAppendable;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.entity.content.encoding.ContentEncoding;
import net.daporkchop.lib.http.entity.content.encoding.StandardContentEncoding;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;
import net.daporkchop.lib.http.header.map.MutableHeaderMap;
import net.daporkchop.lib.http.impl.netty.server.NettyHttpServer;
import net.daporkchop.lib.http.impl.netty.server.NettyResponseBuilder;
import net.daporkchop.lib.http.impl.netty.util.TransferSessionAsFileRegion;
import net.daporkchop.lib.http.message.Message;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.request.query.UnsetQuery;
import net.daporkchop.lib.http.util.exception.GenericHttpException;

import java.util.Formatter;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ChannelHandler.Sharable
public final class HttpServerEventHandler extends ChannelDuplexHandler {
    public static final HttpServerEventHandler INSTANCE = new HttpServerEventHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyHttpServer server = ctx.channel().attr(NettyHttpServer.ATTR_SERVER).get();

        if (msg instanceof Query) {
            if (!ctx.channel().attr(NettyHttpServer.ATTR_QUERY).compareAndSet(UnsetQuery.INSTANCE, (Query) msg)) {
                throw new IllegalStateException("Query was already set!");
            }
            server.handler().handleQuery((Query) msg);
        } else if (msg instanceof RequestHeaderDecoder) {
            RequestHeaderDecoder decoder = (RequestHeaderDecoder) msg;

            server.handler().handleHeaders(decoder.query, decoder.headers);
        } else if (msg instanceof Message) {
            NettyResponseBuilder responseBuilder = new NettyResponseBuilder();
            server.handler().handle(ctx.channel().attr(NettyHttpServer.ATTR_QUERY).get(), (Message) msg, responseBuilder);
            ctx.channel().write(responseBuilder, ctx.voidPromise());
        } else {
            throw new IllegalArgumentException("Cannot handle type: " + PorkUtil.className(msg));
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            //always forward bytebufs down the pipeline

            ctx.write(msg, promise);
            return;
        }

        NettyHttpServer server = ctx.channel().attr(NettyHttpServer.ATTR_SERVER).get();
        NettyResponseBuilder response = (NettyResponseBuilder) msg;

        StatusCode status = response.status();
        if (status == null) {
            server.logger().error("Response to %s has no status code!", ctx.channel().remoteAddress());
            throw GenericHttpException.Internal_Server_Error;
        }

        HttpEntity body = response.body();
        MutableHeaderMap headers = response.headers();
        TransferSession session = body.newSession();
        try {
            long contentLength = session.length();
            TransferEncoding transferEncoding = session.transferEncoding();
            if (transferEncoding == StandardTransferEncoding.identity) {
                if (contentLength < 0L) {
                    server.logger().debug("Using \"transfer-encoding: identity\" for response with unknown content length!");
                    throw GenericHttpException.Internal_Server_Error;
                }
                headers.put("content-length", String.valueOf(contentLength));
            } else if (false && transferEncoding == StandardTransferEncoding.chunked) {
                if (contentLength >= 0L) {
                    server.logger().debug("Using \"transfer-encoding: chunked\" for response with known content length!");
                    throw GenericHttpException.Internal_Server_Error;
                }
            } else {
                server.logger().debug("Using unsupported \"transfer-encoding: %s\" for response with content length %d!", transferEncoding.name(), contentLength);
                throw GenericHttpException.Internal_Server_Error;
            }

            if (transferEncoding != StandardTransferEncoding.identity) {
                headers.put("transfer-encoding", transferEncoding.name());
            }

            ContentType contentType = body.type();
            headers.put("content-type", contentType.formatted());

            ContentEncoding contentEncoding = body.encoding();
            if (contentEncoding != StandardContentEncoding.identity) {
                headers.put("content-encoding", contentEncoding.name());
            }

            headers.put("connection", "close");

            ByteBuf buf = ctx.alloc().heapBuffer();
            ASCIIByteBufAppendable out = new ASCIIByteBufAppendable(buf);
            Formatter fmt = new Formatter(out);
            Object[] args = new Object[2];

            args[0] = status.code();
            args[1] = status.msg();
            fmt.format("HTTP/1.1 %1$d %2$s\r\n", args);

            //server.logger().debug("Response headers:");
            response.headers().forEach((key, value) -> {
                args[0] = key;
                args[1] = value;
                fmt.format("%s: %s\r\n", args);
                //server.logger().debug("  %s: %s", args);
            });
            out.append("\r\n");

            if (!ctx.channel().attr(NettyHttpServer.ATTR_RESPONDED).compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
                server.logger().error("Already sent response to request from %s!", ctx.channel().remoteAddress());
                ctx.close();
                return;
            }

            ctx.write(buf, ctx.voidPromise());

            if (contentLength != 0L) {
                if (session.hasByteBuf()) {
                    ctx.write(session.getByteBuf(), ctx.voidPromise());
                } else {
                    ctx.write(new TransferSessionAsFileRegion(session).retain(), ctx.voidPromise());
                }
            }
            ctx.flush();
            ctx.close();
        } finally {
            session.release();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        NettyHttpServer server = ctx.channel().attr(NettyHttpServer.ATTR_SERVER).get();
        server.logger().debug("Connection from %s closed", ctx.channel().remoteAddress());

        super.channelUnregistered(ctx);
    }
}
