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

package net.daporkchop.lib.network.tcp;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.event.MessageReceived;
import net.daporkchop.lib.network.pipeline.event.MessageSent;
import net.daporkchop.lib.network.pipeline.handler.PacketCodec;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.ChanneledPacket;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * Allows for sending individual packets down the pipeline.
 *
 * @author DaPorkchop_
 */
public abstract class Framer<S extends AbstractUserSession<S>> implements PacketCodec<S, ByteBuf, ByteBuf> {
    @Override
    public void messageReceived(@NonNull S session, @NonNull ByteBuf msg, int channel, @NonNull MessageReceived.Callback<S, ByteBuf> next) {
    }

    @Override
    public void messageSent(@NonNull S session, @NonNull ByteBuf msg, int channel, @NonNull MessageSent.Callback<S, ByteBuf> next) {
        this.pack(session, msg, channel, b -> next.messageSent(session, b, -1));
    }

    /**
     * Decodes as many frames as can be read from the given buffer. If an entire frame cannot be read (as the
     * buffer is incomplete), the data should be left in the buffer.
     *
     * @param session the session that the data was received on
     * @param buf     the buffer to read frames from
     * @param frames  a list of decoded frames. Buffers should be added to this after being removed from the
     *                input buffer using {@link ByteBuf#readRetainedSlice(int)} or similar methods, so long
     *                as the reader index is incremented correctly. The second method parameter is the channel id
     */
    protected abstract void unpack(@NonNull S session, @NonNull ByteBuf buf, @NonNull ObjIntConsumer<ByteBuf> frames);

    /**
     * Packs an encoded packet into (a) frame(s).
     *
     * @param session the session that the packet will be sent on
     * @param packet  a buffer containing the encoded packet
     * @param channel the id that the channel will be sent on
     * @param out     buffers may be passed to this method for sequential sending
     */
    protected abstract void pack(@NonNull S session, @NonNull ByteBuf packet, int channel, @NonNull Consumer<ByteBuf> out);

    class DefaultFramer<S extends AbstractUserSession<S>> extends Framer<S> {
        @Override
        public void unpack(@NonNull ByteBuf buf, @NonNull S session, @NonNull List<ChanneledPacket<ByteBuf>> frames) {
            int origIndex = buf.readerIndex();
            while (buf.readableBytes() >= 8) {
                int len = buf.getInt(origIndex);
                if (buf.readableBytes() - 8 < len) {
                    return;
                } else {
                    int channel = buf.skipBytes(4).readInt();
                    frames.add(new ChanneledPacket<>(buf.readRetainedSlice(len), channel));
                }
            }
        }

        @Override
        public void pack(@NonNull ChanneledPacket<ByteBuf> packet, @NonNull S session, @NonNull ByteBuf out) {
            out.writeInt(packet.packet().readableBytes())
               .writeInt(packet.channel())
               .writeBytes(packet.packet());
        }
    }
}
