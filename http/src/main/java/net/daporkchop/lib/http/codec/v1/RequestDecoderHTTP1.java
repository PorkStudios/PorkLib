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

package net.daporkchop.lib.http.codec.v1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.daporkchop.lib.binary.chars.ByteBufLatinSequence;
import net.daporkchop.lib.http.Request;
import net.daporkchop.lib.http.RequestType;
import net.daporkchop.lib.http.util.exception.InvalidRequestException;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static net.daporkchop.lib.http.util.Constants.*;

/**
 * Decodes HTTP/1.1 requests.
 *
 * @author DaPorkchop_
 */
public final class RequestDecoderHTTP1 extends ByteToMessageDecoder {
    private RequestType type;
    private String query;

    private List<ByteBuf> headers = new LinkedList<>();

    private int lastIndex = 0;

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
                if (this.type == null) {
                    //request line was not sent
                    throw InvalidRequestException.INSTANCE;
                }
                Map<String, String> headers = this.headers.stream()
                        .map(ByteBufLatinSequence::new)
                        .map(PATTERN_HEADER::matcher)
                        .peek(matcher -> {
                            if (!matcher.find()) {
                                throw InvalidRequestException.INSTANCE;
                            }
                        })
                        .collect(Collectors.toMap(m -> m.group(1), m -> m.group(2)));
                out.add(new Request.Simple(this.type, this.query, headers));
                //TODO: replace self with logical pipeline member and forward any remaining data down the pipeline
                in.skipBytes(in.readableBytes());
                ctx.pipeline().remove(this);
                return;
            }

            if (this.type == null) {
                //attempt to read request line
                CharSequence seq = new ByteBufLatinSequence(in.slice(in.readerIndex(), next - in.readerIndex()));
                Matcher matcher = PATTERN_REQUEST.matcher(seq);
                if (!matcher.find()) throw InvalidRequestException.INSTANCE;
                this.type = RequestType.valueOf(matcher.group(1));
                this.query = matcher.group(2);
            } else {
                int i = in.readerIndex() + 1;
                this.headers.add(in.slice(i, next - i));
            }
            in.readerIndex(this.lastIndex = next + 1);

            next = in.indexOf(this.lastIndex, in.writerIndex() - 1, (byte) '\r');
            if (next != -1 && in.getByte(next + 1) != (byte) '\n') {
                next = -1; // \r doesn't count if not followed by \n
            }
        }
        this.lastIndex = in.writerIndex() - 1;
    }
}
