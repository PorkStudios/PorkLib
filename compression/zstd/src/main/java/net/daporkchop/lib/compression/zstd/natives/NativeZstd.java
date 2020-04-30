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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCDict;
import net.daporkchop.lib.compression.zstd.ZstdDDict;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.ZstdProvider;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.natives.impl.NativeFeature;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class NativeZstd extends NativeFeature<ZstdProvider> implements ZstdProvider {
    protected final ZstdDeflaterOptions deflateOptions = new ZstdDeflaterOptions(this);
    protected final ZstdInflaterOptions inflateOptions = new ZstdInflaterOptions(this);

    @Override
    public long frameContentSizeLong(@NonNull ByteBuf src) throws ContentSizeUnknownException {
        throw new UnsupportedOperationException();
    }

    private native long doFrameContentSizeLong(long srcAddr, int srcSize);

    @Override
    public long compressBoundLong(long srcSize) {
        return this.doCompressBoundLong(notNegative(srcSize, "srcSize"));
    }

    private native long doCompressBoundLong(long srcSize);

    @Override
    public ZstdDeflater deflater(@NonNull ZstdDeflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        throw new UnsupportedOperationException();
    }

    @Override
    public ZstdInflater inflater(@NonNull ZstdInflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        throw new UnsupportedOperationException();
    }

    @Override
    public ZstdCDict loadDeflateDictionary(@NonNull ByteBuf dict, int level) {
        return new NativeZstdCDict(this, dict, Zstd.checkLevel(level));
    }

    @Override
    public ZstdDDict loadInflateDictionary(@NonNull ByteBuf dict) {
        return new NativeZstdDDict(this, dict);
    }
}
