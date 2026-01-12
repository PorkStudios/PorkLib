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

package net.daporkchop.lib.compression.deflate.libdeflate;

/**
 * @author DaPorkchop_
 */
abstract class NativeLibdeflateFunctions {
    static final byte MODE_DEFLATE = 1;
    static final byte MODE_ZLIB = 2;
    static final byte MODE_GZIP = 3;

    /* Decompression was successful.  */
    static final int LIBDEFLATE_SUCCESS = 0;

    /* Decompression failed because the compressed data was invalid,
     * corrupt, or otherwise unsupported.  */
    static final int LIBDEFLATE_BAD_DATA = 1;

    /* A NULL 'actual_out_nbytes_ret' was provided, but the data would have
     * decompressed to fewer than 'out_nbytes_avail' bytes.  */
    static final int LIBDEFLATE_SHORT_OUTPUT = 2;

    /* The data would have decompressed to more than 'out_nbytes_avail'
     * bytes.  */
    static final int LIBDEFLATE_INSUFFICIENT_SPACE = 3;

    /**
     * Creates a new compressor.
     *
     * @param compression_level the compression level to use
     * @return a pointer to a new compressor, or {@code 0} if out-of-memory or the compression level is invalid
     */
    abstract long libdeflate_alloc_compressor(int compression_level);

    /**
     * Deletes the given compressor.
     *
     * @param compressor a pointer to the compressor
     */
    abstract void libdeflate_free_compressor(long compressor);

    /**
     * Computes a worst-case upper bound on the compressed size of an input of the given size.
     *
     * @param compressor a pointer to the compresssor, or {@code 0} if the upper bound should be computed for ANY possible libdeflate compressor
     * @param in_nbytes  the input size, in bytes
     * @return the compressed upper bound. If less than the input size, an overflow has occurred.
     */
    abstract long libdeflate_deflate_compress_bound(long compressor, long in_nbytes);

    /**
     * Computes a worst-case upper bound on the compressed size of an input of the given size.
     *
     * @param compressor a pointer to the compresssor, or {@code 0} if the upper bound should be computed for ANY possible libdeflate compressor
     * @param in_nbytes  the input size, in bytes
     * @return the compressed upper bound. If less than the input size, an overflow has occurred.
     */
    abstract long libdeflate_zlib_compress_bound(long compressor, long in_nbytes);

    /**
     * Computes a worst-case upper bound on the compressed size of an input of the given size.
     *
     * @param compressor a pointer to the compresssor, or {@code 0} if the upper bound should be computed for ANY possible libdeflate compressor
     * @param in_nbytes  the input size, in bytes
     * @return the compressed upper bound. If less than the input size, an overflow has occurred.
     */
    abstract long libdeflate_gzip_compress_bound(long compressor, long in_nbytes);

    /**
     * Creates a new decompressor.
     *
     * @return a pointer to a new decompressor, or {@code 0} if out-of-memory
     */
    abstract long libdeflate_alloc_decompressor();

    /**
     * Deletes the given decompressor.
     *
     * @param decompressor a pointer to the decompressor
     */
    abstract void libdeflate_free_decompressor(long decompressor);
}
