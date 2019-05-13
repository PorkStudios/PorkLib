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

package net.daporkchop.lib.network.transport.tcp.endpoint;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.tcp.pipeline.TCPChannelInitializer;
import net.daporkchop.lib.network.transport.tcp.WrapperNioSocketChannel;

/**
 * @author DaPorkchop_
 */
public class TCPClient extends TCPEndpoint<PClient, WrapperNioSocketChannel> implements PClient {
    public TCPClient(@NonNull ClientBuilder builder) {
        super(builder);

        try {
            Bootstrap bootstrap = new Bootstrap();
            if (builder.group() != null) {
                bootstrap.group(builder.group());
            } else {
                bootstrap.group(new NioEventLoopGroup(0, builder.executor()));
            }
            bootstrap.channelFactory(() -> new WrapperNioSocketChannel(this));
            bootstrap.handler(new TCPChannelInitializer<>(this))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);

            this.channel = (WrapperNioSocketChannel) bootstrap.connect(builder.address()).channel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NetSession internalSession() {
        return this.channel;
    }
}
