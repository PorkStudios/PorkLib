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
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.netty.TCPChannelInitializer;
import net.daporkchop.lib.network.tcp.session.TCPNioServerSocket;
import net.daporkchop.lib.network.tcp.session.TCPNioSocket;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DaPorkchop_
 */
public class TCPServer<S extends AbstractUserSession<S>> extends TCPEndpoint<PServer<S>, S, TCPNioServerSocket<S>> implements PServer<S> {
    protected final Map<TCPNioSocket<S>, S> sessions = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public TCPServer(@NonNull ServerBuilder<S> builder) {
        super(builder);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .option(ChannelOption.ALLOCATOR, this.transportEngine.alloc())
                    .childOption(ChannelOption.ALLOCATOR, this.transportEngine.alloc())
                    .group(this.group)
                    .channelFactory(() -> new TCPNioServerSocket<>(this))
                    .childHandler(new TCPChannelInitializer<>(
                            this,
                            s -> this.sessions.put(s, s.userSession()),
                            this.sessions::remove
                    ));

            this.transportEngine.clientOptions().forEach(bootstrap::childOption);
            this.transportEngine.serverOptions().forEach(bootstrap::option);

            this.channel = (TCPNioServerSocket<S>) bootstrap.bind(builder.bind()).syncUninterruptibly().channel();
        } catch (Exception e) {
            this.closeAsync();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<S> sessions() {
        return Collections.unmodifiableCollection(this.sessions.values());
    }
}
