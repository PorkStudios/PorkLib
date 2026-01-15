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

package net.daporkchop.lib.compression.zstd.natives;

import lombok.NonNull;
import lombok.val;
import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.generic.AbstractStreamingCompressor;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotCompressor;
import net.daporkchop.lib.compression.zstd.ZstdStreamingCompressor;
import net.daporkchop.lib.natives.NativeException;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstdFunctions.*;

/**
 * @author DaPorkchop_
 */
abstract class NativeZstdCCtx extends AbstractStreamingCompressor implements ZstdOneshotCompressor, ZstdStreamingCompressor, NativeZstdContext {
    static final byte STATE_COMPRESS = STATE_RESET + 1;

    final NativeZstdFunctions functions;
    NativeZstdObject cctx;

    private long remainingBytesToFlush;

    //parameters
    private NativeZstdCDict dictionary;

    NativeZstdCCtx(@NonNull NativeZstdFunctions functions) {
        this.functions = functions;
        this.cctx = NativeZstdObject.createCCtx(functions);
    }

    @Override
    public final void close() {
        if (this.cctx != null) {
            this.cctx.close();
            this.cctx = null;
        }
    }

    @Override
    public final void resetStream() {
        super.resetStream();

        this.functions.ZSTD_CCtx_reset(this.cctx.addr(), ZSTD_reset_session_only); //resetting session never fails
        this.remainingBytesToFlush = 0;
    }

    @Override
    public final void resetParameters() throws IllegalStateException {
        super.resetParameters();

        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_reset(this.cctx.addr(), ZSTD_reset_parameters));
        this.dictionary = null;
    }

    @Override
    public final void resetStreamAndParameters() {
        super.resetStream();
        super.resetParameters();

        this.functions.ZSTD_CCtx_reset(this.cctx.addr(), ZSTD_reset_session_and_parameters); //resetting session and parameters never fails
        this.remainingBytesToFlush = 0;
        this.dictionary = null;
    }

    @Override
    public final void setLevel(int level) throws IllegalArgumentException, IllegalStateException {
        this.ensureStreamInactive();
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_setParameter(this.cctx.addr(), ZSTD_c_compressionLevel, Zstd.checkLevel(level)));
    }

    @Override
    public final void setDictionary(@ExtendedBorrow ZstdCompressDictionary dictionary) throws IllegalArgumentException, IllegalStateException {
        this.ensureStreamInactive();
        checkArg(dictionary == null || dictionary instanceof NativeZstdCDict, dictionary);
        this.dictionary = (NativeZstdCDict) dictionary;
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_refCDict(this.cctx.addr(), dictionary != null ? this.dictionary.dict : 0L));
    }

    @Override
    public final void setChecksumFlag(boolean checksumFlag) throws IllegalStateException {
        this.ensureStreamInactive();
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_setParameter(this.cctx.addr(), ZSTD_c_checksumFlag, checksumFlag ? 1 : 0));
    }

    @Override
    public final void setContentSizeFlag(boolean contentSizeFlag) throws IllegalStateException {
        this.ensureStreamInactive();
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_setParameter(this.cctx.addr(), ZSTD_c_contentSizeFlag, contentSizeFlag ? 1 : 0));
    }

    @Override
    public final void setDictIdFlag(boolean dictIdFlag) throws IllegalStateException {
        this.ensureStreamInactive();
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_setParameter(this.cctx.addr(), ZSTD_c_dictIDFlag, dictIdFlag ? 1 : 0));
    }

    @Override
    public final void setPledgedSrcSize(long pledgedSrcSize) throws IllegalArgumentException, IllegalStateException {
        this.ensureStreamInactive();
        checkArg(pledgedSrcSize == -1 || pledgedSrcSize >= 0, pledgedSrcSize);
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_CCtx_setPledgedSrcSize(this.cctx.addr(), pledgedSrcSize == -1 ? ZSTD_CONTENTSIZE_UNKNOWN : pledgedSrcSize));
    }

    @Override
    public final @NotNegative long getRequestedOutputBytes() {
        return this.remainingBytesToFlush;
    }

    @Override
    public final boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException {
        this.setLastReadWrittenBytes(0, 0);

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        this.validateFlushParameter(flush);

        if (this.state == STATE_RESET) {
            this.state = STATE_COMPRESS;
        }

        final int endOp;
        switch (flush) {
            case NO:
                endOp = ZSTD_e_continue;
                break;
            case SYNC:
                endOp = ZSTD_e_flush;
                break;
            case FULL:
            case FINISH:
                endOp = ZSTD_e_end;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(flush));
        }

        val initialSrcPosition = src.position();
        val initialDstPosition = dst.position();

        long result = this.ZSTD_compressStream2(src, dst, endOp);

        this.setLastReadWrittenBytes(src.position() - initialSrcPosition, dst.position() - initialDstPosition);

        if (this.functions.ZSTD_isError(result)) {
            throw new NativeException(this.functions.ZSTD_getErrorName(result));
        }

        this.remainingBytesToFlush = result;
        if (result != 0) {
            //wait for more output space
            return false;
        } else {
            switch (endOp) {
                case ZSTD_e_continue:
                case ZSTD_e_flush:
                    //no more output space is required
                    this.handlePartialFlushComplete();
                    return true;
                case ZSTD_e_end:
                    //compression is done, we can reset the stream now :)
                    this.resetStream();
                    return true;
                default:
                    throw new IllegalArgumentException(String.valueOf(endOp));
            }
        }
    }

    protected abstract long ZSTD_compressStream2(ByteBuffer src, ByteBuffer dst, int endOp);
}
