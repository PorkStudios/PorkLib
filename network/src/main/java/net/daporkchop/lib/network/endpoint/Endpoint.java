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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.CryptographySettings;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.network.conn.PorkConnection;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.endpoint.server.PorkServer;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.encapsulated.DisconnectPacket;
import net.daporkchop.lib.network.packet.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.packet.encapsulated.HandshakeCompletePacket;
import net.daporkchop.lib.network.packet.encapsulated.HandshakeInitPacket;
import net.daporkchop.lib.network.packet.encapsulated.HandshakeResponsePacket;
import net.daporkchop.lib.network.packet.protocol.PacketProtocol;
import net.daporkchop.lib.network.util.ReflectionUtil;

import java.io.IOException;
import java.util.Set;

import static net.daporkchop.lib.network.packet.encapsulated.EncapsulatedPacket.*;

/**
 * @author DaPorkchop_
 */
@Getter
@RequiredArgsConstructor
public abstract class Endpoint<S extends Session> {
    protected static final int WRITE_BUFFER_SIZE = 16384;
    protected static final int OBJECT_BUFFER_SIZE = 2048;

    protected final Set<EndpointListener<S>> listeners;
    protected final PacketProtocol<S> protocol;

    @SuppressWarnings("unchecked")
    public void fireConnected(@NonNull S session) {
        this.listeners.forEach(l -> l.onConnect(session));
    }

    @SuppressWarnings("unchecked")
    public void fireDisconnected(S session, String reason) {
        this.listeners.forEach(l -> l.onDisconnect(session, reason));
    }

    @SuppressWarnings("unchecked")
    public void fireReceived(@NonNull S session, @NonNull Packet packet) {
        this.listeners.forEach(l -> l.onReceieve(session, packet));
    }

    @SuppressWarnings("unchecked")
    protected void initKryo(@NonNull Kryo kryo) {
        this.registerProtocol(PROTOCOL, kryo);
        this.registerProtocol(this.protocol, kryo);
    }

    public abstract boolean isRunning();

    public void close() {
        this.close(null);
    }

    public abstract void close(String reason);

    public abstract EndpointType getType();

    @SuppressWarnings("unchecked")
    protected <MS extends Session> void registerProtocol(@NonNull PacketProtocol<MS> protocol, @NonNull Kryo kryo)   {
        protocol.getClassCodecMap().forEach((clazz, codec) ->
            kryo.register(clazz, new Serializer(false, true) {
                @Override
                public void write(Kryo kryo, Output output, Object object) {
                    try {
                        ((Packet) object).write(new DataOut(output));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public Object read(Kryo kryo, Input input, Class type) {
                    try {
                        Packet packet = codec.newPacket();
                        packet.read(new DataIn(input));
                        return packet;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
    }

    @NoArgsConstructor
    protected class KryoListenerEndpoint extends Listener {
        @Override
        public void connected(Connection connection) {
            PorkConnection porkConnection = (PorkConnection) connection;

            porkConnection.setSession(Endpoint.this.protocol.newSession());

            if (Endpoint.this instanceof PorkServer)    {
                HandshakeInitPacket packet = new HandshakeInitPacket();
                CryptographySettings settings = porkConnection.getPacketReprocessor().getCryptographySettings();
                if (settings.doesEncrypt()) {
                    packet.cryptographySettings = new CryptographySettings(
                            settings.getCurveType(),
                            ECDHKeyPair.getKey(settings.getCurveType()),
                            settings.getCipherType(),
                            settings.getCipherMode(),
                            settings.getCipherPadding()
                    );
                } else {
                    packet.cryptographySettings = new CryptographySettings();
                }
                packet.compression = porkConnection.getPacketReprocessor().getCompression();
                porkConnection.send(packet);
                ReflectionUtil.flush(connection);
                porkConnection.incrementState();
            }
        }

        @Override
        public void disconnected(Connection connection) {
            PorkConnection porkConnection = (PorkConnection) connection;
            Endpoint.this.fireDisconnected(porkConnection.getSession(), porkConnection.getDisconnectReason());
        }

        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof Packet) {
                PorkConnection porkConnection = (PorkConnection) connection;

                if (object instanceof EncapsulatedPacket) {
                    EncapsulatedPacket encapsulatedPacket = (EncapsulatedPacket) object;
                    try {
                        switch (encapsulatedPacket.getType())   {
                            case HANDSHAKE_INIT:{
                                HandshakeInitPacket packet = (HandshakeInitPacket) object;
                                EncapsulatedPacket response = porkConnection.getPacketReprocessor().initClient(packet);
                                porkConnection.incrementState();
                                porkConnection.send(response);
                                ReflectionUtil.flush(connection);
                                porkConnection.incrementState();
                            }
                            break;
                            case HANDSHAKE_RESPONSE: {
                                HandshakeResponsePacket packet = (HandshakeResponsePacket) object;
                                EncapsulatedPacket response = porkConnection.getPacketReprocessor().initServer(packet);
                                porkConnection.incrementState();
                                porkConnection.send(response);
                                Endpoint.this.fireConnected(porkConnection.getSession());
                            }
                            break;
                            case HANDSHAKE_COMPLETE: {
                                HandshakeCompletePacket packet = (HandshakeCompletePacket) object;
                                Endpoint.this.fireConnected(porkConnection.getSession());
                            }
                            break;
                            case DISCONNECT: {
                                DisconnectPacket packet = (DisconnectPacket) object;
                                porkConnection.setDisconnectReason(packet.message);
                                //disconnect event is fired by kryonet afterwards
                            }
                            break;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Packet packet = (Packet) object;
                    S session = porkConnection.getSession();
                    Endpoint.this.protocol.handle(session, packet);
                    Endpoint.this.fireReceived(session, packet);
                }
            }
        }

        @Override
        public void idle(Connection connection) {
            PorkConnection porkConnection = (PorkConnection) connection;
            //TODO: handle splitting for large packets
        }
    }
}
