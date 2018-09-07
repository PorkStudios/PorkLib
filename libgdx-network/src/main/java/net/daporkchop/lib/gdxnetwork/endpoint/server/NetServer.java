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

package net.daporkchop.lib.gdxnetwork.endpoint.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.ByteBufferInputStream;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.gdxnetwork.endpoint.Endpoint;
import net.daporkchop.lib.gdxnetwork.endpoint.EndpointType;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.protocol.PacketProtocol;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.EncapsulatedProtocol;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.MessagePacket;
import net.daporkchop.lib.gdxnetwork.session.Session;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public class NetServer extends WebSocketServer implements Endpoint {
    private final PacketProtocol packetProtocol;
    private final Map<WebSocket, Session> sessions = new IdentityHashMap<>();
    @Setter
    private ServerListener listener = new ServerListener() {
        @Override
        public void onConnected(WebSocket socket) {
        }

        @Override
        public void onClosed(WebSocket socket, String reason) {
        }
    };

    public NetServer(@NonNull InetSocketAddress address, @NonNull PacketProtocol packetProtocol) {
        super(address);
        this.packetProtocol = packetProtocol;
    }

    @Override
    public EndpointType getType() {
        return EndpointType.SERVER;
    }

    @Override
    public boolean isRunning() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(String reason) {
        super.getConnections().forEach(socket -> socket.close(CloseFrame.NORMAL, reason));
        try {
            super.stop();
        } catch (IOException
                | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Session session = new SessionServer(this.packetProtocol, this, conn);
        this.sessions.put(conn, session);
        this.listener.onConnected(conn);
        //TODO: handshake protocol
        session.send(new MessagePacket("Hello world!"));

        //this.close();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        this.listener.onClosed(conn, reason);
        this.sessions.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        this.onMessage(conn, ByteBuffer.wrap(message.getBytes(UTF8.utf8)));
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        //System.out.printf("Received message: %s\n", Hexadecimal.encode(message.array()));

        try {
            Session session = this.sessions.get(conn);
            InputStream is = new ByteBufferInputStream(message);
            DataIn dataIn = new DataIn(is);
            Packet packet = EncapsulatedProtocol.INSTANCE.getPacket(is.read());
            packet.decode(dataIn, this.packetProtocol);
            dataIn.close();

            EncapsulatedProtocol.INSTANCE.handle(packet, session);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        this.run(true);
    }

    public void run(boolean async) {
        if (async) {
            new Thread(super::run).start();
        } else {
            super.run();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        conn.close(CloseFrame.NORMAL, ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Started server!");
    }
}
