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

import lombok.NonNull;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link DataIn} for direct-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDirectDataIn extends AbstractDataIn {
    @Override
    protected int readSome0(@NonNull byte[] dst, int start, int length, boolean blocking) throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
            long addr = PUnsafe.pork_directBufferAddress(handle.get());
            int total = 0;
            boolean first = true;
            do {
                int read = toInt(this.readSome0(addr, min(length - total, PorkUtil.BUFFER_SIZE), blocking));
                if (read <= 0) {
                    return read < 0 && first ? read : total;
                }

                //copy to heap buffer
                PUnsafe.copyMemory(null, addr, dst, PUnsafe.ARRAY_BYTE_BASE_OFFSET + start + total, read);

                total += read;
                first = false;
            } while (total < length);
            return total;
        }
    }

    @Override
    protected void readAll0(@NonNull byte[] dst, int start, int length) throws EOFException, IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
            long addr = PUnsafe.pork_directBufferAddress(handle.get());
            int total = 0;
            do {
                int read = min(length - total, PorkUtil.BUFFER_SIZE);
                this.readAll0(addr, min(length - total, PorkUtil.BUFFER_SIZE));

                //copy to heap buffer
                PUnsafe.copyMemory(null, addr, dst, PUnsafe.ARRAY_BYTE_BASE_OFFSET + start + total, read);

                total += read;
            } while (total < length);
        }
    }
}
