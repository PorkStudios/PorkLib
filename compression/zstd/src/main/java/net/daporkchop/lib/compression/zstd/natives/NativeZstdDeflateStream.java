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

package net.daporkchop.lib.compression.zstd.natives;

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
import static net.daporkchop.lib.compression.zstd.natives.NativeZstd.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstdDeflater.*;

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

    NativeZstdDeflateStream(@NonNull DataOut out, @NonNull ByteBuf buf, ByteBuf dict, int level, @NonNull NativeZstdDeflater deflater) {
        checkArg(buf.hasMemoryAddress() || buf.hasArray(), "buffer (%s) does not have address or array!", buf);

        this.ctx = deflater.retain().ctx;
        this.deflater = deflater;
        this.buf = buf;
        this.out = out;
        this.session = deflater.createSessionAndSetDict(dict, level);
        this.dict = null;
    }

    NativeZstdDeflateStream(@NonNull DataOut out, @NonNull ByteBuf buf, NativeZstdDeflateDictionary dict, @NonNull NativeZstdDeflater deflater) {
        checkArg(buf.hasMemoryAddress() || buf.hasArray(), "buffer (%s) does not have address or array!", buf);

        this.ctx = deflater.retain().ctx;
        this.deflater = deflater;
        this.buf = buf;
        this.out = out;
        this.session = deflater.createSessionAndSetDict(dict); //this also retains the dictionary
        this.dict = dict;
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
        this.drain(); //drain buffer completely
        int total = 0;
        do {
            int blockSize = min(length - total, Integer.MAX_VALUE);
            if (this.buf.hasMemoryAddress()) {
                updateH2D0(this.ctx, src, start + total, blockSize,
                        this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                        ZSTD_e_continue);
            } else {
                updateH2H0(this.ctx, src, start + total, blockSize,
                        this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                        ZSTD_e_continue);
            }

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
            if (this.buf.hasMemoryAddress()) {
                updateD2D0(this.ctx, addr + total, blockSize,
                        this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                        ZSTD_e_continue);
            } else {
                updateD2H0(this.ctx, addr + total, blockSize,
                        this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                        ZSTD_e_continue);
            }

            total += this.deflater.getRead();
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            this.drain();
        } while (total < length);
    }

    @Override
    protected void flush0() throws IOException {
        this.drain();
        long remaining;
        do {
            remaining = this.buf.hasMemoryAddress() ?
                    updateD2D0(this.ctx, 0L, 0,
                            this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                            ZSTD_e_flush) :
                    updateD2H0(this.ctx, 0L, 0,
                            this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                            ZSTD_e_flush);
            this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
            this.drain();
        } while (remaining != 0L);
    }

    @Override
    protected void close0() throws IOException {
        try {
            this.ensureValidSession();

            this.drain();
            long remaining;
            do {
                remaining = this.buf.hasMemoryAddress() ?
                        updateD2D0(this.ctx, 0L, 0,
                                this.buf.memoryAddress() + this.buf.writerIndex(), this.buf.writableBytes(),
                                ZSTD_e_end) :
                        updateD2H0(this.ctx, 0L, 0,
                                this.buf.array(), this.buf.arrayOffset() + this.buf.writerIndex(), this.buf.writableBytes(),
                                ZSTD_e_end);
                this.buf.writerIndex(this.buf.writerIndex() + toInt(this.deflater.getWritten(), "written"));
                this.drain();
            } while (remaining != 0L);

            this.out.close();
        } finally {
            if (this.dict != null) {
                this.dict.release();
            }
            this.buf.release();
            this.deflater.release();
        }
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
