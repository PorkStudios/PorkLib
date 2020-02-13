/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.compression;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.natives.util.BufferTyped;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.capability.Releasable;

/**
 * Base interface for {@link PDeflater} and {@link PDeflater}.
 * <p>
 * Unless explicitly specified, implementations of this class are not safe for use on multiple threads.
 *
 * @author DaPorkchop_
 */
interface Context<I extends Context<I>> extends Releasable, BufferTyped {
    /**
     * @return the {@link CompressionProvider} that created this context
     */
    CompressionProvider provider();

    /**
     * Resets this context.
     * <p>
     * This will reset the dictionary buffer to {@code null}.
     *
     * @return this context
     */
    I reset();

    /**
     * @return whether or not this implementation allows use of a dictionary
     */
    default boolean hasDict() {
        return false;
    }

    /**
     * Sets the dictionary to be used by this context.
     * <p>
     * Must be called immediately after being initialized or reset.
     * <p>
     * The dictionary will remain referenced until the context is reset.
     *
     * @param dict the new dictionary to use. The currently readable region of the buffer will be used as the dictionary.
     * @return this context
     * @throws UnsupportedOperationException if this context does not allow use of a dictionary
     * @see #hasDict()
     */
    default I dict(@NonNull ByteBuf dict) throws InvalidBufferTypeException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
