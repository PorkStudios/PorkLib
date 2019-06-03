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

package net.daporkchop.lib.network.tcp.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.worker.group.DefaultGroup;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.tcp.endpoint.TCPEndpoint;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class TCPNetSession<S extends AbstractUserSession<S>> implements NetSession<S> {
    @NonNull
    protected final S userSession;
    @NonNull
    protected final TCPEndpoint<?, S, ?> endpoint;
    @NonNull
    protected final SocketChannel channel;
    @NonNull
    protected final ByteBufAllocator alloc;
    @NonNull
    protected final Pipeline<S> pipeline;
    protected final Promise closePromise;

    protected final CompositeByteBuf sendBuffer;

    public TCPNetSession(@NonNull TCPEndpoint<?, S, ?> endpoint, @NonNull SocketChannel channel)    {
        this(endpoint, channel, DefaultGroup.INSTANCE.newPromise());
    }

    public TCPNetSession(@NonNull TCPEndpoint<?, S, ?> endpoint, @NonNull SocketChannel channel, @NonNull Promise closePromise)    {
        this.endpoint = endpoint;
        this.channel = channel;
        this.closePromise = closePromise;
        this.alloc = endpoint.transportEngine().alloc();
        this.userSession = endpoint.sessionFactory().newSession();
        PUnsafe.putObject(this.userSession, NetSession.ABSTRACTUSERSESSION_INTERNALSESSION_OFFSET, this);
        this.pipeline = new Pipeline<>(this.userSession, new TCPPipelineEdgeListener<>(this));

        this.sendBuffer = this.alloc.compositeBuffer();
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability) {
        return null;
    }

    @Override
    public NetSession<S> send(@NonNull Object packet, Reliability reliability, int channel) {
        return null;
    }

    @Override
    public Promise sendAsync(@NonNull Object packet, Reliability reliability) {
        return null;
    }

    @Override
    public Promise sendAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return null;
    }

    @Override
    public DataOut writer() {
        return null;
    }

    @Override
    public NetSession<S> flushBuffer() {
        return this;
    }

    @Override
    public Reliability fallbackReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    public NetSession<S> fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        if (reliability != Reliability.RELIABLE_ORDERED)    {
            throw new IllegalArgumentException(reliability.name());
        }
        return this;
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

    @Override
    public TransportEngine transportEngine() {
        return this.endpoint.transportEngine();
    }
}
