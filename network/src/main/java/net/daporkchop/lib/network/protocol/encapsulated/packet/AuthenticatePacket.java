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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.endpoint.server.Server;
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
 * Sent to the server to authenticate and make sure encryption is set up correctly
 *
 * @author DaPorkchop_
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatePacket implements EncapsulatedPacket, EncapsulatedConstants {
    private String password;

    @NonNull
    private Long sessionRandom;

    @Override
    public void read(DataIn in, PacketProtocol protocol) throws IOException {
        this.sessionRandom = in.readLong();
        this.password = in.readUTF();
    }

    @Override
    public void write(DataOut out, PacketProtocol protocol) throws IOException {
        out.writeLong(this.sessionRandom);
        out.writeUTF(this.password);
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.SERVERBOUND;
    }

    @Override
    public byte getId() {
        return ID_AUTHENTICATE;
    }

    public static class AuthenticateHandler implements PacketHandler<AuthenticatePacket, EncapsulatedSession> {
        @Override
        public void handle(AuthenticatePacket packet, EncapsulatedSession session) {
            if (ConnectionState.get(session) != ConnectionState.AUTHENTICATE) {
                throw new IllegalStateException("Invalid connection state!");
            }

            if (packet.sessionRandom.longValue() != SessionData.SESSION_RANDOM.<Long>get(session.getSession()).longValue()) {
                throw new IllegalStateException("Invalid random key!");
            }
            Server server = (Server) session.getEndpoint();
            if (server.getPassword() != null && !server.getPassword().equals(packet.password)) {
                throw new IllegalStateException("Invalid password!");
            }
            session.getSession().write(new AuthenticateCompletePacket()).addListener(future -> ConnectionState.advance(session));
        }
    }
}
