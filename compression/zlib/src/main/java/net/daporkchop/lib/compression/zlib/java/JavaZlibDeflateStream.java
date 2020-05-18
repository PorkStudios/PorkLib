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
import net.daporkchop.lib.binary.stream.AbstractHeapDataOut;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.zip.Deflater;

import static java.lang.Math.*;

/**
 * @author DaPorkchop_
 */
class JavaZlibDeflateStream extends AbstractHeapDataOut {
    final long session;
    protected final Deflater def;
    protected final JavaZlibDeflater parent;
    final ByteBuf buf;
    final DataOut out;

    JavaZlibDeflateStream(@NonNull DataOut out, @NonNull ByteBuf buf, ByteBuf dict, @NonNull JavaZlibDeflater parent) {
        this.session = ++parent.retain().sessionCounter;
        this.def = parent.deflater;
        this.parent = parent;
        this.buf = buf;
        this.out = out;

        this.def.reset();

        if (dict != null && dict.isReadable()) {
            if (dict.hasArray()) {
                this.def.setDictionary(dict.array(), dict.arrayOffset(), dict.readableBytes());
            } else {
                try (Handle<byte[]> handle = PorkUtil.BUFFER_POOL.get()) {
                    byte[] arr = handle.get();
                    int len = min(dict.readableBytes(), PorkUtil.BUFFER_SIZE);
                    dict.getBytes(dict.readerIndex(), arr, 0, len);
                    this.def.setDictionary(arr, 0, len);
                }
            }
        }
    }

    @Override
    protected void write0(int b) throws IOException {
        try (Handle<byte[]> handle = PorkUtil.TINY_BUFFER_POOL.get()) {
            byte[] arr = handle.get();
            arr[0] = (byte) b;
            this.write0(arr, 0, 1);
        }
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        this.def.setInput(src, start, length);
        while (!this.def.needsInput()) {
            int len = this.def.deflate(this.buf.array(), this.buf.arrayOffset(), this.buf.capacity());
            if (len > 0) {
                this.out.write(this.buf.array(), this.buf.arrayOffset(), len);
            }
        }
    }

    @Override
    protected void flush0() throws IOException {
        int len;
        while ((len = this.def.deflate(this.buf.array(), this.buf.arrayOffset(), this.buf.capacity(), Deflater.SYNC_FLUSH)) > 0) {
            this.out.write(this.buf.array(), this.buf.arrayOffset(), len);
            if (len < this.buf.capacity()) {
                break;
            }
        }
    }

    @Override
    protected void close0() throws IOException {
        this.ensureValidSession();

        this.def.finish();
        while (!this.def.finished()) {
            int len = this.def.deflate(this.buf.array(), this.buf.arrayOffset(), this.buf.capacity());
            if (len > 0) {
                this.out.write(this.buf.array(), this.buf.arrayOffset(), len);
            }
        }

        this.out.close();
        this.buf.release();
        this.parent.release();
        this.def.reset();
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
