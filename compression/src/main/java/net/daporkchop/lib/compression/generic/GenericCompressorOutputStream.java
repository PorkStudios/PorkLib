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

package net.daporkchop.lib.compression.generic;

import lombok.NonNull;
import lombok.val;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.context.PStreamingCompressor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Implementation of {@link OutputStream} which compresses data using a {@link PStreamingCompressor}.
 *
 * @author DaPorkchop_
 */
public class GenericCompressorOutputStream extends OutputStream {
    protected final @NonNull OutputStream out;
    protected final @NonNull PStreamingCompressor compressor;
    protected final @NonNull PStreamingCompressor.FlushMode flush;

    protected final ByteBuffer outputBuffer;

    protected boolean closed = false;

    public GenericCompressorOutputStream(@NonNull OutputStream out, @NonNull PStreamingCompressor compressor, @NonNull PStreamingCompressor.FlushMode flush) {
        this(out, compressor, flush, compressor.getRecommendedOutputBufferSize().orElse(PorkUtil.bufferSize()));
    }

    public GenericCompressorOutputStream(@NonNull OutputStream out, @NonNull PStreamingCompressor compressor, @NonNull PStreamingCompressor.FlushMode flush, @Positive int bufferSize) {
        PValidation.checkArg(flush != PStreamingCompressor.FlushMode.FINISH, flush);

        this.out = out;
        this.compressor = compressor;
        this.flush = flush;
        this.outputBuffer = PNioBuffers.allocateUninitializedHeapByte(PValidation.positive(bufferSize, "bufferSize"));
    }

    private byte[] singleByteArray;

    @Override
    public void write(int b) throws IOException {
        if (this.singleByteArray == null) {
            this.singleByteArray = PUnsafe.allocateUninitializedByteArray(1);
        }

        this.singleByteArray[0] = (byte) b;
        this.write(this.singleByteArray, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("write beyond end of stream");
        }

        ByteBuffer inputBuffer = ByteBuffer.wrap(b, off, len);

        boolean done;
        do {
            //compress input data into the write buffer
            this.outputBuffer.clear();
            done = this.compressor.compress(inputBuffer, this.outputBuffer, PStreamingCompressor.FlushMode.NO);

            //write the compressed data to the target output stream
            if (this.outputBuffer.position() > 0) {
                this.out.write(this.outputBuffer.array(), this.outputBuffer.arrayOffset(), this.outputBuffer.position());
            }
        } while (!done);

        assert !inputBuffer.hasRemaining() : inputBuffer;
    }

    @Override
    public void flush() throws IOException {
        if (!this.closed && this.flush != PStreamingCompressor.FlushMode.NO) {
            //this can never be FINISH, so it's safe to flush the compressor here
            this.flush(this.flush);
        }

        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;

            try (val ignored = this.out) {
                this.flush(PStreamingCompressor.FlushMode.FINISH);
            } finally {
                this.compressor.resetStream();
            }
        }
    }

    protected void flush(@NonNull PStreamingCompressor.FlushMode flush) throws IOException {
        boolean done;
        do {
            this.outputBuffer.clear();
            done = this.compressor.compress(PNioBuffers.emptyByteBufferHeap(), this.outputBuffer, flush);

            this.out.write(this.outputBuffer.array(), this.outputBuffer.arrayOffset(), this.outputBuffer.position());
        } while (!done);
    }
}
