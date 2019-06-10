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

package net.daporkchop.lib.network.tcp.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.session.TCPNioSocket;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.util.List;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractFramer<S extends AbstractUserSession<S>> implements Framer<S> {
    protected CompositeByteBuf cumulation;
    protected int ctr = 0;

    @Override
    public final void received(@NonNull S session, @NonNull ByteBuf msg, @NonNull Framer.UnpackCallback callback) {
        this.unpack(session, this.cumulation.addComponent(true, msg), callback);
        if (this.ctr++ >= 16) {
            this.cumulation.discardSomeReadBytes();
            this.ctr = 0;
        }
    }

    @Override
    public final void sending(@NonNull S session, @NonNull ByteBuf msg, @NonNull PacketMetadata metadata, @NonNull List<ByteBuf> frames) {
        this.pack(session, msg, metadata, frames);
    }

    @Override
    public void init(@NonNull S session) {
        this.cumulation = ((TCPNioSocket<S>) session.internalSession()).alloc().compositeDirectBuffer();
    }

    @Override
    public void release(@NonNull S session) {
        this.cumulation.release();
        this.cumulation = null;
    }

    /**
     * Decodes as many frames as can be read from the given buffer. If an entire frame cannot be read (as the
     * buffer is incomplete), the data should be left in the buffer.
     *
     * @param session  the session that the data was received on
     * @param buf      the buffer to read frames from
     * @param callback destination for unpacked frames. Buffers should be added to this after being removed from the
     *                 input buffer using {@link ByteBuf#readRetainedSlice(int)} or similar methods, so long
     *                 as the reader index is incremented correctly
     */
    protected abstract void unpack(@NonNull S session, @NonNull ByteBuf buf, @NonNull Framer.UnpackCallback callback);

    /**
     * Packs an encoded packet into (a) frame(s).
     *
     * @param session the session that the packet will be sent on
     * @param packet  a buffer containing the encoded packet
     * @param frames  buffers may be passed to this method for sequential sending
     */
    protected abstract void pack(@NonNull S session, @NonNull ByteBuf packet, @NonNull PacketMetadata metadata, @NonNull List<ByteBuf> frames);
}
