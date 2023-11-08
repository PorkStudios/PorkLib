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

package net.daporkchop.lib.http.entity.transfer;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * A simple {@link TransferSession} that simply returns data stored in a single {@link ByteBuf}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class ByteBufTransferSession implements TransferSession {
    @NonNull
    protected final ByteBuf buf;

    @Override
    public long position() throws Exception {
        return this.buf.readerIndex();
    }

    @Override
    public long length() throws Exception {
        return this.buf.readableBytes();
    }

    @Override
    public long transfer(long position, @NonNull WritableByteChannel out) throws Exception {
        long base = this.position();
        long length = this.length();
        if (position >= base + length || position < base) {
            throw new IndexOutOfBoundsException(String.format("position=%d, length=%d, requested=%d", base, length, position));
        }
        return out.write(this.buf.nioBuffer((int) position, (int) (length - (base - position))));
    }

    @Override
    public long transferAllBlocking(long position, @NonNull WritableByteChannel out) throws Exception {
        long base = this.position();
        long length = this.length();
        if (position >= base + length || position < base) {
            throw new IndexOutOfBoundsException(String.format("position=%d, length=%d, requested=%d", base, length, position));
        }
        long remaining = length - (base - position);
        ByteBuffer buffer = this.buf.nioBuffer((int) position, (int) remaining);
        while (buffer.hasRemaining())   {
            out.write(buffer);
        }
        return remaining;
    }

    @Override
    public boolean hasByteBuf() {
        return true;
    }

    @Override
    public ByteBuf getByteBuf() throws Exception {
        return this.buf.retainedSlice();
    }

    @Override
    public boolean reusable() {
        return true;
    }

    @Override
    public void retain() {
        this.buf.retain();
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }
}
