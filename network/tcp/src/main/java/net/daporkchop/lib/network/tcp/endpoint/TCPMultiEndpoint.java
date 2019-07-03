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

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.endpoint.BaseMultiEndpoint;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.EndpointBuilder;
import net.daporkchop.lib.network.netty.util.group.PorkChannelGroup;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.group.SessionFilter;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public abstract class TCPMultiEndpoint<Impl extends BaseMultiEndpoint<Impl, S>, S extends AbstractUserSession<S>, C extends Channel, B extends EndpointBuilder<B, Impl, S>> extends TCPEndpoint<Impl, S, C, B> implements BaseMultiEndpoint<Impl, S> {
    protected PorkChannelGroup<S> sessions;

    protected TCPMultiEndpoint(@NonNull B builder) {
        super(builder);
    }

    @Override
    protected void preOpenChannel(@NonNull B builder) throws Exception {
        super.preOpenChannel(builder);

        this.sessions = new PorkChannelGroup<>(this.group.next());
    }

    @Override
    public Reliability fallbackReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        return (Impl) this;
    }

    @Override
    public Promise broadcast(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        return this.sessions.broadcast(message, channel, reliability, priority, flags);
    }

    @Override
    public Promise broadcast(@NonNull SessionFilter<S> filter, @NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        return this.sessions.broadcast(filter, message, channel, reliability, priority, flags);
    }

    @Override
    public void flushBuffer() {
        this.sessions.flushBuffer();
    }

    @Override
    public void flushBuffer(@NonNull SessionFilter<S> filter) {
        this.sessions.flushBuffer(filter);
    }

    @Override
    public Promise closeSessions() {
        return this.sessions.closeSessions();
    }

    @Override
    public Promise closeSessions(@NonNull SessionFilter<S> filter) {
        return this.sessions.closeSessions(filter);
    }

    @Override
    public Collection<S> sessions() {
        return this.internalSessions().stream().map(NetSession::userSession).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<NetSession<S>> internalSessions() {
        return (Set<NetSession<S>>) (Object) this.sessions;
    }
}
