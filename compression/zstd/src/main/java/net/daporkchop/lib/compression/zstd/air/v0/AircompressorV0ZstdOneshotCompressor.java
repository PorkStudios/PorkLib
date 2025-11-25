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

package net.daporkchop.lib.compression.zstd.air.v0;

import io.airlift.compress.zstd.ZstdCompressor;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotCompressor;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
final class AircompressorV0ZstdOneshotCompressor extends AbstractAircompressorV0ZstdContext implements ZstdOneshotCompressor {
    private final ZstdCompressor compressor = new ZstdCompressor();

    @Override
    public int compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws ReadOnlyBufferException {
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        try {
            int dstPosition = dst.position();
            this.compressor.compress(src, dst);
            return dst.position() - dstPosition;
        } catch (IllegalArgumentException e) {
            //this is pretty gross but there isn't really a better way to do it
            if ("Output buffer too small".equals(e.getMessage())) {
                return -1;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void setLevel(int level) throws IllegalArgumentException {
        checkArg(level == Zstd.LEVEL_DEFAULT, "only the default compression level is supported!");
    }

    @Override
    public void setDictionary(@ExtendedBorrow ZstdCompressDictionary dictionary) throws IllegalArgumentException {
        checkArg(dictionary == null, "dictionary isn't supported!");
    }

    @Override
    public void setChecksumFlag(boolean checksumFlag) throws IllegalArgumentException {
        checkArg(checksumFlag, "checksumFlag isn't supported!");
    }

    @Override
    public void setContentSizeFlag(boolean contentSizeFlag) throws IllegalArgumentException {
        checkArg(contentSizeFlag, "contentSizeFlag isn't supported!");
    }

    @Override
    public void setDictIdFlag(boolean dictIdFlag) throws IllegalArgumentException {
        checkArg(dictIdFlag, "dictIdFlag isn't supported!");
    }
}
