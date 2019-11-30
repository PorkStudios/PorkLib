/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.natives.zlib;

/**
 * Base representation of the Zlib algorithm.
 *
 * @author DaPorkchop_
 */
public interface Zlib {
    /**
     * No compression will be applied.
     */
    int ZLIB_LEVEL_NONE = 0;

    /**
     * Poorest compression ratio in exchange for fastest speeds.
     */
    int ZLIB_LEVEL_FASTEST = 1;

    /**
     * Best compression ratio in exchange for lowest speeds.
     */
    int ZLIB_LEVEL_BEST = 9;

    /**
     * Allows the zlib library to choose a default compression level.
     */
    int ZLIB_LEVEL_DEFAULT = -1;

    /**
     * Zlib wrapping mode, uses a 6-byte header with an Adler32 checksum.
     * <p>
     * This is the default mode.
     */
    int ZLIB_MODE_ZLIB = 0;

    /**
     * Gzip wrapping mode, uses an (at least) 18-byte header with a CRC32 checksum.
     */
    int ZLIB_MODE_GZIP = 1;

    /**
     * Only for use by {@link PInflater}, automatically detects whether the input data is in the Zlib or Gzip format.
     * <p>
     * Note that this doesn't work for data encoded in the {@link #ZLIB_MODE_RAW} format.
     */
    int ZLIB_MODE_AUTO = 2;

    /**
     * "Raw" Deflate mode, no wrapping is applied to the compressed data.
     */
    int ZLIB_MODE_RAW = 3;

    /**
     * Creates a new {@link PDeflater}.
     * <p>
     * The returned {@link PDeflater} will use {@link #ZLIB_MODE_ZLIB}.
     *
     * @param level the Deflate compression level to use. Must be in range 0-9 (inclusive), or {@link #ZLIB_LEVEL_DEFAULT} to use the library default
     * @return a new {@link PDeflater} instance
     * @see #deflater(int, int)
     */
    default PDeflater deflater(int level) {
        return this.deflater(level, ZLIB_MODE_ZLIB);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param level the Deflate compression level to use. Must be in range 0-9 (inclusive), or {@link #ZLIB_LEVEL_DEFAULT} to use the library default
     * @param mode  the zlib wrapping mode to use. Must be one of {@link #ZLIB_MODE_ZLIB}, {@link #ZLIB_MODE_RAW} or {@link #ZLIB_MODE_GZIP}
     * @return a new {@link PDeflater} instance
     */
    PDeflater deflater(int level, int mode);

    /**
     * Creates a new {@link PInflater}.
     * <p>
     * The returned {@link PInflater} will use {@link #ZLIB_MODE_ZLIB}.
     *
     * @return a new {@link PInflater} instance
     * @see #inflater(int)
     */
    default PInflater inflater() {
        return this.inflater(ZLIB_MODE_ZLIB);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param mode the zlib wrapping mode to use. Must be one of {@link #ZLIB_MODE_ZLIB}, {@link #ZLIB_MODE_GZIP}, {@link #ZLIB_MODE_AUTO} or {@link #ZLIB_MODE_RAW}
     * @return a new {@link PInflater} instance
     */
    PInflater inflater(int mode);
}
