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

package net.daporkchop.lib.network.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.netty.NettyByteBufIn;
import net.daporkchop.lib.binary.netty.NettyByteBufOut;
import net.daporkchop.lib.network.pipeline.event.ReceivedListener;
import net.daporkchop.lib.network.pipeline.event.SendingListener;
import net.daporkchop.lib.network.pipeline.handler.Codec;
import net.daporkchop.lib.network.pipeline.util.EventContext;
import net.daporkchop.lib.network.protocol.DataProtocol;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class NettyDataCodec<S extends AbstractUserSession<S>> implements Codec<S, ByteBuf, Object> {
    @NonNull
    protected final DataProtocol<S> protocol;
    @NonNull
    protected final ByteBufAllocator alloc;

    protected final NettyByteBufIn in = new NettyByteBufIn();
    protected final NettyByteBufOut out = new NettyByteBufOut();

    @Override
    public void received(@NonNull EventContext<S> context, @NonNull S session, @NonNull ByteBuf msg, int channel) {
        try {
            Object decoded = this.protocol.codec().decode(session, this.in.buf(msg), channel);
            if (decoded != null) {
                context.received(session, decoded, channel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.in.buf(null);
            msg.release();
        }
    }

    @Override
    public void sending(@NonNull EventContext<S> context, @NonNull S session, @NonNull Object msg, Reliability reliability, int channel) {
        if (session.transportEngine().isBinary(msg)) {
            context.sending(session, msg, reliability, channel);
        } else {
            ByteBuf buf = this.alloc.ioBuffer();
            try {
                this.protocol.codec().encode(this.out.buf(buf), session, msg, channel);
            } catch (IOException e) {
                buf.release();
                throw new RuntimeException(e);
            } finally {
                this.out.buf(null);
            }
            context.sending(session, buf, reliability, channel);
        }
    }
}
