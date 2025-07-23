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

import lombok.experimental.Accessors;
import net.daporkchop.lib.common.closeable.QuietCloseable;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
abstract class AbstractNativeZstdContext implements QuietCloseable {
    final long ctx;

    private PCleaner cleaner;

    AbstractNativeZstdContext() {
        this.ctx = this.allocate0();
        this.cleaner = PCleaner.cleaner(this, this.makeReleaser(this.ctx));
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

    abstract long allocate0();

    abstract Runnable makeReleaser(long addr);

    protected final long getRead() {
        return PUnsafe.getLongVolatile(null, this.ctx);
    }

    protected final long getWritten() {
        return PUnsafe.getLongVolatile(null, this.ctx + 8L);
    }

    protected final long getSession() {
        return PUnsafe.getLongVolatile(null, this.ctx + 16L);
    }
}
