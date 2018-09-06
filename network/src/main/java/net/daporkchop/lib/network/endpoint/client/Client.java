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

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.network.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.endpoint.AbstractSession;
import net.daporkchop.lib.network.protocol.Packet;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Abstract code shared between client implementations
 *
 * @author DaPorkchop_
 */
@Data
public abstract class Client extends AbstractEndpoint {
    @NonNull
    private final IoConnector ioConnector;

    @NonNull
    private final InetSocketAddress address;

    @NonNull
    @Getter(AccessLevel.PRIVATE)
    private final IoSession session;

    @NonNull
    private volatile AbstractSession protocolSession;

    private volatile CompletableFuture<AbstractSession> completableFuture;

    public Client(@NonNull ClientBuilder builder, @NonNull IoConnector ioConnector) {
        super(builder.getTransmissionProtocol(), builder.getPacketProtocol(), builder.getPassword());

        this.ioConnector = ioConnector;
        this.populateService(this.ioConnector);

        this.address = builder.getAddress();

        ConnectFuture future = this.getIoConnector().connect(this.getAddress()).awaitUninterruptibly();
        if (future.getSession() == null) {
            throw new IllegalStateException("Unable to connect!", future.getException());
        } else {
            this.session = future.getSession();
        }
        this.completableFuture = new CompletableFuture<>();
        Throwable t = null;
        while (!this.completableFuture.isDone()) {
            try {
                this.protocolSession = this.completableFuture.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
                t = e.getCause();
                break;
            }
        }
        if (t != null) {
            this.close();
            throw new IllegalStateException("Unable to connect!", t);
        }
        this.completableFuture = null;
    }

    @Override
    public void close() {
        synchronized (this.session) {
            if (!this.isOpen()) {
                throw new IllegalStateException("Client already closed!");
            }
            this.setOpen(false);
            this.session.closeOnFlush().awaitUninterruptibly();
            this.getIoConnector().dispose(true);
        }
    }

    public void send(@NonNull Packet packet) {
        this.protocolSession.send(packet);
    }

    public void close(@NonNull String reason) {
        synchronized (this.session) {
            if (!this.isOpen()) {
                throw new IllegalStateException("Client already closed!");
            }
            this.protocolSession.close(reason, true);
            this.session.closeOnFlush().awaitUninterruptibly();
            this.setOpen(false);
            this.getIoConnector().dispose(true);
        }
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
