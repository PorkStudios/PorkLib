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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;

import static net.daporkchop.lib.common.util.PValidation.checkArg;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class NativeZstdDeflater extends AbstractRefCounted.Synchronized implements ZstdDeflater {
    static native long allocate0();

    static native void release0(long ctx);

    static native long compress0(long ctx, long src, int srcLen, long dst, int dstLen, long dict, int level);

    final long ctx;

    volatile long sessionCounter;

    @Getter
    final ZstdDeflaterOptions options;

    final PCleaner cleaner;

    NativeZstdDeflater(@NonNull ZstdDeflaterOptions options) {
        this.options = options;

        this.ctx = allocate0();
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    protected void doRelease() {
        this.cleaner.clean();
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) {
        this.ensureNotReleased();
        Zstd.checkLevel(level);
        if (dict != null)   {
            try (ZstdDeflateDictionary digested = this.options.provider().loadDeflateDictionary(dict, level)) {
                return this.compress0(src, dst, digested, level, false);
            }
        } else {
            return this.compress0(src, dst, null, level, false);
        }
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) {
        this.ensureNotReleased();
        return false;
    }

    @Override
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
        Zstd.checkLevel(level);
    }

    @Override
    public synchronized void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
    }

    private synchronized boolean compress0(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict, int level, boolean grow)   {
        this.ensureNotReleased();
        if (dict != null)   {
            dict.retain();
        }
        try {
            long dictAddr = 0L;
            if (dict != null) {
                checkArg(dict instanceof NativeZstdDeflateDictionary);
                dictAddr = ((NativeZstdDeflateDictionary) dict).dict();
            }

            if (src.hasMemoryAddress() && dst.hasMemoryAddress()) {
                if (grow)   {
                } else {
                    long ret = compress0(this.ctx, src.memoryAddress() + src.readerIndex(), src.readableBytes(), dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(), dictAddr, level);
                }
            }
            throw new UnsupportedOperationException();
        } finally {
            if (dict != null)   {
                dict.release();
            }
        }
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException, DictionaryNotAllowedException {
        this.ensureNotReleased();
        throw new UnsupportedOperationException();
    }

    @Override
    public NativeZstdDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @AllArgsConstructor
    private static final class Releaser implements Runnable {
        protected final long ctx;

        @Override
        public void run() {
            release0(this.ctx);
        }
    }
}
