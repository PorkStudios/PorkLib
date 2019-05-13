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

package net.daporkchop.lib.network.endpoint.builder;

import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.transport.tcp.Framer;
import net.daporkchop.lib.network.transport.tcp.TCPEngine;

import java.nio.file.NotLinkException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(chain = true, fluent = true)
public abstract class EndpointBuilder<Impl extends EndpointBuilder<Impl, R>, R extends PEndpoint> {
    /**
     * The {@link TransportEngine} to use.
     * <p>
     * If {@code null}, TCP with simple packet framing will be used.
     */
    protected TransportEngine engine;

    /**
     * The {@link Executor} to use.
     * <p>
     * If {@code null}, {@link PorkUtil#DEFAULT_EXECUTOR} will be used.
     */
    protected Executor executor;

    /**
     * The {@link EventLoopGroup} to use.
     * <p>
     * If {@code null}, a default group will be constructed using {@link #executor}.
     * <p>
     * Not all transport engines will make use of this option.
     */
    protected EventLoopGroup group;

    /**
     * A factory for creating new user session instances.
     * <p>
     * If {@code null}, dummy sessions ({@link net.daporkchop.lib.network.session.AbstractUserSession.NoopUserSession})
     * will be used.
     */
    protected Supplier<AbstractUserSession> sessionFactory;

    /**
     * The default protocol that will be used initially for all connections to and from this endpoint.
     * <p>
     * Must be set!
     */
    protected Protocol protocol;

    @SuppressWarnings("unchecked")
    public Impl engine(@NonNull TransportEngine engine) {
        this.engine = engine;
        return (Impl) this;
    }

    @SuppressWarnings("unchecked")
    public Impl executor(@NonNull Executor executor) {
        this.executor = executor;
        return (Impl) this;
    }

    @SuppressWarnings("unchecked")
    public Impl group(EventLoopGroup group) {
        this.group = group;
        return (Impl) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractUserSession<S>> Impl sessionFactory(@NonNull Supplier<S> sessionFactory) {
        this.sessionFactory = (Supplier<AbstractUserSession>) sessionFactory;
        return (Impl) this;
    }

    @SuppressWarnings("unchecked")
    public Impl protocol(@NonNull Protocol protocol) {
        this.protocol = protocol;
        return (Impl) this;
    }

    public R build() {
        this.validate();
        return this.doBuild();
    }

    protected void validate() {
        if (this.protocol == null)  {
            throw new NullPointerException("protocol");
        }

        if (this.engine == null) {
            this.engine = new TCPEngine(new Framer.DefaultFramer<>());
        }
        if (this.executor == null) {
            this.executor = PorkUtil.DEFAULT_EXECUTOR;
        }
        if (this.sessionFactory == null) {
            this.sessionFactory = AbstractUserSession.NoopUserSession::new;
        }
    }

    protected abstract R doBuild();
}
