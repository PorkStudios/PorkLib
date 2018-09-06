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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gdxnetwork.endpoint.Endpoint;
import net.daporkchop.lib.gdxnetwork.endpoint.EndpointType;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.protocol.PacketProtocol;

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

    @Override
    public EndpointType getType() {
        return EndpointType.CLIENT;
    }

    @Override
    public void start(String host, short port, boolean wss) {
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

    public void sendMessage(@NonNull Packet packet) {
        if (this.isRunning()) {

        }
    }

    @Override
    public boolean isRunning() {
        return this.webSocket != null && this.webSocket.isOpen();
    }

    @Override
    public void stop(String reason) {
        this.webSocket.close(reason);
    }

    private void addListeners() {
        this.connectFuture = new CompletableFuture<>();
        this.disconnectFuture = new CompletableFuture<>();
        this.webSocket.addListener(new ClientListener(this));
    }
}
