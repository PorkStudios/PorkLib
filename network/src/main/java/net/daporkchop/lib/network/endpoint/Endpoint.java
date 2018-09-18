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

package net.daporkchop.lib.network.endpoint;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.conn.PorkConnection;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.protocol.PacketProtocol;

import java.util.Set;

/**
 * @author DaPorkchop_
 */
@Getter
@RequiredArgsConstructor
public abstract class Endpoint {
    protected final Set<EndpointListener> listeners;
    protected final PacketProtocol protocol;

    @SuppressWarnings("unchecked")
    public void fireConnected(@NonNull Session session) {
        this.listeners.forEach(l -> l.onConnect(session));
    }

    @SuppressWarnings("unchecked")
    public void fireDisconnected(@NonNull Session session, String reason) {
        this.listeners.forEach(l -> l.onDisconnect(session, reason));
    }

    @SuppressWarnings("unchecked")
    public void fireReceived(@NonNull Session session, @NonNull Packet packet) {
        this.listeners.forEach(l -> l.onReceieve(session, packet));
    }

    @NoArgsConstructor
    protected class KryoListenerEndpoint extends Listener {
        @Override
        public void connected(Connection connection) {
            PorkConnection porkConnection = (PorkConnection) connection;
        }

        @Override
        public void disconnected(Connection connection) {
            PorkConnection porkConnection = (PorkConnection) connection;
            Endpoint.this.fireDisconnected(porkConnection.getSession(), porkConnection.getDisconnectReason());
        }

        @Override
        public void received(Connection connection, Object object) {
            PorkConnection porkConnection = (PorkConnection) connection;
        }

        @Override
        public void idle(Connection connection) {
            PorkConnection porkConnection = (PorkConnection) connection;
        }
    }
}
