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
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.transport.tcp.Framer;

import java.io.IOException;
import java.util.List;

/**
 * @author DaPorkchop_
 */
public class MinecraftPacketFramer implements Framer<MinecraftPingSession>, Logging {
    @Override
    public void unpack(@NonNull ByteBuf buf, @NonNull MinecraftPingSession session, @NonNull List<ChanneledPacket<ByteBuf>> frames) {
        try (DataIn in = NettyUtil.wrapIn(buf)) {
            int origPos = buf.readerIndex();
            while (buf.readableBytes() >= 2)    {
                int size = in.readVarInt(true);
                if (buf.readableBytes() >= size)    {
                    logger.debug("Read packet @ %d bytes", size);
                    frames.add(new ChanneledPacket<>(buf.readRetainedSlice(size), 0));
                } else {
                    logger.debug("Unable to read %d bytes", size);
                    buf.readerIndex(origPos);
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pack(@NonNull ChanneledPacket<ByteBuf> packet, @NonNull MinecraftPingSession session, @NonNull List<ByteBuf> frames) {
        ByteBuf buf = packet.packet().alloc().ioBuffer();
        try (DataOut out = NettyUtil.wrapOut(buf)) {
            out.writeVarInt(packet.packet().readableBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frames.add(buf);
        frames.add(packet.packet());
    }
}
