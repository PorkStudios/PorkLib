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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class JniZstdFunctions extends NativeZstdFunctions {
    static {
        //TODO: load jni library
    }

    /*private static native void configure(boolean allowJniCritical);

    static {
        configure(NativeUtils.allowJniCritical());
    }*/

    static NativeZstdFunctions get() {
        return new JniZstdFunctions();
    }

    @Override
    native boolean ZSTD_isError(long code);

    @Override
    native int ZSTD_getErrorCode(long code);

    @Override
    native String ZSTD_getErrorName(long code);

    @Override
    native long ZSTD_createCCtx();

    @Override
    native void ZSTD_freeCCtx(long cctx);

    @Override
    native long ZSTD_CCtx_refCDict(long cctx, long cdict);

    @Override
    native long ZSTD_CCtx_reset(long cctx, int reset);

    @Override
    native long ZSTD_CCtx_setParameter(long cctx, int param, int value);

    @Override
    native void ZSTD_freeCDict(long cdict);

    @Override
    native int ZSTD_getDictID_fromCDict(long cdict);

    native long ZSTD_createCDict(
            long dictAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining,
            int level);

    @Override
    native long ZSTD_createDCtx();

    @Override
    native void ZSTD_freeDCtx(long dctx);

    @Override
    native long ZSTD_DCtx_refDDict(long dctx, long ddict);

    @Override
    native long ZSTD_DCtx_reset(long dctx, int reset);

    @Override
    native long ZSTD_DCtx_setParameter(long dctx, int param, int value);

    native long ZSTD_createDDict(
            long dictAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining);

    @Override
    native void ZSTD_freeDDict(long ddict);

    @Override
    native int ZSTD_getDictID_fromDDict(long ddict);
}
