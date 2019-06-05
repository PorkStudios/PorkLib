/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.session.encode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.Recycler;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class BinaryOut extends DataOut {
    private static final Recycler<BinaryOut> RECYCLER = new Recycler<BinaryOut>() {
        @Override
        protected BinaryOut newObject(Handle<BinaryOut> handle) {
            return new BinaryOut(handle);
        }
    };

    public static BinaryOut get(@NonNull PacketMetadata metadata, @NonNull SendCallback callback, ByteBufAllocator alloc) {
        BinaryOut out = RECYCLER.get();
        out.metadata = metadata;
        out.callback = callback;
        out.alloc = alloc == null ? PooledByteBufAllocator.DEFAULT : alloc;
        return out;
    }

    @NonNull
    private final Recycler.Handle<BinaryOut> handle;

    private PacketMetadata metadata;
    private SendCallback callback;
    private ByteBufAllocator alloc;
    private ByteBuf buf;

    @Override
    public void write(int b) throws IOException {
        this.checkRealloc().writeByte(b);
    }

    @Override
    public DataOut writeShort(short s) throws IOException {
        this.checkRealloc().writeShort(s);
        return this;
    }

    @Override
    public DataOut writeUShort(int s) throws IOException {
        this.checkRealloc().writeShort(s);
        return this;
    }

    @Override
    public DataOut writeMedium(int m) throws IOException {
        this.checkRealloc().writeMedium(m);
        return this;
    }

    @Override
    public DataOut writeInt(int i) throws IOException {
        this.checkRealloc().writeInt(i);
        return this;
    }

    @Override
    public DataOut writeLong(long l) throws IOException {
        this.checkRealloc().writeLong(l);
        return this;
    }

    @Override
    public DataOut writeBytes(@NonNull byte[] b) throws IOException {
        this.checkRealloc().writeBytes(b);
        return this;
    }

    @Override
    public DataOut writeBytes(@NonNull byte[] b, int off, int len) throws IOException {
        this.checkRealloc().writeBytes(b, off, len);
        return this;
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        this.checkRealloc().writeBytes(b, off, len);
    }

    private synchronized ByteBuf checkRealloc() {
        if (this.buf == null && this.alloc != null)   {
            this.buf = this.alloc.ioBuffer();
        }
        return this.buf;
    }

    @Override
    public synchronized void flush() throws IOException {
        this.callback.send(this.buf, this.metadata);
        this.buf = null;
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.alloc != null) {
            this.flush();

            //reset
            this.metadata = null;
            this.callback = null;
            this.alloc = null;

            this.handle.recycle(this);
        }
    }
}
