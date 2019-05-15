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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class ClientBuilder<S extends AbstractUserSession<S>> extends EndpointBuilder<ClientBuilder<S>, PClient<S>, S> {
    public static <S extends AbstractUserSession<S>> ClientBuilder<S> of(@NonNull Protocol<?, S> protocol)  {
        return new ClientBuilder<>().protocol(protocol);
    }

    /**
     * The address of the remote endpoint to connect to.
     * <p>
     * Must be set!
     */
    @NonNull
    protected InetSocketAddress address;

    @Override
    @SuppressWarnings("unchecked")
    public <NEW_S extends AbstractUserSession<NEW_S>> ClientBuilder<NEW_S> protocol(@NonNull Protocol<?, NEW_S> protocol)   {
        ((ClientBuilder<NEW_S>) this).protocol = protocol;
        return (ClientBuilder<NEW_S>) this;
    }

    @Override
    protected void validate() {
        if (this.address == null) {
            throw new NullPointerException("address");
        }
        super.validate();
    }

    @Override
    protected PClient<S> doBuild() {
        return this.engine.createClient(this);
    }
}
