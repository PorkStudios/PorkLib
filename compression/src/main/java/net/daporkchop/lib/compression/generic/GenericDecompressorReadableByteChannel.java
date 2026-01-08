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
import net.daporkchop.lib.common.closeable.PResourceUtil;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.common.util.PThrowables;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.context.PStreamingDecompressor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;

/**
 * Implementation of {@link ReadableByteChannel} which decompresses data using a {@link PStreamingDecompressor}.
 *
 * @author DaPorkchop_
 */
public class GenericDecompressorReadableByteChannel extends AbstractNonInterruptibleSynchronizedChannel implements ReadableByteChannel {
    protected final @NonNull ReadableByteChannel in;
    protected final @NonNull PStreamingDecompressor decompressor;

    protected final ByteBuffer inputBuffer;

    protected boolean eof = false; //true iff. 'in' has reached EOF
    protected boolean finished = false; //true iff. a call to 'decompressor.decompress()' has returned true

    public GenericDecompressorReadableByteChannel(@NonNull ReadableByteChannel in, @NonNull PStreamingDecompressor decompressor) {
        this(in, decompressor, decompressor.getRecommendedOutputBufferSize().orElse(PorkUtil.bufferSize()));
    }

    public GenericDecompressorReadableByteChannel(@NonNull ReadableByteChannel in, @NonNull PStreamingDecompressor decompressor, @Positive int bufferSize) {
        this.in = in;
        this.decompressor = decompressor;
        this.inputBuffer = PNioBuffers.allocateUninitializedHeapByte(PValidation.positive(bufferSize, "bufferSize"));
        this.inputBuffer.clear().limit(0);
    }

    @Override
    public synchronized int read(ByteBuffer outputBuffer) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }

        if (this.finished) {
            //already reached the end of the decompressed stream
            return -1;
        }

        val initialOutputBufferPosition = outputBuffer.position();
        while (true) {
            try {
                this.finished = this.decompressor.decompress(this.inputBuffer, outputBuffer, this.eof);
            } catch (DataFormatException caught) {
                //close this channel, as we can't really make any progress from here
                throw PResourceUtil.closeSuppressed(PThrowables.initCause(new ZipException(), caught), this);
            }

            if (this.finished) {
                //we've decompressed everything, no more output will be generated!
                break;
            } else if (!outputBuffer.hasRemaining()) {
                //there's no space left for any more output data, we'll stop here for now
                break;
            } else if (!this.inputBuffer.hasRemaining()) {
                //read some more input data
                try {
                    this.inputBuffer.clear();
                    int bytesIn = this.in.read(this.inputBuffer);
                    this.inputBuffer.flip();

                    if (bytesIn < 0) {
                        //we've reached EOF, loop back to the top to notify the decompressor that there will not be any more input
                        this.eof = true;
                    } else if (bytesIn == 0) {
                        //there is no more input data available right now, and the decompressor should have written everything it can to the output buffer,
                        //so we'll stop here for now
                        break;
                    } else {
                        //we successfully read some input, loop back to the top and try to decompress further
                    }
                } catch (IOException caught) {
                    //close this channel, as we can't really make any progress from here
                    throw PResourceUtil.closeSuppressed(caught, this);
                }
            }
        }

        return outputBuffer.position() - initialOutputBufferPosition;
    }

    @Override
    protected void implCloseChannel() throws IOException {
        try (val ignored = this.in) {
            this.decompressor.resetStream();
        }
    }
}
