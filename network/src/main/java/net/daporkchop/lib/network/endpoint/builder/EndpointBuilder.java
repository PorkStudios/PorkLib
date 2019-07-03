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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.SessionFactory;
import net.daporkchop.lib.network.transport.TransportEngine;
import sun.rmi.runtime.Log;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(chain = true, fluent = true)
public abstract class EndpointBuilder<Impl extends EndpointBuilder<Impl, R, S>, R extends PEndpoint<R, S>, S extends AbstractUserSession<S>> {
    /**
     * The {@link TransportEngine} to use.
     * <p>
     * If {@code null}, TCP with simple packet framing will be used.
     */
    protected TransportEngine engine;

    /**
     * The {@link SessionFactory} that will create new session instances for every connection to and from this endpoint.
     * <p>
     * Must be set!
     */
    protected SessionFactory<S> sessionFactory;

    /**
     * The {@link Logger} to use for printing messages.
     * <p>
     * If {@code null}, {@link Logging#logger} will be used on a custom channel.
     */
    protected Logger logger;

    @SuppressWarnings("unchecked")
    public Impl engine(@NonNull TransportEngine engine) {
        this.engine = engine;
        return (Impl) this;
    }

    public abstract <NEW_S extends AbstractUserSession<NEW_S>> EndpointBuilder sessionFactory(@NonNull SessionFactory<NEW_S> sessionFactory);

    public R build() {
        this.validate();
        return this.doBuild();
    }

    protected void validate() {
        if (this.sessionFactory == null) {
            throw new NullPointerException("sessionFactory");
        } else if (this.engine == null) {
            throw new NullPointerException("engine");
        }
        if (this.logger == null)    {
            this.logger = Logging.logger.channel("network");
        }
    }

    protected abstract R doBuild();
}
