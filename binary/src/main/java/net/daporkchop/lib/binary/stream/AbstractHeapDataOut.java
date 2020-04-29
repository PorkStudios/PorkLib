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
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;

import static java.lang.Math.*;

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
    public void writeShort(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (short) v);
            } else {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Short.reverseBytes((short) v));
            }
            this.write(arr, 0, Short.BYTES);
        }
    }

    @Override
    public void writeShortLE(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (short) v);
            } else {
                PUnsafe.putShort(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Short.reverseBytes((short) v));
            }
            this.write(arr, 0, Short.BYTES);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (char) v);
            } else {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Character.reverseBytes((char) v));
            }
            this.write(arr, 0, Character.BYTES);
        }
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, (char) v);
            } else {
                PUnsafe.putChar(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Character.reverseBytes((char) v));
            }
            this.write(arr, 0, Character.BYTES);
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Integer.reverseBytes(v));
            }
            this.write(arr, 0, Integer.BYTES);
        }
    }

    @Override
    public void writeIntLE(int v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putInt(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Integer.reverseBytes(v));
            }
            this.write(arr, 0, Integer.BYTES);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_BIG_ENDIAN) {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Long.reverseBytes(v));
            }
            this.write(arr, 0, Long.BYTES);
        }
    }

    @Override
    public void writeLongLE(long v) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, v);
            } else {
                PUnsafe.putLong(arr, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Long.reverseBytes(v));
            }
            this.write(arr, 0, Long.BYTES);
        }
    }
}
