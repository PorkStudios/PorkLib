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

import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.protocol.PacketDirection;
import net.daporkchop.lib.network.protocol.PacketHandler;
import net.daporkchop.lib.network.protocol.PacketProtocol;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedConstants;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedPacket;
import net.daporkchop.lib.network.protocol.encapsulated.session.ConnectionState;
import net.daporkchop.lib.network.protocol.encapsulated.session.EncapsulatedSession;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;

/**
 * @author DaPorkchop_
 */
public class HandshakeCompletePacket implements EncapsulatedPacket, EncapsulatedConstants {
    @Override
    public void read(DataIn in, PacketProtocol protocol) {
    }

    @Override
    public void write(DataOut out, PacketProtocol protocol) {
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENTBOUND;
    }

    @Override
    public byte getId() {
        return ID_HANDSHAKECOMPLETE;
    }

    public static class HandshakeCompleteHandler implements PacketHandler<EncapsulatedPacket, EncapsulatedSession>  {
        @Override
        public void handle(EncapsulatedPacket packet, EncapsulatedSession session) {
            if (SessionData.CONNECTION_STATE.get(session.getSession()) != ConnectionState.HANDSHAKE) {
                throw new IllegalStateException("Not currently in handshake mode!");
            }

            ConnectionState.advance(session);

            AuthenticatePacket authenticate = new AuthenticatePacket();
            authenticate.setSessionRandom(SessionData.SESSION_RANDOM.get(session.getSession()));
            authenticate.setPassword(session.getEndpoint().getPassword());
            session.send(authenticate);
            //TODO: send ping packet
        }
    }
}
