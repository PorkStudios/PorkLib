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
import net.daporkchop.lib.binary.stream.AbstractDirectDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zlib.natives.NativeZlib.*;
import static net.daporkchop.lib.compression.zlib.natives.NativeZlibInflater.*;

/**
 * @author DaPorkchop_
 */
final class NativeZlibInflateStream extends AbstractDirectDataIn {
    final long ctx;
    final long session;
    final NativeZlibInflater inflater;
    final ByteBuf buf;
    final DataIn in;

    int lastStatus = -1;
    boolean eof = false;

    NativeZlibInflateStream(@NonNull DataIn in, @NonNull ByteBuf buf, ByteBuf dict, @NonNull NativeZlibInflater inflater) {
        this.ctx = inflater.retain().ctx;
        this.inflater = inflater;
        this.buf = buf;
        this.in = in;

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
    protected int read0() throws IOException {
        try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_TINY_BUFFER_POOL.get()) {
            long addr = PUnsafe.pork_directBufferAddress(handle.get());
            return this.read0(addr, 1L) == 1L ? PUnsafe.getByte(addr) & 0xFF : -1;
        }
    }

    @Override
    protected long read0(long addr, long length) throws IOException {
        if (this.lastStatus == Z_STREAM_END) {
            return RESULT_EOF;
        }
        long totalRead = 0L;
        do {
            this.fill();
            int blockSize = toInt(min(length - totalRead, Integer.MAX_VALUE));
            this.lastStatus = update0(this.ctx, this.buf.memoryAddress() + this.buf.readerIndex(), this.buf.readableBytes(), addr + totalRead, blockSize, Z_NO_FLUSH);

            this.buf.skipBytes(toInt(this.inflater.getRead(), "read"));
            totalRead += toInt(this.inflater.getWritten(), "written");
        } while (this.lastStatus != Z_STREAM_END && totalRead < length);
        return totalRead;
    }

    @Override
    protected long skip0(long count) throws IOException {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    protected long remaining0() throws IOException {
        switch (this.lastStatus)     {
            case Z_OK:
                return 1L;
            case Z_STREAM_END:
                return 0L;
            default:
                throw new IllegalStateException(String.valueOf(this.lastStatus));
        }
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
                if (read < 0) {
                    this.eof = true;
                }
                return read;
            }
        }
        return -1;
    }

    @Override
    protected Object mutex() {
        return this.inflater;
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
