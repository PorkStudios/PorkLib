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

package net.daporkchop.lib.compression.zlib.java;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.ZlibProvider;
import net.daporkchop.lib.compression.zlib.ZlibStrategy;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class JavaZlib implements ZlibProvider {
    protected final ZlibDeflaterOptions deflateOptions = new ZlibDeflaterOptions(this);
    protected final ZlibInflaterOptions inflateOptions = new ZlibInflaterOptions(this);

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public long compressBoundLong(long srcSize, @NonNull ZlibMode mode) {
        //extracted from deflate.c, i'm assuming that the java implementation has the same limits
        PValidation.notNegative(srcSize);
        long conservativeUpperBound = srcSize + ((srcSize + 7L) >> 3L) + ((srcSize + 63L) >> 6L) + 5L;
        switch (mode) {
            case ZLIB:
                return conservativeUpperBound + 6L + 4L; //additional +4 in case `strstart`? whatever that means
            case GZIP:
                return conservativeUpperBound + 18L; //assume there is no gzip message
            case RAW:
                return conservativeUpperBound;
            default:
                throw new IllegalArgumentException("Invalid Zlib compression mode: " + mode);
        }
    }

    @Override
    public PDeflater deflater(@NonNull ZlibDeflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        checkArg(options.strategy() == ZlibStrategy.DEFAULT || options.strategy() == ZlibStrategy.FILTERED || options.strategy() == ZlibStrategy.HUFFMAN, "Java Zlib does not support Zlib strategy %s!", options.strategy());
        return new JavaZlibDeflater(options);
    }

    @Override
    public PInflater inflater(@NonNull ZlibInflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        checkArg(options.mode() != ZlibMode.AUTO, "Java Zlib does not support Zlib mode %s!", options.mode());
        return new JavaZlibInflater(options);
    }
}
