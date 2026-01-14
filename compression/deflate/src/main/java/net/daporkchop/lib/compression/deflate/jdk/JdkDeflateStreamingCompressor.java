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
import net.daporkchop.lib.compression.deflate.DeflateStreamingCompressor;
import net.daporkchop.lib.compression.generic.AbstractStreamingCompressor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * @author DaPorkchop_
 */
final class JdkDeflateStreamingCompressor extends AbstractStreamingCompressor implements DeflateStreamingCompressor, JdkDeflateContext {
    private static final byte STATE_RESET = 0;
    private static final byte STATE_COMPRESS_NORMAL = 1;
    private static final byte STATE_COMPRESS_FINISHING = 2;

    final Deflater deflater;

    private byte state = STATE_RESET;

    JdkDeflateStreamingCompressor(boolean noWrap) {
        this.deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, noWrap);
    }

    @Override
    public void close() {
        this.deflater.end();
    }

    @Override
    public void resetStream() {
        super.resetStream();

        this.deflater.reset();
        this.state = STATE_RESET;
    }

    @Override
    protected boolean isStreamOngoing() {
        return this.state != STATE_RESET;
    }

    @Override
    public void resetParameters() throws IllegalStateException {
        super.resetParameters();
        this.deflater.setLevel(Deflater.DEFAULT_COMPRESSION);
    }

    @Override
    public void setLevel(int level) throws IllegalArgumentException {
        this.ensureStreamInactive();
        this.deflater.setLevel(level);
    }

    @Override
    public OutputStream wrapCompressing(@NonNull OutputStream dst, @NonNull FlushMode flush) throws IllegalArgumentException {
        switch (flush) {
            case NO:
            case SYNC:
                this.resetStream();
                return new DeflaterOutputStream(dst, this.deflater, flush == FlushMode.SYNC);
            default:
                return super.wrapCompressing(dst, flush);
        }
    }

    @Override
    public boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException {
        this.setLastReadWrittenBytes(0, 0);

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        this.validateFlushParameter(flush);

        if (this.state == STATE_RESET) {
            this.state = STATE_COMPRESS_NORMAL;
        }

        final int jdkFlush;
        switch (flush) {
            case NO:
                jdkFlush = Deflater.NO_FLUSH;
                break;
            case SYNC:
                jdkFlush = Deflater.SYNC_FLUSH;
                break;
            case FULL:
                jdkFlush = Deflater.FULL_FLUSH;
                break;
            case FINISH:
                jdkFlush = Deflater.NO_FLUSH;

                if (this.state == STATE_COMPRESS_NORMAL) {
                    //state transition so that we don't call Deflater#finish() multiple times
                    this.state = STATE_COMPRESS_FINISHING;
                    this.deflater.finish();
                }
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(flush));
        }

        val initialBytesRead = this.deflater.getBytesRead();
        val initialBytesWritten = this.deflater.getBytesWritten();

        final boolean deflaterNeedsInput;
        try {
            //sanity check, i haven't implemented a workaround for this because it's such an obscure edge case that i can't imagine it would ever be relevant
            //see https://stackoverflow.com/questions/31861983/deflater-deflate-and-small-output-buffers and DeflaterOutputStream#flush()
            if ((jdkFlush == Deflater.SYNC_FLUSH || jdkFlush == Deflater.FULL_FLUSH) && dst.remaining() <= 6) {
                throw new AssertionError("SYNC_FLUSH and FULL_FLUSH require more than 6 bytes of output space!");
            }

            if (JdkDeflateUtils.supportsByteBufferMethods()) {
                //use the new ByteBuffer methods directly
                JdkDeflateUtils.setInput(this.deflater, src);
                JdkDeflateUtils.deflate(this.deflater, dst, jdkFlush);
            } else {
                //use the old byte[] methods (this may compress in smaller blocks to avoid allocating massive temporary arrays)
                //this will automatically increment src and dst buffer positions
                this.deflateArray(src, dst, jdkFlush);
            }

            //check Deflater#needsInput() before resetting the input array, as otherwise it would always return true
            deflaterNeedsInput = this.deflater.needsInput();
        } finally {
            //ensure that the input array can be GCd
            this.deflater.setInput(PorkUtil.emptyByteArray());

            //set last read/written count (no need to increment, both fields are already set to zero)
            this.setLastReadWrittenBytes(
                    this.deflater.getBytesRead() - initialBytesRead,
                    this.deflater.getBytesWritten() - initialBytesWritten);
        }

        switch (flush) {
            case NO:
            case SYNC:
            case FULL:
                //we can't rely exclusively on Deflater#needsInput() to check if all the output has been flushed according to the current
                //flush mode, as the deflater could have moved all the remaining input into an internal buffer without writing it out yet.
                //to circumvent this, also require that there is some remaining output space before returning true. this could theoretically
                //force the user to make one additional call to compress() in case the flushed data just happens to perfectly fit the
                //provided output buffer size, but that's probably pretty unlikely and shouldn't cause much of a performance hit even if it
                //does occur.
                if (deflaterNeedsInput && dst.hasRemaining()) {
                    this.handlePartialFlushComplete();
                    return true;
                } else {
                    //wait for more output space
                    return false;
                }
            case FINISH:
                if (this.deflater.finished()) {
                    //compression is done, we can reset the stream now :)
                    this.resetStream();
                    return true;
                } else {
                    //wait for more output space
                    return false;
                }
            default:
                throw new IllegalArgumentException(String.valueOf(flush));
        }
    }

    private void deflateArray(ByteBuffer src, ByteBuffer dst, int flush) {
        if (src.hasArray()) {
            this.deflater.setInput(src.array(), src.arrayOffset() + src.position(), src.remaining());
        } else {
            //src is a direct or read-only buffer, copy to a temporary array (slow!)
            //TODO: we could do streaming compression here? it will be difficult to have this interoperate correctly with flush parameters, though...
            this.deflater.setInput(src.hasRemaining() ? PNioBuffers.toArray(src) : PorkUtil.emptyByteArray());
        }

        final int STREAM_BUF_SIZE = 8192;

        final byte[] dstStreamBuf;
        if (dst.hasArray()) {
            dstStreamBuf = null;
        } else {
            dstStreamBuf = PUnsafe.allocateUninitializedByteArray(Math.min(dst.remaining(), STREAM_BUF_SIZE));
        }

        int prevBytesReadTotal = this.deflater.getTotalIn();
        while (true) {
            int bytesWritten;
            if (dst.hasArray()) {
                //deflate directly into the dst buffer's array, then increment the dst buffer's position
                bytesWritten = this.deflater.deflate(dst.array(), dst.arrayOffset() + dst.position(), dst.remaining(), flush);
                dst.position(dst.position() + bytesWritten);
            } else {
                //deflate into dstStreamBuffer, then copy the written bytes to the real dst buffer
                bytesWritten = this.deflater.deflate(dstStreamBuf, 0, Math.min(dstStreamBuf.length, dst.remaining()), flush);
                dst.put(dstStreamBuf, 0, bytesWritten);
            }

            //increment the src position by the number of bytes that were consumed
            int nextBytesReadTotal = this.deflater.getTotalIn();
            src.position(src.position() + (nextBytesReadTotal - prevBytesReadTotal));
            prevBytesReadTotal = nextBytesReadTotal;

            if (this.deflater.finished() //if we've reached the end of the stream, we must be done
                    || bytesWritten == 0 //there was no output produced and we can't supply any more input data to enable further progress
                    || !dst.hasRemaining()) { //there's no more output space, so no more progress is possible
                return;
            }
        }
    }
}
