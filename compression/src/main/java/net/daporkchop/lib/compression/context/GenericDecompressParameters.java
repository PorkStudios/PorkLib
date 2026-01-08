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

/**
 * @author DaPorkchop_
 */
public interface GenericDecompressParameters extends IContext {
    /**
     * Defines the different modes of operation when decompressing data in compression formats consisting of one or more streams which know their own size.
     * <p>
     * Some compression formats consist of one or more separate sized streams, which may be concatenated freely. For example:
     * <ul>
     *     <li>standard ZSTD ("frames")</li>
     *     <li>GZIP ("members")</li>
     *     <li>the {@code .xz} format ("streams")</li>
     *     <li>bzip2 ("streams")</li>
     * </ul>
     * <p>
     * Some compression formats consist of a single sized stream, and do not consider multiple concatenated streams to be part of the same stream. For example:
     * <ul>
     *     <li>raw DEFLATE</li>
     *     <li>ZLIB</li>
     *     <li>raw ZSTD</li>
     *     <li>the legacy {@code .lzma} format</li>
     * </ul>
     * <p>
     * The default value is {@code false}.
     *
     * @param singleStream If {@code true}, the decompressor will stop after fully decompressing exactly one frame from the input stream.
     *                     If {@code false}, the entire input stream will be consumed. Compression formats allowing concatenation will try to decompress everything,
     *                     while formats which do not allow concatenation will throw an exception if there is any data remaining after the first stream.
     * @see #resetParameters()
     */
    void setSingleStream(boolean singleStream);
}
