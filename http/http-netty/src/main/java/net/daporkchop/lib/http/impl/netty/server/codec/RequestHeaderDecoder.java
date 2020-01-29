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

import com.florianingerl.util.regex.Matcher;
import com.florianingerl.util.regex.Pattern;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.daporkchop.lib.binary.chars.DirectASCIISequence;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.header.map.ArrayHeaderMap;
import net.daporkchop.lib.http.impl.netty.util.NettyHttpUtil;
import net.daporkchop.lib.http.message.MessageImpl;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.GenericHttpException;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public final class RequestHeaderDecoder extends ChannelInboundHandlerAdapter {
    protected static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("^([A-Z]+) (.*?) HTTP/1\\.[01]\r\n$");
    protected static final Pattern HEADER_PATTERN       = Pattern.compile("^\\s*([\\x20-\\x7E]*?)\\s*:\\s*([\\x20-\\x7E]*?)\\s*$", Pattern.MULTILINE);

    protected ByteBuf        buf;
    protected Query          query;
    protected ArrayHeaderMap headers;

    protected int queryEnd;

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
            throw StatusCodes.BAD_REQUEST.exception();
        } finally {
            ReferenceCountUtil.release(msg);
        }

        while (buf.readableBytes() >= 4) {
            if (buf.readByte() == '\r') {
                //carriage return
                if (buf.readByte() != '\n') {
                    //if not a newline then something is broken
                    throw StatusCodes.BAD_REQUEST.exception();
                }

                if (this.query == null) {
                    //query has not been set, we need to try and parse it

                    this.parseQuery(ctx, new DirectASCIISequence(buf.memoryAddress(), this.queryEnd = buf.readerIndex()));
                    ctx.fireChannelRead(this.query);
                } else if (buf.readByte() == '\r') {
                    //second carriage return
                    if (buf.readByte() != '\n') {
                        //if not a newline then something is broken
                        throw StatusCodes.BAD_REQUEST.exception();
                    }

                    //double CRLF, we've reached the end of the request headers

                    int queryEnd = this.queryEnd;
                    this.parseHeaders(ctx, new DirectASCIISequence(buf.memoryAddress() + queryEnd, buf.readerIndex() - queryEnd));
                    ctx.fireChannelRead(this);

                    if (this.query.method().hasRequestBody()) {
                        String contentLengthText = this.headers.getValue("content-length");
                        if (contentLengthText == null)  {
                            throw StatusCodes.LENGTH_REQUIRED.exception();
                        }

                        //retain buf so that it isn't released by handlerRemoved
                        buf.retain();
                        try {
                            ctx.pipeline().replace(this, "body", new RequestBodyHandler(this.headers, Integer.parseUnsignedInt(contentLengthText)));

                            //pass remaining buffered bytes along to request body handler
                            ctx.pipeline().fireChannelRead(buf.retain());
                        } finally {
                            buf.release();
                        }
                    } else {
                        //there is no body to be read, fire message event immediately

                        ctx.pipeline().remove(this);
                        ctx.fireChannelRead(new MessageImpl(this.headers, null));
                    }
                    return;
                }
            }
        }
    }

    private void parseQuery(ChannelHandlerContext ctx, DirectASCIISequence request) throws Exception {
        Matcher matcher = REQUEST_LINE_PATTERN.matcher(request);
        if (!matcher.find()) {
            throw StatusCodes.BAD_REQUEST.exception();
        }
        HttpMethod method = HttpMethod.LOOKUP.get(matcher.group(1));
        if (method == null) {
            throw StatusCodes.METHOD_NOT_ALLOWED.exception();
        }
        this.query = NettyHttpUtil.parseQuery(method, NettyHttpUtil.fastGroup(matcher, 2));
    }

    private void parseHeaders(ChannelHandlerContext ctx, DirectASCIISequence request) throws Exception {
        ArrayHeaderMap headers = new ArrayHeaderMap();
        Matcher matcher = HEADER_PATTERN.matcher(request);
        while (matcher.find()) {
            headers.add(matcher.group(1), matcher.group(2));
        }
        this.headers = headers;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (this.buf != null) {
            throw new IllegalStateException("buffer already set?!?");
        }

        this.buf = ctx.alloc().directBuffer(MAX_REQUEST_SIZE, MAX_REQUEST_SIZE);

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
