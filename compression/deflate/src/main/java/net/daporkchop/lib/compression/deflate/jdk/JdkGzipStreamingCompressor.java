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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.util.PNioBuffers;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * @author DaPorkchop_
 */
final class JdkGzipStreamingCompressor extends JdkDeflateStreamingCompressor {
    private static final int GZIP_MAGIC = 0x8B1F;

    private static final byte[] HEADER = {
            (byte) GZIP_MAGIC,
            (byte) (GZIP_MAGIC >> 8),
            Deflater.DEFLATED,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    private final CRC32 crc = new CRC32();

    private State state = State.DONE;

    private int writtenHeaderBytes;

    private final byte[] trailer = new byte[Integer.BYTES * 2];
    private int writtenTrailerBytes;

    JdkGzipStreamingCompressor() {
        super(true);
    }

    @Override
    public void resetStream() {
        super.resetStream();

        this.crc.reset();

        this.state = State.WRITE_HEADER;
        this.writtenHeaderBytes = 0;
        this.writtenTrailerBytes = 0;
    }

    @Override
    public OutputStream wrapCompressing(@NonNull OutputStream dst, @NonNull FlushMode flush) {
        throw new AbstractMethodError(); //TODO
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
    public boolean compress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, @NonNull FlushMode flush) throws ReadOnlyBufferException {
        int readBytes = 0;
        int writtenBytes = 0;

        super.lastReadBytes = 0L;
        super.lastWrittenBytes = 0L;

        try {
            if (this.state == State.DONE) {
                this.resetStream();
            }

            if (this.state == State.WRITE_HEADER) {
                int headerWriteCount = Math.min(HEADER.length - this.writtenHeaderBytes, dst.remaining());
                dst.put(HEADER, this.writtenHeaderBytes, headerWriteCount);
                this.writtenHeaderBytes += headerWriteCount;
                writtenBytes += headerWriteCount;

                if (this.writtenHeaderBytes == HEADER.length) {
                    this.state = State.COMPRESS;
                } else {
                    return false;
                }
            }

            if (this.state == State.COMPRESS) {
                if (flush == FlushMode.FULL) {
                    flush = FlushMode.FINISH;
                }

                boolean result = super.compress(src, dst, flush);

                if (result && (flush == FlushMode.FULL || flush == FlushMode.FINISH)) {
                    this.prepareTrailer();
                    this.state = State.WRITE_TRAILER;
                } else {
                    return result;
                }
            }

            if (this.state == State.WRITE_TRAILER) {
                int trailerWriteCount = Math.min(this.trailer.length - this.writtenTrailerBytes, dst.remaining());
                dst.put(this.trailer, this.writtenTrailerBytes, trailerWriteCount);
                this.writtenTrailerBytes += trailerWriteCount;
                writtenBytes += trailerWriteCount;

                if (this.writtenTrailerBytes == this.trailer.length) {
                    this.state = State.DONE;
                    return true;
                } else {
                    return false;
                }
            }

            throw new IllegalStateException(String.valueOf(this.state));
        } finally {
            super.lastReadBytes += readBytes;
            super.lastWrittenBytes += writtenBytes;
        }
    }

    @Override
    protected void onInputConsumed(byte[] src, int offset, int length) {
        this.crc.update(src, offset, length);
    }

    @Override
    protected void onInputConsumed(ByteBuffer src, int offset, int length) {
        this.crc.update(PNioBuffers.duplicateRange(src, offset, length));
    }

    private void prepareTrailer() {
        ByteBuffer.wrap(this.trailer).order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) this.crc.getValue())
                .putInt(super.deflater.getTotalIn());
    }

    private enum State {
        WRITE_HEADER,
        COMPRESS,
        WRITE_TRAILER,
        DONE,
    }
}
