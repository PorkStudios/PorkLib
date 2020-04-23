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
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.OneShotCompressionProvider;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.natives.impl.Feature;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * Representation of a Zstd implementation.
 *
 * @author DaPorkchop_
 */
public interface ZstdProvider extends OneShotCompressionProvider, Feature<ZstdProvider> {
    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer at the default compression level.
     *
     * @see #compress(ByteBuf, ByteBuf, int)
     */
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        return this.compress(src, dst, Zstd.LEVEL_DEFAULT);
    }

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer.
     * <p>
     * If the destination buffer does not have enough space writable for the compressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     *
     * @param src              the {@link ByteBuf} to read source data from
     * @param dst              the {@link ByteBuf} to write compressed data to
     * @param compressionLevel the Zstd level to compress at
     * @return whether or not compression was successful. If {@code false}, the destination buffer was too small for the compressed data
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, int compressionLevel) throws InvalidBufferTypeException;

    /**
     * Decompresses the given Zstd-compressed into the given destination buffer.
     * <p>
     * If the destination buffer does not have enough space writable for the decompressed data, the operation will fail and both buffer's indices will remain
     * unchanged, however the destination buffer's contents may be modified.
     *
     * @param src the {@link ByteBuf} to read compressed data from
     * @param dst the {@link ByteBuf} to write decompressed data to
     * @return whether or not decompression was successful. If {@code false}, the destination buffer was too small for the decompressed data
     */
    boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException;

    /**
     * @see #frameContentSizeLong(ByteBuf)
     */
    default int frameContentSize(@NonNull ByteBuf src) throws InvalidBufferTypeException, ContentSizeUnknownException {
        return PValidation.toInt(this.frameContentSizeLong(src));
    }

    /**
     * Gets the decompressed size of the given Zstd-compressed data.
     *
     * @param src the {@link ByteBuf} containing the compressed data. This {@link ByteBuf}'s indices will not be modified by this method
     * @return the size (in bytes) of the decompressed data
     * @throws ContentSizeUnknownException if the decompressed size cannot be determined
     */
    long frameContentSizeLong(@NonNull ByteBuf src) throws InvalidBufferTypeException, ContentSizeUnknownException;

    @Override
    default ZstdCCtx compressionContext() {
        return this.compressionContext(Zstd.LEVEL_DEFAULT);
    }

    @Override
    ZstdCCtx compressionContext(int level) throws InvalidCompressionLevelException;

    @Override
    ZstdDCtx decompressionContext();

    /**
     * Digests a Zstd dictionary for compression at the default level.
     *
     * @param dict the {@link ByteBuf} containing the dictionary
     * @return the digested dictionary
     */
    default ZstdCDict compressionDictionary(@NonNull ByteBuf dict) throws InvalidBufferTypeException {
        return this.compressionDictionary(dict, Zstd.LEVEL_DEFAULT);
    }

    /**
     * Digests a Zstd dictionary for compression at the given level.
     *
     * @param dict  the {@link ByteBuf} containing the dictionary
     * @param level the compression level to use
     * @return the digested dictionary
     */
    ZstdCDict compressionDictionary(@NonNull ByteBuf dict, int level) throws InvalidBufferTypeException;

    /**
     * Digests a Zstd dictionary for decompression.
     *
     * @param dict the {@link ByteBuf} containing the dictionary
     * @return the digested dictionary
     */
    ZstdDDict decompressionDictionary(@NonNull ByteBuf dict) throws InvalidBufferTypeException;

}
