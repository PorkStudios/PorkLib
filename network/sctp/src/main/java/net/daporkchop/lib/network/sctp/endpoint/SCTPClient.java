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

package net.daporkchop.lib.network.sctp.endpoint;

import io.netty.bootstrap.Bootstrap;
import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.EndpointBuilder;
import net.daporkchop.lib.network.sctp.netty.SCTPChannelInitializer;
import net.daporkchop.lib.network.sctp.netty.session.WrapperNioSctpChannel;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.NetSession;

/**
 * @author DaPorkchop_
 */
public class SCTPClient<S extends AbstractUserSession<S>> extends SCTPEndpoint<PClient<S>, S, WrapperNioSctpChannel<S>> implements PClient<S> {
    @SuppressWarnings("unchecked")
    public SCTPClient(@NonNull ClientBuilder<S> builder) {
        super(builder);

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(this.group)
                    .channelFactory(() -> new WrapperNioSctpChannel<>(this))
                    .handler(new SCTPChannelInitializer<>(this));

            this.transportEngine.clientOptions().forEach(bootstrap::option);

            this.channel = (WrapperNioSctpChannel<S>) bootstrap.connect(builder.address()).syncUninterruptibly().channel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public S userSession() {
        return this.channel.userSession();
    }

    @Override
    public NetSession<S> internalSession() {
        return this.channel;
    }
}
