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
import net.daporkchop.lib.network.tcp.frame.AbstractFramer;
import net.daporkchop.lib.network.tcp.frame.Framer;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.util.List;

/**
 * @author DaPorkchop_
 */
public class HTTPFramer<S extends HTTPSession<S>> extends AbstractFramer<S> {
    private boolean headersComplete = false;
    private int lastIndex = 0;
    private boolean chunked = false;
    private int nextChunkLength = -1;

    @Override
    protected void unpack(@NonNull S session, @NonNull ByteBuf buf, @NonNull Framer.UnpackCallback callback) {
        if (!this.headersComplete) {
            while (true) {
                buf.readerIndex(this.lastIndex);
                if (buf.readableBytes() >= 4) {
                    if (buf.readByte() == '\r'
                            && buf.readByte() == '\n'
                            && buf.readByte() == '\r'
                            && buf.readByte() == '\n') {
                        this.headersComplete = true;
                        session.logger().debug("Read headers!");
                        ByteBuf copy = buf.copy(0, this.lastIndex);
                        try {
                            callback.add(copy, 0);
                            this.chunked = session.headers.containsKey("Transfer-Encoding") && "chunked".equals(session.headers.get("Transfer-Encoding"));
                        } finally {
                            copy.release();
                        }
                        break;
                    } else {
                        this.lastIndex++;
                    }
                } else {
                    return;
                }
            }
        }
        if (buf.isReadable())   {
            if (this.chunked)   {
                while (true)    {
                    if (this.nextChunkLength != -1) {
                        if (buf.readableBytes() >= this.nextChunkLength)    {
                            ByteBuf copy = buf.copy(buf.readerIndex(), this.nextChunkLength - 2);
                            buf.skipBytes(this.nextChunkLength);
                            try {
                                callback.add(copy, 1);
                            } finally {
                                copy.release();
                                this.nextChunkLength = -1;
                            }
                        } else {
                            return; //we can't read a full chunk
                        }
                    } else {
                        //try to read the next chunk length
                        final int origPos = buf.readerIndex();
                        int count = 0;
                        while (buf.readableBytes() >= 2)    {
                            buf.markReaderIndex();
                            if (buf.readByte() == '\r' && buf.readByte() == '\n')   {
                                this.nextChunkLength = Integer.parseInt(buf.slice(origPos, count).toString(UTF8.utf8), 16) + 2;
                                break; //we read a complete length field
                            } else {
                                count++;
                            }
                        }
                        if (this.nextChunkLength == -1) {
                            buf.readerIndex(origPos);
                            return; //we can't read a full length field
                        }
                    }
                }
            } else {
                ByteBuf copy = buf.copy(buf.readerIndex(), buf.readableBytes());
                try {
                    callback.add(copy, 1);
                } finally {
                    copy.release();
                    buf.skipBytes(buf.readableBytes());
                }
            }
        }
    }

    @Override
    protected void pack(@NonNull S session, @NonNull ByteBuf packet, @NonNull PacketMetadata metadata, @NonNull List<ByteBuf> frames) {
        switch (metadata.channelId())  {
            case 0:
                frames.add(packet);
                break;
            case 1:
                frames.add(packet.alloc().ioBuffer().writeBytes(Integer.toHexString(packet.readableBytes()).getBytes()).writeByte('\r').writeByte('\n'));
                frames.add(packet);
                frames.add(packet.alloc().ioBuffer(2).writeByte('\r').writeByte('\n'));
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
