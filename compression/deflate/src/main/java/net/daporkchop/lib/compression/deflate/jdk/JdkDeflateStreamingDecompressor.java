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
import lombok.val;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.deflate.DeflateStreamingDecompressor;
import net.daporkchop.lib.compression.generic.AbstractStreamingDecompressor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * @author DaPorkchop_
 */
final class JdkDeflateStreamingDecompressor extends AbstractStreamingDecompressor implements DeflateStreamingDecompressor, JdkDeflateContext {
    private static final byte STATE_RESET = 0;
    private static final byte STATE_DECOMPRESS = 1;
    private static final byte STATE_ASSERT_NO_TRAILING_DATA = 2;
    private static final byte STATE_DONE = 3;

    final Inflater inflater;

    private byte state = STATE_RESET;

    JdkDeflateStreamingDecompressor(boolean noWrap) {
        this.inflater = new Inflater(noWrap);
    }

    @Override
    public void close() {
        this.inflater.end();
    }

    @Override
    public void resetStream() {
        super.resetStream();

        this.inflater.reset();
        this.state = STATE_RESET;
    }

    @Override
    protected boolean isStreamOngoing() {
        return this.state != STATE_RESET;
    }

    @Override
    public InputStream wrapDecompressing(@NonNull InputStream src) {
        this.ensureNotSingleFrame();
        this.resetStream();
        return new InflaterInputStream(src, this.inflater);
    }

    @Override
    public boolean decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, boolean eof) throws DataFormatException, ReadOnlyBufferException {
        this.setLastReadWrittenBytes(0, 0);

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        if (this.state == STATE_RESET) {
            this.state = STATE_DECOMPRESS;
        }

        this.validateEofParameter(eof);

        if (this.state == STATE_DECOMPRESS) {
            val initialBytesRead = this.inflater.getBytesRead();
            val initialBytesWritten = this.inflater.getBytesWritten();
            try {
                if (JdkDeflateUtils.supportsByteBufferMethods()) {
                    //use the new ByteBuffer methods directly
                    JdkDeflateUtils.setInput(this.inflater, src);
                    JdkDeflateUtils.inflate(this.inflater, dst);
                } else {
                    //use the old byte[] methods (this may decompress in smaller blocks to avoid allocating massive temporary arrays)
                    //this will automatically increment src and dst buffer positions
                    this.inflateArray(src, dst);
                }
            } finally {
                //ensure that the input array can be GCd
                this.inflater.setInput(PorkUtil.emptyByteArray());

                //set last read/written count (no need to increment, both fields are already set to zero)
                this.setLastReadWrittenBytes(
                        this.inflater.getBytesRead() - initialBytesRead,
                        this.inflater.getBytesWritten() - initialBytesWritten);
            }

            if (this.inflater.finished()) {
                if (this.singleFrame) {
                    //we've reached the end of the compressed stream, stop here and exit
                    this.state = STATE_DONE;
                } else {
                    this.state = STATE_ASSERT_NO_TRAILING_DATA;
                }
            } else if (eof && !src.hasRemaining()) {
                //reached the end of the input stream before the inflater finished, which means the input stream is truncated
                throw new DataFormatException("Unexpected end of ZLIB input stream");
            }
        }

        if (this.state == STATE_ASSERT_NO_TRAILING_DATA) {
            if (src.hasRemaining()) {
                //there is some trailing data, throw an exception
                throw new DataFormatException("unexpected trailing data beyond end of compressed stream");
            } else if (eof) {
                //we've reached the end of the input stream and there was no more input data, so we're all good here.
                this.state = STATE_DONE;
            } else {
                //there's no input data available right now, but we haven't reached the end of the input stream so there might be more coming later. we'll
                //just fallthrough here, return false and wait for more input or an explicit EOF notification.
            }
        }

        if (this.state == STATE_DONE) {
            //decompression is done, we can reset the stream now :)
            this.resetStream();
            return true;
        }

        return false;
    }

    private void inflateArray(ByteBuffer src, ByteBuffer dst) throws DataFormatException {
        final int STREAM_BUF_SIZE = 8192;

        final byte[] srcStreamBuf;
        boolean allInputPresented;
        if (src.hasArray()) {
            this.inflater.setInput(src.array(), src.arrayOffset() + src.position(), src.remaining());
            srcStreamBuf = null;
            allInputPresented = true;
        } else {
            this.inflater.setInput(PorkUtil.emptyByteArray()); //this ensures that inflater.needsInput() will return true when we enter the loop below
            srcStreamBuf = PUnsafe.allocateUninitializedByteArray(Math.min(src.remaining(), STREAM_BUF_SIZE));
            allInputPresented = false;
        }

        final byte[] dstStreamBuf;
        if (dst.hasArray()) {
            dstStreamBuf = null;
        } else {
            dstStreamBuf = PUnsafe.allocateUninitializedByteArray(Math.min(dst.remaining(), STREAM_BUF_SIZE));
        }

        int lastBytesReadTotal = this.inflater.getTotalIn();
        int lastBytesWrittenTotal = this.inflater.getTotalOut();
        while (true) {
            if (!allInputPresented && this.inflater.needsInput()) {
                //copy some more bytes into srcStreamBuffer (without changing its position, we'll do that later)
                int nextInputBytesSubmitted = Math.min(srcStreamBuf.length, src.remaining());
                PNioBuffers.copy(src, src.position(), srcStreamBuf, 0, nextInputBytesSubmitted);
                this.inflater.setInput(srcStreamBuf, 0, nextInputBytesSubmitted);

                if (nextInputBytesSubmitted == src.remaining()) {
                    allInputPresented = true;
                }
            }

            int bytesWritten;
            try {
                if (dstStreamBuf == null) {
                    //inflate directly into the dst buffer's array, then increment the dst buffer's position
                    bytesWritten = this.inflater.inflate(dst.array(), dst.arrayOffset() + dst.position(), dst.remaining());
                } else {
                    //inflate into dstStreamBuffer, then copy the decompressed bytes to the real dst buffer
                    bytesWritten = this.inflater.inflate(dstStreamBuf, 0, Math.min(dstStreamBuf.length, dst.remaining()));
                }
            } finally {
                //increment buffer positions in a finally block, so that they always get updated even in case of an exception

                int nextBytesReadTotal = this.inflater.getTotalIn();
                int nextBytesWrittenTotal = this.inflater.getTotalOut();

                //increment the src position
                src.position(src.position() + (nextBytesReadTotal - lastBytesReadTotal));

                if (dstStreamBuf == null) {
                    //increment the dst position
                    dst.position(dst.position() + (nextBytesWrittenTotal - lastBytesWrittenTotal));
                } else {
                    //copy the decompressed bytes to the real dst buffer
                    dst.put(dstStreamBuf, 0, nextBytesWrittenTotal - lastBytesWrittenTotal);
                }

                lastBytesReadTotal = nextBytesReadTotal;
                lastBytesWrittenTotal = nextBytesWrittenTotal;
            }

            if (this.inflater.finished() //if we've reached the end of the stream, we must be done
                    || (bytesWritten == 0 && !src.hasRemaining()) //there was no output produced and we can't supply any more input data to enable further progress
                    || !dst.hasRemaining()) { //there's no more output space, so no more progress is possible
                return;
            }
        }
    }
}
