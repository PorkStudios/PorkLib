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
import java.util.zip.DataFormatException;

/**
 * @author DaPorkchop_
 */
final class JniLibdeflateDecompressor extends NativeLibdeflateDecompressor {
    private final long[] nbytesArray = new long[2];

    JniLibdeflateDecompressor(@NonNull NativeLibdeflateFunctions functions, byte mode) {
        super(functions, mode);
    }

    @Override
    public int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException {
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

        //we're looping here because GZIP with 'this.singleStream == false' needs to keep going until the entire input has been consumed
        int totalSrcBytesRead = 0;
        int totalDstBytesWritten = 0;
        while (true) {
            int result = JniLibdeflateFunctions.libdeflate_decompress(this.decompressor,
                    srcArray, srcArrayLength, srcAddressOrOffset + totalSrcBytesRead, src.remaining() - totalSrcBytesRead,
                    dstArray, dstArrayLength, dstAddressOrOffset + totalDstBytesWritten, dst.remaining() - totalDstBytesWritten,
                    this.mode, this.nbytesArray);

            switch (result) {
                case NativeLibdeflateFunctions.LIBDEFLATE_SUCCESS: {
                    int actual_in_nbytes_ret = Math.toIntExact(this.nbytesArray[0]);
                    int actual_out_nbytes_ret = Math.toIntExact(this.nbytesArray[1]);

                    if (this.singleStream) {
                        //in single-stream mode we always stop after decompressing exactly one stream/member, there is nothing special to do here
                    } else if (actual_in_nbytes_ret != src.remaining() - totalSrcBytesRead) {
                        switch (this.mode) {
                            case NativeLibdeflateFunctions.MODE_DEFLATE:
                            case NativeLibdeflateFunctions.MODE_ZLIB:
                                //throw an exception if there is any input data remaining
                                throw new DataFormatException("unexpected trailing data beyond end of compressed stream");
                            case NativeLibdeflateFunctions.MODE_GZIP:
                                //increment the offsets and then decompress the next GZIP member until there's no input left
                                totalSrcBytesRead += actual_in_nbytes_ret;
                                totalDstBytesWritten += actual_out_nbytes_ret;
                                continue;
                            default:
                                throw new IllegalStateException(String.valueOf(this.mode));
                        }
                    }

                    //increment buffer positions and exit
                    dst.position(dst.position() + (totalDstBytesWritten + actual_out_nbytes_ret));
                    return totalDstBytesWritten + actual_out_nbytes_ret;
                }
                case NativeLibdeflateFunctions.LIBDEFLATE_BAD_DATA:
                    throw new DataFormatException();
                case NativeLibdeflateFunctions.LIBDEFLATE_INSUFFICIENT_SPACE:
                    return -1;
                case NativeLibdeflateFunctions.LIBDEFLATE_SHORT_OUTPUT: //should be impossible
                default:
                    throw new IllegalStateException("unexpected result: " + result);
            }
        }
    }
}
