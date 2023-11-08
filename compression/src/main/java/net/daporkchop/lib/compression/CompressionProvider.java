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

package net.daporkchop.lib.compression;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.option.DeflaterOptions;
import net.daporkchop.lib.compression.option.InflaterOptions;

/**
 * An implementation of a compression algorithm.
 * <p>
 * This interface is not intended to be referenced directly by user code, rather, implementations should define child interfaces which set the option
 * generic parameters.
 *
 * @author DaPorkchop_
 */
public interface CompressionProvider<I extends CompressionProvider<I, DO, IO>, DO extends DeflaterOptions<DO, I>, IO extends InflaterOptions<IO, I>> {
    //
    //
    // info methods
    //
    //

    /**
     * @return the compression level with the worst compression ratio in exchange for the shortest compression times
     */
    int levelFast();

    /**
     * @return the compression level used by default
     */
    int levelDefault();

    /**
     * @return the compression level with the best compression ratio in exchange for the longest compression times
     */
    int levelBest();

    /**
     * @see #compressBoundLong(long)
     */
    default int compressBound(int srcSize) {
        return PValidation.toInt(this.compressBoundLong(srcSize));
    }

    /**
     * Gets the maximum (worst-case) compressed size for input data of the given length.
     *
     * @param srcSize the size (in bytes) of the source data
     * @return the worst-case size of the compressed data
     */
    long compressBoundLong(long srcSize);

    /**
     * @return the default {@link DeflaterOptions} (used by {@link #deflater()})
     */
    DO deflateOptions();

    /**
     * @return the default {@link InflaterOptions} (used by {@link #inflater()})
     */
    IO inflateOptions();

    //
    //
    // context creation methods
    //
    //

    /**
     * Creates a new {@link PDeflater} using the default options.
     *
     * @see #deflater(DeflaterOptions)
     */
    default PDeflater deflater() {
        return this.deflater(this.deflateOptions());
    }

    /**
     * Creates a new {@link PDeflater} with the given options.
     *
     * @param options the options to use
     * @return a new {@link PDeflater} with the given options
     */
    PDeflater deflater(@NonNull DO options);

    /**
     * Creates a new {@link PInflater} using the default options.
     *
     * @see #inflater(InflaterOptions)
     */
    default PInflater inflater() {
        return this.inflater(this.inflateOptions());
    }

    /**
     * Creates a new {@link PInflater} with the given options.
     *
     * @param options the options to use
     * @return a new {@link PInflater} with the given options
     */
    PInflater inflater(@NonNull IO options);
}
