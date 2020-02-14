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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.compression.zstd.ZstdCCtx;
import net.daporkchop.lib.compression.zstd.ZstdDCtx;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.util.AbstractReleasable;

/**
 * Native implementation of {@link ZstdCCtx}.
 *
 * @author DaPorkchop_
 */
final class NativeZstdDCtx extends AbstractReleasable implements ZstdDCtx {
    private static native long allocateCtx();

    private static native void releaseCtx(long ctx);

    private final long     ctx     = allocateCtx();
    private final PCleaner cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        int val = this.doDecompress(this.ctx,
                this.assertAcceptable(src).memoryAddress() + src.readerIndex(), src.readableBytes(),
                this.assertAcceptable(dst).memoryAddress() + dst.writerIndex(), dst.writableBytes());

        if (val >= 0)    {
            src.skipBytes(src.readableBytes());
            dst.writerIndex(dst.writerIndex() + val);
            return true;
        } else {
            return false;
        }
    }

    private native int doDecompress(long ctx, long srcAddr, int srcSize, long dstAddr, int dstSize);

    @Override
    public boolean directAccepted() {
        return true;
    }

    @Override
    protected void doRelease() {
        this.cleaner.clean();
    }

    @RequiredArgsConstructor
    private static final class Releaser implements Runnable {
        private final long ctx;

        @Override
        public void run() {
            releaseCtx(this.ctx);
        }
    }
}