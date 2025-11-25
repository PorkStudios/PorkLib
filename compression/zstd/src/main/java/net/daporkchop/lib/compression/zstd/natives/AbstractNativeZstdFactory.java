/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.annotation.Borrow;
import net.daporkchop.lib.compression.zstd.ZstdCompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdProviderCapabilities;
import net.daporkchop.lib.compression.zstd.ZstdOneshotFactory;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
abstract class AbstractNativeZstdFactory implements ZstdOneshotFactory {
    final @NonNull NativeZstdFunctions functions;

    @Override
    public final ZstdProviderCapabilities capabilities() {
        return AbstractNativeZstdProvider.class.getAnnotation(ZstdProviderCapabilities.class);
    }

    @Override
    public ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuf dict, int level) throws IllegalArgumentException {
        //just delegate to the ByteBuffer version, any performance hit is negligible and i don't care
        return this.makeCompressionDictionary(dict.nioBuffer(), level);
    }

    @Override
    public ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuf dict) {
        //just delegate to the ByteBuffer version, any performance hit is negligible and i don't care
        return this.makeDecompressionDictionary(dict.nioBuffer());
    }
}
