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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.compression.context.POneshotDecompressor;
import net.daporkchop.lib.compression.context.PStreamingDecompressor;
import net.daporkchop.lib.natives.util.MemoryPreference;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.zip.DataFormatException;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public abstract class GenericStreamingAsOneshotDecompressor<StreamingDecompressor extends PStreamingDecompressor> implements POneshotDecompressor {
    protected final @NonNull StreamingDecompressor decompressor;

    @Override
    public void close() {
        this.decompressor.close();
    }

    @Override
    public final MemoryPreference memoryPreference() {
        return this.decompressor.memoryPreference();
    }

    @Override
    public void resetParameters() {
        this.decompressor.resetParameters();
    }

    @Override
    public void setSingleFrame(boolean singleFrame) {
        this.decompressor.setSingleFrame(singleFrame);
    }

    @Override
    public int decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst) throws DataFormatException, ReadOnlyBufferException {
        boolean done;
        try {
            done = this.decompressor.decompress(src.duplicate(), dst.duplicate(), true);
        } finally {
            this.decompressor.resetStream();
        }

        if (done) {
            //the decompressed data fits in the output buffer, return the decompressed size
            int readBytes = Math.toIntExact(this.decompressor.getLastReadBytes());
            int writtenBytes = Math.toIntExact(this.decompressor.getLastWrittenBytes());
            src.position(src.position() + readBytes);
            dst.position(dst.position() + writtenBytes);
            return writtenBytes;
        } else {
            //not enough output space, reset the buffer indices to their initial values
            return -1;
        }
    }
}
