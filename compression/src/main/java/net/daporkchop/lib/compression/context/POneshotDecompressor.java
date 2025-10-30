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
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

/**
 * A context for doing repeated one-shot decompression operations.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface POneshotDecompressor extends OneshotContext {
    //
    //
    // misc. methods
    //
    //

    /**
     * Resets this decompressor's parameters to the defaults.
     * <p>
     * Parameters are sticky and will remain until explicitly reset.
     */
    POneshotDecompressor resetParameters();

    /**
     * Tries to determine the exact size of the given source data once fully decompressed.
     * <p>
     * Source data is accessed from the given buffer's remaining bytes, but the buffer's position will not be modified.
     *
     * @param src the {@link ByteBuffer} containing the compressed source data
     * @return the exact decompressed size of the given source data, or an empty optional if the decompressed size is not available (either because it was not
     * included in the source data, or the compression format doesn't support it)
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ArithmeticException if the result would be larger than {@link Long#MAX_VALUE}
     */
    @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException;

    /**
     * Tries to determine the exact size of the given source data once fully decompressed.
     * <p>
     * Source data is accessed from the given buffer's readable range, but the buffer's indices will not be modified.
     *
     * @param src the {@link ByteBuf} containing the compressed source data
     * @return the exact decompressed size of the given source data, or an empty optional if the decompressed size is not available (either because it was not
     * included in the source data, or the compression format doesn't support it)
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ArithmeticException if the result would be larger than {@link Long#MAX_VALUE}
     */
    @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException;

    /**
     * Compute an upper bound on the size of the given source data once fully decompressed.
     * <p>
     * Source data is accessed from the given buffer's remaining bytes, but the buffer's position will not be modified.
     *
     * @param src the {@link ByteBuffer} containing the compressed source data
     * @return an upper bound on the source data's decompressed size
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ArithmeticException if the result would be larger than {@link Long#MAX_VALUE}
     */
    @NotNegative long decompressedSizeBound(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException;

    /**
     * Compute an upper bound on the size of the given source data once fully decompressed.
     * <p>
     * Source data is accessed from the given buffer's readable range, but the buffer's indices will not be modified.
     *
     * @param src the {@link ByteBuf} containing the compressed source data
     * @return an upper bound on the source data's decompressed size
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ArithmeticException if the result would be larger than {@link Long#MAX_VALUE}
     */
    @NotNegative long decompressedSizeBound(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException;

    //
    //
    // decompression methods
    //
    //

    /**
     * Decompresses the given source data into the given destination buffer.
     * <p>
     * On success, the source buffer's position will be advanced to its limit, and the destination buffer's position will be increased by the number of bytes
     * produced by this operation (which is equal to the number returned by this method). On failure, both buffer's positions remain unchanged, however the
     * contents of the destination buffer's remaining bytes may be modified.
     *
     * @param src the {@link ByteBuffer} to read source data from
     * @param dst the {@link ByteBuffer} to write compressed data to
     * @return the size of the decompressed data in bytes, or a negative value if the destination buffer was too small for the decompressed data
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException;

    /**
     * Decompresses the given source data into the given destination buffer.
     * <p>
     * If the destination buffer does not have enough space writable for the decompressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     *
     * @param src the {@link ByteBuf} to read source data from
     * @param dst the {@link ByteBuf} to write decompressed data to
     * @return the size of the decompressed data in bytes, or a negative value if the destination buffer was too small for the decompressed data
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    int decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws DataFormatException, ReadOnlyBufferException;

    /**
     * Decompresses the given source data into the given destination buffer.
     * <p>
     * This will continually grow the the destination buffer's capacity until enough space is available for decompression to be completed. If at any point
     * during the decompression the destination buffer's capacity cannot be increased sufficiently, the operation will fail and both buffer's indices will
     * remain unchanged, however the destination buffer's contents may be modified.
     *
     * @param src the {@link ByteBuf} to read source data from
     * @param dst the {@link ByteBuf} to write decompressed data to
     * @return the size of the decompressed data in bytes
     * @throws DataFormatException       if the source data is not valid compressed data
     * @throws IndexOutOfBoundsException if the destination buffer's capacity could not be increased sufficiently
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    @NotNegative int decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws DataFormatException, IndexOutOfBoundsException, ReadOnlyBufferException;
}
