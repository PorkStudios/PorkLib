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

import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.context.StreamingContext;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractStreamingContext implements StreamingContext {
    protected static final byte STATE_RESET = 0;

    protected byte state = STATE_RESET; //implementations don't have to use this field, but it's here for convenience for ones that are backed by a state machine

    private @NotNegative long lastReadBytes;
    private @NotNegative long lastWrittenBytes;

    @Override
    public final @NotNegative long getLastReadBytes() {
        return this.lastReadBytes;
    }

    @Override
    public final @NotNegative long getLastWrittenBytes() {
        return this.lastWrittenBytes;
    }

    protected final void setLastReadWrittenBytes(@NotNegative long lastReadBytes, @NotNegative long lastWrittenBytes) {
        this.lastReadBytes = lastReadBytes;
        this.lastWrittenBytes = lastWrittenBytes;
    }

    protected final void addLastReadWrittenBytes(@NotNegative long lastReadBytes, @NotNegative long lastWrittenBytes) {
        this.lastReadBytes += lastReadBytes;
        this.lastWrittenBytes += lastWrittenBytes;
    }

    @Override
    public void resetStream() {
        this.state = STATE_RESET;
    }

    protected final void ensureStreamInactive() throws IllegalStateException {
        if (this.isStreamOngoing()) {
            throw new IllegalStateException("streaming in progress");
        }
    }

    /**
     * @return {@code true} if this context is currently processing an incomplete [de]compression stream (i.e. has not finished or been manually reset)
     */
    protected boolean isStreamOngoing() {
        return this.state != STATE_RESET;
    }

    @Override
    public void resetParameters() throws IllegalStateException {
        this.ensureStreamInactive();
        //no-op
    }
}
