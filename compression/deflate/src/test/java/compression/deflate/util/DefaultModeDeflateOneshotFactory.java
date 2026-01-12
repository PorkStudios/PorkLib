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

package compression.deflate.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotCompressor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotDecompressor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotFactory;
import net.daporkchop.lib.compression.deflate.DeflateProviderCapabilities;
import net.daporkchop.lib.compression.deflate.util.DeflateWrapperFormat;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class DefaultModeDeflateOneshotFactory<DELEGATE extends DeflateOneshotFactory> implements DeflateOneshotFactory {
    protected final @NonNull DELEGATE delegate;
    protected final @NonNull DeflateWrapperFormat format;

    @Override
    public final DeflateProviderCapabilities capabilities() {
        return this.delegate.capabilities();
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotCompressor() {
        return this.delegate.makeOneshotCompressor(this.format);
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotDecompressor() {
        return this.delegate.makeOneshotDecompressor(this.format);
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotCompressor(@NonNull DeflateWrapperFormat format) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotDecompressor(@NonNull DeflateWrapperFormat format) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotDeflateCompressor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotDeflateDecompressor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotGzipCompressor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotGzipDecompressor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotCompressor makeOneshotZlibCompressor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final DeflateOneshotDecompressor makeOneshotZlibDecompressor() {
        throw new UnsupportedOperationException();
    }
}
