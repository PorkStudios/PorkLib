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
        try {
            //TODO: SYNC_FLUSH/FULL_FLUSH modes require a minimum of 6 bytes of output space, we should probably add a temporary small buffer in case the output buffer
            //      is very small... see DeflaterOutputStream#flush()
            if (JdkDeflateUtils.supportsByteBufferMethods()) {
                //use the new ByteBuffer methods directly
                JdkDeflateUtils.setInput(this.deflater, src);
                JdkDeflateUtils.deflate(this.deflater, dst, jdkFlush);
            } else {
                if (src.hasArray()) {
                    this.deflater.setInput(src.array(), src.arrayOffset() + src.position(), src.remaining());
                } else {
                    //src is a direct or read-only buffer, copy to a temporary array (slow!)
                    //TODO: we could do streaming compression here?
                    this.deflater.setInput(PNioBuffers.toArray(src));
                }

                int written;
                if (dst.hasArray()) {
                    written = this.deflater.deflate(dst.array(), dst.arrayOffset() + dst.position(), dst.remaining(), jdkFlush);
                    dst.position(dst.position() + written);
                } else {
                    //dst is a direct buffer, compress into a temporary array and then copy the actually written bytes to dst (slow!)
                    //TODO: we could do streaming compression here?
                    byte[] dstTmpArray = PUnsafe.allocateUninitializedByteArray(dst.remaining());
                    written = this.deflater.deflate(dstTmpArray, 0, dstTmpArray.length, jdkFlush);
                    dst.put(dstTmpArray, 0, written);
                }

                //increment src position
                // Unlike in the decompressor, here's no need for any exception handling here, since Deflater#deflate() shouldn't be able
                // to throw an exception (assuming we're using it correctly) and therefore this code will always execute
                src.position(src.position() + Math.toIntExact(this.deflater.getBytesRead() - initialBytesRead));
            }
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
                if (this.deflater.needsInput() && dst.hasRemaining()) {
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
}
