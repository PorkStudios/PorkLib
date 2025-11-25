/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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

package net.daporkchop.lib.compression.zstd.natives;

import lombok.NonNull;
import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotCompressor;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstdFactory.*;

/**
 * @author DaPorkchop_
 */
abstract class NativeZstdCCtx extends AbstractNativeZstdContext implements ZstdOneshotCompressor {
    private int level = Zstd.LEVEL_DEFAULT;
    private NativeZstdCDict dictionary;

    NativeZstdCCtx(@NonNull NativeZstdFactory factory) {
        super(factory, factory.ZSTD_createCCtx());
    }

    @Override
    final Runnable freeCtxRunnable(@NonNull NativeZstdFactory factory, long ctx) {
        return () -> factory.ZSTD_freeCCtx(ctx);
    }

    @Override
    public final void resetParameters() { //TODO: merge exception rules when we also implement streaming
        this.factory.checkForErrorAndThrow(this.factory.ZSTD_CCtx_reset(this.ctx, ZSTD_reset_parameters));
        this.level = Zstd.LEVEL_DEFAULT;
        this.dictionary = null;
    }

    @Override
    public final void setLevel(int level) throws IllegalArgumentException {
        this.level = Zstd.checkLevel(level);
        this.factory.checkForErrorAndThrow(this.factory.ZSTD_CCtx_setParameter(this.ctx, ZSTD_c_compressionLevel, level));
    }

    @Override
    public final void setDictionary(@ExtendedBorrow ZstdCompressDictionary dictionary) throws IllegalArgumentException {
        checkArg(dictionary == null || dictionary instanceof NativeZstdCDict, dictionary);
        this.dictionary = (NativeZstdCDict) dictionary;
        this.factory.checkForErrorAndThrow(this.factory.ZSTD_CCtx_refCDict(this.ctx, dictionary != null ? this.dictionary.dict : 0L));
    }

    @Override
    public final void setChecksumFlag(boolean checksumFlag) throws IllegalArgumentException {
        this.factory.checkForErrorAndThrow(this.factory.ZSTD_CCtx_setParameter(this.ctx, ZSTD_c_checksumFlag, checksumFlag ? 1 : 0));
    }

    @Override
    public final void setContentSizeFlag(boolean contentSizeFlag) throws IllegalArgumentException {
        this.factory.checkForErrorAndThrow(this.factory.ZSTD_CCtx_setParameter(this.ctx, ZSTD_c_contentSizeFlag, contentSizeFlag ? 1 : 0));
    }

    @Override
    public final void setDictIdFlag(boolean dictIdFlag) throws IllegalArgumentException {
        this.factory.checkForErrorAndThrow(this.factory.ZSTD_CCtx_setParameter(this.ctx, ZSTD_c_dictIDFlag, dictIdFlag ? 1 : 0));
    }
}
