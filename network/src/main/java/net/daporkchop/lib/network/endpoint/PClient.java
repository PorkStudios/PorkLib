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

package net.daporkchop.lib.network.endpoint;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.session.UserSession;
import net.daporkchop.lib.network.transport.NetSession;

/**
 * A client can connect to a single remote endpoint.
 *
 * @author DaPorkchop_
 */
public interface PClient<S extends AbstractUserSession<S>> extends PEndpoint<PClient<S>, S>, UserSession<PClient<S>, S> {
    @Override
    default EndpointType type() {
        return EndpointType.CLIENT;
    }

    /**
     * @return this session's user session instance
     */
    S userSession();

    @Override
    NetSession<S> internalSession();

    @Override
    @SuppressWarnings("unchecked")
    default <E extends PEndpoint<E, S>> E endpoint() {
        return (E) this;
    }

    @Override
    default PClient<S> send(@NonNull Object packet, Reliability reliability) {
        this.internalSession().send(packet, reliability);
        return this;
    }

    @Override
    default PClient<S> send(@NonNull Object packet, Reliability reliability, int channel) {
        this.internalSession().send(packet, reliability, channel);
        return this;
    }

    @Override
    default Promise sendAsync(@NonNull Object packet, Reliability reliability) {
        return this.internalSession().sendAsync(packet, reliability);
    }

    @Override
    default Promise sendAsync(@NonNull Object packet, Reliability reliability, int channel) {
        return this.internalSession().sendAsync(packet, reliability, channel);
    }

    @Override
    default DataOut writer() {
        return this.internalSession().writer();
    }

    @Override
    default PClient<S> flushBuffer() {
        this.internalSession().flushBuffer();
        return this;
    }

    @Override
    default Reliability fallbackReliability() {
        return this.internalSession().fallbackReliability();
    }

    @Override
    default PClient<S> fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException {
        this.internalSession().fallbackReliability(reliability);
        return this;
    }
}
