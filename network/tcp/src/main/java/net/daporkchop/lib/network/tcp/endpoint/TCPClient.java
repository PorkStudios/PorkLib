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
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.EndpointBuilder;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.session.TCPNetSession;
import net.daporkchop.lib.network.transport.NetSession;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class TCPClient<S extends AbstractUserSession<S>> extends TCPEndpoint<PClient<S>, S, SocketChannel> implements PClient<S> {
    protected final TCPNetSession<S> internalSession;

    public TCPClient(@NonNull ClientBuilder<S> builder) {
        super(builder);

        try {
            SocketChannel channel = SocketChannel.open(builder.address());
            this.internalSession = new TCPNetSession<>(this, null, this.closePromise);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public S userSession() {
        return this.internalSession.userSession();
    }
}
