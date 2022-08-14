/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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
import net.daporkchop.lib.common.pool.recycler.Recycler;
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
@SuppressWarnings("Duplicates")
final class NativeZlibDeflateStream extends AbstractDirectDataOut {
    final long ctx;
    final long session;
    final NativeZlibDeflater deflater;
    final ByteBuf buf;
    final DataOut out;

    /**
     * Indicates whether or not the stream is currently flushed. This is set to {@code false} on every write.
     * <p>
     * This prevents exceptions when {@link #flush()} is called multiple times consecutively, as zlib expects more data to be written to the
     * stream after a successful {@link NativeZlib#Z_SYNC_FLUSH}.
     */
    boolean flushed = false;

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
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();

        long addr = PUnsafe.pork_directBufferAddress(buf);
        buf.put((byte) b);
        this.write0(addr, 1L);

        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        this.flushed = false;

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
        this.flushed = false;

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
        if (!this.flushed) { //we can actually flush the deflater
            this.flushed = true;

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
            } while (status != Z_OK);

            //now that all data has been written, flush the delegate stream
            this.out.flush();
        }
    }

    @Override
    protected void close0() throws IOException {
        try {
            this.ensureValidSession();

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
        } finally {
            this.buf.release();
            this.deflater.release();
        }
    }

    protected void drain() throws IOException {
        if (this.buf.isReadable()) { //the write buffer contains some data
            //write the buffered data to the delegate stream and remove it from the buffer
            this.out.write(this.buf);
            this.buf.clear();
        }
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
