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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.deflate.DeflateStreamingCompressor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.zip.DeflaterOutputStream;

/**
 * @author DaPorkchop_
 */
class JdkDeflateStreamingCompressor extends AbstractJdkDeflateCompressContext implements DeflateStreamingCompressor {
    boolean ongoingStream = false;

    FlushMode expectedNextFlushMode = null;

    long lastReadBytes = 0L;
    long lastWrittenBytes = 0L;

    JdkDeflateStreamingCompressor(boolean noWrap) {
        super(noWrap);
    }

    @Override
    public void resetStream() {
        this.deflater.reset();

        this.ongoingStream = false;
        this.expectedNextFlushMode = null;
    }

    @Override
    public OutputStream wrapCompressing(@NonNull OutputStream dst, @NonNull FlushMode flush) {
        this.resetStream();

        switch (flush) {
            case NO:
                return new DeflaterOutputStream(dst, this.deflater, false);
            case SYNC:
                return new DeflaterOutputStream(dst, this.deflater, true);
            case FULL:
            case FINISH:
                //TODO
            default:
                throw new IllegalArgumentException(String.valueOf(flush));
        }
    }

    @Override
    public GatheringByteChannel wrapCompressing(@NonNull WritableByteChannel dst) {
        throw new AbstractMethodError(); //TODO
    }

    @Override
    public DataOut wrapCompressing(@NonNull DataOut dst, @NonNull FlushMode flush) {
        throw new AbstractMethodError(); //TODO
    }

    @Override
    public final @NotNegative long getLastReadBytes() {
        return this.lastReadBytes;
    }

    @Override
    public final @NotNegative long getLastWrittenBytes() {
        return this.lastWrittenBytes;
    }

    @Override
    public boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException {
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        if (!this.ongoingStream) {
            this.resetStream();
        } else if (this.expectedNextFlushMode != null && this.expectedNextFlushMode != flush) {
            throw new IllegalArgumentException("expected " + this.expectedNextFlushMode + " but got " + flush);
        }

        try {
            val initialBytesRead = this.deflater.getBytesRead();
            val initialBytesWritten = this.deflater.getBytesWritten();

            if (this.expectedNextFlushMode == null && flush == FlushMode.FINISH) {
                this.deflater.finish();
            }

            if (JdkDeflateUtils.supportsByteBufferMethods()) {
                //use the new ByteBuffer methods directly
                val initialSrcPosition = src.position();

                JdkDeflateUtils.setInput(this.deflater, src);
                JdkDeflateUtils.deflate(this.deflater, dst, JdkDeflateUtils.flushModeToJdk(flush));

                //notify input callback
                this.onInputConsumed(src, initialSrcPosition, src.position() - initialSrcPosition);
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
                    written = this.deflater.deflate(dst.array(), dst.arrayOffset() + dst.position(), dst.remaining(), JdkDeflateUtils.flushModeToJdk(flush));
                    dst.position(dst.position() + written);
                } else {
                    //dst is a direct buffer, compress into a temporary array and then copy the actually written bytes to dst (slow!)
                    //TODO: we could do streaming compression here?
                    byte[] dstTmpArray = PUnsafe.allocateUninitializedByteArray(dst.remaining());
                    written = this.deflater.deflate(dstTmpArray, 0, dstTmpArray.length, JdkDeflateUtils.flushModeToJdk(flush));
                    dst.put(dstTmpArray, 0, written);
                }

                //notify input callback and increment src position
                int read = Math.toIntExact(this.deflater.getBytesRead() - initialBytesRead);
                this.onInputConsumed(src, src.position(), read);
                src.position(src.position() + read);
            }

            boolean result;
            FlushMode nextFlushMode;
            switch (flush) {
                case NO:
                    result = this.deflater.needsInput();
                    nextFlushMode = null;
                    break;
                case SYNC:
                case FULL:
                    result = this.deflater.needsInput();
                    nextFlushMode = result ? null : flush;
                    break;
                case FINISH:
                    result = this.deflater.finished();
                    if (result) {
                        nextFlushMode = null;
                        //the stream is finished, mark it as such
                        this.ongoingStream = false;
                    } else {
                        nextFlushMode = flush;
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.valueOf(flush));
            }

            this.expectedNextFlushMode = nextFlushMode;

            this.lastReadBytes = this.deflater.getBytesRead() - initialBytesRead;
            this.lastWrittenBytes = this.deflater.getBytesWritten() - initialBytesWritten;
            return result;
        } finally {
            //ensure that the input array can be GCd
            this.deflater.setInput(PorkUtil.emptyByteArray());
        }
    }

    protected void onInputConsumed(byte[] src, int offset, int length) {
        //no-op, used by gzip compression
    }

    protected void onInputConsumed(ByteBuffer src, int offset, int length) {
        //no-op, used by gzip compression
    }
}
