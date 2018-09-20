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

package net.daporkchop.lib.network.endpoint.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.PorkConnection;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.EndpointType;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.packet.KryoSerializationWrapper;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.encapsulated.DisconnectPacket;
import net.daporkchop.lib.network.util.NetworkConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class PorkServer<S extends Session> extends Endpoint<S> {
    private final Server server;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public PorkServer(@NonNull ServerBuilder<S> builder) {
        super(builder.getListeners(), builder.getProtocol());
        try {
            this.server = new Server(NetworkConstants.WRITE_BUFFER_SIZE, NetworkConstants.OBJECT_BUFFER_SIZE, new KryoSerializationWrapper(this)) {
                @Override
                protected Connection newConnection() {
                    return new ServerConnection(PorkServer.this, builder);
                }

                @Override
                public void bind(InetSocketAddress tcpPort, InetSocketAddress udpPort) throws IOException {
                    PorkServer.this.running.set(true);
                    super.bind(tcpPort, udpPort);
                }
            };
            this.initKryo(this.server.getKryo());
            this.server.addListener(new KryoListenerEndpoint());
            this.server.start();
            this.server.bind(builder.getAddress(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isRunning() {
        synchronized (this.server) {
            return this.running.get();
        }
    }

    @Override
    public void close(String reason) {
        synchronized (this.server) {
            if (!this.isRunning()) {
                throw new IllegalStateException("Server already closed!");
            }

            this.running.set(false);

            this.server.sendToAllTCP(new DisconnectPacket(reason));

            try {
                //this.server.close();
                this.server.dispose();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <MS extends S> Collection<MS> getSessions() {
        return Arrays.stream(this.server.getConnections())
                .map(connection -> ((PorkConnection) connection).<MS>getSession())
                .collect(Collectors.toList());
    }

    public void broadcast(@NonNull Packet... packets) {
        for (Packet packet : packets) {
            if (packet == null) {
                throw new NullPointerException("packet");
            }
        }

        this.getSessions().forEach(session -> {
            for (Packet packet : packets) {
                session.send(packet);
            }
        });
    }

    public void broadcast(@NonNull Packet packet) {
        this.getSessions().forEach(session -> session.send(packet));
    }

    @Override
    public Kryo getKryo() {
        return this.server.getKryo();
    }

    @Override
    public EndpointType getType() {
        return EndpointType.SERVER;
    }
}
