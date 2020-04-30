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
import net.daporkchop.lib.compression.CompressionProvider;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.provider.OneShotCompressionProvider;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.natives.impl.Feature;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * Representation of a Zstd implementation.
 *
 * @author DaPorkchop_
 */
public interface ZstdProvider extends CompressionProvider<ZstdProvider, ZstdDeflaterOptions, ZstdInflaterOptions>, Feature<ZstdProvider> {
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
    default int levelFast() {
        return Zstd.LEVEL_MIN;
    }

    @Override
    default int levelDefault() {
        return Zstd.LEVEL_DEFAULT;
    }

    @Override
    default int levelBest() {
        return Zstd.LEVEL_MAX;
    }

    @Override
    ZstdDeflater deflater(@NonNull ZstdDeflaterOptions options);

    @Override
    ZstdInflater inflater(@NonNull ZstdInflaterOptions options);

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
