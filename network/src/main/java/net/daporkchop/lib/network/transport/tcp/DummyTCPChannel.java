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

package net.daporkchop.lib.network.transport.tcp;

import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.session.PChannel;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.session.UserSession;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class DummyTCPChannel implements PChannel {
    @NonNull
    protected final WrapperNioSocketChannel session;
    @Getter
    protected final int id;

    @Override
    public <T extends UserSession<T>> T session() {
        return this.session.userSession();
    }

    @Override
    public NetSession internalSession() {
        return this.session;
    }

    @Override
    public PChannel send(@NonNull Object packet, Reliability reliability) {
        this.session.send(packet, Reliability.RELIABLE_ORDERED, this.id);
        return this;
    }

    @Override
    public Future<Void> sendFuture(@NonNull Object packet, Reliability reliability) {
        return this.session.sendFuture(packet, Reliability.RELIABLE_ORDERED, this.id);
    }

    @Override
    public Reliability fallbackReliability() {
        return Reliability.RELIABLE_ORDERED;
    }

    @Override
    public PChannel fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        if (reliability != Reliability.RELIABLE_ORDERED)    {
            throw new IllegalArgumentException(reliability.name());
        }
        return this;
    }

    @Override
    public TransportEngine transportEngine() {
        return this.session.transportEngine();
    }
}
