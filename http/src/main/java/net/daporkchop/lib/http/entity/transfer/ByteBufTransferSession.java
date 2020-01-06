/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
public final class ByteBufTransferSession implements TransferSession {
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
    public boolean hasNioBuffer() {
        return true;
    }

    @Override
    public ByteBuffer getNioBuffer() throws Exception {
        return this.buf.nioBuffer();
    }

    @Override
    public void close() throws Exception {
        this.buf.release();
    }
}
