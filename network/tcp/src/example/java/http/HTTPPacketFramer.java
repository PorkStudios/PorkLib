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

package http;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.tcp.Framer;

import java.util.List;

/**
 * @author DaPorkchop_
 */
public class HTTPPacketFramer implements Framer<HTTPSession>, Logging {
    @Override
    public void unpack(@NonNull ByteBuf buf, @NonNull HTTPSession session, @NonNull List<ChanneledPacket<ByteBuf>> frames) {
        TOP:
        while (buf.isReadable()) {
            if (session.headers != null) {
                logger.debug("Read %d bytes!", buf.readableBytes());
                frames.add(new ChanneledPacket<>(buf.readRetainedSlice(buf.readableBytes()), 1));
            } else {
                int origPos = buf.readerIndex();
                int i = -1;
                while (buf.isReadable()) {
                    char c = (char) (buf.readByte() & 0xFFFF);
                    switch (i) {
                        case -1:
                        case 1:
                            if (c == '\r') {
                                i++;
                            } else {
                                i = -1;
                            }
                            break;
                        case 0:
                            if (c == '\n') {
                                i++;
                            } else {
                                i = -1;
                            }
                            break;
                        case 2:
                            if (c == '\n') {
                                byte[] arr = new byte[buf.readerIndex() - origPos];
                                buf.getBytes(origPos, arr);
                                session.headers = new String(arr, UTF8.utf8);
                                frames.add(new ChanneledPacket<>(buf.retainedSlice(origPos, buf.readerIndex() - origPos), 0));
                                logger.debug("Read headers!", buf.readableBytes());
                                continue TOP;
                            } else {
                                i = -1;
                            }
                            break;
                        default:
                            throw new IllegalStateException(String.valueOf(i));
                    }
                }
                logger.debug("Unable to find headers.");
                return;
            }
        }
    }

    @Override
    public void pack(@NonNull ChanneledPacket<ByteBuf> packet, @NonNull HTTPSession session, @NonNull ByteBuf out) {
        out.writeBytes(packet.packet());
    }
}
