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

package net.daporkchop.lib.compression.zstd;

import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.compression.context.PStreamingCompressor;

/**
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface ZstdStreamingCompressor extends PStreamingCompressor, ZstdCompressParameters {
    /**
     * Set the total input size of the next ZSTD frame. The special value {@link -1} means that the input size is unknown.
     * <p>
     * Setting this parameter means that the content size field will be included in the ZSTD frame header.
     * <p>
     * This parameter may only be set while the compressor is not currently compressing a ZSTD frame, i.e. the compressor must be reset or
     * have been flushed with either {@link FlushMode#FULL} or {@link FlushMode#FINISH}.
     * <p>
     * This parameter only applies to the next ZSTD frame, it will be reset when the next frame is completed or the stream (note: NOT parameters!) is reset.
     * <p>
     * The default value is {@link -1}.
     *
     * @param pledgedSrcSize the pledged input data size
     * @throws IllegalArgumentException      if {@code pledgedSrcSize} isn't a non-negative value or the special value {@code -1}
     * @throws IllegalStateException         if a frame compression is currently ongoing (i.e. the compressor hasn't been flushed with either {@link FlushMode#FULL} or {@link FlushMode#FINISH})
     * @throws UnsupportedOperationException if the ZSTD implementation doesn't support this option
     * @see #resetParameters()
     */
    void setPledgedSrcSize(long pledgedSrcSize) throws IllegalArgumentException, IllegalStateException;
}
