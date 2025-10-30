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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.math.PMath;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotDecompressor;

import java.nio.ReadOnlyBufferException;
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstdProvider.*;

/**
 * @author DaPorkchop_
 */
abstract class NativeZstdDCtx extends AbstractNativeZstdContext implements ZstdOneshotDecompressor {
    private NativeZstdDDict dictionary;

    NativeZstdDCtx(@NonNull NativeZstdProvider provider) {
        super(provider, provider.ZSTD_createDCtx());
    }

    @Override
    final Runnable freeCtxRunnable(@NonNull NativeZstdProvider provider, long ctx) {
        return () -> provider.ZSTD_freeDCtx(ctx);
    }

    @Override
    public final ZstdOneshotDecompressor resetParameters() { //TODO: merge exception rules when we also implement streaming
        this.provider.checkForErrorAndThrow(this.provider.ZSTD_DCtx_reset(this.ctx, ZSTD_reset_parameters));
        this.dictionary = null;
        return this;
    }

    @Override
    public final ZstdOneshotDecompressor setDictionary(@ExtendedBorrow ZstdDecompressDictionary dictionary) throws IllegalArgumentException {
        checkArg(dictionary == null || dictionary instanceof NativeZstdDDict, dictionary);
        this.dictionary = (NativeZstdDDict) dictionary;
        this.provider.checkForErrorAndThrow(this.provider.ZSTD_DCtx_refDDict(this.ctx, dictionary != null ? this.dictionary.dict : 0L));
        return this;
    }

    @Override
    public @NotNegative int decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws DataFormatException, IndexOutOfBoundsException, ReadOnlyBufferException {
        //TODO: maybe implement this in a smarter way using streaming?
        OptionalLong decompressedSizeExact = this.decompressedSizeExact(src);
        if (decompressedSizeExact.isPresent()) {
            //we know the exact decompressed size, try to reserve necessary buffer space and then decompress
            dst.ensureWritable(PMath.toIntSaturate(decompressedSizeExact.getAsLong()));

            int result = this.decompress(src, dst);
            if (result < 0) {
                //this should be impossible, but we'll check just in case and throw the same exception type that would be expected if the buffer couldn't be grown sufficiently
                throw new IndexOutOfBoundsException("need more output space?!?");
            }
            return result;
        }

        //the compressed data doesn't store its exact decompressed size, we'll have to resort to streaming decompression
        throw new UnsupportedOperationException("streaming decompression not implemented"); //TODO: implement this
    }
}
