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

package net.daporkchop.lib.binary.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.binary.netty.buf.FreeingWrappedUnpooledUnsafeDirectByteBuf;
import net.daporkchop.lib.binary.netty.buf.NotFreeingWrappedUnpooledUnsafeDirectByteBuf;

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
            return free
                    ? new FreeingWrappedUnpooledUnsafeDirectByteBuf(buffer, size)
                    : new NotFreeingWrappedUnpooledUnsafeDirectByteBuf(buffer, size);
        } else {
            return Unpooled.wrappedBuffer(buffer);
        }
    }
}
