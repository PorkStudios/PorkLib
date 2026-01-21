/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
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

package net.daporkchop.lib.compression.context;

import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.util.OptionalInt;

/**
 * Base interface for {@link PStreamingCompressor} and {@link PStreamingDecompressor}.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface StreamingContext extends IContext {
    /**
     * Returns a hint for the recommended input buffer size. The return value is generally a constant, independent of the current context state.
     *
     * @return a hint for the recommended input buffer size, or an empty optional if the implementation either doesn't know or doesn't care
     */
    default @Positive OptionalInt getRecommendedInputBufferSize() {
        //don't care
        return OptionalInt.empty();
    }

    /**
     * Returns a hint for the recommended output buffer size. The return value is generally a constant, independent of the current context state.
     *
     * @return a hint for the recommended output buffer size, or an empty optional if the implementation either doesn't know or doesn't care
     */
    default @Positive OptionalInt getRecommendedOutputBufferSize() {
        //don't care
        return OptionalInt.empty();
    }

    /**
     * @return the number of input bytes which were consumed by the last call to {@link PStreamingCompressor#compress}/{@link PStreamingDecompressor#decompress}
     */
    @NotNegative long getLastReadBytes();

    /**
     * @return the number of output bytes which were written by the last call to {@link PStreamingCompressor#compress}/{@link PStreamingDecompressor#decompress}
     */
    @NotNegative long getLastWrittenBytes();

    /**
     * Resets this context's state.
     * <p>
     * This will cause the ongoing [de]compression session (if any) to be aborted. Call this method before starting to [de]compress new data.
     */
    void resetStream();

    /**
     * Equivalent to calling {@link #resetStream()} followed by {@link #resetParameters()}.
     */
    default void resetStreamAndParameters() {
        this.resetStream();
        this.resetParameters();
    }
}
