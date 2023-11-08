/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.binary.netty.buf.FreeingWrappedUnpooledUnsafeDirectByteBuf;
import net.daporkchop.lib.binary.netty.buf.NotFreeingWrappedUnpooledUnsafeDirectByteBuf;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

/**
 * Extension of {@link io.netty.buffer.Unpooled} with slightly more customization.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PUnpooled {
    /**
     * Identical to {@link #wrap(ByteBuffer, int, boolean)}, using {@link ByteBuffer#remaining()} as the size.
     *
     * @see #wrap(ByteBuffer, int, boolean)
     */
    public ByteBuf wrap(@NonNull ByteBuffer buffer, boolean free) {
        return wrap(buffer, buffer.remaining(), free);
    }

    /**
     * Wraps a {@link ByteBuffer} into a {@link ByteBuf}.
     *
     * @param buffer the {@link ByteBuffer} to wrap
     * @param size   the size of the wrapped area
     * @param free   whether or not to free the {@link ByteBuffer} once the {@link ByteBuf} has been released. This can be convenient, but be aware
     *               that this can cause undefined behavior if the {@link ByteBuffer} is accessed after the {@link ByteBuf} is released! Should generally
     *               only be set to {@code true} if the {@link ByteBuffer} will no longer be used unless you know exactly what you're doing. Has no effect
     *               for heap buffers.
     * @return a {@link ByteBuf} wrapping the contents of the given {@link ByteBuffer}
     */
    public ByteBuf wrap(@NonNull ByteBuffer buffer, int size, boolean free) {
        if (buffer.isDirect()) {
            if (buffer.isReadOnly())    {
                //we have to do some hackery to make it be a read-only buffer
                ByteBuffer notReadOnly = PlatformDependent.directBuffer(((DirectBuffer) buffer).address() + buffer.position(), size);
                return free
                        ? new FreeingWrappedUnpooledUnsafeDirectByteBuf(buffer, notReadOnly, size).asReadOnly()
                        : new NotFreeingWrappedUnpooledUnsafeDirectByteBuf(notReadOnly, size).asReadOnly();
            } else {
                return free
                        ? new FreeingWrappedUnpooledUnsafeDirectByteBuf(buffer, buffer, size)
                        : new NotFreeingWrappedUnpooledUnsafeDirectByteBuf(buffer, size);
            }
        } else {
            return Unpooled.wrappedBuffer(buffer);
        }
    }
}
