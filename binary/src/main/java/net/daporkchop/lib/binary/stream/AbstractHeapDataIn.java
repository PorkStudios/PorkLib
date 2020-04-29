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
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.toInt;

/**
 * Base implementation of {@link DataIn} for heap-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractHeapDataIn extends AbstractDataIn {
    @Override
    protected long read0(long addr, long length) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] buf = handle.get();
            long total = 0L;
            boolean first = true;
            do {
                int read = this.read0(buf, 0, (int) min(length - total, PorkUtil.BUFFER_SIZE));
                if (read <= 0) {
                    return read < 0 && first ? read : total;
                }

                //copy to direct buffer
                PUnsafe.copyMemory(buf, PUnsafe.ARRAY_BYTE_BASE_OFFSET, null, addr + total, read);

                total += read;
                first = false;
            } while (total < length);
            return total;
        }
    }

    @Override
    public boolean isHeap() {
        return true;
    }

    //
    //
    // primitives
    //
    //

    @Override
    public short readShort() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Short.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Short.reverseBytes(PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public short readShortLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Short.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Short.reverseBytes(PUnsafe.getShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public char readChar() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Character.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Character.reverseBytes(PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public char readCharLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Character.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Character.reverseBytes(PUnsafe.getChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public int readInt() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Integer.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Integer.reverseBytes(PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public int readIntLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Integer.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Integer.reverseBytes(PUnsafe.getInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public long readLong() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Long.BYTES);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Long.reverseBytes(PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    @Override
    public long readLongLE() throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            this.readFully(arr, 0, Long.BYTES);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET);
            } else {
                return Long.reverseBytes(PUnsafe.getLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET));
            }
        }
    }

    //
    //
    // other stuff
    //
    //

    @Override
    protected long skip0(long count) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] buf = handle.get();
            long total = 0L;
            boolean first = true;
            do {
                int read = this.read0(buf, 0, (int) min(count - total, PorkUtil.BUFFER_SIZE));
                if (read <= 0) {
                    return read < 0 && first ? read : total;
                }

                total += read;
                first = false;
            } while (total < count);
            return total;
        }
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, long count) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
            byte[] buf = handle.get();
            long total = 0L;
            boolean first = true;
            do {
                int read = this.read0(buf, 0, (int) min(count - total, PorkUtil.BUFFER_SIZE));
                if (read <= 0) {
                    return read < 0 && first ? read : total;
                }

                //write to dst
                dst.write(buf, 0, read);

                total += read;
                first = false;
            } while (count < 0L || total < count);
            return total;
        }
    }
}
