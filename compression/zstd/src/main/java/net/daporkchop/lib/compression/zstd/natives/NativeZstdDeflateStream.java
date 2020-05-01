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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.AbstractDirectDataOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import static net.daporkchop.lib.common.util.PValidation.checkArg;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("Duplicates")
final class NativeZstdDeflateStream extends AbstractDirectDataOut {
    final long ctx;
    final long session;
    final NativeZstdDeflater deflater;
    final ByteBuf buf;
    final DataOut out;
    final NativeZstdDeflateDictionary dict;

    NativeZstdDeflateStream(@NonNull DataOut out, @NonNull ByteBuf buf, NativeZstdDeflateDictionary dict, int level, @NonNull NativeZstdDeflater deflater) {
        checkArg(buf.hasMemoryAddress() || buf.hasArray(), "buffer (%s) does not have address or array!", buf);

        this.ctx = deflater.retain().ctx;
        this.deflater = deflater;
        this.buf = buf;
        this.out = out;
        this.session = deflater.createSessionAndSetDict(dict, level); //this also retains the dictionary
        this.dict = dict;
    }

    @Override
    protected void write0(int b) throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buffer = handle.get();
            long addr = PUnsafe.pork_directBufferAddress(buffer.clear());
            buffer.put((byte) b);
            this.write0(addr, 1L);
        }
    }

    @Override
    protected void write0(long addr, long length) throws IOException {
    }

    @Override
    protected void flush0() throws IOException {
    }

    @Override
    protected void close0() throws IOException {
        if (this.dict != null)  {
            this.dict.release();
        }
        this.out.close();
    }

    @Override
    protected Object mutex() {
        return this.deflater;
    }

    @Override
    protected void ensureOpen() throws IOException {
        super.ensureOpen();
        this.ensureValidSession();
    }

    protected void ensureValidSession() {
        if (this.deflater.getSession() != this.session) {
            throw new ConcurrentModificationException();
        }
    }
}
