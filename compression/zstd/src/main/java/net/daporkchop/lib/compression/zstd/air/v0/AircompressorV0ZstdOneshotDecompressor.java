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

package net.daporkchop.lib.compression.zstd.air.v0;

import io.airlift.compress.MalformedInputException;
import io.airlift.compress.zstd.ZstdDecompressor;
import lombok.NonNull;
import lombok.val;
import net.daporkchop.lib.common.annotation.ExtendedBorrow;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotDecompressor;
import net.daporkchop.lib.compression.zstd.util.JavaZstdFrameInspector;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
final class AircompressorV0ZstdOneshotDecompressor extends AbstractAircompressorV0ZstdContext implements ZstdOneshotDecompressor {
    private final ZstdDecompressor decompressor = new ZstdDecompressor(); //TODO: share this weakly between all instances, the ByteBuffer method constructs a new state every time anyway
    private boolean singleFrame;

    @Override
    public int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException {
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        val srcSlice = src.slice();
        val dstSlice = dst.slice();

        if (this.singleFrame) {
            //figure out the compressed size of the first frame in the input buffer, then only decompress to that point
            int srcFrameSize = Math.toIntExact(JavaZstdFrameInspector.getFrameSizeInfo(src).compressedSize());
            srcSlice.limit(srcFrameSize);
        } else {
            //we'll decompress the entire input buffer
        }

        try {
            this.decompressor.decompress(srcSlice, dstSlice);
        } catch (MalformedInputException e) {
            //this is pretty gross but there isn't really a better way to do it
            if ("Output buffer too small".equals(e.getMessage())) {
                return -1;
            } else {
                throw (DataFormatException) new DataFormatException(e.getMessage()).initCause(e);
            }
        }

        //increment buffer positions by the number of bytes consumed, then exit
        src.position(src.position() + srcSlice.position());
        dst.position(dst.position() + dstSlice.position());
        return dstSlice.position();
    }

    @Override
    public void resetParameters() {
        this.singleFrame = false;
    }

    @Override
    public void setDictionary(@ExtendedBorrow ZstdDecompressDictionary dictionary) throws IllegalArgumentException {
        checkArg(dictionary == null, "dictionary isn't supported!");
    }

    @Override
    public void setSingleFrame(boolean singleFrame) {
        this.singleFrame = singleFrame;
    }
}
