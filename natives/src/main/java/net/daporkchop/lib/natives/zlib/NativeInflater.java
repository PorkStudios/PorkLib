/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.natives.zlib;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Implementation of {@link PInflater} using native code.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class NativeInflater implements PInflater {
    static native void load();

    private static native long init(boolean nowrap);

    private static native void end(long ctx);

    @Getter(AccessLevel.NONE)
    private final long ctx;
    private long readBytes;
    private long writtenBytes;

    @Getter(AccessLevel.NONE)
    private final PCleaner cleaner;

    private boolean finished;

    NativeInflater(boolean nowrap) {
        long ctx = this.ctx = init(nowrap);
        this.cleaner = PCleaner.cleaner(this, () -> end(ctx));
    }

    @Override
    public native void input(long addr, long size);

    @Override
    public native void output(long addr, long size);

    @Override
    public native void inflate();

    @Override
    public native void reset();

    @Override
    public void release() throws AlreadyReleasedException {
        if (!this.cleaner.tryClean()) {
            throw new AlreadyReleasedException();
        }
    }
}
