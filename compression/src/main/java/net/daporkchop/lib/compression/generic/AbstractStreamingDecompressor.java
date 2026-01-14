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
import net.daporkchop.lib.compression.context.PStreamingDecompressor;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractStreamingDecompressor extends AbstractStreamingContext implements PStreamingDecompressor {
    // misc. shared state
    private boolean expectedEof = false;

    // parameters
    protected boolean singleFrame = false;

    // misc. shared state

    @Override
    public void resetStream() {
        super.resetStream();
        this.expectedEof = false;
    }

    protected final void validateEofParameter(boolean eof) throws IllegalArgumentException {
        if (eof) {
            this.expectedEof = true;
        } else if (this.expectedEof) {
            throw new IllegalArgumentException("expected eof=true");
        }
    }

    // parameters

    @Override
    public void resetParameters() throws IllegalStateException {
        super.resetParameters();
        this.singleFrame = false;
    }

    @Override
    public final void setSingleFrame(boolean singleFrame) {
        this.ensureStreamInactive();
        this.singleFrame = singleFrame;
    }

    // IO stream wrappers

    protected final void ensureNotSingleFrame() {
        if (this.singleFrame) {
            throw new UnsupportedOperationException("stream wrappers are incompatible with parameter singleFrame=true");
        }
    }

    @Override
    public InputStream wrapDecompressing(@NonNull InputStream src) {
        this.ensureNotSingleFrame();
        this.resetStream();
        return new GenericDecompressorInputStream(src, this);
    }

    @Override
    public ReadableByteChannel wrapDecompressing(@NonNull ReadableByteChannel src) {
        this.ensureNotSingleFrame();
        this.resetStream();
        return new GenericDecompressorReadableByteChannel(src, this);
    }
}
