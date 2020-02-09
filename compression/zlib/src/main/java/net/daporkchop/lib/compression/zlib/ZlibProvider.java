/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.compression.zlib;

import net.daporkchop.lib.compression.PDeflater;

/**
 * A wrapper around a Zlib implementation.
 *
 * @author DaPorkchop_
 */
public interface ZlibProvider {
    /**
     * Creates a new {@link PDeflater}.
     *
     * @return a new {@link PDeflater}
     */
    default PDeflater deflater() {
        return this.deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT, Zlib.MODE_ZLIB);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param level the {@link Zlib} level to use
     * @return a new {@link PDeflater} with the given level
     */
    default PDeflater deflater(int level) {
        return this.deflater(level, Zlib.STRATEGY_DEFAULT, Zlib.MODE_ZLIB);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param level    the {@link Zlib} level to use
     * @param strategy the {@link Zlib} strategy to use
     * @return a new {@link PDeflater} with the given level and strategy
     */
    default PDeflater deflater(int level, int strategy) {
        return this.deflater(level, strategy, Zlib.MODE_ZLIB);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @return a new {@link PDeflater}
     */
    default PDeflater deflaterGzip() {
        return this.deflater(Zlib.LEVEL_DEFAULT, Zlib.STRATEGY_DEFAULT, Zlib.MODE_GZIP);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param level the {@link Zlib} level to use
     * @return a new {@link PDeflater} with the given level
     */
    default PDeflater deflaterGzip(int level) {
        return this.deflater(level, Zlib.STRATEGY_DEFAULT, Zlib.MODE_GZIP);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param level    the {@link Zlib} level to use
     * @param strategy the {@link Zlib} strategy to use
     * @return a new {@link PDeflater} with the given level and strategy
     */
    default PDeflater deflaterGzip(int level, int strategy) {
        return this.deflater(level, strategy, Zlib.MODE_GZIP);
    }

    /**
     * Creates a new {@link PDeflater}.
     *
     * @param level    the {@link Zlib} level to use. Must be in range {@link Zlib#LEVEL_NONE} to {@link Zlib#LEVEL_BEST} (inclusive), or {@link Zlib#LEVEL_DEFAULT}
     * @param strategy the {@link Zlib} strategy to use. Must be one of {@link Zlib#STRATEGY_DEFAULT}, {@link Zlib#STRATEGY_FILTERED}, {@link Zlib#STRATEGY_HUFFMAN},
     *                 {@link Zlib#STRATEGY_RLE} or {@link Zlib#STRATEGY_FIXED}
     * @param mode     the {@link Zlib} mode to use. Must be one of {@link Zlib#MODE_ZLIB}, {@link Zlib#MODE_GZIP} or {@link Zlib#MODE_RAW}
     * @return a new {@link PDeflater} with the given level and strategy
     */
    PDeflater deflater(int level, int strategy, int mode);
}
