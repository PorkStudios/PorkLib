/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.compression.context;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.OptionalInt;

/**
 * A context for doing repeated one-shot compression operations.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface POneshotCompressor extends OneshotContext {
    //
    //
    // misc. methods
    //
    //

    /**
     * Resets this compressor's parameters to the defaults.
     * <p>
     * Parameters are sticky and will remain until explicitly reset.
     */
    void resetParameters();

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length.
     *
     * @param srcSize the size (in bytes) of the source data
     * @return the worst-case size of the compressed data
     * @throws IllegalArgumentException if {@code srcSize} is negative
     * @throws ArithmeticException      if the result would be larger than {@link Integer#MAX_VALUE}
     */
    default @NotNegative int compressBound(@NotNegative int srcSize) throws IllegalArgumentException, ArithmeticException {
        return Math.toIntExact(this.compressBound((long) srcSize));
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length.
     *
     * @param srcSize the size (in bytes) of the source data
     * @return the worst-case size of the compressed data
     * @throws IllegalArgumentException if {@code srcSize} is negative
     * @throws ArithmeticException      if the result would be larger than {@link Long#MAX_VALUE}
     */
    @NotNegative long compressBound(@NotNegative long srcSize) throws IllegalArgumentException, ArithmeticException;

    //
    //
    // compression methods
    //
    //

    /**
     * Compresses the given source data into the given destination buffer.
     * <p>
     * On success, the source buffer's position will be advanced to its limit, and the destination buffer's position will be increased by the number of bytes
     * produced by this operation (which is equal to the number returned by this method). On failure, both buffer's positions remain unchanged, however the
     * contents of the destination buffer's remaining bytes may be modified.
     *
     * @param src the {@link ByteBuffer} to read source data from
     * @param dst the {@link ByteBuffer} to write compressed data to
     * @return the size of the compressed data in bytes, or a negative value if the destination buffer was too small for the compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    int compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws ReadOnlyBufferException;

    /**
     * Compresses the given source data into the given destination buffer.
     * <p>
     * If the destination buffer does not have enough space writable for the compressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     *
     * @param src the {@link ByteBuf} to read source data from
     * @param dst the {@link ByteBuf} to write compressed data to
     * @return the size of the compressed data in bytes, or a negative value if the destination buffer was too small for the compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    int compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws ReadOnlyBufferException;

    /**
     * Compresses the given source data into the given destination buffer.
     * <p>
     * This will continually grow the the destination buffer's capacity until enough space is available for compression to be completed. If at any point
     * during the compression the destination buffer's capacity cannot be increased sufficiently, the operation will fail and both buffer's indices will
     * remain unchanged, however the destination buffer's contents may be modified.
     *
     * @param src the {@link ByteBuf} to read source data from
     * @param dst the {@link ByteBuf} to write compressed data to
     * @return the size of the compressed data in bytes
     * @throws IndexOutOfBoundsException if the destination buffer's capacity could not be increased sufficiently
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    @NotNegative int compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws IndexOutOfBoundsException, ReadOnlyBufferException;
}
