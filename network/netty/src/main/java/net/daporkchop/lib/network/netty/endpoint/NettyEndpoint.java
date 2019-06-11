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

package net.daporkchop.lib.network.netty.endpoint;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.endpoint.builder.EndpointBuilder;
import net.daporkchop.lib.network.netty.LoopPool;
import net.daporkchop.lib.network.netty.NettyEngine;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.SessionFactory;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class NettyEndpoint<Impl extends PEndpoint<Impl, S>, S extends AbstractUserSession<S>, C extends Channel, E extends NettyEngine> implements PEndpoint<Impl, S> {
    protected final E transportEngine;
    protected final SessionFactory<S> sessionFactory;
    protected final Logger logger;
    protected final EventLoopGroup group;

    /**
     * The Netty channel instance that backs this endpoint.
     * <p>
     * Must be set before implementation constructor returns!
     */
    protected C channel;

    @SuppressWarnings("unchecked")
    protected NettyEndpoint(@NonNull EndpointBuilder builder) {
        this.transportEngine = (E) builder.engine();
        this.sessionFactory = builder.sessionFactory();
        this.logger = builder.logger();

        EventLoopGroup group;
        if ((group = this.transportEngine.group()) == null) {
            group = LoopPool.defaultGroup();
        } else if (this.transportEngine.autoShutdownGroup()) {
            LoopPool.useGroup(group);
        }
        this.group = group;
    }

    @Override
    public void closeNow() {
        this.closeAsync().syncUninterruptibly();
    }

    @Override
    public boolean isClosed() {
        return this.channel.isOpen();
    }

    @Override
    public Future<Void> closeAsync() {
        if (this.channel == null)   {
            //exception was caught while starting channel, safely close executor and shut down
            if (this.transportEngine.autoShutdownGroup()) {
                LoopPool.returnGroup(this.group);
            }
            return GlobalEventExecutor.INSTANCE.newPromise();
        } else {
            return this.channel.close().addListener(v -> {
                if (this.transportEngine.autoShutdownGroup()) {
                    LoopPool.returnGroup(this.group);
                }
            });
        }
    }
}
