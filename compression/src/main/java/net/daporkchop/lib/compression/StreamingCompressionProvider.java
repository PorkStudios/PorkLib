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

import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.exception.ValueCannotFitException;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.natives.util.BufferTyped;

/**
 * An implementation of a compression algorithm that supports streaming compression via {@link PDeflater} and {@link PInflater}.
 *
 * @author DaPorkchop_
 */
public interface StreamingCompressionProvider extends CompressionProvider {
    /**
     * Creates a new {@link PDeflater} with the default compression level.
     *
     * @see #deflater(int)
     */
    default PDeflater deflater() {
        return this.deflater(this.levelDefault());
    }

    /**
     * Creates a new {@link PDeflater} with the given compression level.
     *
     * @param level the compression level to use
     * @return a new {@link PDeflater} with the given compression level
     * @throws InvalidCompressionLevelException if the given compression level is invalid
     */
    PDeflater deflater(int level) throws InvalidCompressionLevelException;

    /**
     * @return a new {@link PInflater}
     */
    PInflater inflater();
}
