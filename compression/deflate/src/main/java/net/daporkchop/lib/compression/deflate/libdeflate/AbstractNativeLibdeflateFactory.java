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

package net.daporkchop.lib.compression.deflate.libdeflate;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotCompressor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotDecompressor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotFactory;
import net.daporkchop.lib.compression.deflate.DeflateProviderCapabilities;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
abstract class AbstractNativeLibdeflateFactory implements DeflateOneshotFactory {
    final @NonNull NativeLibdeflateFunctions functions;

    @Override
    public final DeflateProviderCapabilities capabilities() {
        return AbstractNativeLibdeflateProvider.class.getAnnotation(DeflateProviderCapabilities.class);
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotDeflateCompressor() {
        return this.makeCompressor(NativeLibdeflateFunctions.MODE_DEFLATE);
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotDeflateDecompressor() {
        return this.makeDecompressor(NativeLibdeflateFunctions.MODE_DEFLATE);
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotGzipCompressor() {
        return this.makeCompressor(NativeLibdeflateFunctions.MODE_GZIP);
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotGzipDecompressor() {
        return this.makeDecompressor(NativeLibdeflateFunctions.MODE_GZIP);
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotZlibCompressor() {
        return this.makeCompressor(NativeLibdeflateFunctions.MODE_ZLIB);
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotZlibDecompressor() {
        return this.makeDecompressor(NativeLibdeflateFunctions.MODE_ZLIB);
    }

    protected abstract DeflateOneshotCompressor makeCompressor(byte mode);

    protected abstract DeflateOneshotDecompressor makeDecompressor(byte mode);
}
