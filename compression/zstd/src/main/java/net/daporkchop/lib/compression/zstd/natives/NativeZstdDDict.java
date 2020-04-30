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
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.zstd.ZstdDDict;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class NativeZstdDDict extends AbstractRefCounted implements ZstdDDict {
    private static native long digest0(long dictAddr, int dictSize);

    private static native void release0(long dict);

    @Getter(AccessLevel.PACKAGE)
    private final long dict;

    private final PCleaner cleaner;

    @Getter
    private final NativeZstd provider;

    NativeZstdDDict(@NonNull NativeZstd provider, @NonNull ByteBuf dict) {
        this.provider = provider;

        if (dict.hasMemoryAddress())    {
            this.dict = digest0(dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
        } else {
            ByteBuf buf = Unpooled.directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                this.dict = digest0(buf.memoryAddress() + buf.readerIndex(), buf.readableBytes());
            } finally {
                buf.release();
            }
        }

        this.cleaner = PCleaner.cleaner(this, new Releaser(this.dict));
    }

    @Override
    public ZstdDDict retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.cleaner.clean();
    }

    @RequiredArgsConstructor
    private static final class Releaser implements Runnable {
        private final long    dict;

        @Override
        public void run() {
            release0(this.dict);
        }
    }
}
