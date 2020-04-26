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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class NativeZlibDeflater extends AbstractRefCounted.Synchronized implements ZlibDeflater {
    static native long allocate0(int level, int mode, int strategy);

    static native void release0(long ctx);

    static native boolean compress0(long ctx, long src, int srcLen, long dst, int dstLen, long dict, int dictLen);

    protected final long ctx;

    @Getter
    protected final ZlibDeflaterOptions options;

    protected final PCleaner cleaner;

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
        if (src.hasMemoryAddress() && dst.hasMemoryAddress()) {
            //if both buffers are direct we can do it in one shot
            long srcAddr = src.memoryAddress() + src.readerIndex();
            int srcLen = src.readableBytes();
            long dstAddr = dst.memoryAddress() + dst.writerIndex();
            int dstLen = dst.writableBytes();

            boolean success;
            if (dict == null) {
                //no dictionary will be used
                success = compress0(this.ctx, srcAddr, srcLen, dstAddr, dstLen, 0L, 0);
            } else if (dict.hasMemoryAddress()) {
                //dictionary is direct (fast)
                success = compress0(this.ctx, srcAddr, srcLen, dstAddr, dstLen, dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
            } else {
                //dictionary is not direct, copy it to a direct buffer first
                try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
                    ByteBuffer buffer = (ByteBuffer) handle.get().clear();
                    buffer.limit(min(buffer.capacity(), dict.readableBytes()));
                    dict.getBytes(dict.readerIndex(), buffer);
                    buffer.position(0);
                    success = compress0(this.ctx, srcAddr, srcLen, dstAddr, dstLen, PUnsafe.pork_directBufferAddress(buffer), buffer.limit());
                }
            }

            if (success) {
                src.skipBytes(toInt(this.getRead(), "read"));
                dst.writerIndex(dst.writerIndex() + toInt(this.getWritten(), "written"));
            }
            return success;
        }
        return false;
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, @NonNull ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException {
        this.ensureNotReleased();
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public NativeZlibDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    protected long getRead()    {
        return PUnsafe.getLong(this.ctx);
    }

    protected long getWritten()    {
        return PUnsafe.getLong(this.ctx + 8L);
    }

    protected long getSession()    {
        return PUnsafe.getLong(this.ctx + 16L);
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
