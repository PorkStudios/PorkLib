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

package net.daporkchop.lib.network.tcp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.cache.SoftThreadCache;
import net.daporkchop.lib.common.cache.ThreadCache;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.Reliability;
import net.daporkchop.lib.network.session.encode.SendCallback;
import net.daporkchop.lib.network.tcp.session.TCPNioSocket;
import net.daporkchop.lib.network.transport.ChanneledPacket;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class TCPWriter<S extends AbstractUserSession<S>> extends MessageToMessageEncoder<Object> {
    protected static final ThreadCache<SendCallbackImpl> SEND_CALLBACK_CACHE = SoftThreadCache.of(SendCallbackImpl::new);

    @NonNull
    protected final TCPNioSocket<S> session;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        int channel = 0;
        if (msg instanceof ChanneledPacket) {
            ChanneledPacket pck = (ChanneledPacket) msg;
            msg = pck.packet();
            channel = pck.channel();
            pck.release();
        }
        if (msg instanceof ByteBuf) {
            ((ByteBuf) msg).retain(); //prevent buf from being released unintentionally
        }
        PacketMetadata metadata = PacketMetadata.instance(Reliability.RELIABLE_ORDERED, channel, 0, true);
        SendCallbackImpl callback = SEND_CALLBACK_CACHE.get().out(out).session(this.session);
        try {
            this.session.encodeMessage(msg, metadata, callback);
        } finally {
            callback.reset();
            metadata.release();
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    private static final class SendCallbackImpl implements SendCallback {
        protected List<Object> out;
        protected TCPNioSocket session;

        @Override
        @SuppressWarnings("unchecked")
        public void send(@NonNull Object msg, @NonNull PacketMetadata metadata) {
            try {
                if (metadata.checkReliabilitySet()) {
                    throw new IllegalStateException("reliability set");
                }

                if (msg instanceof byte[]) {
                    msg = Unpooled.wrappedBuffer((byte[]) msg);
                } else if (msg instanceof ByteBuffer) {
                    msg = Unpooled.wrappedBuffer((ByteBuffer) msg);
                } else if (msg instanceof byte[][]) {
                    msg = Unpooled.wrappedBuffer((byte[][]) msg);
                } else if (msg instanceof ByteBuffer[]) {
                    msg = Unpooled.wrappedBuffer((ByteBuffer[]) msg);
                }
                if (msg instanceof ByteBuf) {
                    this.session.framer().sending(this.session.userSession(), (ByteBuf) msg, metadata, this.out);
                } else {
                    throw new IllegalStateException("Not a ByteBuf: " + PorkUtil.className(msg));
                }
            } finally {
                if (!metadata.isOriginal()) {
                    metadata.release();
                }
            }
        }

        public void reset() {
            this.out = null;
            this.session = null;
        }
    }
}
