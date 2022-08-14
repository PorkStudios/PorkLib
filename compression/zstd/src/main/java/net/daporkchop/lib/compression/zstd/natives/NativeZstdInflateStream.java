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
import net.daporkchop.lib.binary.stream.AbstractDirectDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstdInflater.*;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("Duplicates")
final class NativeZstdInflateStream extends AbstractDirectDataIn {
    final long ctx;
    final long session;
    final NativeZstdInflater inflater;
    final ByteBuf buf;
    final DataIn in;

    long lastStatus = -1L;
    boolean eof = false;

    NativeZstdInflateStream(@NonNull DataIn in, @NonNull ByteBuf buf, ByteBuf dict, @NonNull NativeZstdInflater inflater) {
        checkArg(buf.hasMemoryAddress() || buf.hasArray(), "buffer (%s) does not have address or array!", buf);

        this.ctx = inflater.retain().ctx;
        this.inflater = inflater;
        this.buf = buf;
        this.in = in;
        this.session = inflater.createSessionAndSetDict(dict);
    }

    NativeZstdInflateStream(@NonNull DataIn in, @NonNull ByteBuf buf, NativeZstdInflateDictionary dict, @NonNull NativeZstdInflater inflater) {
        checkArg(buf.hasMemoryAddress() || buf.hasArray(), "buffer (%s) does not have address or array!", buf);

        this.ctx = inflater.retain().ctx;
        this.inflater = inflater;
        this.buf = buf;
        this.in = in;
        this.session = inflater.createSessionAndSetDict(dict); //this also retains the dictionary
    }

    @Override
    protected int read0() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();

        long addr = PUnsafe.pork_directBufferAddress(buf);
        int result = this.read0(addr, 1L) == 1L ? PUnsafe.getByte(addr) & 0xFF : -1;

        recycler.release(buf); //release the buffer to the recycler
        return result;
    }

    /*@Override
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        if (this.lastStatus == 0L && this.eof) {
            return RESULT_EOF;
        }
        int totalRead = 0;
        do {
            this.fill();
            if (this.eof && !this.buf.isReadable()) {
                throw new EOFException("Unexpected end of Zstd input stream");
            }
            int blockSize = min(length - totalRead, Integer.MAX_VALUE);
            this.lastStatus = this.buf.hasMemoryAddress() ?
                              updateD2H0(this.ctx,
                                      this.buf.memoryAddress() + this.buf.readerIndex(), this.buf.readableBytes(),
                                      dst, start + totalRead, blockSize,
                                      Z_NO_FLUSH) :
                              updateH2H0(this.ctx,
                                      this.buf.array(), this.buf.arrayOffset() + this.buf.readerIndex(), this.buf.readableBytes(),
                                      dst, start + totalRead, blockSize,
                                      Z_NO_FLUSH);

            this.buf.skipBytes(toInt(this.inflater.getRead(), "read"));
            totalRead += toInt(this.inflater.getWritten(), "written");
        } while (this.lastStatus != Z_STREAM_END && totalRead < length);
        return totalRead;
    }*/

    @Override
    protected long read0(long addr, long length) throws IOException {
        if (this.lastStatus == 0L && this.eof) {
            return RESULT_EOF;
        }
        long totalRead = 0L;
        do {
            this.fill();
            if (this.eof && !this.buf.isReadable()) {
                throw new EOFException("Unexpected end of Zstd input stream");
            }
            int blockSize = toInt(min(length - totalRead, Integer.MAX_VALUE));
            this.lastStatus = this.buf.hasMemoryAddress() ?
                    updateD2D0(this.ctx,
                            this.buf.memoryAddress() + this.buf.readerIndex(), this.buf.readableBytes(),
                            addr + totalRead, blockSize) :
                    updateH2D0(this.ctx,
                            this.buf.array(), this.buf.arrayOffset() + this.buf.readerIndex(), this.buf.readableBytes(),
                            addr + totalRead, blockSize);

            this.buf.skipBytes(toInt(this.inflater.getRead(), "read"));
            totalRead += toInt(this.inflater.getWritten(), "written");
        } while (!(this.lastStatus == 0L && this.eof) && totalRead < length);
        return totalRead;
    }

    @Override
    protected long remaining0() throws IOException {
        return this.lastStatus == 0L && this.eof ? 0L : 1L;
    }

    @Override
    protected void close0() throws IOException {
        this.ensureValidSession();

        this.in.close();
        this.buf.release();
        this.inflater.release();
    }

    protected int fill() throws IOException {
        if (!this.eof) {
            this.buf.discardSomeReadBytes();
            if (this.buf.isWritable()) {
                int read = this.in.read(this.buf);
                if (read < 0 || this.buf.isWritable()) {
                    this.eof = true;
                }
                return read;
            }
        }
        return -1;
    }

    @Override
    protected void ensureOpen() throws IOException {
        super.ensureOpen();
        this.ensureValidSession();
    }

    protected void ensureValidSession() {
        if (this.inflater.getSession() != this.session) {
            throw new ConcurrentModificationException();
        }
    }
}
