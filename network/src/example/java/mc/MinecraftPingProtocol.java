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
import io.netty.buffer.PooledByteBufAllocator;
import lombok.NonNull;
import net.daporkchop.lib.binary.netty.NettyByteBufIn;
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.protocol.SimpleProtocol;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class MinecraftPingProtocol implements SimpleProtocol<ByteBuf, MinecraftPingSession>, Logging {
    @Override
    public ByteBuf decode(@NonNull DataIn in, @NonNull MinecraftPingSession session, int channel) throws IOException {
        return ((NettyByteBufIn) in).buf();
    }

    @Override
    public void encode(@NonNull DataOut out, @NonNull ByteBuf packet, @NonNull MinecraftPingSession session, int channel) throws IOException {
        while (packet.isReadable()) {
            out.write(packet.readByte() & 0xFF);
        }
    }

    @Override
    public void handle(@NonNull ByteBuf packet, @NonNull MinecraftPingSession session, int channel) {
        logger.debug("Handling packet @ %d bytes", packet.readableBytes());
        switch (session.state) {
            case NONE:
                session.state = PingState.RESPONSE;
                try (DataIn in = NettyUtil.wrapIn(packet))  {
                    byte[] b = new byte[in.readVarInt(true)];
                    in.readFully(b);
                    logger.info("Received ping response:").info(new String(b));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (false) {
                    session.send(PooledByteBufAllocator.DEFAULT.ioBuffer()
                            .writeByte(0x01) //packet id
                    );
                } else {
                    session.closeAsync().addListener(v -> logger.info("Disconnected."));
                }
                break;
        }
    }

    @Override
    public MinecraftPingSession newSession() {
        return new MinecraftPingSession();
    }
}
