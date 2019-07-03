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

package net.daporkchop.lib.network.tcp.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.TCPEngine;
import net.daporkchop.lib.network.tcp.endpoint.TCPEndpoint;
import net.daporkchop.lib.network.tcp.session.TCPNioSocket;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class TCPChannelInitializer<E extends TCPEndpoint<?, S, ?, ?>, S extends AbstractUserSession<S>> extends ChannelInitializer<TCPNioSocket<S>> {
    @NonNull
    protected final E endpoint;
    @NonNull
    protected final Consumer<TCPNioSocket<S>> addedCallback;
    @NonNull
    protected final Consumer<TCPNioSocket<S>> removedCallback;

    public TCPChannelInitializer(@NonNull E endpoint) {
        this(endpoint,
                ch -> {
                },
                ch -> {
                });
    }

    @Override
    protected void initChannel(@NonNull TCPNioSocket<S> channel) throws Exception {
        channel.pipeline()
                .addLast("write", new TCPWriter<>(channel))
                .addLast("handle", new TCPHandler<>(channel));

        TCPEngine engine = this.endpoint.transportEngine();
        if (channel.incoming()) {
            if (engine.sslServerContext() != null)  {
                channel.pipeline().addFirst("ssl", engine.sslServerContext().newHandler(channel.alloc()));
            }
        } else {
            if (engine.sslClientContext() != null)  {
                InetSocketAddress address = channel.address();
                if (address != null)    {
                    channel.pipeline().addFirst("ssl", engine.sslClientContext().newHandler(channel.alloc(), address.getHostString(), address.getPort()));
                } else {
                    channel.logger().warn("No target address found, but client SSL is enabled!");
                    channel.pipeline().addFirst("ssl", engine.sslClientContext().newHandler(channel.alloc()));
                }
            }
        }

        this.addedCallback.accept(channel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        this.removedCallback.accept((TCPNioSocket) ctx.channel());
    }
}
