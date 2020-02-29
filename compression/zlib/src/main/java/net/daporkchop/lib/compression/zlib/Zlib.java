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

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.compression.zlib.java.JavaZlib;
import net.daporkchop.lib.compression.zlib.natives.NativeZlib;
import net.daporkchop.lib.natives.FeatureBuilder;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class Zlib {
    public final ZlibProvider PROVIDER = FeatureBuilder.<ZlibProvider>create(Zlib.class)
            .addNative("net.daporkchop.lib.compression.zlib.natives.NativeZlib", "zlib")
            .addJava("net.daporkchop.lib.compression.zlib.java.JavaZlib")
            .build();

    public final int LEVEL_NONE    = 0; //no compression at all
    public final int LEVEL_FASTEST = 1; //fastest compression, worst ratio
    public final int LEVEL_BEST    = 9; //best ratio, slowest compression
    public final int LEVEL_DEFAULT = -1; //uses the library default level

    public final int STRATEGY_DEFAULT  = 0;
    public final int STRATEGY_FILTERED = 1;
    public final int STRATEGY_HUFFMAN  = 2;
    public final int STRATEGY_RLE      = 3; //only supported by native impl
    public final int STRATEGY_FIXED    = 4; //only supported by native impl

    public final int MODE_ZLIB = 0; //DEFLATE with zlib headers
    public final int MODE_GZIP = 1; //DEFLATE with gzip headers
    public final int MODE_RAW  = 2; //raw DEFLATE output
    public final int MODE_AUTO = 3; //automatically detects zlib or gzip (inflater only)
}
