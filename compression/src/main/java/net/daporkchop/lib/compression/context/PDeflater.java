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
import io.netty.buffer.ByteBufAllocator;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.compression.option.DeflaterOptions;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A context for doing repeated one-shot compression operations.
 *
 * @author DaPorkchop_
 */
public interface PDeflater extends Context {
    //
    //
    // compression methods
    //
    //

    /**
     * Convenience method, equivalent to {@code compress(src, dst, null);}.
     *
     * @see #compress(ByteBuf, ByteBuf, ByteBuf)
     */
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        return this.compress(src, dst, null);
    }

    /**
     * Compresses the given source data into the given destination buffer at the configured compression level.
     * <p>
     * If the destination buffer does not have enough space writable for the compressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write compressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for compression
     * @return whether or not compression was successful. If {@code false}, the destination buffer was too small for the compressed data
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException;

    /**
     * Convenience method, equivalent to {@code compressGrowing(src, dst, null);}.
     *
     * @see #compressGrowing(ByteBuf, ByteBuf, ByteBuf)
     */
    default void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws DictionaryNotAllowedException, IndexOutOfBoundsException {
        this.compressGrowing(src, dst, null);
    }

    /**
     * Compresses the given source data into the given destination buffer at the configured compression level.
     * <p>
     * This will continually grow the the destination buffer's capacity until enough space is available for compression to be completed. If at any point
     * during the compression the destination buffer's capacity cannot be increased sufficiently, the operation will fail and both buffer's indices will
     * remain unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write compressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for compression
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     * @throws IndexOutOfBoundsException     if the destination buffer's capacity could not be increased sufficiently
     */
    void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException, IndexOutOfBoundsException;

    //
    //
    // stream creator methods
    //
    //

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf)
     */
    default DataOut compressionStream(@NonNull DataOut out) {
        return this.compressionStream(out, null, -1, null);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf)
     */
    default DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc) {
        return this.compressionStream(out, bufferAlloc, -1, null);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     *
     * @param bufferSize the size of the internal write buffer. If not positive, the default buffer size will be used
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf)
     */
    default DataOut compressionStream(@NonNull DataOut out, int bufferSize) {
        return this.compressionStream(out, null, bufferSize, null);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default {@link ByteBufAllocator} and size.
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf)
     */
    default DataOut compressionStream(@NonNull DataOut out, ByteBuf dict) throws DictionaryNotAllowedException {
        return this.compressionStream(out, null, -1, dict);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default size.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf)
     */
    default DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, ByteBuf dict) throws DictionaryNotAllowedException {
        return this.compressionStream(out, bufferAlloc, -1, dict);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default {@link ByteBufAllocator}.
     *
     * @param bufferSize the size of the internal write buffer. If not positive, the default buffer size will be used
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf)
     */
    default DataOut compressionStream(@NonNull DataOut out, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException {
        return this.compressionStream(out, null, bufferSize, dict);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @param bufferSize  the size of the internal write buffer. If not positive, the default buffer size will be used
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     */
    DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException;

    //
    //
    // misc. methods
    //
    //

    /**
     * @return the options that this {@link PDeflater} is configured with
     */
    DeflaterOptions options();

    @Override
    boolean hasDict();

    @Override
    int refCnt();

    @Override
    PDeflater retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
