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

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.closeable.QuietCloseable;
import net.daporkchop.lib.unsafe.PCleaner;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
abstract class AbstractNativeZstdDictionary implements QuietCloseable {
    @Getter
    final NativeZstdProvider provider;

    final long dict;
    private PCleaner cleaner;

    AbstractNativeZstdDictionary(@NonNull NativeZstdProvider provider, long dict) {
        this.provider = provider;
        this.dict = dict;
        this.cleaner = PCleaner.cleaner(this, this.freeDictRunnable(provider, dict));
    }

    final void ensureOpen() {
        if (this.cleaner == null) {
            throw new IllegalStateException("already closed!");
        }
    }

    @Override
    public final void close() {
        PCleaner cleaner = this.cleaner;
        this.cleaner = null;
        if (cleaner != null) {
            cleaner.clean();
        }
    }

    abstract Runnable freeDictRunnable(@NonNull NativeZstdProvider provider, long dict);
}
