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

package net.daporkchop.lib.compression.context;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.util.PNetty4Buffers;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.WritableByteChannel;

/**
 * A context for doing repeated streaming compression operations.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface PStreamingCompressor extends StreamingContext, GenericCompressParameters {
    /**
     * Creates an {@link OutputStream} which will compress data written to it and write the compressed data to the given {@link OutputStream}.
     * <p>
     * This compressor will be automatically {@link #resetStream() reset}, cancelling any ongoing compression work.
     * <p>
     * The returned {@link OutputStream} will borrow ownership of this context until explicitly {@link OutputStream#close() closed}. In particular,
     * context state such as {@link #getLastReadBytes()}/{@link #getLastWrittenBytes()} are meaningless when streaming in this way and so their values are not defined.
     * Additionally, the {@link #compress} methods cannot be used while the {@link OutputStream} is open.
     * <p>
     * {@link OutputStream#flush() Flushing} the {@link OutputStream} will attempt to flush this context using the {@link FlushMode} provided to this method and write
     * all resulting output to the destination.
     * <p>
     * {@link OutputStream#close() Closing} the {@link OutputStream} will attempt to {@link FlushMode#FINISH finish} the compressed stream and write all resulting output
     * to the target. Once the call returns, this context is guaranteed to be {@link #resetStream() reset}.
     *
     * @param dst   the {@link OutputStream} to write to
     * @param flush the {@link FlushMode} to use when {@link OutputStream#flush()} is called. If you don't care, use {@link FlushMode#NO}.
     * @return an {@link OutputStream}
     * @throws IllegalArgumentException if the provided {@link FlushMode} is {@link FlushMode#FINISH}
     */
    OutputStream wrapCompressing(@NonNull OutputStream dst, @NonNull FlushMode flush) throws IllegalArgumentException;

    /**
     * Creates a {@link WritableByteChannel} which will compress data written to it and write the compressed data to the given {@link WritableByteChannel}.
     * <p>
     * This compressor will be automatically {@link #resetStream() reset}, cancelling any ongoing compression work.
     * <p>
     * The returned {@link WritableByteChannel} will borrow ownership of this context until explicitly {@link WritableByteChannel#close() closed}. In particular,
     * context state such as {@link #getLastReadBytes()}/{@link #getLastWrittenBytes()} are meaningless when streaming in this way and so their values are not defined.
     * Additionally, the {@link #compress} methods cannot be used while the {@link WritableByteChannel} is open.
     * <p>
     * {@link WritableByteChannel#close() Closing} the {@link WritableByteChannel} will attempt to {@link FlushMode#FINISH finish} the compressed stream and write all
     * resulting output to the target. Once the call returns, this context is guaranteed to be {@link #resetStream() reset}.
     *
     * @param dst the {@link WritableByteChannel} to write to
     * @return a {@link WritableByteChannel}
     */
    WritableByteChannel wrapCompressing(@NonNull WritableByteChannel dst);

    /**
     * @return a hint for the remaining number of bytes to be flushed to the output
     * @apiNote this may return {@code 0} for implementations which don't know/care
     */
    default @NotNegative long getRequestedOutputBytes() {
        //don't care
        return 0L;
    }

    /**
     * Compresses as much data as possible and writes it to the output buffer.
     * <p>
     * An invocation of this method will always make as much forward progress as possible until all available input data has been read and all pending output was written
     * according to the given {@link FlushMode}, or until it runs out of output space. In particular, it is guaranteed that if the output buffer contains exactly enough
     * space (according to the given {@link FlushMode}) for the remaining input data plus any buffered output, this method will compress all the remaining data and return
     * {@code true} after a single invocation.
     * <p>
     * After every invocation of this method, even those that fail with an exception, it is guaranteed that {@link #getLastReadBytes()} and {@link #getLastWrittenBytes()} will
     * return the number of bytes read from the input stream and the number of bytes written to the output stream during that invocation, and that the input and output buffers'
     * positions will be increased by the same amount.
     * <p>
     * The behavior is undefined if the source and destination buffer's memory regions overlap.
     *
     * @param src   the buffer to read the input data from. The bytes remaining in the input buffer once this method returns are expected to be a prefix of the remaining bytes in
     *              the input buffer passed to subsequent calls to this method.
     * @param dst   the buffer to write the decompressed data to
     * @param flush the flush mode to use
     * @return {@code true} if all the available input data was read and all output was written according to the given {@link FlushMode}, {@code false} if more output space is requested
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException;

    /**
     * Compresses as much data as possible and writes it to the output buffer.
     * <p>
     * An invocation of this method will always make as much forward progress as possible until all available input data has been read and all pending output was written
     * according to the given {@link FlushMode}, or until it runs out of output space. In particular, it is guaranteed that if the output buffer contains exactly enough
     * space (according to the given {@link FlushMode}) for the remaining input data plus any buffered output, this method will compress all the remaining data and return
     * {@code true} after a single invocation.
     * <p>
     * After every invocation of this method, even those that fail with an exception, it is guaranteed that {@link #getLastReadBytes()} and {@link #getLastWrittenBytes()} will
     * return the number of bytes read from the input stream and the number of bytes written to the output stream during that invocation, and that the input and output buffers'
     * reader resp. writer indices will be increased by the same amount.
     * <p>
     * The behavior is undefined if the source and destination buffer's memory regions overlap.
     *
     * @param src   the buffer to read the input data from. The readable bytes remaining in the input buffer once this method returns are expected to be a prefix of the remaining bytes in
     *              the input buffer passed to subsequent calls to this method.
     * @param dst   the buffer to write the compressed data to. This buffer's capacity will not be increased.
     * @param flush the flush mode to use
     * @return {@code true} if all the available input data was read and all output was written according to the given {@link FlushMode}, {@code false} if more output space is requested
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, @NonNull FlushMode flush) throws ReadOnlyBufferException {
        //this default implementation simply delegates to NIO ByteBuffer overload
        ByteBuffer nioSrc = PNetty4Buffers.getNioBufferForRead(src); //copies content to heap if src is composite
        ByteBuffer nioDst = PNetty4Buffers.getNioBufferForWrite(dst); //throws ReadOnlyBufferException or CompositeBufferException as necessary
        //TODO: handle composite buffers

        int initialNioSrcPosition = nioSrc.position();
        int initialNioDstPosition = nioDst.position();

        boolean result = this.compress(nioSrc, nioDst, flush);
        src.skipBytes(nioSrc.position() - initialNioSrcPosition);
        dst.writerIndex(dst.writerIndex() + nioDst.position() - initialNioDstPosition);
        return result;
    }

    //TODO: add a method which reads from multiple buffers and writes to multiple buffers (essentially concatenating multiple inputs and outputs without additional copies)

    /**
     * @author DaPorkchop_
     */
    enum FlushMode {
        /**
         * The compressed stream will not be flushed. The compressor is allowed to buffer as much data internally as it likes, resulting in an optimal compression ratio.
         */
        NO,
        /**
         * All pending output data is flushed to the specified output buffer, so that a decompressor will be able to decompress all data written so far.
         * <p>
         * Note that this may degrade the compression ratio, and so should be used sparingly.
         * <p>
         * If {@link #compress} is called with this mode and returns {@code false}, it must be called again with the same mode, the same input data (excluding the first
         * {@link #getLastReadBytes()} bytes) and more output space until it returns {@code true}. Once {@code true} is returned, the user may continue to call
         * {@link #compress} with additional input data and any flush mode.
         */
        SYNC,
        /**
         * Similarly to {@link #SYNC}, all pending output data is flushed to the specified output buffer, so that a decompressor will be able to decompress all
         * data written so far. Additionally, the compression state will be reset such that decompression can resume from this point if previous compressed data
         * has been damaged or random access is desired.
         * <p>
         * Note that this may <strong>significantly</strong> degrade the compression ratio, and so should be used sparingly.
         * <p>
         * If {@link #compress} is called with this mode and returns {@code false}, it must be called again with the same mode, the same input data (excluding the first
         * {@link #getLastReadBytes()} bytes) and more output space until it returns {@code true}. Once {@code true} is returned, the user may continue to call
         * {@link #compress} with additional input data and any flush mode.
         */
        //TODO: figure out if i want to keep this, as not all compression algorithms necessarily support this mode
        FULL,
        /**
         * All pending output data is flushed to the specified output buffer, and the compressed output stream is ended.
         * <p>
         * If {@link #compress} is called with this mode and returns {@code false}, it must be called again with the same mode, the same input data (excluding the first
         * {@link #getLastReadBytes()} bytes) and more output space until it returns {@code true}. Once {@code true} is returned, the compressor's stream is effectively
         * reset: compressor parameters may be modified and subsequent calls to {@link #compress} will begin a new stream.
         */
        FINISH,
    }
}
