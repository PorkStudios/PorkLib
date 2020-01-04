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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.daporkchop.lib.binary.oio.appendable.PAppendable;
import net.daporkchop.lib.binary.oio.appendable.UTF8ByteBufAppendable;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.HttpException;
import net.daporkchop.lib.logging.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Formatter;
import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@ChannelHandler.Sharable
public final class HttpServerExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ByteBuf bodyBuf = ctx.alloc().ioBuffer();
        PAppendable out = new UTF8ByteBufAppendable(bodyBuf, "\n");
        Formatter fmt = new Formatter(out);

        StatusCode status = StatusCodes.Internal_Server_Error;
        if (cause instanceof HttpException && ((HttpException) cause).status() != null) {
            status = ((HttpException) cause).status();
        }

        Object[] args = {
                status.code(),
                status.msg(),
                status.errorMessage(),
                -1,
                PorkUtil.PORKLIB_VERSION,
                PlatformInfo.OPERATING_SYSTEM,
                ((InetSocketAddress) ctx.channel().localAddress()).getHostString(),
                ((InetSocketAddress) ctx.channel().localAddress()).getPort()
        };
        fmt.format("<html><head><title>%1$d %2$s</title></head><body><h1>%1$d %2$s</h1>", args);
        if (args[2] != null)    {
            fmt.format("<p>%3$s</p>", args);
        }
        if (cause != null)  {
            out.append("<code><pre>");
            Logger.getStackTrace(cause, (IOConsumer<String>) out::appendLn);
            out.append("</pre></code>");
        }
        fmt.format("<hr><address>PorkLib/%5$s (%6$s) Server at %7$s port %8$d</body></html>", args);

        ByteBuf headersBuf = ctx.alloc().ioBuffer();
        fmt = new Formatter(new UTF8ByteBufAppendable(headersBuf));
        args[3] = bodyBuf.readableBytes();
        fmt.format("HTTP/1.1 %1$d %2$s\r\nContent-length: %4$d\r\nContent-type: text/html; charset=UTF-8\r\n\r\n", args);

        ctx.channel().write(headersBuf);
        ctx.channel().writeAndFlush(bodyBuf);
        ctx.channel().close();
    }
}
