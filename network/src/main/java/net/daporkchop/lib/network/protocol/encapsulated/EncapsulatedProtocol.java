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

import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.protocol.PacketHandler;
import net.daporkchop.lib.network.protocol.PacketProtocol;
import net.daporkchop.lib.network.protocol.encapsulated.packet.*;
import net.daporkchop.lib.network.protocol.encapsulated.session.EncapsulatedSession;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;
import org.apache.mina.core.session.IoSession;

/**
 * Implementation of the encapsulated protocol as a generic packet protocol
 *
 * @author DaPorkchop_
 */
public class EncapsulatedProtocol extends PacketProtocol<EncapsulatedPacket, EncapsulatedSession> implements EncapsulatedConstants {
    @NonNull
    private final AbstractEndpoint endpoint;

    public EncapsulatedProtocol(@NonNull AbstractEndpoint endpoint) {
        super(NETWORK_VERSION, "");

        this.endpoint = endpoint;

        //handshake protocol
        this.registerPacket(HandshakeInitPacket::new, new HandshakeInitPacket.HandshakeInitHandler());
        this.registerPacket(HandshakeClientInitPacket::new, new HandshakeClientInitPacket.HandshakeClientInitHandler());
        this.registerPacket(HandshakeEncodingPacket::new, new HandshakeEncodingPacket.HandshakeEncodingHandler());
        this.registerPacket(HandshakeStartEncryptionPacket::new, new HandshakeStartEncryptionPacket.HandshakeStartEncryptionHandler());
        this.registerPacket(HandshakeCompletePacket::new, new HandshakeCompletePacket.HandshakeCompleteHandler());

        //authenticate protocol
        this.registerPacket(AuthenticatePacket::new, new AuthenticatePacket.AuthenticateHandler());
        this.registerPacket(AuthenticateCompletePacket::new, new AuthenticateCompletePacket.AuthenticateCompleteHandler());

        //run protocol
        this.registerPacket(WrappedPacket::new, new WrappedPacket.WrappedHandler());
        this.registerPacket(KeepalivePacket::new, new PacketHandler.UnimplementedHandler<>());
        this.registerPacket(DisconnectPacket::new, new DisconnectPacket.DisconnectHandler());
    }

    @Override
    public EncapsulatedSession newSession(IoSession session, boolean server) {
        SessionData.PROTOCOL_SESSION.set(session, this.endpoint.getPacketProtocol().newSession(session, server));
        return new EncapsulatedSession(session, server, this.endpoint);
    }
}
