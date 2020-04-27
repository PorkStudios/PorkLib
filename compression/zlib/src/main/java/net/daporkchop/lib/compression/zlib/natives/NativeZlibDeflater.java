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
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.natives.NativeException;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zlib.natives.NativeZlib.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class NativeZlibDeflater extends AbstractRefCounted.Synchronized implements ZlibDeflater {
    static native long allocate0(int level, int mode, int strategy);

    static native void release0(long ctx);

    static native long newSession0(long ctx, long dict, int dictLen);

    //static native boolean compress0(long ctx, long src, int srcLen, long dst, int dstLen);

    static native int update0(long ctx, long src, int srcLen, long dst, int dstLen, int flush);

    final long ctx;

    @Getter
    final ZlibDeflaterOptions options;

    final PCleaner cleaner;

    NativeZlibDeflater(@NonNull ZlibDeflaterOptions options) {
        checkArg(options.provider() instanceof NativeZlib, "provider must be %s!", NativeZlib.class);
        this.options = options;

        this.ctx = allocate0(options.level(), options.mode().ordinal(), options.strategy().ordinal());
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    protected void doRelease() {
        checkState(this.cleaner.clean(), "already cleaned?!?");
    }

    @Override
    public synchronized boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException {
        this.ensureNotReleased();
        long session = -1L;
        try {
            if (dict == null || dict.readableBytes() == 0) {
                //empty dictionary, no dictionary will be used
                session = newSession0(this.ctx, 0L, 0);
            } else if (dict.hasMemoryAddress()) {
                //dictionary is direct (this is fast)
                session = newSession0(this.ctx, dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
            } else {
                //dictionary isn't direct, we have to manually copy it into a new buffer
                try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
                    //this buffer is more than large enough to handle the largest valid dictionary, if a dictionary is WAY too large it'll probably break but you shouldn't be trying to do that in the first place :P
                    ByteBuffer buffer = handle.get();
                    buffer.clear().limit(min(buffer.capacity(), dict.readableBytes()));
                    dict.getBytes(dict.readerIndex(), buffer);
                    session = newSession0(this.ctx, PUnsafe.pork_directBufferAddress(buffer.position(0)), buffer.limit());
                }
            }

            if (src.hasMemoryAddress() && dst.hasMemoryAddress()) {
                //if both buffers are direct we can do it in one shot
                long srcAddr = src.memoryAddress() + src.readerIndex();
                int srcLen = src.readableBytes();
                long dstAddr = dst.memoryAddress() + dst.writerIndex();
                int dstLen = dst.writableBytes();

                int status = update0(this.ctx, srcAddr, srcLen, dstAddr, dstLen, Z_FINISH);
                if (status == Z_STREAM_END) {
                    src.skipBytes(toInt(this.getRead(), "read"));
                    dst.writerIndex(dst.writerIndex() + toInt(this.getWritten(), "written"));
                    return true;
                } else if (status == Z_OK) {
                    return false;
                } else {
                    throw new NativeException(status);
                }
            } else {
                //TODO: something
            }
            return false;
        } finally {
            if (session != this.getSession()) {
                throw new ConcurrentModificationException(); //probably impossible
            }
        }
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException {
        this.ensureNotReleased();
        if (bufferAlloc == null)    {
            bufferAlloc = PooledByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0)    {
            bufferSize = PorkUtil.BUFFER_SIZE;
        }
        return new NativeZlibDeflateStream(out, bufferAlloc.directBuffer(bufferSize, bufferSize), dict, this);
    }

    @Override
    public NativeZlibDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    protected long getRead() {
        return PUnsafe.getLongVolatile(null, this.ctx);
    }

    protected long getWritten() {
        return PUnsafe.getLongVolatile(null, this.ctx + 8L);
    }

    protected long getSession() {
        return PUnsafe.getLongVolatile(null, this.ctx + 16L);
    }

    @RequiredArgsConstructor
    private static final class Releaser implements Runnable {
        private final long ctx;

        @Override
        public void run() {
            release0(this.ctx);
        }
    }
}
