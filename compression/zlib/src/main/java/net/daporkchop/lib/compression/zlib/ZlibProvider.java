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

package net.daporkchop.lib.compression.zlib;

import lombok.NonNull;
import net.daporkchop.lib.compression.CompressionProvider;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.natives.impl.Feature;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Representation of a Zlib implementation.
 *
 * @author DaPorkchop_
 */
public interface ZlibProvider extends CompressionProvider<ZlibProvider, ZlibDeflaterOptions, ZlibInflaterOptions>, Feature<ZlibProvider> {
    @Override
    default int levelFast() {
        return Zlib.LEVEL_FASTEST;
    }

    @Override
    default int levelDefault() {
        return Zlib.LEVEL_DEFAULT;
    }

    @Override
    default int levelBest() {
        return Zlib.LEVEL_BEST;
    }

    @Override
    default int compressBound(int srcSize) {
        return toInt(this.compressBoundLong(srcSize, ZlibMode.ZLIB));
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the Gzip format.
     *
     * @see #compressBound(int)
     */
    default int compressBoundGzip(int srcSize) {
        return toInt(this.compressBoundLong(srcSize, ZlibMode.GZIP));
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the given mode.
     *
     * @param srcSize the size (in bytes) of the source data
     * @param mode    the {@link ZlibMode} to use
     * @return the worst-case size of the compressed data
     * @see #compressBound(int)
     */
    default int compressBound(long srcSize, @NonNull ZlibMode mode) {
        return toInt(this.compressBoundLong(srcSize, mode));
    }

    @Override
    default long compressBoundLong(long srcSize) {
        return this.compressBoundLong(srcSize, ZlibMode.ZLIB);
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the Gzip format.
     *
     * @see #compressBoundLong(long)
     */
    default long compressBoundGzipLong(long srcSize) {
        return this.compressBoundLong(srcSize, ZlibMode.GZIP);
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the given mode.
     *
     * @param srcSize the size (in bytes) of the source data
     * @param mode    the {@link ZlibMode} to use
     * @return the worst-case size of the compressed data
     */
    long compressBoundLong(long srcSize, @NonNull ZlibMode mode);
}
