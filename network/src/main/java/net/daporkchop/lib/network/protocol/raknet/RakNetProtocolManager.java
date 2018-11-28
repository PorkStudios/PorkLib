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

package net.daporkchop.lib.network.protocol.raknet;

import com.nukkitx.network.raknet.RakNet;
import com.nukkitx.network.raknet.RakNetClient;
import com.nukkitx.network.raknet.RakNetServer;
import com.nukkitx.network.raknet.RakNetServerEventListener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.client.PorkClient;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.packet.DisconnectPacket;
import net.daporkchop.lib.network.protocol.api.EndpointManager;
import net.daporkchop.lib.network.protocol.api.ProtocolManager;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * https://en.wikipedia.org/wiki/RakNet
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RakNetProtocolManager implements ProtocolManager {
    public static final RakNetProtocolManager INSTANCE = new RakNetProtocolManager();

    @Override
    public EndpointManager.ServerEndpointManager createServerManager() {
        return new RakNetServerManager();
    }

    @Override
    public EndpointManager.ClientEndpointManager createClientManager() {
        return new RakNetClientManager();
    }

    private abstract static class RakNetEndpointManager<E extends Endpoint, R extends RakNet<RakNetPorkSession>> implements EndpointManager<E> {
        protected R rakNet;

        @Override
        public boolean isRunning() {
            return this.rakNet.getChannel().isActive();
        }
    }

    private static class RakNetServerManager extends RakNetEndpointManager<Server, RakNetServer<RakNetPorkSession>> implements EndpointManager.ServerEndpointManager {
        private com.nukkitx.network.SessionManager<RakNetPorkSession> sessionManager;

        @Override
        public void start(@NonNull InetSocketAddress address, @NonNull Executor executor, @NonNull Server endpoint) {
            if (this.sessionManager != null) {
                throw new IllegalStateException("already initialized!");
            }
            this.sessionManager = new SessionManager(endpoint);
            this.rakNet = new RakNetServer.Builder<RakNetPorkSession>()
                    .address(address)
                    .id(0L)
                    .sessionFactory(connection -> new RakNetPorkSession(connection, endpoint))
                    .sessionManager(this.sessionManager)
                    .packet(RakNetPacketWrapper::new, 0x20)
                    .eventListener(new EventHandler(endpoint))
                    .build();
            this.rakNet.bind();
        }

        @Override
        public <C extends UserConnection> Collection<C> getConnections(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
            return this.sessionManager.all().stream()
                    .map(session -> session.getUserConnection(protocolClass))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        @Override
        public void broadcast(Packet packet, boolean blocking, Void callback) {
            this.sessionManager.all().forEach(session -> session.send(packet, blocking, callback));
        }

        @Override
        public void close(String reason) {
            this.broadcast(new DisconnectPacket(reason), false, null);
            this.rakNet.close();
        }

        @RequiredArgsConstructor
        private static class EventHandler implements RakNetServerEventListener {
            private static final Advertisement advertisement = new Advertisement(
                    "PorkLib network - RakNet mode",
                    "what are you doing? this isn't supposed to be pinged!",
                    -1,
                    "v0.0.0",
                    -1, -1,
                    "like seriously, stop it",
                    "just go away NOOB"
            );

            @NonNull
            private final Server server;

            @Override
            public Action onConnectionRequest(InetSocketAddress address) {
                return Action.CONTINUE;
            }

            @Override
            public Advertisement onQuery(InetSocketAddress address) {
                return advertisement;
            }
        }

        @RequiredArgsConstructor
        private static class SessionManager implements com.nukkitx.network.SessionManager<RakNetPorkSession>, Logging {
            @NonNull
            private final Server server;

            private final Map<InetSocketAddress, RakNetPorkSession> connections = new ConcurrentHashMap<>();
            private final Map<RakNetPorkSession, InetSocketAddress> theseAreAlsoConnections = new ConcurrentHashMap<>();

            @Override
            public boolean add(@NonNull InetSocketAddress address, @NonNull RakNetPorkSession session) {
                logger.debug("[Server] Incoming client: ${0}", address);
                if (this.connections.containsKey(address)) {
                    return false;
                } else {
                    this.connections.put(address, session);
                    this.theseAreAlsoConnections.put(session, address);
                    session.getConnection().sendPacket(new RakNetPacketWrapper());
                    return true;
                }
            }

            @Override
            public boolean remove(@NonNull RakNetPorkSession session) {
                InetSocketAddress address = this.theseAreAlsoConnections.remove(session);
                return address != null && this.connections.remove(address) != null;
            }

            @Override
            public RakNetPorkSession get(@NonNull InetSocketAddress address) {
                return this.connections.get(address);
            }

            @Override
            public Collection<RakNetPorkSession> all() {
                return this.connections.values();
            }

            @Override
            public int getCount() {
                return this.connections.size();
            }

            @Override
            public void onTick() {
            }
        }
    }

    private static class RakNetClientManager extends RakNetEndpointManager<Client, RakNetClient<RakNetPorkSession>> implements EndpointManager.ClientEndpointManager {
        private RakNetPorkSession session;

        @Override
        public void close() {
            this.rakNet.close();
        }

        @Override
        public void start(@NonNull InetSocketAddress address, @NonNull Executor executor, @NonNull Client client) {
            if (this.rakNet != null) {
                throw new IllegalStateException("already started!");
            }
            this.rakNet = new RakNetClient.Builder<RakNetPorkSession>()
                    .id(0L)
                    .sessionFactory(connection -> new RakNetPorkSession(connection, client))
                    .sessionManager(new SessionManager(client))
                    .packet(RakNetPacketWrapper::new, 0x20)
                    .build();
            try {
                this.rakNet.connect(address);
            } catch (Exception e)   {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
            return this.session.getUserConnection(protocolClass);
        }

        @Override
        public void send(@NonNull Packet packet, boolean blocking, Void callback) {
            this.session.send(packet, blocking, callback);
        }

        @RequiredArgsConstructor
        private class SessionManager implements com.nukkitx.network.SessionManager<RakNetPorkSession>, Logging {
            @NonNull
            private final Client client;
            private InetSocketAddress address;

            @Override
            public boolean add(@NonNull InetSocketAddress address, @NonNull RakNetPorkSession session) {
                logger.debug("[Client] Connected to ${0}", address);
                synchronized (this) {
                    if (this.address == null) {
                        this.address = address;
                        RakNetClientManager.this.session = session;
                        ((PorkClient) this.client).postConnectCallback(null); //TODO!!!
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public boolean remove(RakNetPorkSession session) {
                synchronized (this) {
                    if (this.address == null) {
                        return false;
                    } else {
                        this.address = null;
                        RakNetClientManager.this.session = null;
                        return true;
                    }
                }
            }

            @Override
            public RakNetPorkSession get(@NonNull InetSocketAddress address) {
                if (address.equals(this.address)) {
                    return RakNetClientManager.this.session;
                } else {
                    return null;
                }
            }

            @Override
            public Collection<RakNetPorkSession> all() {
                return Collections.singleton(RakNetClientManager.this.session);
            }

            @Override
            public int getCount() {
                return this.address == null ? 0 : 1;
            }

            @Override
            public void onTick() {
            }
        }
    }
}
