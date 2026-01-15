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

package net.daporkchop.lib.compression.zstd.natives;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import net.daporkchop.lib.common.closeable.QuietCloseable;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class NativeZstdObject implements QuietCloseable {
    @Getter
    private final long addr;
    private final PCleaner cleaner;

    private NativeZstdObject(long addr, @NonNull Runnable free) {
        if (addr == 0) {
            throw new OutOfMemoryError();
        }

        this.addr = addr;
        this.cleaner = PCleaner.cleaner(this, free);
    }

    @Override
    public void close() {
        this.cleaner.clean();
    }

    public static NativeZstdObject createCCtx(@NonNull NativeZstdFunctions functions) {
        val cctx = functions.ZSTD_createCCtx();
        try {
            return new NativeZstdObject(cctx, functions.freeCCtxRunnable(cctx));
        } catch (Throwable t) {
            functions.ZSTD_freeCCtx(cctx);
            throw PUnsafe.throwException(t);
        }
    }

    public static NativeZstdObject createDCtx(@NonNull NativeZstdFunctions functions) {
        val dctx = functions.ZSTD_createDCtx();
        try {
            return new NativeZstdObject(dctx, functions.freeDCtxRunnable(dctx));
        } catch (Throwable t) {
            functions.ZSTD_freeDCtx(dctx);
            throw PUnsafe.throwException(t);
        }
    }
}
