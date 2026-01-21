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
import net.daporkchop.lib.compression.generic.AbstractStreamingDecompressor;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotDecompressor;
import net.daporkchop.lib.compression.zstd.ZstdStreamingDecompressor;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstdFunctions.*;

/**
 * @author DaPorkchop_
 */
abstract class NativeZstdDCtx extends AbstractStreamingDecompressor implements ZstdOneshotDecompressor, ZstdStreamingDecompressor, NativeZstdContext {
    static final byte STATE_AWAIT_FRAME = STATE_RESET + 1;
    static final byte STATE_DECOMPRESS = STATE_RESET + 2;

    final NativeZstdFunctions functions;
    NativeZstdObject dctx;

    //parameters
    private NativeZstdDDict dictionary;

    NativeZstdDCtx(@NonNull NativeZstdFunctions functions) {
        this.functions = functions;
        this.dctx = NativeZstdObject.createDCtx(functions);
    }

    @Override
    public final void close() {
        if (this.dctx != null) {
            this.dctx.close();
            this.dctx = null;
        }
    }

    @Override
    public final void resetStream() {
        super.resetStream();

        this.functions.ZSTD_DCtx_reset(this.dctx.addr(), ZSTD_reset_session_only); //resetting session never fails
    }

    @Override
    public final void resetParameters() throws IllegalStateException {
        super.resetParameters();

        this.functions.checkForErrorAndThrow(this.functions.ZSTD_DCtx_reset(this.dctx.addr(), ZSTD_reset_parameters));
        this.dictionary = null;
    }

    @Override
    public final void resetStreamAndParameters() {
        super.resetStream();
        super.resetParameters();

        this.functions.ZSTD_DCtx_reset(this.dctx.addr(), ZSTD_reset_session_and_parameters); //resetting session never fails
        this.dictionary = null;
    }

    @Override
    public final void setDictionary(@ExtendedBorrow ZstdDecompressDictionary dictionary) throws IllegalArgumentException, IllegalStateException {
        this.ensureStreamInactive();
        checkArg(dictionary == null || dictionary instanceof NativeZstdDDict, dictionary);
        this.dictionary = (NativeZstdDDict) dictionary;
        this.functions.checkForErrorAndThrow(this.functions.ZSTD_DCtx_refDDict(this.dctx.addr(), dictionary != null ? this.dictionary.dict : 0L));
    }

    @Override
    public final boolean decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, boolean eof) throws DataFormatException, ReadOnlyBufferException {
        this.setLastReadWrittenBytes(0, 0);

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        if (this.state == STATE_RESET) {
            this.state = STATE_AWAIT_FRAME;
        }

        this.validateEofParameter(eof);

        while (true) {
            if (this.state == STATE_AWAIT_FRAME) {
                if (!src.hasRemaining()) {
                    if (eof) {
                        if (this.singleFrame) {
                            //we've reached the end of the input stream and there's no input data remaining, but not a single ZSTD frame has been decompressed so we're going to abort
                            throw new DataFormatException("empty input stream is not allowed in single frame mode");
                        } else {
                            //we've reached the end of the input stream and there's no input data remaining, so we're finished here :)
                            this.resetStream();
                            return true;
                        }
                    } else {
                        //wait for more input
                        return false;
                    }
                } else {
                    //there is some input available, begin decompressing the next frame
                    this.state = STATE_DECOMPRESS;
                }
            }

            if (this.state == STATE_DECOMPRESS) {
                val initialSrcPosition = src.position();
                val initialDstPosition = dst.position();

                long result = this.ZSTD_decompressStream(src, dst);
                this.addLastReadWrittenBytes(src.position() - initialSrcPosition, dst.position() - initialDstPosition);

                if (this.functions.ZSTD_isError(result)) {
                    throw new DataFormatException(this.functions.ZSTD_getErrorName(result));
                } else if (result != 0) {
                    //wait for more input or output space
                    return false;
                } else {
                    //the frame has been completely decoded and fully flushed
                    if (this.singleFrame) {
                        //stop after completing a single frame
                        this.resetStream();
                        return true;
                    } else {
                        //wait for the next ZSTD frame or EOF
                        this.state = STATE_AWAIT_FRAME;
                        continue; //jump back to function beginning, STATE_AWAIT_FRAME is handled at the top
                    }
                }
            }
        }
    }

    protected abstract long ZSTD_decompressStream(ByteBuffer src, ByteBuffer dst);
}
