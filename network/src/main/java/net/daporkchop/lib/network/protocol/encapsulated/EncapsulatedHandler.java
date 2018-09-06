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

package net.daporkchop.lib.network.protocol.encapsulated;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.endpoint.AbstractSession;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.endpoint.server.Server;
import net.daporkchop.lib.network.protocol.encapsulated.packet.DisconnectPacket;
import net.daporkchop.lib.network.protocol.encapsulated.packet.HandshakeInitPacket;
import net.daporkchop.lib.network.protocol.encapsulated.session.ConnectionState;
import net.daporkchop.lib.network.protocol.encapsulated.session.EncapsulatedSession;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles the base encapsulated protocol packet receiving and sending
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class EncapsulatedHandler implements IoHandler {
    @NonNull
    private final AbstractEndpoint endpoint;

    @Override
    public void sessionCreated(IoSession session) {
    }

    @Override
    public void sessionOpened(IoSession session) {
        EncapsulatedSession encapsulatedSession = this.endpoint.getEncapsulatedProtocol().newSession(session, this.endpoint.isServer());
        SessionData.ENCAPSULATED_SESSION.set(session, encapsulatedSession);

        if (this.endpoint.isServer()) {
            Server server = (Server) this.endpoint;
            Long sessionRandom = ThreadLocalRandom.current().nextLong();
            SessionData.SESSION_RANDOM.set(session, sessionRandom);
            SessionData.COMPRESSION.set(session, server.getCompression());
            HandshakeInitPacket packet = new HandshakeInitPacket();
            packet.setCurveType(server.getCipherType() == BlockCipherType.NONE ? null : server.getCurveType());
            packet.setPasswordAuth(server.getPassword() != null);
            packet.setSessionRandom(sessionRandom);
            session.write(packet).addListener(future -> ConnectionState.advance(session));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sessionClosed(IoSession session) {
        if (!this.endpoint.isServer()) {
            CompletableFuture<AbstractSession> future = ((Client) this.endpoint).getCompletableFuture();
            if (future != null) {
                future.completeExceptionally(new IllegalStateException("Disconnected while trying to connect!"));
            }
        }
        EncapsulatedSession encapsulatedSession = SessionData.ENCAPSULATED_SESSION.get(session);
        if (!encapsulatedSession.isDisconnected()) {
            encapsulatedSession.setDisconnected(true);
            this.endpoint.getPacketProtocol().getHandler().onDisconnect(this.endpoint.isServer(), SessionData.PROTOCOL_SESSION.get(session), null);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        if (!this.endpoint.isServer()) {
            CompletableFuture<AbstractSession> future = ((Client) this.endpoint).getCompletableFuture();
            if (future != null) {
                future.completeExceptionally(cause);
            }
        }
        if (!(cause instanceof IOException)) {
            SessionData.ENCAPSULATED_SESSION.<EncapsulatedSession>get(session).close(cause.toString() + ": " + cause.getMessage(), false);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        //System.out.println((endpoint.isServer() ? "Server" : "Client") + " received: " + message.getClass().getCanonicalName());
        if (message instanceof EncapsulatedPacket) {
            EncapsulatedSession encapsulatedSession = SessionData.ENCAPSULATED_SESSION.get(session);
            encapsulatedSession.getEndpoint().getEncapsulatedProtocol().handle((EncapsulatedPacket) message, encapsulatedSession);
        } else {
            throw new IllegalStateException("Invalid packet type: " + message.getClass().getCanonicalName());
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        //System.out.println((endpoint.isServer() ? "Server" : "Client") + " sent: " + message.getClass().getCanonicalName());
        if (message instanceof DisconnectPacket) {
            EncapsulatedSession encapsulatedSession = SessionData.ENCAPSULATED_SESSION.get(session);
            if (!encapsulatedSession.isDisconnected()) {
                encapsulatedSession.setDisconnected(true);
                this.endpoint.getPacketProtocol().getHandler().onDisconnect(this.endpoint.isServer(), SessionData.PROTOCOL_SESSION.get(session), ((DisconnectPacket) message).getReason());
            }
        }
    }

    @Override
    public void inputClosed(IoSession session) {
    }

    @Override
    public void event(IoSession session, FilterEvent event) {
    }
}
