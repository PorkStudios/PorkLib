/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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
 *
 */

package net.daporkchop.lib.binary.stream;

import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;

import static java.lang.Math.min;

/**
 * Base implementation of {@link DataOut} for heap-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractHeapDataOut extends AbstractDataOut {
    @Override
    protected void write0(long addr, long length) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] buf = handle.get();
            long total = 0L;
            do {
                int blockSize = (int) min(length - total, PorkUtil.BUFFER_SIZE);

                //copy to heap buffer
                PUnsafe.copyMemory(null, addr, buf, PUnsafe.ARRAY_BYTE_BASE_OFFSET, blockSize);

                this.write0(buf, 0, blockSize);

                total += blockSize;
            } while (total < length);
        }
    }
}
