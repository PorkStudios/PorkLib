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
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.tcp.pipeline.Framer;

/**
 * @author DaPorkchop_
 */
public class HTTPPacketFramer extends Framer<HTTPSession> implements Logging {
    @Override
    public void unpack(@NonNull HTTPSession session, @NonNull ByteBuf buf, @NonNull UnpackOut<HTTPSession> frames) {
        TOP:
        while (buf.isReadable())    {
            if (session.headersComplete)    {
                logger.debug("Read %d bytes!", buf.readableBytes());
                frames.received(session, buf.readRetainedSlice(buf.readableBytes()), 1);
            } else if (buf.readableBytes() >= 4) {
                int origPos = buf.readerIndex();
                while (buf.readableBytes() >= 4)    {
                    if (buf.readByte() == '\r'
                            && buf.readByte() == '\n'
                            && buf.readByte() == '\r'
                            && buf.readByte() == '\n')  {
                        int len = buf.readerIndex() - origPos;
                        logger.debug("Read headers @ %d bytes!", len);
                        frames.received(session, buf.readerIndex(origPos).readRetainedSlice(len), 0);
                        session.headersComplete = true;
                        continue TOP;
                    }
                }
                buf.readerIndex(origPos);
                logger.debug("Headers didn't end after %d bytes...", buf.readableBytes());
                return;
            }
        }
    }

    @Override
    public void pack(@NonNull HTTPSession session, @NonNull ByteBuf packet, int channel, @NonNull PackOut<HTTPSession> frames) {
        frames.add(session, packet);
    }
}
