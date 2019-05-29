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

package net.daporkchop.lib.network.tcp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.pork.pool.FixedSelectionPool;
import net.daporkchop.lib.network.pork.pool.SelectionPool;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.tcp.endpoint.TCPClient;
import net.daporkchop.lib.network.transport.TransportEngine;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadFactory;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class TCPEngine implements TransportEngine {
    protected static final Collection<Reliability> SUPPORTED_RELIABILITES = Collections.singleton(Reliability.RELIABLE_ORDERED);

    public static Builder builder() {
        return new Builder();
    }

    protected final SelectionPool pool;
    protected final boolean autoClosePool;

    protected TCPEngine(@NonNull Builder builder) {
        this.pool = builder.pool;
        this.autoClosePool = builder.autoClosePool;
    }

    @Override
    public <S extends AbstractUserSession<S>> PClient<S> createClient(@NonNull ClientBuilder<S> builder) {
        return new TCPClient<>(builder);
    }

    @Override
    public <S extends AbstractUserSession<S>> PServer<S> createServer(@NonNull ServerBuilder<S> builder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return SUPPORTED_RELIABILITES;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Setter
    @Getter
    @Accessors(fluent = true, chain = true)
    public static class Builder {
        @NonNull
        protected SelectionPool pool;
        protected boolean autoClosePool;

        public synchronized TCPEngine build() {
            if (this.pool == null) {
                //TODO: default selection pool
                this.pool = new FixedSelectionPool(1, (ThreadFactory) Thread::new);
                this.autoClosePool = true;
            }
            return new TCPEngine(this);
        }
    }
}
