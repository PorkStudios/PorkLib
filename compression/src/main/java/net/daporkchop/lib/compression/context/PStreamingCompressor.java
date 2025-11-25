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

package net.daporkchop.lib.compression.context;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.OptionalLong;

/**
 * A context for doing repeated streaming compression operations.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface PStreamingCompressor extends StreamingContext {
    //
    //
    // misc. methods
    //
    //

    /**
     * Returns a hint for the recommended input buffer size. The return value is generally a constant, independent of the current decompressor state.
     *
     * @return a hint for the recommended input buffer size, or an empty optional if the implementation either doesn't know or doesn't care
     */
    @Positive
    OptionalLong getRecommendedInputBufferSize();

    /**
     * Returns a hint for the recommended output buffer size. The return value is generally a constant, independent of the current decompressor state.
     *
     * @return a hint for the recommended output buffer size, or an empty optional if the implementation either doesn't know or doesn't care
     */
    @Positive
    OptionalLong getRecommendedOutputBufferSize();

    /**
     * Resets this compressor's parameters to the defaults.
     * <p>
     * Parameters may only be reset between sessions (i.e. no compression is currently ongoing).
     * <p>
     * Parameters are sticky and will remain until explicitly reset.
     *
     * @throws IllegalStateException if a compression session is currently ongoing
     */
    @Override
    void resetParameters() throws IllegalStateException;

    //
    //
    // compression methods
    //
    //

    /**
     * Resets this compressor's state.
     * <p>
     * This will cause the ongoing compression session (if any) to be aborted. Call this method before starting to compress new data.
     */
    void resetStream();

    /**
     * Equivalent to calling {@link #resetStream()} followed by {@link #resetParameters()}.
     */
    void resetStreamAndParameters();

    /**
     * Creates an {@link OutputStream} which will compress data written to it and write the compressed data to the given {@link OutputStream}.
     * <p>
     * This compressor will be automatically {@link #resetStream() reset}, cancelling any ongoing compression work.
     *
     * @param dst   the {@link OutputStream} to write to
     * @param close if {@code true}, closing the returned {@link OutputStream} will also close {@code dst}
     * @param flush the {@link FlushMode} to use when {@link OutputStream#flush()} is called
     * @return an {@link OutputStream}
     */
    OutputStream wrapCompressing(@NonNull OutputStream dst, boolean close, @NonNull FlushMode flush);

    /**
     * Creates a {@link WritableByteChannel} which will compress data written to it and write the compressed data to the given {@link WritableByteChannel}.
     * <p>
     * This compressor will be automatically {@link #resetStream() reset}, cancelling any ongoing compression work.
     *
     * @param dst   the {@link WritableByteChannel} to write to
     * @param close if {@code true}, closing the returned {@link WritableByteChannel} will also close {@code dst}
     * @return a {@link WritableByteChannel}
     */
    GatheringByteChannel wrapCompressing(@NonNull WritableByteChannel dst, boolean close);

    /**
     * Creates a {@link DataOut} which will compress data written to it and write the compressed data to the given {@link DataOut}.
     * <p>
     * This compressor will be automatically {@link #resetStream() reset}, cancelling any ongoing compression work.
     *
     * @param dst   the {@link DataOut} to write to
     * @param close if {@code true}, closing the returned {@link DataOut} will also close {@code dst}
     * @param flush the {@link FlushMode} to use when {@link OutputStream#flush()} is called
     * @return a {@link DataOut}
     */
    DataOut wrapCompressing(@NonNull DataOut dst, boolean close, @NonNull FlushMode flush);

    /**
     * @return the number of input bytes which were read by the last successful call to {@link #compress}
     */
    @NotNegative long getLastReadBytes();

    /**
     * @return the number of output bytes which were written by the last successful call to {@link #compress}
     */
    @NotNegative long getLastWrittenBytes();

    /**
     * @return a hint for the remaining number of bytes to be flushed to the output
     * @apiNote this may return {@code 0} for implementations which don't know/care
     */
    @NotNegative long getRequestedOutputBytes();

    /**
     * Compresses as much data as possible and writes it to the output buffer.
     *
     * @param src   the buffer to read the input data from. If no exception is thrown, this buffer's position will be incremented by {@link #getLastReadBytes()}.
     *              The bytes remaining in the input buffer once this method returns are expected to be a prefix of the readable bytes in the input buffer passed
     *              to subsequent calls to this method
     * @param dst   the buffer to write the compressed data to. If no exception is thrown, this buffer's position will be incremented by {@link #getLastWrittenBytes()}
     * @param flush the flush mode to use
     * @return {@code true} if all the available input data was read and all output was written according to the given {@link FlushMode}, {@code false} if more output space is requested
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException;

    /**
     * Compresses as much data as possible and writes it to the output buffer.
     *
     * @param src   the buffer to read the input data from. If no exception is thrown, this buffer's reader index will be incremented by {@link #getLastReadBytes()}.
     *              The bytes remaining in the input buffer once this method returns are expected to be a prefix of the readable bytes in the input buffer passed
     *              to subsequent calls to this method
     * @param dst   the buffer to write the compressed data to. If no exception is thrown, this buffer's writer index will be incremented by {@link #getLastWrittenBytes()}
     * @param flush the flush mode to use
     * @return {@code true} if all the available input data was read and all output was written according to the given {@link FlushMode}, {@code false} if more output space is requested
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, @NonNull FlushMode flush) throws ReadOnlyBufferException;

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
