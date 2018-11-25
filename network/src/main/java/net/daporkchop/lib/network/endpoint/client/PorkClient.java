/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.endpoint.client;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.conn.Connection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.PacketRegistry;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.protocol.EndpointManager;
import net.daporkchop.lib.network.protocol.pork.PorkConnection;
import net.daporkchop.lib.network.protocol.pork.PorkProtocol;
import net.daporkchop.lib.network.protocol.pork.packet.DisconnectPacket;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * @author DaPorkchop_
 */
@Getter
public class PorkClient implements Endpoint, Logging, Connection {
    private final PacketRegistry packetRegistry;

    private final EndpointManager.ClientEndpointManager manager;
    private final CompletableFuture<Object> connectWaiter = new CompletableFuture<>();
    private final PorkConnection porkConnection;

    @SuppressWarnings("unchecked")
    public PorkClient(@NonNull ClientBuilder builder) {
        this.packetRegistry = new PacketRegistry(builder.getProtocols());
        this.manager = builder.getManager().createClientManager();

        this.manager.start(builder.getAddress(), builder.getExecutor(), this);

        try {
            this.connectWaiter.get();
        } catch (Exception e) {
            throw this.exception(e);
        }
        this.porkConnection = this.manager.getConnection(PorkProtocol.class);
    }

    @Override
    public EndpointType getType() {
        return EndpointType.CLIENT;
    }

    @Override
    public <C extends UserConnection> Collection<C> getConnections(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return Collections.singletonList(this.manager.getConnection(protocolClass));
    }

    @Override
    public boolean isRunning() {
        return this.manager.isRunning();
    }

    @Override
    public String getName() {
        return "Client";
    }

    public void postConnectCallback(Throwable t)   {
        if (this.connectWaiter.isDone())    {
            if (t == null)  {
                throw new IllegalStateException("already connected! why the heck are you even trying to run this method ye dummy?");
            }
        } else {
            if (t == null) {
                this.connectWaiter.complete(null);
            } else {
                this.connectWaiter.completeExceptionally(t);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Endpoint> E getEndpoint() {
        return (E) this;
    }

    @Override
    public void close(String reason) {
        this.closeConnection(reason);
    }

    @Override
    public void closeConnection(String reason) {
        synchronized (this) {
            if (!this.isRunning()) {
                throw new IllegalStateException("Already closed!");
            }

            this.send(new DisconnectPacket(reason));
            this.manager.close();
        }
    }

    @Override
    public boolean isConnected() {
        return this.porkConnection.isConnected();
    }

    @Override
    public void send(@NonNull Packet packet, boolean blocking, Void postSendCallback) {
        this.porkConnection.send(packet, blocking, postSendCallback);
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.porkConnection.getAddress();
    }
}
