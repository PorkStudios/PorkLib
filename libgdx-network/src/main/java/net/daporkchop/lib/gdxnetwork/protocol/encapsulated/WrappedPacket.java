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

package net.daporkchop.lib.gdxnetwork.protocol.encapsulated;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.protocol.IPacketHandler;
import net.daporkchop.lib.gdxnetwork.protocol.PacketProtocol;
import net.daporkchop.lib.gdxnetwork.session.Session;

import java.io.IOException;

import static net.daporkchop.lib.gdxnetwork.protocol.encapsulated.EncapsulatedProtocol.WRAPPED_ID;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
public class WrappedPacket implements EncapsulatedPacket {
    public Packet packet;

    @Override
    public void decode(DataIn in, PacketProtocol protocol) throws IOException {
        this.packet = protocol.getPacket(in.read());
        this.packet.decode(in, protocol);
    }

    @Override
    public void encode(DataOut out) throws IOException {
        out.write(this.packet.getId());
        this.packet.encode(out);
    }

    @Override
    public int getDataLength() {
        return this.packet.getDataLength() + 1;
    }

    @Override
    public int getId() {
        return WRAPPED_ID;
    }

    public static class WrappedPacketHandler implements IPacketHandler<WrappedPacket> {
        @Override
        public void handle(WrappedPacket packet, Session session) {
            session.getProtocol().handle(packet.packet, session);
        }
    }
}
