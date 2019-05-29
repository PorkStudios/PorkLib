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

import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.PChannel;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class TCPNetSession<S extends AbstractUserSession<S>> implements NetSession<S> {
    @NonNull
    protected final S userSession;
    @NonNull
    protected final PEndpoint<?, S> endpoint;

    @Override
    public PChannel<S> channel(int id) {
        return null;
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
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability) {
        return null;
    }

    @Override
    public Future<Void> sendAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return null;
    }

    @Override
    public DataOut writer() {
        return null;
    }

    @Override
    public NetSession<S> flushBuffer() {
        return null;
    }

    @Override
    public void closeNow() {
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
    public boolean isClosed() {
        return false;
    }

    @Override
    public Future<Void> closeAsync() {
        return null;
    }

    @Override
    public TransportEngine transportEngine() {
        return null;
    }
}
