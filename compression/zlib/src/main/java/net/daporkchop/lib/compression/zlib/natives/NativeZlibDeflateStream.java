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
        this.ctx = deflater.retain().ctx;
        this.deflater = deflater;
        this.buf = buf;
        this.out = out;

        if (dict == null || dict.readableBytes() == 0) {
            this.session = newSession0(this.ctx, 0L, 0);
        } else if (dict.hasMemoryAddress()) {
            this.session = newSession0(this.ctx, dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
        } else {
            try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
                ByteBuffer dictBuffer = handle.get();
                dictBuffer.clear().limit(min(dictBuffer.capacity(), dict.readableBytes()));
                dict.getBytes(dict.readerIndex(), dictBuffer);
                this.session = newSession0(this.ctx, PUnsafe.pork_directBufferAddress(dictBuffer.position(0)), dictBuffer.limit());
            }
        }
    }

    @Override
    protected void write0(int b) throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            ByteBuffer buffer = handle.get();
            long addr = PUnsafe.pork_directBufferAddress(buffer.clear());
            buffer.put((byte) b);
            this.writeAll0(addr, 1L);
        }
    }

    @Override
    protected long writeSome0(long addr, long length) throws IOException {
        long total = 0L;
        do {
            if (this.drainSome() == 0)    {
                //the buffer is full and no data could be flushed, don't bother trying to continue
                return total;
            }

            int blockSize = toInt(min(length - total, Integer.MAX_VALUE));
            int status = update0(this.ctx, addr + total, blockSize, this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(), Z_NO_FLUSH);
            checkState(status == Z_OK, "deflate() returned invalid status: %s", status);

            total += this.deflater.getRead();
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            //System.out.println("Z_NO_FLUSH: " + this.deflater.getWritten());
        } while (total < length);
        this.drainSome(); //final attempt to drain as much of the buffer as possible
        return total;
    }

    @Override
    protected void writeAll0(long addr, long length) throws IOException {
        this.drainAll(); //drain buffer completely
        long total = 0L;
        do {
            int blockSize = toInt(min(length - total, Integer.MAX_VALUE));
            int status = update0(this.ctx, addr + total, blockSize, this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(), Z_NO_FLUSH);
            checkState(status == Z_OK, "deflate() returned invalid status: %s", status);

            total += this.deflater.getRead();
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            //System.out.println("Z_NO_FLUSH: " + this.deflater.getWritten());
            this.drainAll();
        } while (total < length);
    }

    @Override
    protected void flush0() throws IOException {
        this.drainAll();
        int status;
        do {
            status = update0(this.ctx, 0L, 0, this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(), Z_SYNC_FLUSH);
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            //System.out.println("Z_SYNC_FLUSH: " + this.deflater.getWritten());
            this.drainAll();
        } while (status != Z_OK || !this.buf.isWritable());
        //System.out.println("post-Z_SYNC_FLUSH: " + this.deflater.getWritten());
    }

    @Override
    protected void close0() throws IOException {
        this.ensureValidSession();

        this.drainAll();
        int status;
        do {
            status = update0(this.ctx, 0L, 0, this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(), Z_FINISH);
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            //System.out.println("Z_FINISH: " + this.deflater.getWritten());
            this.drainAll();
        } while (status != Z_STREAM_END);
        //System.out.println("post-Z_FINISH: " + this.buf.readableBytes());

        this.out.close();
        this.buf.release();
    }

    protected int drainSome() throws IOException {
        if (this.buf.isReadable()) {
            int written = this.out.write(this.buf);
            this.buf.discardReadBytes();
            //System.out.println("Partial drain: " + written);
            return written;
        } else {
            return -1;
        }
    }

    protected int drainAll() throws IOException {
        if (this.buf.isReadable()) {
            int written = this.out.writeFully(this.buf);
            this.buf.clear();
            //System.out.println("Full drain: " + written);
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
