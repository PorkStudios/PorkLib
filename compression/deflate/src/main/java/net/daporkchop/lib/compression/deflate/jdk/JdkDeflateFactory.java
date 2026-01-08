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

package net.daporkchop.lib.compression.deflate.jdk;

import net.daporkchop.lib.compression.deflate.DeflateOneshotCompressor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotDecompressor;
import net.daporkchop.lib.compression.deflate.DeflateOneshotFactory;
import net.daporkchop.lib.compression.deflate.DeflateProviderCapabilities;
import net.daporkchop.lib.compression.deflate.DeflateStreamingCompressor;
import net.daporkchop.lib.compression.deflate.DeflateStreamingDecompressor;
import net.daporkchop.lib.compression.deflate.DeflateStreamingFactory;

/**
 * @author DaPorkchop_
 */
final class JdkDeflateFactory implements DeflateStreamingFactory {
    @Override
    public DeflateProviderCapabilities capabilities() {
        return JdkDeflateProvider.class.getAnnotation(DeflateProviderCapabilities.class);
    }

    @Override
    public DeflateOneshotCompressor makeOneshotDeflateCompressor() {
        return new JdkOneshotDeflateCompressor(true);
    }

    @Override
    public DeflateOneshotDecompressor makeOneshotDeflateDecompressor() {
        return new JdkOneshotDeflateDecompressor(true);
    }

    @Override
    public DeflateOneshotCompressor makeOneshotGzipCompressor() {
        throw new AbstractMethodError(); //TODO
    }

    @Override
    public DeflateOneshotDecompressor makeOneshotGzipDecompressor() {
        throw new AbstractMethodError(); //TODO
    }

    @Override
    public DeflateOneshotCompressor makeOneshotZlibCompressor() {
        return new JdkOneshotDeflateCompressor(false);
    }

    @Override
    public DeflateOneshotDecompressor makeOneshotZlibDecompressor() {
        return new JdkOneshotDeflateDecompressor(false);
    }

    @Override
    public DeflateStreamingCompressor makeStreamingDeflateCompressor() {
        return new JdkDeflateStreamingCompressor(true);
    }

    @Override
    public DeflateStreamingDecompressor makeStreamingDeflateDecompressor() {
        return new JdkDeflateStreamingDecompressor(true);
    }

    @Override
    public DeflateStreamingCompressor makeStreamingGzipCompressor() {
        return new JdkGzipStreamingCompressor();
    }

    @Override
    public DeflateStreamingDecompressor makeStreamingGzipDecompressor() {
        return new JdkGzipStreamingDecompressor();
    }

    @Override
    public DeflateStreamingCompressor makeStreamingZlibCompressor() {
        return new JdkDeflateStreamingCompressor(false);
    }

    @Override
    public DeflateStreamingDecompressor makeStreamingZlibDecompressor() {
        return new JdkDeflateStreamingDecompressor(false);
    }
}
