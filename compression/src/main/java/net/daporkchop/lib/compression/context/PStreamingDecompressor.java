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
import java.nio.channels.ScatteringByteChannel;
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
     * @throws UnsupportedOperationException if the {@link #setSingleStream(boolean) single stream} parameter is set to {@code true}
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
     * @throws UnsupportedOperationException if the {@link #setSingleStream(boolean) single stream} parameter is set to {@code true}
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

    //TODO: update documentation about stopping points, it's no longer valid

    /**
     * Reads compressed data from the input buffer and writes it to the output buffer until the next stopping point is reached.
     * <p>
     * A stopping point is generally a point at which the compressed data stream was {@link PStreamingCompressor.FlushMode#FINISH finished}, but may also include points
     * where it was {@link PStreamingCompressor.FlushMode#SYNC flushed} or {@link PStreamingCompressor.FlushMode#FULL fully flushed}. Most compression algorithms allow
     * concatenating multiple compressed streams into a single compressed stream, so whether or not to continue decompressing after a stopping point has been reached is
     * generally up to the user.
     * <p>
     * If a stopping point is read from the input buffer but there is insufficient space in the output buffer, this method will not read any more bytes from the
     * input buffer or return {@code true} until all output data has been written to the output buffer.
     *
     * @param src the buffer to read the input data from. If no exception is thrown, this buffer's position will be incremented by {@link #getLastReadBytes()}.
     *            The bytes remaining in the input buffer once this method returns are expected to be a prefix of the readable bytes in the input buffer passed
     *            to subsequent calls to this method
     * @param dst the buffer to write the decompressed data to. If no exception is thrown, this buffer's position will be incremented by {@link #getLastWrittenBytes()}.
     *            This buffer's capacity will not be increased.
     * @param eof {@code true} if the end of the input data stream has been reached. Once this argument has been {@code true}, no more input data may be provided,
     *            and this method must be called until it returns {@code true}, with more output space provided as needed. If {@code eof} is {@code true} and the
     *            input data ends before the decompressor reaches a stopping point, this method will fail with {@link DataFormatException}.
     * @return {@code true} if the decompressor has reached a stopping point and all data up to the stopping point has been written to the output buffer, or {@code false} if more input data and/or output space is required
     * @throws DataFormatException     if the source data is not valid compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    boolean decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, boolean eof) throws DataFormatException, ReadOnlyBufferException;

    /**
     * Reads compressed data from the input buffer and writes it to the output buffer until the next stopping point is reached.
     * <p>
     * A stopping point is generally a point at which the compressed data stream was {@link PStreamingCompressor.FlushMode#FINISH finished}, but may also include points
     * where it was {@link PStreamingCompressor.FlushMode#SYNC flushed} or {@link PStreamingCompressor.FlushMode#FULL fully flushed}. Most compression algorithms allow
     * concatenating multiple compressed streams into a single compressed stream, so whether or not to continue decompressing after a stopping point has been reached is
     * generally up to the user.
     * <p>
     * If a stopping point is read from the input buffer but there is insufficient space in the output buffer, this method will not read any more bytes from the
     * input buffer or return {@code true} until all output data has been written to the output buffer.
     *
     * @param src the buffer to read the input data from. If no exception is thrown, this buffer's reader index will be incremented by {@link #getLastReadBytes()}.
     *            The bytes remaining in the input buffer once this method returns are expected to be a prefix of the readable bytes in the input buffer passed
     *            to subsequent calls to this method
     * @param dst the buffer to write the decompressed data to. If no exception is thrown, this buffer's writer index will be incremented by {@link #getLastWrittenBytes()}.
     *            This buffer's capacity will not be increased.
     * @param eof {@code true} if the end of the input data stream has been reached. Once this argument has been {@code true}, no more input data may be provided,
     *            and this method must be called until it returns {@code true}, with more output space provided as needed. If {@code eof} is {@code true} and the
     *            input data ends before the decompressor reaches a stopping point, this method will fail with {@link DataFormatException}.
     * @return {@code true} if the decompressor has reached a stopping point and all data up to the stopping point has been written to the output buffer, or {@code false} if more input data and/or output space is required
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
