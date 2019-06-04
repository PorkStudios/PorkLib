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
import lombok.NonNull;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * A {@link Framer} that prefixes messages with a length field indicating the size of each frame.
 *
 * @author DaPorkchop_
 */
public abstract class LengthPrefixedFramer<S extends AbstractUserSession<S>> extends Framer<S> {
    @Override
    protected void unpack(@NonNull S session, @NonNull ByteBuf buf, @NonNull UnpackOut<S> frames) {
        //TODO
    }

    @Override
    protected void pack(@NonNull S session, @NonNull ByteBuf packet, int channel, @NonNull PackOut<S> frames) {
        ByteBuf prefix = packet.alloc().ioBuffer(this.lengthFieldLength());
        this.writeLengthField(prefix, packet.readableBytes());
        frames.add(session, prefix);
        frames.add(session, packet);
    }

    /**
     * @return the length of the length field, in bytes
     */
    protected abstract int lengthFieldLength();

    /**
     * Writes a length field to a given buffer
     *
     * @param buf    the buffer to write to
     * @param length the length to write
     */
    protected abstract void writeLengthField(@NonNull ByteBuf buf, int length);

    /**
     * Reads a length field from a given buffer.
     * <p>
     * The buffer is guaranteed to have at least {@link #lengthFieldLength()} bytes readable.
     *
     * @param buf the buffer to read from
     * @return the length
     */
    protected abstract int readLengthField(@NonNull ByteBuf buf);
}
