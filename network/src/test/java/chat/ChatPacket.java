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

package chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.endpoint.AbstractSession;
import net.daporkchop.lib.network.protocol.Packet;
import net.daporkchop.lib.network.protocol.PacketDirection;
import net.daporkchop.lib.network.protocol.PacketHandler;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatPacket implements Packet {
    @NonNull
    private String message;

    @Override
    public void read(DataIn in) throws IOException {
        this.message = in.readUTF();
    }

    @Override
    public void write(DataOut out) throws IOException {
        out.writeUTF(this.message);
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.BOTH;
    }

    @Override
    public byte getId() {
        return 0;
    }

    public static class ChatHandler implements PacketHandler<ChatPacket, AbstractSession>   {
        @Override
        public void handle(ChatPacket packet, AbstractSession session) {
            System.out.println((session.isServer() ? "Server: " : "Client: ") + packet.message);
            if (session.isServer()) {
                session.send(packet);
            }
        }
    }
}
