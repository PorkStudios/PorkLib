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

import lombok.NonNull;
import net.daporkchop.lib.network.conn.PorkConnection;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.EndpointType;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.packet.KryoSerializationWrapper;
import net.daporkchop.lib.network.packet.Packet;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class PorkClient<S extends Session> extends Endpoint<S> {
    private final KryoClientWrapper client;

    public PorkClient(@NonNull ClientBuilder<S> builder) {
        super(builder.getListeners(), builder.getProtocol());

        try {
            this.client = new KryoClientWrapper(this, WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE, new KryoSerializationWrapper(this));
            this.initKryo(this.client.getKryo());
            this.client.addListener(new KryoListenerEndpoint());
            this.client.start();
            this.client.connect(10000, builder.getAddress().getHostString(), builder.getAddress().getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <MS extends S> MS getSession()   {
        return ((PorkConnection) this.client).getSession();
    }

    @Override
    public boolean isRunning() {
        synchronized (this.client) {
            return this.client.isConnected();
        }
    }

    @Override
    public void close(String reason) {
        synchronized (this.client) {
            if (!this.isRunning()) {
                throw new IllegalStateException("Client already closed!");
            }

            try {
                this.client.stop();
                this.client.dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void send(@NonNull Packet... packets) {
        for (Packet packet : packets)   {
            if (packet == null) {
                throw new NullPointerException("packet");
            }
            this.send(packet);
        }
    }

    public void send(@NonNull Packet packet)    {
        this.client.send(packet);
    }

    @Override
    public EndpointType getType() {
        return EndpointType.CLIENT;
    }
}
