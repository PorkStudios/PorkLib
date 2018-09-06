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

package net.daporkchop.lib.network.protocol.encapsulated.packet;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.sig.ec.ECCurves;
import net.daporkchop.lib.network.endpoint.AbstractEndpoint;
import net.daporkchop.lib.network.protocol.PacketDirection;
import net.daporkchop.lib.network.protocol.PacketHandler;
import net.daporkchop.lib.network.protocol.PacketProtocol;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedConstants;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.protocol.encapsulated.session.ConnectionState;
import net.daporkchop.lib.network.protocol.encapsulated.session.EncapsulatedSession;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;

import java.io.IOException;

/**
 * First packet in the encapsulated protocol, followed by {@link HandshakeClientInitPacket}
 *
 * @author DaPorkchop_
 */
@Data
@NoArgsConstructor
public class HandshakeInitPacket implements EncapsulatedPacket, EncapsulatedConstants {
    @NonNull
    private Long sessionRandom;

    private boolean passwordAuth;

    private ECCurves curveType;

    @Override
    public void read(DataIn in, PacketProtocol protocol) throws IOException {
        this.passwordAuth = in.readBoolean();
        String ecName = in.readUTF();
        if (ecName.isEmpty())   {
            this.curveType = null;
        } else {
            this.curveType = ECCurves.valueOf(ecName);
        }
        this.sessionRandom = in.readLong();
    }

    @Override
    public void write(DataOut out, PacketProtocol protocol) throws IOException {
        out.writeBoolean(this.passwordAuth);
        out.writeUTF(this.curveType == null ? "" : this.curveType.name());
        out.writeLong(this.sessionRandom);
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENTBOUND;
    }

    @Override
    public byte getId() {
        return ID_HANDSHAKEINIT;
    }

    public static class HandshakeInitHandler implements PacketHandler<HandshakeInitPacket, EncapsulatedSession> {
        @Override
        public void handle(HandshakeInitPacket packet, EncapsulatedSession session) {
            if (SessionData.CONNECTION_STATE.get(session.getSession()) != ConnectionState.NONE) {
                throw new IllegalStateException("Attempted to initiate handshake at invalid time!");
            }

            ConnectionState.advance(session);
            SessionData.PASSWORD_AUTH.set(session.getSession(), packet.passwordAuth);
            SessionData.SESSION_RANDOM.set(session.getSession(), packet.sessionRandom);
            if (packet.curveType != null)   {
                //start generating ECDH key pair
                AbstractEndpoint.getECKeyPair(packet.curveType);
                SessionData.ECDH_CURVE_TYPE.set(session.getSession(), packet.curveType);
            }
            session.send(new HandshakeClientInitPacket());
        }
    }
}
