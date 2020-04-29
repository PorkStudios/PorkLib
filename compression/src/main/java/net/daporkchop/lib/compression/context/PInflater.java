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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.compression.option.InflaterOptions;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;

/**
 * A context for doing repeated one-shot decompression operations.
 *
 * @author DaPorkchop_
 */
public interface PInflater extends Context {
    //
    //
    // decompression methods
    //
    //

    /**
     * Convenience method, equivalent to {@code decompress(src, dst, null);}.
     *
     * @see #decompress(ByteBuf, ByteBuf, ByteBuf)
     */
    default boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        return this.decompress(src, dst, null);
    }

    /**
     * Dedecompresses the given source data into the given destination buffer at the configured decompression level.
     * <p>
     * If the destination buffer does not have enough space writable for the decompressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write decompressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for decompression
     * @return whether or not decompression was successful. If {@code false}, the destination buffer was too small for the decompressed data
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     */
    boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException;

    /**
     * Convenience method, equivalent to {@code decompressGrowing(src, dst, null);}.
     *
     * @see #decompressGrowing(ByteBuf, ByteBuf, ByteBuf)
     */
    default void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws DictionaryNotAllowedException, IndexOutOfBoundsException {
        this.decompressGrowing(src, dst, null);
    }

    /**
     * Dedecompresses the given source data into the given destination buffer at the configured decompression level.
     * <p>
     * This will continually grow the the destination buffer's capacity until enough space is available for decompression to be completed. If at any point
     * during the decompression the destination buffer's capacity cannot be increased sufficiently, the operation will fail and both buffer's indices will
     * remain unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write decompressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for decompression
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     * @throws IndexOutOfBoundsException     if the destination buffer's capacity could not be increased sufficiently
     */
    void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException, IndexOutOfBoundsException;

    //
    //
    // stream creator methods
    //
    //

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     *
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ByteBuf)
     */
    default DataIn decompressionStream(@NonNull DataIn in) throws IOException {
        return this.decompressionStream(in, null, -1, null);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ByteBuf)
     */
    default DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc) throws IOException {
        return this.decompressionStream(in, bufferAlloc, -1, null);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     *
     * @param bufferSize the size of the internal write buffer. If not positive, the default buffer size will be used
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ByteBuf)
     */
    default DataIn decompressionStream(@NonNull DataIn in, int bufferSize) throws IOException {
        return this.decompressionStream(in, null, bufferSize, null);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default {@link ByteBufAllocator} and size.
     *
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ByteBuf)
     */
    default DataIn decompressionStream(@NonNull DataIn in, ByteBuf dict) throws IOException, DictionaryNotAllowedException {
        return this.decompressionStream(in, null, -1, dict);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default size.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ByteBuf)
     */
    default DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, ByteBuf dict) throws IOException, DictionaryNotAllowedException {
        return this.decompressionStream(in, bufferAlloc, -1, dict);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default {@link ByteBufAllocator}.
     *
     * @param bufferSize the size of the internal write buffer. If not positive, the default buffer size will be used
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ByteBuf)
     */
    default DataIn decompressionStream(@NonNull DataIn in, int bufferSize, ByteBuf dict) throws IOException, DictionaryNotAllowedException {
        return this.decompressionStream(in, null, bufferSize, dict);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @param bufferSize  the size of the internal write buffer. If not positive, the default buffer size will be used
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     */
    DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException, DictionaryNotAllowedException;

    //
    //
    // misc. methods
    //
    //

    /**
     * @return the options that this {@link PInflater} is configured with
     */
    InflaterOptions options();

    @Override
    boolean hasDict();

    @Override
    int refCnt();

    @Override
    PInflater retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
