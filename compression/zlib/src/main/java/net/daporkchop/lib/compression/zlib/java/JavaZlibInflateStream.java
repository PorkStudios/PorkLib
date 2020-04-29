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

package net.daporkchop.lib.compression.zlib.java;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.AbstractHeapDataIn;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static java.lang.Math.*;

/**
 * @author DaPorkchop_
 */
class JavaZlibInflateStream extends AbstractHeapDataIn {
    final long session;
    final Inflater inf;
    final JavaZlibInflater parent;
    final ByteBuf buf;
    final DataIn in;

    byte[] dict;
    boolean eof = false;

    JavaZlibInflateStream(@NonNull DataIn in, @NonNull ByteBuf buf, ByteBuf dict, @NonNull JavaZlibInflater parent) {
        this.session = ++parent.retain().sessionCounter;
        this.inf = parent.inflater;
        this.parent = parent;
        this.buf = buf;
        this.in = in;

        this.inf.reset();

        if (dict != null && dict.isReadable()) {
            if (dict.hasArray()) {
                this.inf.setDictionary(dict.array(), dict.arrayOffset(), dict.readableBytes());
            } else {
                try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
                    byte[] arr = handle.get();
                    int len = min(dict.readableBytes(), PorkUtil.BUFFER_SIZE);
                    dict.getBytes(dict.readerIndex(), arr, 0, len);
                    this.inf.setDictionary(arr, 0, len);
                }
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
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        if (this.eof) {
            return RESULT_EOF;
        }
        int total = 0;
        boolean first = true;
        try {
            do {
                int read = this.inf.inflate(dst, start + total, length - total);
                if (read == 0) {
                    if (this.inf.finished()) {
                        this.eof = true;
                        return first ? RESULT_EOF : total;
                    } else if (this.inf.needsDictionary()) {
                        throw new UnsupportedOperationException();
                    } else if (this.inf.needsInput() && (this.eof || this.fill() < 0)) {
                        throw new EOFException("Unexpected end of ZLIB input stream");
                    }
                }
                total += read;
                first = false;
            } while (total < length);
        } catch (DataFormatException e) {
            throw new IOException(e);
        }
        return total;
    }

    @Override
    protected long remaining0() throws IOException {
        return this.eof ? 0L : 1L;
    }

    @Override
    protected void close0() throws IOException {
        this.ensureValidSession();

        this.in.close();
        this.buf.release();
        this.parent.release();
        this.inf.reset();
    }

    protected int fill() throws IOException {
        int i = this.in.read(this.buf.clear());
        if (i < 0) {
            this.eof = true;
        } else {
            this.inf.setInput(this.buf.array(), this.buf.arrayOffset(), this.buf.readableBytes());
        }
        return i;
    }

    @Override
    protected Object mutex() {
        return this.parent;
    }

    @Override
    protected void ensureOpen() throws IOException {
        super.ensureOpen();
        this.ensureValidSession();
    }

    protected void ensureValidSession() {
        if (this.parent.sessionCounter != this.session) {
            throw new ConcurrentModificationException();
        }
    }
}
