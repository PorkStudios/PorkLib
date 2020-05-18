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

package net.daporkchop.lib.compression.zstd;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;

/**
 * Decompression context for {@link Zstd}.
 * <p>
 * Not thread-safe.
 *
 * @author DaPorkchop_
 */
public interface ZstdInflater extends PInflater {
    @Override
    ZstdInflaterOptions options();

    @Override
    default ZstdProvider provider() {
        return this.options().provider();
    }

    /**
     * Decompresses the given source data into the given destination buffer at the configured decompression level.
     * <p>
     * If the destination buffer does not have enough space writable for the decompressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     * <p>
     * This will digest the dictionary before decompressing, which is an expensive operation. If the same dictionary is going to be used multiple times,
     * it is strongly advised to use {@link #decompress(ByteBuf, ByteBuf, ZstdInflateDictionary)}.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write decompressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for decompression
     * @return whether or not decompression was successful. If {@code false}, the destination buffer was too small for the decompressed data
     */
    @Override
    boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict);

    /**
     * Decompresses the given compressed data into the given destination buffer using the given dictionary.
     * <p>
     * As the dictionary has already been digested, this is far faster than {@link #decompress(ByteBuf, ByteBuf, ByteBuf)}.
     *
     * @param dict the dictionary to use
     * @see #decompress(ByteBuf, ByteBuf, ByteBuf)
     */
    boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdInflateDictionary dict);

    /**
     * Decompresses the given source data into the given destination buffer at the configured decompression level.
     * <p>
     * This will continually grow the the destination buffer's capacity until enough space is available for decompression to be completed. If at any point
     * during the decompression the destination buffer's capacity cannot be increased sufficiently, the operation will fail and both buffer's indices will
     * remain unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     * <p>
     * This will digest the dictionary before decompressing, which is an expensive operation. If the same dictionary is going to be used multiple times,
     * it is strongly advised to use {@link #decompressGrowing(ByteBuf, ByteBuf, ZstdInflateDictionary)}.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write decompressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for decompression
     * @throws IndexOutOfBoundsException if the destination buffer's capacity could not be increased sufficiently
     */
    @Override
    void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException;

    /**
     * Decompresses the given compressed data into the given destination buffer using the given dictionary.
     * <p>
     * As the dictionary has already been digested, this is far faster than {@link #decompress(ByteBuf, ByteBuf, ByteBuf)}.
     *
     * @param dict the dictionary to use
     * @throws IndexOutOfBoundsException if the destination buffer's capacity could not be increased sufficiently
     * @see #decompressGrowing(ByteBuf, ByteBuf, ByteBuf)
     */
    void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdInflateDictionary dict) throws IndexOutOfBoundsException;

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     * <p>
     * This will digest the dictionary before decompressing, which is an expensive operation. If the same dictionary is going to be used multiple times,
     * it is strongly advised to use {@link #decompressionStream(DataIn, ByteBufAllocator, int, ZstdInflateDictionary)}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @param bufferSize  the size of the internal write buffer. If not positive, the default buffer size will be used
     */
    @Override
    DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException;

    /**
     * Convenience method equivalent to {@code compressionStream(in, null, -1, dict);}
     *
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ZstdInflateDictionary)
     */
    default DataIn decompressionStream(@NonNull DataIn in, ZstdInflateDictionary dict) throws IOException {
        return this.decompressionStream(in, null, -1, dict);
    }

    /**
     * Convenience method equivalent to {@code compressionStream(in, bufferAlloc, -1, dict);}
     *
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ZstdInflateDictionary)
     */
    default DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, ZstdInflateDictionary dict) throws IOException {
        return this.decompressionStream(in, bufferAlloc, -1, dict);
    }

    /**
     * Convenience method equivalent to {@code compressionStream(in, null, bufferSize, dict);}
     *
     * @see #decompressionStream(DataIn, ByteBufAllocator, int, ZstdInflateDictionary)
     */
    default DataIn decompressionStream(@NonNull DataIn in, int bufferSize, ZstdInflateDictionary dict) throws IOException {
        return this.decompressionStream(in, null, bufferSize, dict);
    }

    /**
     * Gets a {@link DataIn} which will decompress data written to it using this {@link PDeflater} and write the decompressed data to the given {@link DataIn}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @param bufferSize  the size of the internal write buffer. If not positive, the default buffer size will be used
     */
    DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ZstdInflateDictionary dict) throws IOException;

    @Override
    default boolean hasDict() {
        return true;
    }

    @Override
    int refCnt();

    @Override
    ZstdInflater retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
