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

package big.protocol;

import big.BigSession;
import big.BigTestMain;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.endpoint.EndpointType;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor
@AllArgsConstructor
public class BigPacket implements Packet {
    public byte[] data;

    @Override
    public void read(DataIn in) throws IOException {
        this.data = in.readBytesSimple();
    }

    @Override
    public void write(DataOut out) throws IOException {
        out.writeBytesSimple(this.data);
    }

    public static class MessageCodec implements Codec<BigPacket, BigSession> {
        @Override
        public void handle(BigPacket packet, BigSession session) {
            boolean server = session.getEndpoint().getType() == EndpointType.SERVER;
            if (!Arrays.equals(packet.data, BigTestMain.RANDOM_DATA)) {
                throw new IllegalStateException("Invalid data!");
            }
            System.out.printf("[%s] Data valid!\n", server ? "Server" : "Client");
            if (server) {
                session.send(packet);
            }
        }

        @Override
        public BigPacket newPacket() {
            return new BigPacket();
        }
    }
}
