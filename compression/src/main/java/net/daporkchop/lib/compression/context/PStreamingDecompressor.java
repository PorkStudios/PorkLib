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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.DataFormatException;

/**
 * A context for doing repeated streaming compression operations.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface PStreamingDecompressor extends StreamingContext, GenericDecompressParameters {
    /**
     * Creates an {@link InputStream} which will read compressed data from the given {@link InputStream} and decompress it.
     * <p>
     * This decompressor will be automatically {@link #resetStream() reset}, cancelling any ongoing decompression work.
     * <p>
     * The returned {@link InputStream} will borrow ownership of this context until explicitly {@link InputStream#close() closed}. In particular,
     * context state such as {@link #getLastReadBytes()}/{@link #getLastWrittenBytes()} are meaningless when streaming in this way and so their values are not defined.
     * Additionally, the {@link #decompress} methods cannot be used while the {@link InputStream} is open.
     * <p>
     * {@link InputStream#close() Closing} the {@link InputStream} will effectively call {@link #resetStream()}, cancelling any ongoing decompression work and
     * allowing regular decompression to continue. Note that the {@link InputStream} implementation may buffer input data eagerly and therefore could end up
     * discarding data which was otherwise intended to be read.
     *
     * @param src the {@link InputStream} to read from
     * @return an {@link InputStream}
     * @throws UnsupportedOperationException if the {@link #setSingleFrame(boolean) single frame} parameter is set to {@code true}
     */
    InputStream wrapDecompressing(@NonNull InputStream src);

    /**
     * Creates a {@link ReadableByteChannel} which will read compressed data from the given {@link ReadableByteChannel} and decompress it.
     * <p>
     * This decompressor will be automatically {@link #resetStream() reset}, cancelling any ongoing decompression work.
     * <p>
     * The returned {@link ReadableByteChannel} will borrow ownership of this context until explicitly {@link ReadableByteChannel#close() closed}. In particular,
     * context state such as {@link #getLastReadBytes()}/{@link #getLastWrittenBytes()} are meaningless when streaming in this way and so their values are not defined.
     * Additionally, the {@link #decompress} methods cannot be used while the {@link ReadableByteChannel} is open.
     * <p>
     * {@link ReadableByteChannel#close() Closing} the {@link ReadableByteChannel} will effectively call {@link #resetStream()}, cancelling any ongoing decompression work and
     * allowing regular decompression to continue. Note that the {@link ReadableByteChannel} implementation may buffer input data eagerly and therefore could end up
     * discarding data which was otherwise intended to be read.
     *
     * @param src the {@link ReadableByteChannel} to read from
     * @return a {@link ReadableByteChannel}
     * @throws UnsupportedOperationException if the {@link #setSingleFrame(boolean) single frame} parameter is set to {@code true}
     */
    ReadableByteChannel wrapDecompressing(@NonNull ReadableByteChannel src);

    /**
     * @return a hint for the suggested minimum number of input bytes to provide to the next call to {@link #decompress}
     * @apiNote this may return {@code 0} for implementations which don't know/care
     */
    default @NotNegative long getRequestedInputBytes() {
        //don't care
        return 0L;
    }

    /**
     * @return a hint for the remaining number of bytes to be flushed to the output
     * @apiNote this may return {@code 0} for implementations which don't know/care
     */
    default @NotNegative long getRequestedOutputBytes() {
        //don't care
        return 0L;
    }

    /**
     * Reads compressed data from the input buffer and writes decompressed data to the output buffer until the end of the compressed stream has been consumed and
     * all uncompressed data has been returned.
     * <p>
     * What exactly defines the end of the compressed stream depends on the compression algorithm and the {@link #setSingleFrame(boolean) single stream} parameter:
     * <p>
     * <ul>
     *     <li>If the {@link #setSingleFrame(boolean) single frame} parameter is {@code true}, this will occur after exactly one stream has been decompressed from
     *     the input. Note that for compression formats which support stream concatenation, this may include points at which the compressor was
     *     {@link PStreamingCompressor.FlushMode#FULL fully flushed}.</li>
     *     <li>If the {@link #setSingleFrame(boolean) single frame} parameter is {@code false} and the compression format <strong>does not</strong> support concatenation,
     *     this method returns {@code true} once exactly one stream has been decompressed from the input.</li>
     *     <li>If the {@link #setSingleFrame(boolean) single frame} parameter is {@code false} and the compression format <strong>does</strong> support concatenation,
     *     this method returns {@code true} once the end of the input stream has been reached (i.e. {@code eof} is {@code true} and all of the input data has been consumed).</li>
     * </ul>
     * <p>
     * An invocation of this method will always make as much forward progress as possible until it runs out of either input or output space, or the end of the
     * compressed stream is reached (which would cause this method to return {@code true}). In particular, it is guaranteed that if the input buffer contains
     * all the remaining compressed data, the output buffer contains exactly enough space for the remaining uncompressed data and the {@code eof} parameter
     * is set appropriately for the algorithm to determine whether the end of the compressed stream has been reached according to the rules above, this method will
     * decompress the entire compressed stream and return {@code true} after a single invocation.
     * <p>
     * If the end of the compressed stream has been read from the input stream but there is still buffered output remaining, this method will not read any more bytes
     * from the input until sufficient output space is provided that this method is able to flush all the remaining output and return {@code true}.
     * <p>
     * After every invocation of this method, even those that fail with an exception, it is guaranteed that {@link #getLastReadBytes()} and {@link #getLastWrittenBytes()} will
     * return the number of bytes read from the input stream and the number of bytes written to the output stream during that invocation, and that the input and output buffers'
     * positions will be increased by the same amount.
     *
     * @param src the buffer to read the input data from. The bytes remaining in the input buffer once this method returns are expected to be a prefix of the remaining bytes in
     *            the input buffer passed to subsequent calls to this method.
     * @param dst the buffer to write the decompressed data to
     * @param eof {@code true} if the end of the input data stream has been reached. Once this argument has been {@code true}, no more input data may be provided,
     *            and this method must be called until it returns {@code true}, with more output space provided as needed. If {@code eof} is {@code true} and the
     *            input data ends before the decompressor reaches the end of the current compressed stream, this method will fail with {@link DataFormatException}.
     * @return {@code true} if the decompressor has reached the end of the compressed stream and all pending data has been written to the output buffer, or {@code false} if more input data and/or output space and/or an explicit indicator of end-of-stream status is required
     * @throws DataFormatException     if the source data is not valid compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    boolean decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, boolean eof) throws DataFormatException, ReadOnlyBufferException;

    /**
     * Reads compressed data from the input buffer and writes decompressed data to the output buffer until the end of the compressed stream has been consumed and
     * all uncompressed data has been returned.
     * <p>
     * What exactly defines the end of the compressed stream depends on the compression algorithm and the {@link #setSingleFrame(boolean) single stream} parameter:
     * <p>
     * <ul>
     *     <li>If the {@link #setSingleFrame(boolean) single frame} parameter is {@code true}, this will occur after exactly one stream has been decompressed from
     *     the input. Note that for compression formats which support stream concatenation, this may include points at which the compressor was
     *     {@link PStreamingCompressor.FlushMode#FULL fully flushed}.</li>
     *     <li>If the {@link #setSingleFrame(boolean) single frame} parameter is {@code false} and the compression format <strong>does not</strong> support concatenation,
     *     this method returns {@code true} once exactly one stream has been decompressed from the input.</li>
     *     <li>If the {@link #setSingleFrame(boolean) single frame} parameter is {@code false} and the compression format <strong>does</strong> support concatenation,
     *     this method returns {@code true} once the end of the input stream has been reached (i.e. {@code eof} is {@code true} and all of the input data has been consumed).</li>
     * </ul>
     * <p>
     * An invocation of this method will always make as much forward progress as possible until it runs out of either input or output space, or the end of the
     * compressed stream is reached (which would cause this method to return {@code true}). In particular, it is guaranteed that if the input buffer contains
     * all the remaining compressed data, the output buffer contains exactly enough space for the remaining uncompressed data and the {@code eof} parameter
     * is set appropriately for the algorithm to determine whether the end of the compressed stream has been reached according to the rules above, this method will
     * decompress the entire compressed stream and return {@code true} after a single invocation.
     * <p>
     * If the end of the compressed stream has been read from the input stream but there is still buffered output remaining, this method will not read any more bytes
     * from the input until sufficient output space is provided that this method is able to flush all the remaining output and return {@code true}.
     * <p>
     * After every invocation of this method, even those that fail with an exception, it is guaranteed that {@link #getLastReadBytes()} and {@link #getLastWrittenBytes()} will
     * return the number of bytes read from the input stream and the number of bytes written to the output stream during that invocation, and that the input and output buffers'
     * reader resp. writer indices will be increased by the same amount.
     *
     * @param src the buffer to read the input data from. The readable bytes remaining in the input buffer once this method returns are expected to be a prefix of the readable bytes in
     *            the input buffer passed to subsequent calls to this method.
     * @param dst the buffer to write the decompressed data to. This buffer's capacity will not be increased.
     * @param eof {@code true} if the end of the input data stream has been reached. Once this argument has been {@code true}, no more input data may be provided,
     *            and this method must be called until it returns {@code true}, with more output space provided as needed. If {@code eof} is {@code true} and the
     *            input data ends before the decompressor reaches the end of the current compressed stream, this method will fail with {@link DataFormatException}.
     * @return {@code true} if the decompressor has reached the end of the compressed stream and all pending data has been written to the output buffer, or {@code false} if more input data and/or output space and/or an explicit indicator of end-of-stream status is required
     * @throws DataFormatException     if the source data is not valid compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    default boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, boolean eof) throws DataFormatException, ReadOnlyBufferException {
        //this default implementation simply delegates to NIO ByteBuffer overload
        ByteBuffer nioSrc = PNetty4Buffers.getNioBufferForRead(src); //copies content to heap if src is composite
        ByteBuffer nioDst = PNetty4Buffers.getNioBufferForWrite(dst); //throws ReadOnlyBufferException or CompositeBufferException as necessary

        int initialNioSrcPosition = nioSrc.position();
        int initialNioDstPosition = nioDst.position();

        boolean result = this.decompress(nioSrc, nioDst, eof);
        src.skipBytes(nioSrc.position() - initialNioSrcPosition);
        dst.writerIndex(dst.writerIndex() + nioDst.position() - initialNioDstPosition);
        return result;
    }

    //TODO: add a method which reads from multiple buffers and writes to multiple buffers (essentially concatenating multiple inputs and outputs without additional copies)
}
