/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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
 *
 */

package net.daporkchop.lib.compression.context;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.compression.util.exception.ContextFinishedException;
import net.daporkchop.lib.compression.util.exception.ContextFinishingException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * Deflates (compresses) data.
 *
 * @author DaPorkchop_
 */
public interface PDeflater extends StreamingContext<PDeflater> {
    /**
     * Deflates the given source data into the given destination buffer.
     * <p>
     * Rather than expanding the destination buffer if needed, this method will simply abort compression if not enough space is available. In such a case the
     * reader/writer indices of both buffers will remain unaffected, however the contents of the destination buffer may be modified.
     * <p>
     * This method will implicitly reset the context before the actual compression. Any previous state will be ignored.
     *
     * @param src the {@link ByteBuf} to read data from
     * @param dst the {@link ByteBuf} to write data to
     * @return whether or not there was enough space in the destination buffer for the compressed data
     */
    boolean fullDeflate(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException;

    /**
     * Deflates the given source data into the given destination buffer.
     * <p>
     * This will continually grow the destination buffer until there is enough space for deflation to be finished successfully.
     * <p>
     * This method will implicitly reset the context before the actual compression. Any previous state will be ignored.
     *
     * @param src the {@link ByteBuf} to read data from
     * @param dst the {@link ByteBuf} to write data to
     */
    default void fullDeflateGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        this.reset().src(src).dst(dst);

        do {
            this.update(false);
        } while (src.isReadable() && dst.ensureWritable(8192).isWritable());

        while (!this.finish()) {
            dst.ensureWritable(8192);
        }
    }

    @Override
    PDeflater update(boolean flush) throws ContextFinishedException, ContextFinishingException;

    @Override
    boolean finish() throws ContextFinishedException;
}
