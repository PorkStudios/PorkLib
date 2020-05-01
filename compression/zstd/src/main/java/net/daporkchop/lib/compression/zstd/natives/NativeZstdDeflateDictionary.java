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
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class NativeZstdDeflateDictionary extends AbstractRefCounted implements ZstdDeflateDictionary {
    private static native long digestD0(long dict, int dictLen, int level);

    private static native long digestH0(byte[] dict, int dictOff, int dictLen, int level);

    private static native void release0(long dict);

    @Getter(AccessLevel.PACKAGE)
    private final long dict;

    @Getter(AccessLevel.NONE)
    private final PCleaner cleaner;

    private final NativeZstd provider;
    private final int        level;

    NativeZstdDeflateDictionary(@NonNull NativeZstd provider, @NonNull ByteBuf dict, int level) {
        this.provider = provider;
        this.level = level;

        if (dict.hasMemoryAddress())    {
            this.dict = digestD0(dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(), level);
        } else if (dict.hasArray()) {
            this.dict = digestH0(dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes(), level);
        } else {
            ByteBuf buf = Unpooled.directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                this.dict = digestD0(buf.memoryAddress() + buf.readerIndex(), buf.readableBytes(), level);
            } finally {
                buf.release();
            }
        }

        this.cleaner = PCleaner.cleaner(this, new Releaser(this.dict));
    }

    @Override
    protected void doRelease() {
        this.cleaner.clean();
    }

    @Override
    public ZstdDeflateDictionary retain() throws AlreadyReleasedException {
        super.retain();
        return this;
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
