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
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
            long addr = PUnsafe.pork_directBufferAddress(handle.get());
            int total = 0;
            boolean first = true;
            do {
                int read = toInt(this.read0(addr, min(length - total, PorkUtil.BUFFER_SIZE)));
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
    public boolean isDirect() {
        return true;
    }

    //
    //
    // primitives
    //
    //

    @Override
    public short readShort() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Short.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getShort(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Short.reverseBytes(PUnsafe.getShort(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public short readShortLE() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Short.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getShort(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Short.reverseBytes(PUnsafe.getShort(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public char readChar() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Character.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getChar(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Character.reverseBytes(PUnsafe.getChar(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public char readCharLE() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Character.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getChar(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Character.reverseBytes(PUnsafe.getChar(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public int readInt() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Integer.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getInt(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Integer.reverseBytes(PUnsafe.getInt(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public int readIntLE() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Integer.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getInt(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Integer.reverseBytes(PUnsafe.getInt(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public long readLong() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Long.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_BIG_ENDIAN) {
                return PUnsafe.getLong(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Long.reverseBytes(PUnsafe.getLong(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }

    @Override
    public long readLongLE() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buf = (ByteBuffer) handle.get().position(0).limit(Long.BYTES);
            this.readFully(buf);
            if (PlatformInfo.IS_LITTLE_ENDIAN) {
                return PUnsafe.getLong(PUnsafe.pork_directBufferAddress(buf));
            } else {
                return Long.reverseBytes(PUnsafe.getLong(PUnsafe.pork_directBufferAddress(buf)));
            }
        }
    }
}
