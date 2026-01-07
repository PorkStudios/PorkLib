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

package net.daporkchop.lib.compression.deflate.jdk;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.deflate.DeflateOneshotDecompressor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;

/**
 * @author DaPorkchop_
 */
class JdkOneshotDeflateDecompressor extends AbstractJdkDeflateDecompressContext implements DeflateOneshotDecompressor {
    JdkOneshotDeflateDecompressor(boolean noWrap) {
        super(noWrap);
    }

    @Override
    public int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException {
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        try {
            if (src.hasArray()) {
                this.inflater.setInput(src.array(), src.arrayOffset() + src.position(), src.remaining());
            } else {
                // src is a direct or read-only buffer, copy contents to an array (slow!)
                this.inflater.setInput(PNioBuffers.toArray(src));
            }

            byte[] dstCopyArray = null;
            int written;
            if (dst.hasArray()) {
                written = this.inflater.inflate(dst.array(), dst.arrayOffset() + dst.position(), dst.remaining());
            } else {
                // dst is a direct buffer, compress to a temporary array and then copy back on success (slow!)
                dstCopyArray = PUnsafe.allocateUninitializedByteArray(dst.remaining());
                written = this.inflater.inflate(dstCopyArray);
            }

            //TODO: check if we read to the end of the input buffer

            if (this.inflater.finished()) {
                if (dstCopyArray == null) {
                    dst.position(dst.position() + written);
                } else {
                    dst.put(dstCopyArray, 0, written);
                }
                return written;
            } else {
                return -1;
            }
        } finally {
            this.inflater.setInput(PorkUtil.emptyByteArray());
            this.inflater.reset();
        }
    }
}
