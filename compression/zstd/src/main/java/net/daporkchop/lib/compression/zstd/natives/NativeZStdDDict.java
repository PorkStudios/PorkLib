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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
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
final class NativeZStdDDict extends AbstractRefCounted implements ZstdDDict {
    private static native long createDDict(long dictAddr, int dictSize, boolean copy);

    private static native void releaseDDict(long dict);

    @Getter(AccessLevel.PACKAGE)
    private final long dict;

    private final PCleaner cleaner;

    @Getter
    private final NativeZstd provider;

    NativeZStdDDict(@NonNull NativeZstd provider, @NonNull ByteBuf dict, boolean copy) {
        this.provider = provider;

        this.dict = createDDict(dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(), copy);

        this.cleaner = PCleaner.cleaner(this, new Releaser(this.dict, copy ? dict : null));
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
        private final ByteBuf data;

        @Override
        public void run() {
            releaseDDict(this.dict);
            if (this.data != null) {
                this.data.release();
            }
        }
    }
}
