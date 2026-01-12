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
import net.daporkchop.lib.compression.deflate.DeflateStreamingCompressor;
import net.daporkchop.lib.compression.generic.AbstractStreamingCompressor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;

/**
 * @author DaPorkchop_
 */
final class JdkGzipStreamingCompressor extends AbstractStreamingCompressor implements DeflateStreamingCompressor, JdkDeflateContext {
    private static final byte STATE_RESET = 0;
    private static final byte STATE_WRITE_HEADER = 1;
    private static final byte STATE_COMPRESS = 2;
    private static final byte STATE_WRITE_TRAILER = 3;

    private static final byte[] HEADER = {
            (byte) GZIPInputStream.GZIP_MAGIC,
            (byte) (GZIPInputStream.GZIP_MAGIC >> 8),
            Deflater.DEFLATED,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    private final JdkDeflateStreamingCompressor deflater;
    private final CRC32 crc = new CRC32();

    private byte state = STATE_RESET;

    private byte[] rawBytes;
    private int rawBytesIndex;

    //not re-using 'this.deflater.deflater.getTotalIn()' because 'this.deflater' gets reset before this class begins writing the trailer
    private int totalInputBytesDeflated;

    //temporary array for re-use
    private final byte[] trailer = new byte[Integer.BYTES * 2];

    JdkGzipStreamingCompressor() {
        this.deflater = new JdkDeflateStreamingCompressor(true);
    }

    @Override
    public void close() {
        this.deflater.close();
    }

    @Override
    public void resetStream() {
        super.resetStream();

        this.deflater.resetStream();
        this.crc.reset();
        this.totalInputBytesDeflated = 0;
        this.state = STATE_RESET;
    }

    @Override
    protected boolean isStreamOngoing() {
        return this.state != STATE_RESET;
    }

    @Override
    public void resetParameters() throws IllegalStateException {
        super.resetParameters();
        this.deflater.resetParameters();
    }

    @Override
    public void setLevel(int level) throws IllegalArgumentException {
        this.ensureStreamInactive();
        this.deflater.setLevel(level);
    }

    @Override
    public boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException {
        this.setLastReadWrittenBytes(0, 0);

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        this.validateFlushParameter(flush);

        switch (this.state) {
            case STATE_RESET:
                this.state = STATE_WRITE_HEADER;
                this.rawBytesBegin(HEADER);

                //fallthrough
            case STATE_WRITE_HEADER:
                if (!this.rawBytesStep(dst)) {
                    //wait for more output space
                    return false;
                }

                this.state = STATE_COMPRESS;

                //fallthrough
            case STATE_COMPRESS:

                //implementation note:
                // both FULL and FINISH modes are treated equivalently. in either mode, the current GZIP member is finished by first finishing the
                // underlying DEFLATE stream, then appending the GZIP member trailer. subsequent compression calls will cause a new GZIP member to
                // be started.
                // the reason for not simply passing FULL mode through to the underlying DEFLATE stream is that it would make FULL mode effectively
                // useless, as that would not be enough to guarantee that compression can be resumed if the previous data is corrupted. separate GZIP
                // members are needed between FULL flushes to ensure that the GZIP checksums do not cover corrupt data.
                //TODO: i should probably double-check the actual zlib library to see how it handles the above case.

                boolean done;
                try {
                    done = this.deflater.compress(src, dst, flush == FlushMode.FULL ? FlushMode.FINISH : flush);
                } finally {
                    //increment last read/written bytes
                    int lastReadBytes = Math.toIntExact(this.deflater.getLastReadBytes());
                    this.addLastReadWrittenBytes(lastReadBytes, this.deflater.getLastWrittenBytes());
                    this.totalInputBytesDeflated += lastReadBytes;

                    //update checksum with the input bytes which have actually been read
                    this.crc.update(PNioBuffers.duplicateRange(src, src.position() - lastReadBytes, lastReadBytes));
                }

                if (!done) {
                    //wait for more input/output space
                    return false;
                } else if (flush != FlushMode.FULL && flush != FlushMode.FINISH) {
                    //all pending output data has been flushed, but we aren't terminating the GZIP member here so just return
                    //true and keep going
                    this.handlePartialFlushComplete();
                    return true;
                } else {
                    assert flush == FlushMode.FULL || flush == FlushMode.FINISH : flush;

                    //the DEFLATE stream has been finished, we can now terminate the GZIP member by appending the trailer
                    this.state = STATE_WRITE_TRAILER;
                    this.rawBytesBegin(this.prepareTrailer());
                }

                //fallthrough
            case STATE_WRITE_TRAILER:
                if (!this.rawBytesStep(dst)) {
                    //wait for more output space
                    return false;
                }

                //compression is done, we can reset the stream now :)
                this.resetStream();
                return true;
            default:
                throw new IllegalStateException(String.valueOf(this.state));
        }
    }

    private void rawBytesBegin(byte[] rawBytes) {
        this.rawBytes = rawBytes;
        this.rawBytesIndex = 0;
    }

    private boolean rawBytesStep(ByteBuffer dst) {
        val count = Math.min(this.rawBytes.length - this.rawBytesIndex, dst.remaining());
        if (count > 0) {
            dst.put(this.rawBytes, this.rawBytesIndex, count);
            this.rawBytesIndex += count;
        }

        return this.rawBytesIndex == this.rawBytes.length;
    }

    private byte[] prepareTrailer() {
        ByteBuffer.wrap(this.trailer).order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) this.crc.getValue())
                .putInt(this.totalInputBytesDeflated);
        return this.trailer;
    }
}
