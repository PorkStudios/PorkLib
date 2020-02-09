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
import net.daporkchop.lib.compression.util.exception.ContextFinishedException;
import net.daporkchop.lib.compression.util.exception.ContextFinishingException;
import net.daporkchop.lib.compression.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.capability.Releasable;

/**
 * Base interface for deflaters and inflaters.
 * <p>
 * Unless explicitly specified, implementations of this class are not safe for use on multiple threads.
 *
 * @author DaPorkchop_
 */
interface Context<I extends Context<I>> extends Releasable {
    /**
     * Checks whether this context uses direct or heap memory.
     * <p>
     * {@link io.netty.buffer.ByteBuf}s of the wrong type will not be accepted by any methods, and will cause an {@link InvalidBufferTypeException} to be thrown.
     *
     * @return whether this context uses direct or heap memory
     */
    boolean direct();

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
     * This will erase any internal buffers and reset the source and destination buffers to {@code null}.
     *
     * @return this context
     */
    I reset();
}
