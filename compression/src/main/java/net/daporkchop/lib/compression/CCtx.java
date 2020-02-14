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
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * A context for doing repeated one-shot compression operations.
 *
 * @author DaPorkchop_
 */
public interface CCtx extends Context<CCtx> {
    /**
     * Compresses the given source data into the given destination buffer at the currently configured compression level.
     * <p>
     * If the destination buffer does not have enough space writable for the compressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     * <p>
     * The currently configured dictionary will always remain unaffected by this method.
     *
     * @param src the {@link ByteBuf} to read source data from
     * @param dst the {@link ByteBuf} to write compressed data to
     * @return whether or not compression was successful. If {@code false}, the destination buffer was too small for the compressed data
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException;

    /**
     * @return the currently configured compression level
     */
    int level();

    /**
     * Updates this context's compression level.
     *
     * @param level the new compression level to use
     * @return this context
     * @throws InvalidCompressionLevelException if the given compression level is invalid
     */
    CCtx level(int level) throws InvalidCompressionLevelException;
}
