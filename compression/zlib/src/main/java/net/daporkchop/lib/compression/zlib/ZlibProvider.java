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


import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.exception.ValueCannotFitException;
import net.daporkchop.lib.compression.CompressionProvider;
import net.daporkchop.lib.compression.OneShotCompressionProvider;
import net.daporkchop.lib.compression.StreamingCompressionProvider;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.natives.impl.Feature;

/**
 * Representation of a Zlib implementation.
 *
 * @author DaPorkchop_
 */
public interface ZlibProvider extends StreamingCompressionProvider, OneShotCompressionProvider, Feature<ZlibProvider> {
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
    default int compressBound(int srcSize) throws ValueCannotFitException {
        return PValidation.toInt(this.compressBoundLong(srcSize, Zlib.MODE_ZLIB));
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the Gzip format.
     *
     * @see #compressBound(int)
     */
    default int compressBoundGzip(int srcSize) throws ValueCannotFitException {
        return PValidation.toInt(this.compressBoundLong(srcSize, Zlib.MODE_GZIP));
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the given mode.
     *
     * @param srcSize the size (in bytes) of the source data
     * @param mode    the {@link Zlib} mode to use. Must be one of {@link Zlib#MODE_ZLIB}, {@link Zlib#MODE_GZIP} or {@link Zlib#MODE_RAW}
     * @return the worst-case size of the compressed data
     * @see #compressBound(int)
     */
    default int compressBound(long srcSize, int mode) throws ValueCannotFitException {
        return PValidation.toInt(this.compressBoundLong(srcSize, mode));
    }

    @Override
    default long compressBoundLong(long srcSize) {
        return this.compressBoundLong(srcSize, Zlib.MODE_ZLIB);
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the Gzip format.
     *
     * @see #compressBoundLong(long)
     */
    default long compressBoundGzipLong(long srcSize) {
        return this.compressBoundLong(srcSize, Zlib.MODE_GZIP);
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length using the given mode.
     *
     * @param srcSize the size (in bytes) of the source data
     * @param mode    the {@link Zlib} mode to use. Must be one of {@link Zlib#MODE_ZLIB}, {@link Zlib#MODE_GZIP} or {@link Zlib#MODE_RAW}
     * @return the worst-case size of the compressed data
     */
    long compressBoundLong(long srcSize, int mode);

    /**
     * @return a new {@link ZlibDeflater}
     */
    @Override
    default ZlibDeflater deflater() {
        return this.deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT, Zlib.MODE_ZLIB);
    }

    /**
     * Creates a new {@link ZlibDeflater}.
     *
     * @param level the {@link Zlib} level to use
     * @return a new {@link ZlibDeflater} with the given level
     */
    @Override
    default ZlibDeflater deflater(int level) throws InvalidCompressionLevelException {
        return this.deflater(level, Zlib.STRATEGY_DEFAULT, Zlib.MODE_ZLIB);
    }

    /**
     * Creates a new {@link ZlibDeflater}.
     *
     * @param level    the {@link Zlib} level to use
     * @param strategy the {@link Zlib} strategy to use
     * @return a new {@link ZlibDeflater} with the given level and strategy
     */
    default ZlibDeflater deflater(int level, int strategy) throws InvalidCompressionLevelException {
        return this.deflater(level, strategy, Zlib.MODE_ZLIB);
    }

    /**
     * @return a new {@link ZlibDeflater} using the Gzip format
     */
    default ZlibDeflater deflaterGzip() {
        return this.deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT, Zlib.MODE_GZIP);
    }

    /**
     * Creates a new {@link ZlibDeflater} using the Gzip format.
     *
     * @param level the {@link Zlib} level to use
     * @return a new {@link ZlibDeflater} using the Gzip format with the given level
     */
    default ZlibDeflater deflaterGzip(int level) throws InvalidCompressionLevelException {
        return this.deflater(level, Zlib.STRATEGY_DEFAULT, Zlib.MODE_GZIP);
    }

    /**
     * Creates a new {@link ZlibDeflater} using the Gzip format.
     *
     * @param level    the {@link Zlib} level to use
     * @param strategy the {@link Zlib} strategy to use
     * @return a new {@link ZlibDeflater} using the Gzip format with the given level and strategy
     */
    default ZlibDeflater deflaterGzip(int level, int strategy) throws InvalidCompressionLevelException {
        return this.deflater(level, strategy, Zlib.MODE_GZIP);
    }

    /**
     * Creates a new {@link ZlibDeflater}.
     *
     * @param level    the {@link Zlib} level to use. Must be in range {@link Zlib#LEVEL_NONE} to {@link Zlib#LEVEL_BEST} (inclusive), or {@link Zlib#LEVEL_DEFAULT}
     * @param strategy the {@link Zlib} strategy to use. Must be one of {@link Zlib#STRATEGY_DEFAULT}, {@link Zlib#STRATEGY_FILTERED}, {@link Zlib#STRATEGY_HUFFMAN},
     *                 {@link Zlib#STRATEGY_RLE} or {@link Zlib#STRATEGY_FIXED}
     * @param mode     the {@link Zlib} mode to use. Must be one of {@link Zlib#MODE_ZLIB}, {@link Zlib#MODE_GZIP} or {@link Zlib#MODE_RAW}
     * @return a new {@link ZlibDeflater} with the given level, strategy and mode
     * @throws InvalidCompressionLevelException if the given compression level is invalid
     */
    ZlibDeflater deflater(int level, int strategy, int mode) throws InvalidCompressionLevelException;

    /**
     * @return a new {@link ZlibInflater}
     */
    @Override
    default ZlibInflater inflater() {
        return this.inflater(Zlib.MODE_ZLIB);
    }

    /**
     * @return a new {@link ZlibInflater} using the Gzip format
     */
    default ZlibInflater inflaterGzip() {
        return this.inflater(Zlib.MODE_GZIP);
    }

    /**
     * @return a new {@link ZlibInflater} that will automatically detect whether the compressed data is in Zlib or Gzip format
     */
    default ZlibInflater inflaterAuto() {
        return this.inflater(Zlib.MODE_AUTO);
    }

    /**
     * Creates a new {@link ZlibInflater}.
     *
     * @param mode the {@link Zlib} mode to use. Must be one of {@link Zlib#MODE_ZLIB}, {@link Zlib#MODE_GZIP}, {@link Zlib#MODE_RAW} or {@link Zlib#MODE_AUTO}
     * @return a new {@link ZlibInflater}
     */
    ZlibInflater inflater(int mode);

    /**
     * @return a new {@link ZlibCCtx}
     */
    @Override
    default ZlibCCtx compressionContext() {
        return this.compressionContext(Zlib.LEVEL_BEST, Zlib.STRATEGY_DEFAULT, Zlib.MODE_ZLIB);
    }

    /**
     * @return a new {@link ZlibCCtx} with the given level
     */
    @Override
    default ZlibCCtx compressionContext(int level) throws InvalidCompressionLevelException {
        return this.compressionContext(level, Zlib.STRATEGY_DEFAULT, Zlib.MODE_ZLIB);
    }

    /**
     * @return a new {@link ZlibCCtx} with the given level and strategy
     */
    default ZlibCCtx compressionContext(int level, int strategy) throws InvalidCompressionLevelException {
        return this.compressionContext(level, strategy, Zlib.MODE_ZLIB);
    }

    /**
     * @return a new {@link ZlibCCtx} using the Gzip format
     */
    default ZlibCCtx compressionContextGzip() {
        return this.compressionContext(Zlib.LEVEL_BEST, Zlib.STRATEGY_DEFAULT, Zlib.MODE_GZIP);
    }

    /**
     * @return a new {@link ZlibCCtx} using the Gzip format with the given level
     */
    default ZlibCCtx compressionContextGzip(int level) throws InvalidCompressionLevelException {
        return this.compressionContext(level, Zlib.STRATEGY_DEFAULT, Zlib.MODE_GZIP);
    }

    /**
     * @return a new {@link ZlibCCtx} using the Gzip format with the given level and strategy
     */
    default ZlibCCtx compressionContextGzip(int level, int strategy) throws InvalidCompressionLevelException {
        return this.compressionContext(level, strategy, Zlib.MODE_GZIP);
    }

    /**
     * @return a new {@link ZlibCCtx} with the given level, strategy and mode
     * @see #deflater(int, int, int)
     */
    ZlibCCtx compressionContext(int level, int strategy, int mode);

    /**
     * @return a new {@link ZlibDCtx}
     */
    @Override
    default ZlibDCtx decompressionContext() {
        return this.decompressionContext(Zlib.MODE_ZLIB);
    }

    /**
     * @return a new {@link ZlibDCtx} using the Gzip format
     */
    default ZlibDCtx decompressionContextGzip() {
        return this.decompressionContext(Zlib.MODE_GZIP);
    }

    /**
     * @return a new {@link ZlibDCtx} that will automatically detect whether the compressed data is in Zlib or Gzip format
     */
    default ZlibDCtx decompressionContextAuto() {
        return this.decompressionContext(Zlib.MODE_AUTO);
    }

    /**
     * @return a new {@link ZlibDCtx} with the given mode
     * @see #inflater(int)
     */
    ZlibDCtx decompressionContext(int mode);
}
