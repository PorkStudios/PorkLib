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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.compression.util.exception.CompositeBufferException;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;

import static net.daporkchop.lib.compression.zstd.natives.NativeZstdFunctions.*;

/**
 * @author DaPorkchop_
 */
final class JniZstdDCtx extends NativeZstdDCtx {
    private final long[] nbytesArray = new long[2];

    JniZstdDCtx(@NonNull NativeZstdFunctions functions) {
        super(functions);
    }

    @Override
    public int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException {
        this.resetStream();

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
            //TODO: maybe do streaming decompression here if the source data is very big?
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

        if (this.singleFrame) {
            long result;
            try {
                //use ZSTD_decompressStream() to stop decompressing after reaching the end of the first frame
                result = JniZstdFunctions.ZSTD_decompressStream(this.dctx.addr(),
                        srcArray, srcArrayLength, srcAddressOrOffset, src.remaining(),
                        dstArray, dstArrayLength, dstAddressOrOffset, dst.remaining(),
                        this.nbytesArray);
            } finally {
                //make sure to reset the DCtx session afterwards to ensure it's in a valid non-streaming state
                this.resetStream();
            }

            if (this.functions.ZSTD_isError(result)) {
                throw new DataFormatException(this.functions.ZSTD_getErrorName(result));
            } else if (result != 0) {
                //didn't decompress the entire frame
                return -1;
            } else {
                //advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
                src.position(src.position() + (int) this.nbytesArray[0]);
                dst.position(dst.position() + (int) this.nbytesArray[1]);
                return (int) this.nbytesArray[1];
            }
        }

        long result = JniZstdFunctions.ZSTD_decompressDCtx(this.dctx.addr(),
                srcArray, srcArrayLength, srcAddressOrOffset, src.remaining(),
                dstArray, dstArrayLength, dstAddressOrOffset, dst.remaining());

        switch (this.functions.ZSTD_getErrorCode(result)) {
            case ZSTD_error_no_error:
                //success, advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
                src.position(src.limit());
                dst.position(dst.position() + (int) result);
                return (int) result;
            case ZSTD_error_dstSize_tooSmall:
                return -1;
            default:
                throw new DataFormatException(this.functions.ZSTD_getErrorName(result));
        }
    }

    @Override
    public int decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws DataFormatException, ReadOnlyBufferException, CompositeBufferException {
        if (this.singleFrame) {
            //ByteBuffer overload has the stuff for single-frame decompression
            return super.decompress(src, dst);
        }

        this.resetStream();

        //get buffer pointers
        byte[] srcArray;
        int srcArrayLength;
        long srcAddressOrOffset;
        if (src.hasMemoryAddress()) {
            srcArray = null;
            srcArrayLength = 0;
            srcAddressOrOffset = src.memoryAddress() + src.readerIndex();
        } else if (src.hasArray()) {
            srcArray = src.array();
            srcArrayLength = srcArray.length;
            srcAddressOrOffset = src.arrayOffset() + src.readerIndex();
        } else {
            // This is most likely either a read-only heap buffer, a composite buffer, or another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a heap array (slow!!!)
            //TODO: maybe do streaming decompression here if the source data is composite and/or if the source data is very big
            srcArray = ByteBufUtil.getBytes(src);
            srcArrayLength = srcArray.length;
            srcAddressOrOffset = 0;
        }

        byte[] dstArray;
        int dstArrayLength;
        long dstAddressOrOffset;
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (dst.hasMemoryAddress()) {
            dstArray = null;
            dstArrayLength = 0;
            dstAddressOrOffset = dst.memoryAddress() + dst.writerIndex();
        } else if (dst.hasArray()) {
            dstArray = dst.array();
            dstArrayLength = dstArray.length;
            dstAddressOrOffset = dst.arrayOffset() + dst.writerIndex();
        } else {
            // This is almost certainly a composite buffer, we won't bother handling it.
            throw new CompositeBufferException(dst);
        }

        long result = JniZstdFunctions.ZSTD_decompressDCtx(this.dctx.addr(),
                srcArray, srcArrayLength, srcAddressOrOffset, src.readableBytes(),
                dstArray, dstArrayLength, dstAddressOrOffset, dst.writableBytes());

        switch (this.functions.ZSTD_getErrorCode(result)) {
            case ZSTD_error_no_error:
                //success, advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
                src.skipBytes(src.readableBytes());
                dst.writerIndex(dst.writerIndex() + (int) result);
                return (int) result;
            case ZSTD_error_dstSize_tooSmall:
                return -1;
            default:
                throw new DataFormatException(this.functions.ZSTD_getErrorName(result));
        }
    }

    @Override
    protected long ZSTD_decompressStream(ByteBuffer src, ByteBuffer dst) {
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
            //TODO: maybe do streaming decompression here if the source data is very big?
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

        long result = JniZstdFunctions.ZSTD_decompressStream(this.dctx.addr(),
                srcArray, srcArrayLength, srcAddressOrOffset, src.remaining(),
                dstArray, dstArrayLength, dstAddressOrOffset, dst.remaining(),
                this.nbytesArray);

        //advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
        src.position(src.position() + (int) this.nbytesArray[0]);
        dst.position(dst.position() + (int) this.nbytesArray[1]);

        return result;
    }
}
