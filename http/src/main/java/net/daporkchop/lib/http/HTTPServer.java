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

package net.daporkchop.lib.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.http.codec.v1.RequestDecoderHTTP1;
import net.daporkchop.lib.http.codec.v1.ResponseEncoderHTTP1;

import java.util.Scanner;

/**
 * Test class for things, will probably be turned into an actual HTTP server helper at some point.
 *
 * @author DaPorkchop_
 */
public class HTTPServer {
    public static void main(String... args) throws Exception {
        Channel ch = new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channelFactory(NioServerSocketChannel::new)
                .childHandler(new ChannelInitializer()    {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("http_decode", new RequestDecoderHTTP1())
                                .addLast("http_encode", new ResponseEncoderHTTP1())
                        .addLast("handle", new ChannelInboundHandlerAdapter()   {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof Request) {
                                    Request request = (Request) msg;
                                    System.out.printf("%s to \"%s\" from %s\n", request.type(), request.query(), ctx.channel().remoteAddress());
                                    ctx.channel().writeAndFlush("Hello World!").addListener(f -> ctx.channel().close());
                                } else {
                                    System.out.printf("[ERROR] Received invalid message (type: \"%s\"): %s\n", PorkUtil.className(msg), msg);
                                }
                            }
                        });
                    }
                })
                .bind(8080).syncUninterruptibly().channel();

        new Scanner(System.in).nextLine();

        ch.close().addListener(c -> System.exit(0));
    }
}
