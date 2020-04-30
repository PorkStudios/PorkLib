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

package net.daporkchop.lib.compression.zlib.natives;

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

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zlib.natives.NativeZlib.*;
import static net.daporkchop.lib.compression.zlib.natives.NativeZlibDeflater.*;

/**
 * @author DaPorkchop_
 */
final class NativeZlibDeflateStream extends AbstractDirectDataOut {
    final long ctx;
    final long session;
    final NativeZlibDeflater deflater;
    final ByteBuf buf;
    final DataOut out;

    NativeZlibDeflateStream(@NonNull DataOut out, @NonNull ByteBuf buf, ByteBuf dict, @NonNull NativeZlibDeflater deflater) {
        checkArg(buf.hasMemoryAddress() || buf.hasArray(), "buffer (%s) does not have address or array!", buf);

        this.ctx = deflater.retain().ctx;
        this.deflater = deflater;
        this.buf = buf;
        this.out = out;
        this.session = deflater.createSessionAndSetDict(dict);
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
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        this.drain(); //drain buffer completely
        int total = 0;
        do {
            int blockSize = min(length - total, Integer.MAX_VALUE);
            int status = this.buf.hasMemoryAddress() ?
                         updateH2D0(this.ctx, src, start + total, blockSize,
                                 this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                                 Z_NO_FLUSH) :
                         updateH2H0(this.ctx, src, start + total, blockSize,
                                 this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                                 Z_NO_FLUSH);
            checkState(status == Z_OK, "deflate() returned invalid status: %d", status);

            total += this.deflater.getRead();
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            this.drain();
        } while (total < length);
    }

    @Override
    protected void write0(long addr, long length) throws IOException {
        this.drain(); //drain buffer completely
        long total = 0L;
        do {
            int blockSize = toInt(min(length - total, Integer.MAX_VALUE));
            int status = this.buf.hasMemoryAddress() ?
                         updateD2D0(this.ctx, addr + total, blockSize,
                                 this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                                 Z_NO_FLUSH) :
                         updateD2H0(this.ctx, addr + total, blockSize,
                                 this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                                 Z_NO_FLUSH);
            checkState(status == Z_OK, "deflate() returned invalid status: %d", status);

            total += this.deflater.getRead();
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            this.drain();
        } while (total < length);
    }

    @Override
    protected void flush0() throws IOException {
        this.drain();
        int status;
        do {
            status = this.buf.hasMemoryAddress() ?
                     updateD2D0(this.ctx, 0L, 0,
                             this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                             Z_SYNC_FLUSH) :
                     updateD2H0(this.ctx, 0L, 0,
                             this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                             Z_SYNC_FLUSH);
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            this.drain();
        } while (status != Z_OK || !this.buf.isWritable());
    }

    @Override
    protected void close0() throws IOException {
        this.ensureValidSession();

        this.drain();
        int status;
        do {

            status = this.buf.hasMemoryAddress() ?
                     updateD2D0(this.ctx, 0L, 0,
                             this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                             Z_FINISH) :
                     updateD2H0(this.ctx, 0L, 0,
                             this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                             Z_FINISH);
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            this.drain();
        } while (status != Z_STREAM_END);

        this.out.close();
        this.buf.release();
        this.deflater.release();
    }

    protected int drain() throws IOException {
        if (this.buf.isReadable()) {
            int written = this.out.write(this.buf);
            this.buf.clear();
            return written;
        } else {
            return -1;
        }
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
