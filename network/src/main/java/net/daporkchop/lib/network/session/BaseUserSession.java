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

package net.daporkchop.lib.network.session;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.OldDataOut;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.transport.NetSession;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.net.SocketAddress;

/**
 * A user-level session implementation. End users will most likely only ever be interacting with this class (and
 * subclasses thereof).
 *
 * @author DaPorkchop_
 */
public interface BaseUserSession<Impl extends BaseUserSession<Impl, S>, S extends AbstractUserSession<S>> extends PSession<Impl, S> {
    @Override
    default <E extends PEndpoint<E, S>> E endpoint() {
        return this.internalSession().endpoint();
    }

    @Override
    default Promise send(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        return this.internalSession().send(message, channel, reliability, priority, flags);
    }

    @Override
    default OldDataOut writer() {
        return this.internalSession().writer();
    }

    @Override
    default void flushBuffer() {
        this.internalSession().flushBuffer();
    }

    @Override
    default Reliability fallbackReliability() {
        return this.internalSession().fallbackReliability();
    }

    @Override
    @SuppressWarnings("unchecked")
    default Impl fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        this.internalSession().fallbackReliability(reliability);
        return (Impl) this;
    }

    @Override
    default void closeNow() {
        this.internalSession().closeNow();
    }

    @Override
    default Promise closeAsync() {
        return this.internalSession().closeAsync();
    }

    @Override
    default Promise closePromise() {
        return this.internalSession().closePromise();
    }

    @Override
    default TransportEngine transportEngine() {
        return this.internalSession().transportEngine();
    }

    @Override
    default SocketAddress localAddress() {
        return this.internalSession().localAddress();
    }

    @Override
    default SocketAddress remoteAddress() {
        return this.internalSession().remoteAddress();
    }

    /**
     * Gets the internal transport-level session instance.
     *
     * @return the internal transport-level session instance
     */
    NetSession<S> internalSession();
}
