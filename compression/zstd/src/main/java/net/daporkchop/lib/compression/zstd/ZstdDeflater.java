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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;

/**
 * Compression context for {@link Zstd}.
 * <p>
 * Not thread-safe.
 *
 * @author DaPorkchop_
 */
public interface ZstdDeflater extends PDeflater {
    @Override
    ZstdDeflaterOptions options();

    @Override
    default ZstdProvider provider() {
        return this.options().provider();
    }

    /**
     * Convenience method equivalent to {@code compress(src, dst, null, this.options().level());}
     *
     * @see #compress(ByteBuf, ByteBuf, ByteBuf, int)
     */
    @Override
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        return this.compress(src, dst, null, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compress(src, dst, null, level);}
     *
     * @see #compress(ByteBuf, ByteBuf, ByteBuf, int)
     */
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, int level) {
        return this.compress(src, dst, null, level);
    }

    /**
     * Convenience method equivalent to {@code compress(src, dst, dict, this.options().level());}
     *
     * @see #compress(ByteBuf, ByteBuf, ByteBuf, int)
     */
    @Override
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        return this.compress(src, dst, dict, this.options().level());
    }

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer at the given Zstd level using the given dictionary.
     * <p>
     * If the destination buffer does not have enough space writable for the compressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     * <p>
     * This will digest the dictionary before compressing, which is an expensive operation. If the same dictionary is going to be used multiple times,
     * it is strongly advised to use {@link #compress(ByteBuf, ByteBuf, ZstdDeflateDictionary)}.
     *
     * @param src   the {@link ByteBuf} to read source data from
     * @param dst   the {@link ByteBuf} to write compressed data to
     * @param dict  the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for compression
     * @param level the compression level to use
     * @return whether or not compression was successful. If {@code false}, the destination buffer was too small for the compressed data
     * @see #compress(ByteBuf, ByteBuf, ZstdDeflateDictionary)
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level);

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer using the given dictionary.
     * <p>
     * As the dictionary has already been digested, this is far faster than the other dictionary compression methods.
     *
     * @param dict the dictionary to use
     * @see #compress(ByteBuf, ByteBuf, ByteBuf, int)
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict);

    /**
     * Convenience method equivalent to {@code compressGrowing(src, dst, null, this.options().level());}
     *
     * @see #compressGrowing(ByteBuf, ByteBuf, ByteBuf, int)
     */
    @Override
    default void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws IndexOutOfBoundsException {
        this.compressGrowing(src, dst, null, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressGrowing(src, dst, null, level);}
     *
     * @see #compressGrowing(ByteBuf, ByteBuf, ByteBuf, int)
     */
    default void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, int level) throws IndexOutOfBoundsException {
        this.compressGrowing(src, dst, null, level);
    }

    /**
     * Convenience method equivalent to {@code compressGrowing(src, dst, dict, this.options().level());}
     *
     * @see #compressGrowing(ByteBuf, ByteBuf, ByteBuf, int)
     */
    @Override
    default void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        this.compressGrowing(src, dst, dict, this.options().level());
    }

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer at the given Zstd level using the given dictionary.
     * <p>
     * This will continually grow the the destination buffer's capacity until enough space is available for compression to be completed. If at any point
     * during the compression the destination buffer's capacity cannot be increased sufficiently, the operation will fail and both buffer's indices will
     * remain unchanged, however the destination buffer's contents may be modified.
     * <p>
     * In either case, the indices of the dictionary buffer remain unaffected.
     * <p>
     * This will digest the dictionary before compressing, which is an expensive operation. If the same dictionary is going to be used multiple times,
     * it is strongly advised to use {@link #compressGrowing(ByteBuf, ByteBuf, ZstdDeflateDictionary)}.
     *
     * @param src  the {@link ByteBuf} to read source data from
     * @param dst  the {@link ByteBuf} to write compressed data to
     * @param dict the (possibly {@code null}) {@link ByteBuf} containing the dictionary to be used for compression
     * @throws IndexOutOfBoundsException if the destination buffer's capacity could not be increased sufficiently
     * @see #compressGrowing(ByteBuf, ByteBuf, ZstdDeflateDictionary)
     */
    void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) throws IndexOutOfBoundsException;

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer using the given dictionary.
     * <p>
     * As the dictionary has already been digested, this is far faster than the other dictionary compression methods.
     *
     * @param dict the dictionary to use
     * @throws IndexOutOfBoundsException if the destination buffer's capacity could not be increased sufficiently
     * @see #compressGrowing(ByteBuf, ByteBuf, ByteBuf, int)
     */
    void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) throws IndexOutOfBoundsException;

    /**
     * Convenience method equivalent to {@code compressionStream(out, null, -1, null, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out) throws IOException {
        return this.compressionStream(out, null, -1, null, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressionStream(out, bufferAlloc, -1, null, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc) throws IOException {
        return this.compressionStream(out, bufferAlloc, -1, null, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressionStream(out, null, bufferSize, null, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out, int bufferSize) throws IOException {
        return this.compressionStream(out, null, bufferSize, null, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressionStream(out, null, -1, dict, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out, ByteBuf dict) throws IOException {
        return this.compressionStream(out, null, -1, dict, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressionStream(out, bufferAlloc, -1, dict, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, ByteBuf dict) throws IOException {
        return this.compressionStream(out, bufferAlloc, -1, dict, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressionStream(out, null, bufferSize, dict, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out, int bufferSize, ByteBuf dict) throws IOException {
        return this.compressionStream(out, null, bufferSize, dict, this.options().level());
    }

    /**
     * Convenience method equivalent to {@code compressionStream(out, bufferAlloc, bufferSize, dict, this.options().level());}
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ByteBuf, int)
     */
    @Override
    default DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException {
        return this.compressionStream(out, bufferAlloc, bufferSize, dict, this.options().level());
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @param bufferSize  the size of the internal write buffer. If not positive, the default buffer size will be used
     * @param level       the Zstd compression level to use
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     */
    DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict, int level) throws IOException;

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default {@link ByteBufAllocator} and size.
     *
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ZstdDeflateDictionary)
     */
    default DataOut compressionStream(@NonNull DataOut out, ZstdDeflateDictionary dict) throws IOException {
        return this.compressionStream(out, null, -1, dict);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default size.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ZstdDeflateDictionary)
     */
    default DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, ZstdDeflateDictionary dict) throws IOException {
        return this.compressionStream(out, bufferAlloc, -1, dict);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     * <p>
     * This will cause the internal write buffer to be allocated using the default {@link ByteBufAllocator}.
     *
     * @param bufferSize the size of the internal write buffer. If not positive, the default buffer size will be used
     * @see #compressionStream(DataOut, ByteBufAllocator, int, ZstdDeflateDictionary)
     */
    default DataOut compressionStream(@NonNull DataOut out, int bufferSize, ZstdDeflateDictionary dict) throws IOException {
        return this.compressionStream(out, null, bufferSize, dict);
    }

    /**
     * Gets a {@link DataOut} which will compress data written to it using this {@link PDeflater} and write the compressed data to the given {@link DataOut}.
     *
     * @param bufferAlloc the {@link ByteBufAllocator} to be used for allocating the internal write buffer. If {@code null}, the default allocator will be used
     * @param bufferSize  the size of the internal write buffer. If not positive, the default buffer size will be used
     * @throws DictionaryNotAllowedException if the dictionary buffer is not {@code null} and this context does not allow use of a dictionary
     */
    DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ZstdDeflateDictionary dict) throws IOException;

    @Override
    default boolean hasDict() {
        return true;
    }

    @Override
    int refCnt();

    @Override
    ZstdDeflater retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
