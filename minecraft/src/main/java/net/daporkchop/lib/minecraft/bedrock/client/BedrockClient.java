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

package net.daporkchop.lib.minecraft.bedrock.client;

import io.gomint.jraknet.ClientSocket;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.api.PingData;
import net.daporkchop.lib.minecraft.api.protocol.Packet;
import net.daporkchop.lib.minecraft.api.session.SessionHandler;
import net.daporkchop.lib.minecraft.bedrock.BedrockProfile;
import net.daporkchop.lib.minecraft.common.AbstractClient;
import net.daporkchop.lib.minecraft.data.Platform;
import net.daporkchop.lib.minecraft.data.Protocol;
import net.daporkchop.lib.minecraft.util.ping.BedrockPing;

import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * @author DaPorkchop_
 */
public class BedrockClient extends AbstractClient {
    private final ClientSocket socket = new ClientSocket();

    public BedrockClient(BedrockProfile profile, @NonNull SessionHandler sessionHandler) {
        super(profile);

        try {
            this.socket.setMojangModificationEnabled(true);
            this.socket.setEventHandler((socket, event) -> {
                String reason = event.getReason();
                switch (event.getType()) {
                    case CONNECTION_ATTEMPT_SUCCEEDED: {
                        sessionHandler.connected(BedrockClient.this);
                    }
                    break;
                    case CONNECTION_ATTEMPT_FAILED:
                    case CONNECTION_DISCONNECTED:
                    case CONNECTION_CLOSED: {
                        sessionHandler.disconnected(BedrockClient.this, reason);
                    }
                    break;
                }
            });
            this.socket.initialize();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPacket(@NonNull Packet packet) {
        //TODO: encoding and whatnot lol
    }

    @Override
    public PingData ping(@NonNull InetSocketAddress address) {
        return BedrockPing.ping(address);
    }

    @Override
    public boolean isRunning() {
        return this.socket.getConnection() != null && this.socket.getConnection().isConnected();
    }

    @Override
    public void stop(String reason) {
        if (!this.isRunning()) {
            throw new IllegalStateException("Already closed!");
        }
        this.socket.getConnection().disconnect(reason);
    }

    @Override
    public void start(InetSocketAddress address) {
        if (this.isRunning()) {
            throw new IllegalStateException("Already running!");
        }
        this.socket.connect(address);
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.getLatest(Platform.BEDROCK);
    }
}
