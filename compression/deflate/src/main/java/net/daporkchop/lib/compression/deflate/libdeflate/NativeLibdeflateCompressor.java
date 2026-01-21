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
import lombok.val;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.deflate.DeflateOneshotCompressor;
import net.daporkchop.lib.unsafe.PCleaner;

import java.util.zip.Deflater;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
abstract class NativeLibdeflateCompressor implements DeflateOneshotCompressor, LibdeflateDeflateContext {
    private static final int DEFAULT_LEVEL = 6;

    final @NonNull NativeLibdeflateFunctions functions;
    final byte mode;

    //unfortunately, libdeflate won't let us re-use the same compressor instance for different compression levels, so instead we'll re-create them as needed when the level changes
    private long compressor;
    private int compressorLevel = -1;
    private final PCleaner cleaner = PCleaner.cleaner(this, PFunctions.noopRunnable());

    private int configuredLevel = DEFAULT_LEVEL;

    @Override
    public final void close() {
        this.compressor = 0L;
        this.cleaner.clean();
    }

    @Override
    public final void resetParameters() {
        this.configuredLevel = Deflater.DEFAULT_COMPRESSION;
    }

    @Override
    public final void setLevel(int level) throws IllegalArgumentException {
        PValidation.checkArg(level == Deflater.DEFAULT_COMPRESSION || (level >= 0 && level <= 12), level);
        this.configuredLevel = level == Deflater.DEFAULT_COMPRESSION ? DEFAULT_LEVEL : level;
    }

    protected final long compressor() {
        if (this.compressor != 0L && this.compressorLevel == this.configuredLevel) {
            return this.compressor;
        }

        val functions = this.functions;
        long oldCompressor = this.compressor;
        long newCompressor = functions.libdeflate_alloc_compressor(this.configuredLevel);
        if (newCompressor == 0L) {
            throw new OutOfMemoryError();
        }

        this.cleaner.replace(() -> functions.libdeflate_free_compressor(newCompressor));
        this.compressor = newCompressor;

        functions.libdeflate_free_compressor(oldCompressor);

        return newCompressor;
    }

    @Override
    public final @NotNegative long compressBound(@NotNegative long srcSize) throws IllegalArgumentException, ArithmeticException {
        PValidation.notNegative(srcSize, "srcSize");

        long result;
        switch (this.mode) {
            case NativeLibdeflateFunctions.MODE_DEFLATE:
                result = this.functions.libdeflate_deflate_compress_bound(0L, srcSize);
                break;
            case NativeLibdeflateFunctions.MODE_ZLIB:
                result = this.functions.libdeflate_zlib_compress_bound(0L, srcSize);
                break;
            case NativeLibdeflateFunctions.MODE_GZIP:
                result = this.functions.libdeflate_gzip_compress_bound(0L, srcSize);
                break;
            default:
                throw new IllegalStateException(String.valueOf(this.mode));
        }

        if (result < srcSize) {
            throw new ArithmeticException();
        }

        return result;
    }
}
