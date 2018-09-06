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

package net.daporkchop.lib.gdxnetwork.endpoint.client;

import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.github.czyzby.websocket.serialization.impl.AbstractBinarySerializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.ByteBufferOutputStream;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.gdxnetwork.endpoint.Endpoint;
import net.daporkchop.lib.gdxnetwork.endpoint.EndpointType;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.protocol.PacketProtocol;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.EncapsulatedProtocol;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.WrappedPacket;
import net.daporkchop.lib.gdxnetwork.session.Session;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class NetClient implements Endpoint {
    @Getter
    @NonNull
    private final PacketProtocol packetProtocol;

    WebSocket webSocket;

    CompletableFuture<Object> connectFuture;
    CompletableFuture<String> disconnectFuture;

    @Getter
    private Session session;

    @Override
    public EndpointType getType() {
        return EndpointType.CLIENT;
    }

    public void start(@NonNull InetSocketAddress address) {
        this.start(address.getHostName(), (short) address.getPort(), false);
    }

    public void start(String host, short portIn, boolean wss) {
        int port = portIn & 0xFFFF;
        if (wss) {
            this.webSocket = ExtendedNet.getNet().newSecureWebSocket(host, port);
        } else {
            this.webSocket = ExtendedNet.getNet().newWebSocket(host, port);
        }

        if (!this.webSocket.isSupported()) {
            throw new IllegalStateException("Websockets not supported!");
        }

        this.addListeners();

        try {
            this.webSocket.connect();
        } catch (WebSocketException e) {
            throw new RuntimeException(String.format("Unable to connect to websocket on %s://%s:%d", wss ? "wss" : "ws", host, port), e);
        }
    }

    /**
     * Blocks the calling thread until the connection is complete
     */
    public void waitForConnect() {
        try {
            this.connectFuture.get();
        } catch (InterruptedException
                | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Blocks the calling thread until the client disconnects
     *
     * @return the reason for disconnection
     */
    public String waitForDisconnect() {
        try {
            return this.disconnectFuture.get();
        } catch (InterruptedException
                | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isRunning() {
        return this.webSocket != null && this.webSocket.isOpen();
    }

    @Override
    public void close(String reason) {
        this.webSocket.close(reason);
    }

    private void addListeners() {
        this.connectFuture = new CompletableFuture<>();
        this.disconnectFuture = new CompletableFuture<>();
        this.webSocket.addListener(new ClientListener(this));
        this.webSocket.setSerializer(new AbstractBinarySerializer() {
            @Override
            public byte[] serialize(Object object) {
                try {
                    if (object instanceof Packet && !(object instanceof EncapsulatedPacket)) {
                        object = new WrappedPacket((Packet) object);
                    }
                    if (object instanceof EncapsulatedPacket) {
                        Packet packet = (Packet) object;
                        ByteBuffer buffer = ByteBuffer.allocate(packet.getDataLength());
                        OutputStream os = new ByteBufferOutputStream(buffer);
                        os = NetClient.this.session.getCryptHelper().wrap(os);
                        os = new DataOut(os);
                        packet.encode((DataOut) os);
                        os.close();
                        return buffer.array();
                    } else {
                        throw new IllegalArgumentException(String.format("Invalid packet class: %s", object.getClass().getCanonicalName()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object deserialize(byte[] data) {
                System.out.printf("Received data: %s\n", Hexadecimal.encode(data));
                JOptionPane.showMessageDialog(null, Hexadecimal.encode(data));

                try {
                    InputStream is = new ByteArrayInputStream(data);
                    is = NetClient.this.session.getCryptHelper().wrap(is);
                    DataIn dataIn = new DataIn(is);
                    Packet packet = EncapsulatedProtocol.INSTANCE.getPacket(is.read());
                    packet.decode(dataIn);
                    dataIn.close();
                    return packet;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
