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

package net.daporkchop.lib.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.IllegalReferenceCountException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.common.pool.array.ArrayHandle;
import net.daporkchop.lib.common.ref.ReferenceType;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import static net.daporkchop.lib.common.util.PValidation.checkState;

/**
 * A simple {@link ArrayAllocator} which operates using a Netty {@link ByteBufAllocator}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class PooledByteArrayAllocator implements ArrayAllocator<byte[]> {
    public static final PooledByteArrayAllocator DEFAULT = new PooledByteArrayAllocator(
            PooledByteBufAllocator.DEFAULT,
            ArrayAllocator.pow2(byte[]::new, ReferenceType.SOFT, 8));

    @NonNull
    protected final ByteBufAllocator delegate;
    protected final ArrayAllocator<byte[]> exclusiveFallback;

    @Override
    public ArrayHandle<byte[]> slice(int size) {
        return new Handle(this.delegate.heapBuffer(size, size));
    }

    @Override
    public net.daporkchop.lib.common.pool.handle.Handle<byte[]> atLeast(int minSize) {
        checkState(this.exclusiveFallback != null, "exclusive allocations not supported!");
        return this.exclusiveFallback.atLeast(minSize);
    }

    @Override
    public net.daporkchop.lib.common.pool.handle.Handle<byte[]> exactly(int size) {
        checkState(this.exclusiveFallback != null, "exclusive allocations not supported!");
        return this.exclusiveFallback.exactly(size);
    }

    private static final class Handle implements ArrayHandle<byte[]> {
        protected final byte[] arr;
        protected final int offset;
        protected final int size;

        protected final ByteBuf buf;

        public Handle(@NonNull ByteBuf buf) {
            this.arr = buf.array();
            this.offset = buf.arrayOffset();
            this.size = buf.capacity();
            this.buf = buf;
        }

        @Override
        public byte[] get() {
            return this.arr;
        }

        @Override
        public int offset() {
            return this.offset;
        }

        @Override
        public int length() {
            return this.size;
        }

        @Override
        public ArrayHandle<byte[]> retain() throws AlreadyReleasedException {
            try {
                this.buf.retain();
            } catch (IllegalReferenceCountException e)  {
                throw new AlreadyReleasedException(e);
            }
            return this;
        }

        @Override
        public int refCnt() {
            return this.buf.refCnt();
        }

        @Override
        public boolean release() throws AlreadyReleasedException {
            try {
                return this.buf.release();
            } catch (IllegalReferenceCountException e)  {
                throw new AlreadyReleasedException(e);
            }
        }
    }
}
