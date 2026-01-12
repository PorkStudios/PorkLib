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

package net.daporkchop.lib.compression.deflate;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.context.POneshotDecompressor;

import java.nio.ByteBuffer;
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

/**
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface DeflateOneshotDecompressor extends POneshotDecompressor, DeflateDecompressParameters {
    @Override
    default @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException {
        //cannot be computed easily
        return OptionalLong.empty();
    }

    @Override
    default @NotNegative OptionalLong decompressedSizeExact(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException {
        //cannot be computed easily
        return OptionalLong.empty();
    }

    @Override
    default @NotNegative long decompressedSizeBound(@NonNull ByteBuffer src) throws DataFormatException, ArithmeticException {
        //TODO: this could be made quite a bit smarter
        //the theoretical maximum ZLIB compression factor is 1032:1 according to https://www.zlib.net/zlib_tech.html
        return Math.multiplyExact(src.remaining(), 1032L);
    }

    @Override
    default @NotNegative long decompressedSizeBound(@NonNull ByteBuf src) throws DataFormatException, ArithmeticException {
        return Math.multiplyExact(src.readableBytes(), 1032L);
    }
}
