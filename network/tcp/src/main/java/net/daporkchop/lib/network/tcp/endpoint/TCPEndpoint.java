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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.io.IORunnable;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.group.DefaultGroup;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.endpoint.builder.EndpointBuilder;
import net.daporkchop.lib.network.pork.pool.SelectionPool;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.SessionFactory;
import net.daporkchop.lib.network.tcp.TCPEngine;
import net.daporkchop.lib.network.transport.TransportEngine;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public abstract class TCPEndpoint<Impl extends PEndpoint<Impl, S>, S extends AbstractUserSession<S>, C extends SelectableChannel> implements PEndpoint<Impl, S> {
    @Getter
    protected final TCPEngine transportEngine;
    @Getter
    protected final Promise closePromise = DefaultGroup.INSTANCE.newPromise();
    @Getter
    protected final SessionFactory<S> sessionFactory;

    protected C channel;

    public TCPEndpoint(@NonNull EndpointBuilder<?, ?, S> builder)   {
        this.transportEngine = (TCPEngine) builder.engine();
        this.sessionFactory = builder.sessionFactory();

        if (this.transportEngine.autoClosePool())   {
            this.closePromise.addListener((IORunnable) this.transportEngine.pool()::closeAsync);
        }
    }

    @Override
    public Promise closeAsync() {
        try {
            this.channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.closePromise;
    }
}
