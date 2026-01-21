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

package net.daporkchop.lib.compression.zstd.air.v0;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.Borrow;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.compression.zstd.ZstdCompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdProviderCapabilities;
import net.daporkchop.lib.compression.zstd.ZstdOneshotCompressor;
import net.daporkchop.lib.compression.zstd.ZstdOneshotDecompressor;
import net.daporkchop.lib.compression.zstd.ZstdOneshotFactory;

import java.nio.ByteBuffer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
final class AircompressorV0ZstdOneshotFactory implements ZstdOneshotFactory {
    static { //TODO: this can be safely removed, we already perform these checks in AircompressorV0ZstdImplementation
        checkState(PlatformInfo.IS_LITTLE_ENDIAN, "aircompressor only works on little-endian systems!");
        checkState(PlatformInfo.UNALIGNED, "aircompressor requires unaligned memory access!");
    }

    @Override
    public ZstdProviderCapabilities capabilities() {
        return AircompressorV0ZstdProvider.class.getAnnotation(ZstdProviderCapabilities.class);
    }

    @Override
    public ZstdOneshotCompressor makeOneshotCompressor() {
        return new AircompressorV0ZstdOneshotCompressor();
    }

    @Override
    public ZstdOneshotDecompressor makeOneshotDecompressor() {
        return new AircompressorV0ZstdOneshotDecompressor();
    }

    @Override
    public ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuffer dict, int level) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuf dict, int level) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuffer dict) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuf dict) {
        throw new UnsupportedOperationException();
    }
}
