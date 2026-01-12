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

package net.daporkchop.lib.compression.deflate.libdeflate;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * @author DaPorkchop_
 */
final class JniLibdeflateCompressor extends NativeLibdeflateCompressor {
    JniLibdeflateCompressor(@NonNull NativeLibdeflateFunctions functions, byte mode) {
        super(functions, mode);
    }

    @Override
    public int compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws ReadOnlyBufferException {
        //get buffer pointers
        byte[] srcArray;
        int srcArrayLength;
        long srcAddressOrOffset;
        if (src.isDirect()) {
            srcArray = null;
            srcArrayLength = 0;
            srcAddressOrOffset = PUnsafe.pork_directBufferAddress(src) + src.position();
        } else if (src.hasArray()) {
            srcArray = src.array();
            srcArrayLength = srcArray.length;
            srcAddressOrOffset = src.arrayOffset() + src.position();
        } else {
            // This is most likely a read-only heap buffer, or another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a heap array (slow!!!)
            srcArray = PNioBuffers.toArray(src);
            srcArrayLength = srcArray.length;
            srcAddressOrOffset = 0;
        }

        byte[] dstArray;
        int dstArrayLength;
        long dstAddressOrOffset;
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (dst.isDirect()) {
            dstArray = null;
            dstArrayLength = 0;
            dstAddressOrOffset = PUnsafe.pork_directBufferAddress(dst) + dst.position();
        } else if (dst.hasArray()) {
            dstArray = dst.array();
            dstArrayLength = dstArray.length;
            dstAddressOrOffset = dst.arrayOffset() + dst.position();
        } else {
            throw new IllegalArgumentException("buffer not supported: " + dst);
        }

        long result = JniLibdeflateFunctions.libdeflate_compress(this.compressor(),
                srcArray, srcArrayLength, srcAddressOrOffset, src.remaining(),
                dstArray, dstArrayLength, dstAddressOrOffset, dst.remaining(),
                this.mode);

        if (result == 0) {
            //not enough space
            return -1;
        } else {
            //success, advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
            dst.position(dst.position() + (int) result);
            return (int) result;
        }
    }
}
