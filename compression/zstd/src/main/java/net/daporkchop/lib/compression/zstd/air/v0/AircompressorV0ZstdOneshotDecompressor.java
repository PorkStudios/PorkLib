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

import io.airlift.compress.MalformedInputException;
import io.airlift.compress.zstd.ZstdDecompressor;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotDecompressor;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
final class AircompressorV0ZstdOneshotDecompressor extends AbstractAircompressorV0ZstdContext implements ZstdOneshotDecompressor {
    private final ZstdDecompressor decompressor = new ZstdDecompressor(); //TODO: share this weakly between all instances, the ByteBuffer method constructs a new state every time anyway

    @Override
    public int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException {
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        try {
            int dstPosition = dst.position();
            this.decompressor.decompress(src, dst);
            src.position(src.limit());
            return dst.position() - dstPosition;
        } catch (MalformedInputException e) {
            //this is pretty gross but there isn't really a better way to do it
            if ("Output buffer too small".equals(e.getMessage())) {
                return -1;
            } else {
                throw (DataFormatException) new DataFormatException(e.getMessage()).initCause(e);
            }
        }
    }

    @Override
    public void setDictionary(@ExtendedBorrow ZstdDecompressDictionary dictionary) throws IllegalArgumentException {
        checkArg(dictionary == null, "dictionary isn't supported!");
    }
}
