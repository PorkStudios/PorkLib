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

package net.daporkchop.lib.compression.zstd;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * Compression context for {@link Zstd}.
 * <p>
 * Not thread-safe.
 *
 * @author DaPorkchop_
 */
public interface ZstdCCtx extends PDeflater {
    @Override
    ZstdProvider provider();

    /**
     * Compresses the given source data into the given destination buffer at the configured Zstd level.
     *
     * @see PDeflater#compress(ByteBuf, ByteBuf)
     */
    @Override
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        return this.compress(src, dst, this.level());
    }

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer at the given Zstd level.
     * <p>
     * This is possible because Zstd allows using the same context for any compression level without having to reallocate it.
     *
     * @see #compress(ByteBuf, ByteBuf)
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, int compressionLevel) throws InvalidBufferTypeException;

    /**
     * Compresses the given source data into the given destination buffer at the configured Zstd level using the given dictionary.
     *
     * @see PDeflater#compress(ByteBuf, ByteBuf, ByteBuf)
     */
    @Override
    default boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws InvalidBufferTypeException, DictionaryNotAllowedException {
        return this.compress(src, dst, dict, this.level());
    }

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer at the given Zstd level using the given dictionary.
     * <p>
     * This is possible because Zstd allows using the same context for any compression level without having to reallocate it.
     *
     * @see #compress(ByteBuf, ByteBuf, int)
     * @see #compress(ByteBuf, ByteBuf, ByteBuf)
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int compressionLevel) throws InvalidBufferTypeException;

    /**
     * Compresses the given source data into a single Zstd frame into the given destination buffer using the given dictionary.
     * <p>
     * As the dictionary has already been digested, this is far faster than the other dictionary compression methods.
     *
     * @param dictionary the dictionary to use
     * @see #compress(ByteBuf, ByteBuf)
     */
    boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, @NonNull ZstdCDict dictionary) throws InvalidBufferTypeException;

    @Override
    default boolean hasDict() {
        return true;
    }
}
