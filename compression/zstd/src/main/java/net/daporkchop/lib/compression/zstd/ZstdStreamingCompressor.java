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

import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.compression.context.PStreamingCompressor;

/**
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface ZstdStreamingCompressor extends PStreamingCompressor {
    @Override
    void resetParameters();

    /**
     * Set the compression level. The special value {@link Zstd#LEVEL_DEFAULT} means that the default compression level will be used.
     * <p>
     * If a dictionary is set, the compression level is ignored.
     * <p>
     * The default value is {@link Zstd#LEVEL_DEFAULT}.
     *
     * @param level the compression level
     * @throws IllegalArgumentException if the given compression level isn't supported by the zstd library
     * @see #resetParameters()
     */
    void setLevel(int level) throws IllegalArgumentException;

    /**
     * Sets the dictionary used for compression. Setting this to {@code null} means that no dictionary will be used.
     * <p>
     * If the given dictionary is non-{@code null}, it must not be closed before being removed from the compressor (either by setting the dictionary to {@code null} or
     * by {@link #resetParameters()}).
     * <p>
     * The default value is {@code null}.
     *
     * @param dictionary the dictionary
     * @throws IllegalArgumentException      if the given dictionary isn't compatible with this compressor
     * @throws UnsupportedOperationException if this implementation doesn't support zstd dictionaries
     * @see #resetParameters()
     */
    void setDictionary(@ExtendedBorrow ZstdCompressDictionary dictionary) throws IllegalArgumentException;
}
