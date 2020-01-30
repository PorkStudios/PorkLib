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

package net.daporkchop.lib.compression.zlib.natives;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.compression.PDeflater;
import net.daporkchop.lib.compression.util.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.util.AbstractReleasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * @author DaPorkchop_
 */
public final class NativeZlibDeflater extends AbstractReleasable implements PDeflater {
    static native void load();

    private static native long allocateCtx(int level, int strategy, int mode);

    private static native void resetCtx(long ctx);

    private static native void releaseCtx(long ctx);

    private final long ctx;

    private final PCleaner cleaner;

    private ByteBuf src;
    private ByteBuf dst;

    private int readBytes;
    private int writtenBytes;

    public NativeZlibDeflater(int level, int strategy, int mode)    {
        this.ctx = allocateCtx(level, strategy, mode);
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    public boolean deflate(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        return false;
    }

    private native boolean doDeflate(long srcAddr, int srcSize, long dstAddr, int dstSize);

    @Override
    public PDeflater reset() {
        this.src = null;
        this.dst = null;

        this.readBytes = 0;
        this.writtenBytes = 0;

        resetCtx(this.ctx);
        return this;
    }

    @Override
    public PDeflater src(@NonNull ByteBuf src) throws InvalidBufferTypeException {
        if (!src.hasMemoryAddress())    {
            throw new InvalidBufferTypeException(true);
        }
        this.src = src;
        return this;
    }

    @Override
    public PDeflater dst(@NonNull ByteBuf dst) throws InvalidBufferTypeException {
        if (!dst.hasMemoryAddress())    {
            throw new InvalidBufferTypeException(true);
        }
        this.dst = dst;
        return this;
    }

    @Override
    public boolean direct() {
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
