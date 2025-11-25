/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.unsafe.PCleaner;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class NativeZstdInflateDictionary implements ZstdDecompressDictionary {
    private static native long digest(
            long dictDirectAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining);

    private static native void release0(long dict);

    private static native int id0(long dict);

    @Getter(AccessLevel.PACKAGE)
    private final long addr;

    @Getter
    private final int id;

    private final PCleaner cleaner;

    @Getter
    private final NativeZstdFactory provider;

    NativeZstdInflateDictionary(@NonNull NativeZstdFactory provider, @NonNull ByteBuf dict) {
        this.provider = provider;

        if (dict.hasMemoryAddress()) {
            this.addr = digest(dict.memoryAddress(), null, 0, dict.readerIndex(), dict.readableBytes());
        } else if (dict.hasArray()) {
            this.addr = digest(0L, dict.array(), dict.arrayOffset(), dict.readerIndex(), dict.readableBytes());
        } else {
            ByteBuf buf = dict.alloc().directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                this.addr = digest(buf.memoryAddress(), null, 0, buf.readerIndex(), buf.readableBytes());
            } finally {
                buf.release();
            }
        }

        this.cleaner = PCleaner.cleaner(this, new Releaser(this.addr));

        this.id = id0(this.addr);
    }

    @Override
    public void close() {
        this.cleaner.clean();
    }

    @RequiredArgsConstructor
    private static final class Releaser implements Runnable {
        private final long addr;

        @Override
        public void run() {
            release0(this.addr);
        }
    }
}
