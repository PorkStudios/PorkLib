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

import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdOneshotProvider;
import net.daporkchop.lib.natives.NativeException;

/**
 * @author DaPorkchop_
 */
abstract class NativeZstdProvider implements ZstdOneshotProvider {
    static final int ZSTD_error_no_error = 0;
    static final int ZSTD_error_GENERIC = 1;
    static final int ZSTD_error_prefix_unknown = 10;
    static final int ZSTD_error_version_unsupported = 12;
    static final int ZSTD_error_frameParameter_unsupported = 14;
    static final int ZSTD_error_frameParameter_windowTooLarge = 16;
    static final int ZSTD_error_corruption_detected = 20;
    static final int ZSTD_error_checksum_wrong = 22;
    static final int ZSTD_error_dictionary_corrupted = 30;
    static final int ZSTD_error_dictionary_wrong = 32;
    static final int ZSTD_error_dictionaryCreation_failed = 34;
    static final int ZSTD_error_parameter_unsupported = 40;
    static final int ZSTD_error_parameter_outOfBound = 42;
    static final int ZSTD_error_tableLog_tooLarge = 44;
    static final int ZSTD_error_maxSymbolValue_tooLarge = 46;
    static final int ZSTD_error_maxSymbolValue_tooSmall = 48;
    static final int ZSTD_error_stage_wrong = 60;
    static final int ZSTD_error_init_missing = 62;
    static final int ZSTD_error_memory_allocation = 64;
    static final int ZSTD_error_workSpace_tooSmall = 66;
    static final int ZSTD_error_dstSize_tooSmall = 70;
    static final int ZSTD_error_srcSize_wrong = 72;
    static final int ZSTD_error_dstBuffer_null = 74;

    static final int ZSTD_c_compressionLevel = 100;
    static final int ZSTD_c_contentSizeFlag = 200;
    static final int ZSTD_c_checksumFlag = 201;
    static final int ZSTD_c_dictIDFlag = 202;

    static final int ZSTD_reset_session_only = 1;
    static final int ZSTD_reset_parameters = 2;
    static final int ZSTD_reset_session_and_parameters = 3;

    /**
     * Checks if the given value indicates an error.
     *
     * @param code the 64-bit return value from a zstd function
     * @return {@code true} iff the given value is an error
     */
    abstract boolean ZSTD_isError(long code);

    /**
     * Gets an error code from the given value.
     *
     * @param code the 64-bit return value from a zstd function
     * @return an error code, likely one of the {@code ZSTD_error_*} constants declared in {@link NativeZstdProvider}
     */
    abstract int ZSTD_getErrorCode(long code);

    /**
     * Gets a string describing the given value, assuming it {@link #ZSTD_isError(long) is an error}.
     *
     * @param code the 64-bit return value from a zstd function
     * @return a string describing the error
     */
    abstract String ZSTD_getErrorName(long code);

    final long checkForErrorAndThrow(long code) throws NativeException {
        if (this.ZSTD_isError(code)) {
            throw new NativeException(code);
        }
        return code;
    }

    /**
     * Creates a new compression context.
     *
     * @return a pointer to a new compression context
     */
    abstract long ZSTD_createCCtx();

    /**
     * Deletes the given compression context.
     *
     * @param cctx the compression context
     */
    abstract void ZSTD_freeCCtx(long cctx);

    /**
     * Attaches a dictionary to a compression context.
     *
     * @param cctx  the compression context
     * @param cdict the compression dictionary (may be {@code null})
     * @return zero, or an error (should be checked using {@link #ZSTD_isError(long)})
     */
    abstract long ZSTD_CCtx_refCDict(long cctx, long cdict);

    /**
     * Attaches a dictionary to a compression context.
     *
     * @param cctx  the compression context
     * @param reset the reset directive, should be one of the {@code ZSTD_reset_*} constants declared in {@link NativeZstdProvider}
     * @return an undefined value, which may be an error (should be checked using {@link #ZSTD_isError(long)})
     */
    abstract long ZSTD_CCtx_reset(long cctx, int reset);

    /**
     * Sets a parameter for a compression context.
     *
     * @param cctx  the compression context
     * @param param the parameter name, should be one of the {@code ZSTD_c_*} constants declared in {@link NativeZstdProvider}
     * @param value the parameter value
     * @return an undefined value, which may be an error (should be checked using {@link #ZSTD_isError(long)})
     */
    abstract long ZSTD_CCtx_setParameter(long cctx, int param, int value);

    /**
     * Deletes the given compression dictionary.
     *
     * @param cdict the compression dictionary
     */
    abstract void ZSTD_freeCDict(long cdict);

    /**
     * Gets the dictionary ID from the given compression dictionary.
     *
     * @param cdict the compression dictionary
     * @return the dictionary ID (positive), or {@code 0} if the dictionary is empty or content-only
     */
    abstract int ZSTD_getDictID_fromCDict(long cdict);

    /**
     * Creates a new decompression context.
     *
     * @return a pointer to a new decompression context
     */
    abstract long ZSTD_createDCtx();

    /**
     * Deletes the given decompression context.
     *
     * @param dctx the decompression context
     */
    abstract void ZSTD_freeDCtx(long dctx);

    /**
     * Attaches a dictionary to a decompression context.
     *
     * @param dctx  the decompression context
     * @param ddict the decompression dictionary (may be {@code null})
     * @return zero, or an error (should be checked using {@link #ZSTD_isError(long)})
     */
    abstract long ZSTD_DCtx_refDDict(long dctx, long ddict);

    /**
     * Attaches a dictionary to a decompression context.
     *
     * @param dctx  the decompression context
     * @param reset the reset directive, should be one of the {@code ZSTD_reset_*} constants declared in {@link NativeZstdProvider}
     * @return an undefined value, which may be an error (should be checked using {@link #ZSTD_isError(long)})
     */
    abstract long ZSTD_DCtx_reset(long dctx, int reset);

    /**
     * Sets a parameter for a decompression context.
     *
     * @param dctx  the decompression context
     * @param param the parameter name, should be one of the {@code ZSTD_d_*} constants declared in {@link NativeZstdProvider}
     * @param value the parameter value
     * @return an undefined value, which may be an error (should be checked using {@link #ZSTD_isError(long)})
     */
    abstract long ZSTD_DCtx_setParameter(long dctx, int param, int value);

    /**
     * Deletes the given decompression dictionary.
     *
     * @param ddict the decompression dictionary
     */
    abstract void ZSTD_freeDDict(long ddict);

    /**
     * Gets the dictionary ID from the given decompression dictionary.
     *
     * @param ddict the decompression dictionary
     * @return the dictionary ID (positive), or {@code 0} if the dictionary is empty or content-only
     */
    abstract int ZSTD_getDictID_fromDDict(long ddict);

    @Override
    public final boolean isDictionarySupported() {
        return true;
    }

    @Override
    public final @Positive int minLevel() {
        //TODO: use ZSTD_minCLevel()
        return Zstd.LEVEL_MIN;
    }

    @Override
    public final @Positive int maxLevel() {
        //TODO: use ZSTD_maxCLevel()
        return Zstd.LEVEL_MAX;
    }
}
