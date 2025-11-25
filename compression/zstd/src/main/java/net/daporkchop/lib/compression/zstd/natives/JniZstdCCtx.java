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
import io.netty.buffer.ByteBufUtil;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.compression.util.exception.CompositeBufferException;
import net.daporkchop.lib.natives.NativeException;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import static net.daporkchop.lib.compression.zstd.natives.NativeZstdFactory.*;

/**
 * @author DaPorkchop_
 */
final class JniZstdCCtx extends NativeZstdCCtx {
    JniZstdCCtx(@NonNull JniZstdFactory factory) {
        super(factory);
    }

    static native long ZSTD_compress2(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining);

    @Override
    public int compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws ReadOnlyBufferException {
        //get buffer pointers
        long srcMemoryAddress = 0L;
        byte[] srcArray = null;
        int srcArrayOffset = 0;
        if (src.isDirect()) {
            srcMemoryAddress = PUnsafe.pork_directBufferAddress(src);
        } else if (src.hasArray()) {
            srcArray = src.array();
            srcArrayOffset = src.arrayOffset();
        } else {
            // This is most likely a read-only heap buffer, or another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a heap array (slow!!!)
            //TODO: maybe do streaming compression here if the source data is very big?
            srcArray = PNioBuffers.toArray(src);
        }

        long dstMemoryAddress = 0L;
        byte[] dstArray = null;
        int dstArrayOffset = 0;
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (dst.isDirect()) {
            dstMemoryAddress = PUnsafe.pork_directBufferAddress(dst);
        } else if (dst.hasArray()) {
            dstArray = dst.array();
            dstArrayOffset = dst.arrayOffset();
        } else {
            throw new IllegalArgumentException("buffer not supported: " + dst);
        }

        long result = ZSTD_compress2(this.ctx,
                srcMemoryAddress, srcArray, srcArrayOffset, src.position(), src.remaining(),
                dstMemoryAddress, dstArray, dstArrayOffset, dst.position(), dst.remaining());

        switch (this.factory.ZSTD_getErrorCode(result)) {
            case ZSTD_error_no_error:
                //success, advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
                src.position(src.limit());
                dst.position(dst.position() + (int) result);
                return (int) result;
            case ZSTD_error_dstSize_tooSmall:
                return -1;
            default:
                throw new NativeException(this.factory.ZSTD_getErrorName(result));
        }
    }

    @Override
    public int compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws ReadOnlyBufferException, CompositeBufferException {
        //get buffer pointers
        long srcMemoryAddress = 0L;
        byte[] srcArray = null;
        int srcArrayOffset = 0;
        if (src.hasMemoryAddress()) {
            srcMemoryAddress = src.memoryAddress();
        } else if (src.hasArray()) {
            srcArray = src.array();
            srcArrayOffset = src.arrayOffset();
        } else {
            // This is most likely either a read-only heap buffer, a composite buffer, or another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a heap array (slow!!!)
            //TODO: maybe do streaming compression here if the source data is composite and/or if the source data is very big
            srcArray = ByteBufUtil.getBytes(src);
        }

        long dstMemoryAddress = 0L;
        byte[] dstArray = null;
        int dstArrayOffset = 0;
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (dst.hasMemoryAddress()) {
            dstMemoryAddress = dst.memoryAddress();
        } else if (dst.hasArray()) {
            dstArray = dst.array();
            dstArrayOffset = dst.arrayOffset();
        } else {
            // This is almost certainly a composite buffer, we won't bother handling it.
            throw new CompositeBufferException(dst);
        }

        long result = ZSTD_compress2(this.ctx,
                srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes());

        switch (this.factory.ZSTD_getErrorCode(result)) {
            case ZSTD_error_no_error:
                //success, advance buffer indices (we assume that the result is in bounds and therefore won't overflow)
                src.skipBytes(src.readableBytes());
                dst.writerIndex(dst.writerIndex() + (int) result);
                return (int) result;
            case ZSTD_error_dstSize_tooSmall:
                return -1;
            default:
                throw new NativeException(this.factory.ZSTD_getErrorName(result));
        }
    }
}
