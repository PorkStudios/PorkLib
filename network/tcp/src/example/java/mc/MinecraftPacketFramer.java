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
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.session.DefaultUserSession;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.tcp.pipeline.Framer;

import java.util.List;

/**
 * @author DaPorkchop_
 */
public class MinecraftPacketFramer extends Framer<MCSession> implements Logging {
    protected static int readVarInt(@NonNull ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            if (!buf.isReadable())  {
                return -1;
            }
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    protected static void writeVarInt(@NonNull ByteBuf buf, int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    @Override
    public void unpack(@NonNull MCSession session, @NonNull ByteBuf buf, @NonNull UnpackOut<MCSession> frames) {
        int origPos = buf.readerIndex();
        int size;
        while ((size = readVarInt(buf)) != -1) {
            buf.readerIndex(origPos);
            if (buf.readableBytes() >= size) {
                logger.debug("Read packet @ %d bytes", size);
                frames.received(session, buf.readRetainedSlice(readVarInt(buf)), 0);
                origPos = buf.readerIndex();
            } else {
                logger.debug("Unable to read %d bytes", size);
                return;
            }
        }
        buf.readerIndex(origPos);
    }

    @Override
    public void pack(@NonNull MCSession session, @NonNull ByteBuf packet, int channel, @NonNull PackOut<MCSession> frames) {
        ByteBuf len = packet.alloc().ioBuffer();
        writeVarInt(len, packet.readableBytes());

        frames.sending(session, len, channel);
        frames.sending(session, packet, channel);
    }
}
