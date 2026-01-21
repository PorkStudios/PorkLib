/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
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

package net.daporkchop.lib.compression.zstd;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.context.POneshotDecompressor;
import net.daporkchop.lib.compression.zstd.util.JavaZstdFrameInspector;

import java.nio.ByteBuffer;
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

/**
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface ZstdOneshotDecompressor extends POneshotDecompressor, ZstdDecompressParameters {
    //TODO: these methods should respect the current singleFrame parameter

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
    default @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeExact();
    }

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
    default @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeExact();
    }

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
    default @NotNegative long decompressedSizeBound(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeUpperBound();
    }

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
    default @NotNegative long decompressedSizeBound(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeUpperBound();
    }
}
