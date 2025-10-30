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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.io.InputStream;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

/**
 * A context for doing repeated streaming compression operations.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface PStreamingDecompressor extends StreamingContext {
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
    @Positive OptionalLong getRecommendedInputBufferSize();

    /**
     * Returns a hint for the recommended output buffer size. The return value is generally a constant, independent of the current decompressor state.
     *
     * @return a hint for the recommended output buffer size, or an empty optional if the implementation either doesn't know or doesn't care
     */
    @Positive OptionalLong getRecommendedOutputBufferSize();

    /**
     * Resets this decompressor's parameters to the defaults.
     * <p>
     * Parameters may only be reset between sessions (i.e. no decompression is currently ongoing).
     * <p>
     * Parameters are sticky and will remain until explicitly reset.
     *
     * @throws IllegalStateException if a decompression session is currently ongoing
     */
    PStreamingDecompressor resetParameters() throws IllegalStateException;

    //
    //
    // compression methods
    //
    //

    /**
     * Resets this decompressor's state.
     * <p>
     * This will cause the ongoing decompression session (if any) to be aborted. Call this method before starting to decompress new data.
     */
    PStreamingDecompressor resetStream();

    /**
     * Equivalent to calling {@link #resetStream()} followed by {@link #resetParameters()}.
     */
    PStreamingDecompressor resetStreamAndParameters();

    /**
     * Creates an {@link InputStream} which will read compressed data from the given {@link InputStream} and decompress it.
     * <p>
     * This decompressor will be automatically {@link #resetStream() reset}, cancelling any ongoing decompression work.
     *
     * @param src   the {@link InputStream} to read from
     * @param close if {@code true}, closing the returned {@link InputStream} will also close {@code src}
     * @return an {@link InputStream}
     */
    InputStream wrapDecompressing(@NonNull InputStream src, boolean close);

    /**
     * Creates a {@link ReadableByteChannel} which will read compressed data from the given {@link ReadableByteChannel} and decompress it.
     * <p>
     * This decompressor will be automatically {@link #resetStream() reset}, cancelling any ongoing decompression work.
     *
     * @param src   the {@link ReadableByteChannel} to read from
     * @param close if {@code true}, closing the returned {@link ReadableByteChannel} will also close {@code src}
     * @return a {@link ReadableByteChannel}
     */
    ScatteringByteChannel wrapDecompressing(@NonNull ReadableByteChannel src, boolean close);

    /**
     * Creates a {@link DataIn} which will read compressed data from the given {@link DataIn} and decompress it.
     * <p>
     * This decompressor will be automatically {@link #resetStream() reset}, cancelling any ongoing decompression work.
     *
     * @param src   the {@link DataIn} to read from
     * @param close if {@code true}, closing the returned {@link DataIn} will also close {@code src}
     * @return a {@link DataIn}
     */
    DataIn wrapDecompressing(@NonNull DataIn src, boolean close);

    /**
     * @return the number of input bytes which were read by the last successful call to {@link #decompress}
     */
    @NotNegative long getLastReadBytes();

    /**
     * @return the number of output bytes which were written by the last successful call to {@link #decompress}
     */
    @NotNegative long getLastWrittenBytes();

    /**
     * @return a hint for the suggested minimum number of input bytes to provide to the next call to {@link #decompress}
     * @apiNote this may return {@code 0} for implementations which don't know/care
     */
    @NotNegative long getRequestedInputBytes();

    /**
     * @return a hint for the remaining number of bytes to be flushed to the output
     * @apiNote this may return {@code 0} for implementations which don't know/care
     */
    @NotNegative long getRequestedOutputBytes();

    //TODO: add ByteBuffer overload

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
     *            and this method must be called until it returns {@code true}, with more output space provided as needed.
     * @return {@code true} if the decompressor has reached a stopping point and all data up to the stopping point has been written to the output buffer, or {@code false} if more input data and/or output space is required
     * @throws DataFormatException if the source data is not valid compressed data
     * @throws ReadOnlyBufferException if the destination buffer is read-only
     */
    boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, boolean eof) throws DataFormatException, ReadOnlyBufferException;

    //TODO: add a method which reads from multiple buffers and writes to multiple buffers (essentially concatenating multiple inputs and outputs without additional copies)
}
