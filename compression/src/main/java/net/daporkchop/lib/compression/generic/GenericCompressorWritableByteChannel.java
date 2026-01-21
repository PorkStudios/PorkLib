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
import net.daporkchop.lib.binary.nio.channel.AbstractNonInterruptibleSynchronizedChannel;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.context.PStreamingCompressor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Implementation of {@link OutputStream} which compresses data using a {@link PStreamingCompressor}.
 *
 * @author DaPorkchop_
 */
public class GenericCompressorWritableByteChannel extends AbstractNonInterruptibleSynchronizedChannel implements WritableByteChannel {
    protected final @NonNull WritableByteChannel out;
    protected final @NonNull PStreamingCompressor compressor;

    protected final ByteBuffer outputBuffer;

    public GenericCompressorWritableByteChannel(@NonNull WritableByteChannel out, @NonNull PStreamingCompressor compressor) {
        this(out, compressor, compressor.getRecommendedOutputBufferSize().orElse(PorkUtil.bufferSize()));
    }

    public GenericCompressorWritableByteChannel(@NonNull WritableByteChannel out, @NonNull PStreamingCompressor compressor, @Positive int bufferSize) {
        this.out = out;
        this.compressor = compressor;

        //TODO: this might actually benefit from using direct buffers, but we should probably only do that if there's a mechanism to free them explicitly
        this.outputBuffer = PNioBuffers.allocateUninitializedHeapByte(PValidation.positive(bufferSize, "bufferSize"));
        this.outputBuffer.limit(0);
    }

    @Override
    public synchronized int write(ByteBuffer inputBuffer) throws IOException {
        this.requireOpen();

        //try to flush any remaining output which might be left over from previous write calls
        if (this.outputBuffer.hasRemaining() && this.outputBuffer.remaining() != this.out.write(this.outputBuffer)) {
            //not all of the buffered output could be written out, stop early
            return 0;
        }

        val initialInputBufferPosition = inputBuffer.position();
        boolean done;
        do {
            //compress input data into the write buffer
            this.outputBuffer.clear();
            done = this.compressor.compress(inputBuffer, this.outputBuffer, PStreamingCompressor.FlushMode.NO);
            this.outputBuffer.flip();

            //write the compressed data to the destination channel
            if (this.outputBuffer.hasRemaining() && this.outputBuffer.remaining() != this.out.write(this.outputBuffer)) {
                //not all of the buffered output could be written, keep it buffered for now and exit
                break;
            }
        } while (!done);

        return inputBuffer.position() - initialInputBufferPosition;
    }

    @Override
    protected void implCloseChannel() throws IOException {
        try (val ignored = this.out) {
            //flush all buffered output
            while (this.outputBuffer.hasRemaining()) {
                this.out.write(this.outputBuffer);
            }

            //keep calling compress(FINISH) and flushing all buffered output until the compressor is finished and everything has been written
            boolean done;
            do {
                this.outputBuffer.clear();
                done = this.compressor.compress(PNioBuffers.emptyByteBufferHeap(), this.outputBuffer, PStreamingCompressor.FlushMode.FINISH);
                this.outputBuffer.flip();

                while (this.outputBuffer.hasRemaining()) {
                    this.out.write(this.outputBuffer);
                }
            } while (!done);
        } finally {
            this.compressor.resetStream();
        }
    }
}
