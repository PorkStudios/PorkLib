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

package net.daporkchop.lib.compression.zstd;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.Borrow;

import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
public interface ZstdDictionaryFactory {
    /**
     * Creates a digested dictionary for repeated zstd compression.
     *
     * @param dict  a {@link ByteBuffer} whose remaining bytes are the dictionary contents. The buffer's indices will not be modified. Note that this buffer may be empty, in which case
     *              the resulting dictionary will contain only the compression level.
     * @param level the compression level, or {@link Zstd#LEVEL_DEFAULT}
     * @return a compression dictionary
     * @throws IllegalArgumentException      if the given compression level isn't supported by the zstd library
     * @throws UnsupportedOperationException if this implementation doesn't support zstd dictionaries
     */
    ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuffer dict, int level) throws IllegalArgumentException;

    /**
     * Creates a digested dictionary for repeated zstd compression.
     *
     * @param dict  a {@link ByteBuf} whose readable bytes are the dictionary contents. The buffer's indices will not be modified. Note that this buffer may be empty, in which case
     *              the resulting dictionary will contain only the compression level.
     * @param level the compression level, or {@link Zstd#LEVEL_DEFAULT}
     * @return a compression dictionary
     * @throws IllegalArgumentException      if the given compression level isn't supported by the zstd library
     * @throws UnsupportedOperationException if this implementation doesn't support zstd dictionaries
     */
    ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuf dict, int level) throws IllegalArgumentException;

    /**
     * Creates a digested dictionary for repeated zstd decompression.
     *
     * @param dict a {@link ByteBuf} whose readable bytes are the dictionary contents. The buffer's indices will not be modified.
     * @return a decompression dictionary
     * @throws UnsupportedOperationException if this implementation doesn't support zstd dictionaries
     */
    ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuffer dict);

    /**
     * Creates a digested dictionary for repeated zstd decompression.
     *
     * @param dict a {@link ByteBuf} whose readable bytes are the dictionary contents. The buffer's indices will not be modified.
     * @return a decompression dictionary
     * @throws UnsupportedOperationException if this implementation doesn't support zstd dictionaries
     */
    ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuf dict);
}
