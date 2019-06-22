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

package net.daporkchop.lib.network.tcp.endpoint;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.netty.util.group.PorkChannelGroup;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.netty.TCPChannelInitializer;
import net.daporkchop.lib.network.tcp.session.TCPNioServerSocket;
import net.daporkchop.lib.network.tcp.session.TCPNioSocket;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.group.SessionFilter;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class TCPServer<S extends AbstractUserSession<S>> extends TCPMultiEndpoint<PServer<S>, S, TCPNioServerSocket<S>, ServerBuilder<S>> implements PServer<S> {
    public TCPServer(@NonNull ServerBuilder<S> builder) {
        super(builder);
    }

    @Override
    protected ChannelFuture openChannel(@NonNull ServerBuilder<S> builder) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .option(ChannelOption.ALLOCATOR, this.transportEngine.alloc())
                .childOption(ChannelOption.ALLOCATOR, this.transportEngine.alloc())
                .group(this.group)
                .channelFactory(() -> new TCPNioServerSocket<>(this))
                .childHandler(new TCPChannelInitializer<>(this, this.sessions::add, this.sessions::remove));

        this.transportEngine.clientOptions().forEach(bootstrap::childOption);
        this.transportEngine.serverOptions().forEach(bootstrap::option);

        return bootstrap.bind(builder.bind());
    }
}
