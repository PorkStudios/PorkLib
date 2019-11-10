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
import io.netty.handler.codec.ByteToMessageDecoder;
import net.daporkchop.lib.binary.chars.ByteBufASCIISequence;
import net.daporkchop.lib.http.Request;
import net.daporkchop.lib.http.RequestMethod;
import net.daporkchop.lib.http.util.exception.GenericHTTPException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * Decodes HTTP/1.1 requests.
 *
 * @author DaPorkchop_
 */
public final class RequestDecoderHTTP1 extends ByteToMessageDecoder {
    private RequestMethod method;
    private String        query;

    private Map<String, String> headers;

    private int lastIndex;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        super.setCumulator(COMPOSITE_CUMULATOR); //prevent lots of copying for no reason
        this.headers = new HashMap<>();
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0(ctx);

        //set everything to null
        //this allows a safe reset if this instance is re-used, or if not it can at least help the garbage collector a bit
        this.method = null;
        this.query = null;
        this.headers = null;
        this.lastIndex = 0;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int next = in.indexOf(this.lastIndex, in.writerIndex() - 1, (byte) '\r');
        if (next != -1 && in.getByte(next + 1) != (byte) '\n') {
            next = -1; // \r doesn't count if not followed by \n
        }
        while (next != -1) {
            if (next - 1 == in.readerIndex()) {
                //two newlines immediately after each other, end of headers
                //validate what we have so far
                if (this.method == null) {
                    //request line was not sent
                    throw GenericHTTPException.Bad_Request;
                }
                //TODO: have body actually be a Source which can read from the incoming data
                out.add(new Request.Simple(this.method, this.query, Collections.unmodifiableMap(this.headers), null));

                //TODO: replace self with next required pipeline member and forward any remaining data down the pipeline
                in.skipBytes(in.readableBytes());
                ctx.pipeline().remove(this);
                return;
            }

            if (this.headers.size() >= MAX_HEADER_COUNT)    {
                //if this is true there are already too many headers
                throw GenericHTTPException.Request_Header_Fields_Too_Large;
            }

            if (this.method == null) {
                //attempt to read request line
                int len = next - in.readerIndex();
                if (len > MAX_QUERY_SIZE)   {
                    throw GenericHTTPException.URI_Too_Long;
                }
                Matcher matcher = PATTERN_REQUEST.matcher(new ByteBufASCIISequence(in.slice(in.readerIndex(), len)));
                if (!matcher.find()) {
                    throw GenericHTTPException.Bad_Request;
                }
                this.method = RequestMethod.valueOf(matcher.group(1));
                this.query = matcher.group(2);
            } else {
                int i = in.readerIndex() + 1;
                int len = next - i;
                if (len > MAX_HEADER_SIZE)  {
                    throw GenericHTTPException.Request_Header_Fields_Too_Large;
                }
                Matcher matcher = PATTERN_HEADER.matcher(new ByteBufASCIISequence(in.slice(i, len)));
                if (!matcher.find())    {
                    throw GenericHTTPException.Bad_Request;
                }
                this.headers.put(matcher.group(1), matcher.group(2));
            }
            in.readerIndex(this.lastIndex = next + 1);

            next = in.indexOf(this.lastIndex, in.writerIndex() - 1, (byte) '\r');
            if (next != -1 && in.getByte(next + 1) != (byte) '\n') {
                next = -1; // \r doesn't count if not followed by \n
            }
        }

        if (this.method == null)  {
            //request line has not been read completely
            if (in.readableBytes() >= MAX_QUERY_SIZE)   {
                throw GenericHTTPException.URI_Too_Long;
            }
        } else {
            //we're reading headers
            if (in.readableBytes() >= MAX_HEADER_SIZE)  {
                throw GenericHTTPException.Request_Header_Fields_Too_Large;
            }
        }

        this.lastIndex = in.writerIndex() - 1;
    }
}
