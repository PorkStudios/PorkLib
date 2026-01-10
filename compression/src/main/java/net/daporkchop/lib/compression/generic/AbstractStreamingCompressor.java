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
import net.daporkchop.lib.compression.context.PStreamingCompressor;

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractStreamingCompressor extends AbstractStreamingContext implements PStreamingCompressor {
    // misc. shared state
    private FlushMode expectedFlushMode = null;

    @Override
    public void resetStream() {
        super.resetStream();
        this.expectedFlushMode = null;
    }

    protected final void validateFlushParameter(@NonNull FlushMode flush) throws IllegalArgumentException {
        if (this.expectedFlushMode == null) {
            if (flush == FlushMode.NO) {
                //this is fine
            } else {
                this.expectedFlushMode = flush;
            }
        } else if (this.expectedFlushMode != flush) {
            throw new IllegalArgumentException("expected flush=" + this.expectedFlushMode + " but got " + flush);
        }
    }

    protected final void handlePartialFlushComplete() {
        this.expectedFlushMode = null;
    }

    // IO stream wrappers

    protected final void ensureNotFinishMode(@NonNull FlushMode flush) throws IllegalArgumentException {
        if (flush == FlushMode.FINISH) {
            throw new IllegalArgumentException(String.valueOf(flush));
        }
    }

    @Override
    public OutputStream wrapCompressing(@NonNull OutputStream dst, @NonNull FlushMode flush) throws IllegalArgumentException {
        this.ensureNotFinishMode(flush);
        this.resetStream();
        return new GenericCompressorOutputStream(dst, this, flush);
    }

    @Override
    public WritableByteChannel wrapCompressing(@NonNull WritableByteChannel dst) {
        this.resetStream();
        return new GenericCompressorWritableByteChannel(dst, this);
    }
}
