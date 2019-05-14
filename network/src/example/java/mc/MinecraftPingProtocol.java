/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package mc;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.netty.NettyByteBufIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.protocol.SimpleProtocol;
import net.daporkchop.lib.network.session.NoopUserSession;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class MinecraftPingProtocol implements SimpleProtocol<ByteBuf, NoopUserSession>, Logging {
    @Override
    public ByteBuf decode(@NonNull DataIn in, @NonNull NoopUserSession session, int channel) throws IOException {
        return ((NettyByteBufIn) in).buf();
    }

    @Override
    public void encode(@NonNull DataOut out, @NonNull ByteBuf packet, @NonNull NoopUserSession session, int channel) throws IOException {
        while (packet.isReadable()) {
            out.write(packet.readByte() & 0xFF);
        }
        packet.release();
    }

    @Override
    public void handle(@NonNull ByteBuf packet, @NonNull NoopUserSession session, int channel) {
        logger.debug("Handling packet @ %d bytes", packet.readableBytes());
        switch (MinecraftPacketFramer.readVarInt(packet)) {
            case 0x00:
                byte[] b = new byte[MinecraftPacketFramer.readVarInt(packet)];
                packet.readBytes(b);
                logger.info("Received ping response:").info(new String(b));
                break;
            case 0x01:
                TestMCPinger.endTime.complete(packet.readLong());
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public NoopUserSession newSession() {
        return new NoopUserSession();
    }
}
