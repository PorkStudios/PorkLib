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
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.protocol.IPacketHandler;
import net.daporkchop.lib.gdxnetwork.protocol.encapsulated.EncapsulatedProtocol;

/**
 * @author DaPorkchop_
 */
@Getter
public class ClientListener extends WebSocketHandler {
    @NonNull
    private final NetClient client;

    @SuppressWarnings("unchecked")
    public ClientListener(@NonNull NetClient client) {
        this.client = client;

        EncapsulatedProtocol.INSTANCE.getRegisteredPackets().forEach((id, registeredPacket) -> {
            Packet packet = registeredPacket.getSupplier().get();
            this.registerHandler(packet.getClass(), (webSocket, pck) -> {
                ((IPacketHandler<Packet>) registeredPacket.getHandler()).handle((Packet) pck, this.client.getSession());
                return true;
            });
        });
    }

    @Override
    public boolean onOpen(WebSocket webSocket) {
        this.client.connectFuture.complete(null);
        return super.onOpen(webSocket);
    }

    @Override
    public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
        this.client.disconnectFuture.complete(reason);
        return super.onClose(webSocket, code, reason);
    }

    @Override
    public boolean onError(WebSocket webSocket, Throwable error) {
        error.printStackTrace();
        this.client.disconnectFuture.completeExceptionally(error);
        webSocket.close(error.toString());
        return super.onError(webSocket, error);
    }
}
