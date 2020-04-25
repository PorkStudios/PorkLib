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

package net.daporkchop.lib.compression.context;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.compression.util.exception.ContextFinishedException;
import net.daporkchop.lib.compression.util.exception.ContextFinishingException;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * Base interface for {@link PDeflater} and {@link PDeflater}.
 * @author DaPorkchop_
 */
interface StreamingContext<I extends StreamingContext<I>> extends Context {
    /**
     * Sets the context's current source buffer when processing data in streaming mode.
     *
     * @param src the {@link ByteBuf} to read data from
     * @return this context
     * @throws ContextFinishingException if this context is already being finished, and as such the source buffer may not be updated any more
     */
    I src(@NonNull ByteBuf src) throws InvalidBufferTypeException, ContextFinishingException;

    /**
     * Sets the context's current destination buffer when processing data in streaming mode.
     *
     * @param dst the {@link ByteBuf} to write data to
     * @return this context
     */
    I dst(@NonNull ByteBuf dst) throws InvalidBufferTypeException;

    /**
     * Updates this context, processing as much data as possible.
     * <p>
     * This will read from the source buffer and write to the destination buffer until the source buffer runs dry or the destination buffer fills up.
     * <p>
     * Implementations may buffer any amount of data internally.
     *
     * @param flush whether or not the internal buffer should be flushed. If {@code true}, an attempt will be made to flush as much buffered data as possible. Note
     *              that this can cause a negative impact on the compression ratio.
     * @return this context
     * @throws ContextFinishedException  if this context is already finished and needs to be reset before being used again
     * @throws ContextFinishingException if this context is already being finished (but is not yet completely finished)
     */
    I update(boolean flush) throws ContextFinishedException, ContextFinishingException;

    /**
     * Finishes this context.
     * <p>
     * This will read from the source buffer and write to the destination buffer until the source buffer runs dry or the destination buffer fills up, and then
     * attempt to flush any internally buffered data and finish the (de)compression process.
     *
     * @return whether or not the context could be completed. If {@code false}, there is not enough space in the destination buffer for the context to finish
     * @throws ContextFinishedException if this context is already finished and needs to be reset before being used again
     */
    boolean finish() throws ContextFinishedException;

    /**
     * Resets this context.
     * <p>
     * This will discard any internal buffers and reset the source, destination and dictionary buffers to {@code null}.
     *
     * @return this context
     */
    I reset();

    /**
     * Sets the dictionary to be used by this context.
     * <p>
     * Must be called immediately after being initialized or reset.
     * <p>
     * The dictionary will remain referenced until the context is reset.
     *
     * @param dict the new dictionary to use. The currently readable region of the buffer will be used as the dictionary.
     * @return this context
     * @throws DictionaryNotAllowedException if this context does not allow use of a dictionary
     * @see #hasDict()
     */
    default I dict(@NonNull ByteBuf dict) throws InvalidBufferTypeException, DictionaryNotAllowedException {
        throw new DictionaryNotAllowedException();
    }
}
