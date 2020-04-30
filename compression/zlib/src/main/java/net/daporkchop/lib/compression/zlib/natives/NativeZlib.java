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

package net.daporkchop.lib.compression.zlib.natives;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.ZlibProvider;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.natives.impl.NativeFeature;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Native implementation of {@link ZlibProvider}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class NativeZlib extends NativeFeature<ZlibProvider> implements ZlibProvider {
    //flush parameters
    static final int Z_NO_FLUSH = 0;
    static final int Z_PARTIAL_FLUSH = 1;
    static final int Z_SYNC_FLUSH = 2;
    static final int Z_FULL_FLUSH = 3;
    static final int Z_FINISH = 4;
    static final int Z_BLOCK = 5;
    static final int Z_TREES = 6;

    //status codes
    static final int Z_OK = 0;
    static final int Z_STREAM_END = 1;
    static final int Z_NEED_DICT = 2;
    static final int Z_ERRNO = -1;
    static final int Z_STREAM_ERROR = -2;
    static final int Z_DATA_ERROR = -3;
    static final int Z_MEM_ERROR = -4;
    static final int Z_BUF_ERROR = -5;
    static final int Z_VERSION_ERROR = -6;

    protected static native long compressBound0(long srcSize, int mode);

    protected final ZlibDeflaterOptions deflateOptions = new ZlibDeflaterOptions(this);
    protected final ZlibInflaterOptions inflateOptions = new ZlibInflaterOptions(this);

    @Override
    public long compressBoundLong(long srcSize, @NonNull ZlibMode mode) {
        checkArg(mode.compression(), "Zlib mode %s cannot be usd for compression!", mode);

        return compressBound0(srcSize, mode.ordinal());
    }

    @Override
    public PDeflater deflater(@NonNull ZlibDeflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        return new NativeZlibDeflater(options);
    }

    @Override
    public PInflater inflater(@NonNull ZlibInflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        return new NativeZlibInflater(options);
    }
}
