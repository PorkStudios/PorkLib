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

package net.daporkchop.lib.natives.util;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * A type with method(s) that accept {@link ByteBuf}(s) as parameters, but are restricted to either direct buffers only or heap buffers only. Attempting to
 * pass a {@link ByteBuf} of an unsupported type to any superclass methods will result in an {@link InvalidBufferTypeException}.
 *
 * @author DaPorkchop_
 */
public interface BufferTyped {
    /**
     * @return whether this implementation accepts direct buffers
     */
    boolean directAccepted();

    /**
     * @return whether this implementation accepts heap buffers
     */
    default boolean heapAccepted() {
        return !this.directAccepted();
    }

    /**
     * Checks whether the given {@link ByteBuf} is accepted by this implementation.
     *
     * @param buf the {@link ByteBuf} to check
     * @return whether or not the given {@link ByteBuf} is accepted
     */
    default boolean isAcceptable(@NonNull ByteBuf buf) {
        return (this.directAccepted() && buf.hasMemoryAddress()) || (this.heapAccepted() && buf.hasArray());
    }

    /**
     * Ensures that the given {@link ByteBuf} will be accepted by this implementation.
     *
     * @param buf the {@link ByteBuf} to check
     * @return the {@link ByteBuf}
     * @throws InvalidBufferTypeException if the given {@link ByteBuf} is not acceptable
     */
    default ByteBuf assertAcceptable(@NonNull ByteBuf buf) throws InvalidBufferTypeException {
        if (!this.isAcceptable(buf)) {
            throw InvalidBufferTypeException.of(this.directAccepted(), this.heapAccepted());
        }
        return buf;
    }
}
