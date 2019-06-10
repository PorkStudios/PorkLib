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
import net.daporkchop.lib.network.tcp.frame.AbstractFramer;
import net.daporkchop.lib.network.tcp.frame.Framer;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.util.List;

/**
 * @author DaPorkchop_
 */
public class HTTPFramer extends AbstractFramer<HTTPSession> {
    private boolean headersComplete = false;
    private int lastHeaderIndex = 0;

    @Override
    protected void unpack(@NonNull HTTPSession session, @NonNull ByteBuf buf, @NonNull Framer.UnpackCallback callback) {
        if (!this.headersComplete) {
            while (true) {
                buf.readerIndex(this.lastHeaderIndex);
                if (buf.readableBytes() >= 4) {
                    if (buf.readByte() == '\r'
                            && buf.readByte() == '\n'
                            && buf.readByte() == '\r'
                            && buf.readByte() == '\n') {
                        this.headersComplete = true;
                        session.logger().debug("Read headers!");
                        ByteBuf copy = buf.copy(0, this.lastHeaderIndex);
                        try {
                            callback.add(copy, 0);
                        } finally {
                            copy.release();
                        }
                        break;
                    } else {
                        this.lastHeaderIndex++;
                    }
                } else {
                    return;
                }
            }
        }
        if (buf.isReadable())   {
            callback.add(buf, 1);
        }
    }

    @Override
    protected void pack(@NonNull HTTPSession session, @NonNull ByteBuf packet, @NonNull PacketMetadata metadata, @NonNull List<ByteBuf> frames) {
        frames.add(packet);
    }
}
