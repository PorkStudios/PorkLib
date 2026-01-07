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
import net.daporkchop.lib.common.util.PThrowables;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.context.PStreamingDecompressor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;

/**
 * Implementation of {@link InputStream} which decompresses data using a {@link PStreamingDecompressor}.
 *
 * @author DaPorkchop_
 */
public class GenericDecompressorInputStream extends InputStream {
    protected final @NonNull InputStream in;
    protected final @NonNull PStreamingDecompressor decompressor;

    protected final ByteBuffer inputBuffer;

    protected boolean reachedEOF = false; //true iff. 'in' has reached EOF
    protected boolean finished = false; //true iff. a call to 'decompressor.decompress()' has returned true
    protected boolean closed = false;

    public GenericDecompressorInputStream(@NonNull InputStream in, @NonNull PStreamingDecompressor decompressor) {
        this(in, decompressor, decompressor.getRecommendedOutputBufferSize().orElse(PorkUtil.bufferSize()));
    }

    public GenericDecompressorInputStream(@NonNull InputStream in, @NonNull PStreamingDecompressor decompressor, @Positive int bufferSize) {
        this.in = in;
        this.decompressor = decompressor;
        this.inputBuffer = PNioBuffers.allocateUninitializedHeapByte(PValidation.positive(bufferSize, "bufferSize"));
        this.inputBuffer.clear().limit(0);
    }

    private byte[] singleByteArray;

    @Override
    public int read() throws IOException {
        if (this.singleByteArray == null) {
            this.singleByteArray = PUnsafe.allocateUninitializedByteArray(1);
        }

        int count = this.read(this.singleByteArray, 0, 1);
        if (count < 0) {
            //reached EOF
            return count;
        }

        assert count == 1 : count;
        return Byte.toUnsignedInt(this.singleByteArray[0]);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ByteBuffer outputBuffer = ByteBuffer.wrap(b, off, len);

        if (!outputBuffer.hasRemaining()) {
            //nothing to read
            return 0;
        } else if (this.finished) {
            //already reached the end of the decompressed stream
            return -1;
        }

        do {
            try {
                this.finished = this.decompressor.decompress(this.inputBuffer, outputBuffer, this.reachedEOF);
            } catch (DataFormatException caught) {
                throw PThrowables.initCause(new ZipException(), caught);
            }

            //TODO: decide what to do with remaining input data, if any
            /*if (this.finished && this.inputBuffer.hasRemaining()) {
                //handle remaining input data...
            }*/

            if (outputBuffer.position() > 0) {
                //some output was generated, return that
                return outputBuffer.position();
            } else if (this.finished) {
                //we have reached the end of the stream! no output was generated, so we'll return -1.
                return -1;
            } else if (!this.inputBuffer.hasRemaining()) {
                //we need more input data
                this.fillInputBuffer();
            } else {
                throw new AssertionError("unreachable");
            }
        } while (true);
    }

    @Override
    public int available() {
        return this.finished ? 0 : 1;
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;

            try (val ignored = this.in) {
                this.decompressor.resetStream();
            }
        }
    }

    private void fillInputBuffer() throws IOException {
        assert !this.reachedEOF : "already reached EOF!";
        assert !this.inputBuffer.hasRemaining() : this.inputBuffer;

        int count = this.in.read(this.inputBuffer.array(), this.inputBuffer.arrayOffset(), this.inputBuffer.capacity());
        if (count < 0) {
            this.reachedEOF = true;
            return;
        }

        this.inputBuffer.clear().limit(count);
        assert this.inputBuffer.hasRemaining() : this.inputBuffer;
    }
}
