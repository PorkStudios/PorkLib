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

package net.daporkchop.lib.compression.zstd;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.context.POneshotDecompressor;
import net.daporkchop.lib.compression.zstd.util.JavaZstdFrameInspector;

import java.nio.ByteBuffer;
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

/**
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface ZstdOneshotDecompressor extends POneshotDecompressor, ZstdDecompressParameters {
    @Override
    default @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeExact();
    }

    @Override
    default @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeExact();
    }

    @Override
    default @NotNegative long decompressedSizeBound(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeUpperBound();
    }

    @Override
    default @NotNegative long decompressedSizeBound(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException {
        return JavaZstdFrameInspector.getSequenceSizeInfo(src).decompressedSizeUpperBound();
    }
}
