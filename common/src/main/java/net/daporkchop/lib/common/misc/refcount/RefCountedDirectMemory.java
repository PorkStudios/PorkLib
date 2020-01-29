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

package net.daporkchop.lib.common.misc.refcount;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A reference-counted block of direct memory.
 *
 * @author DaPorkchop_
 */
@ToString
@Getter
@Accessors(fluent = true)
public final class RefCountedDirectMemory extends AbstractRefCounted {
    private final long addr;
    private final long size;

    private final PCleaner cleaner;

    public RefCountedDirectMemory(long size)    {
        this(PUnsafe.allocateMemory(size), size, true);
    }

    public RefCountedDirectMemory(long addr, long size, boolean doFree)    {
        this.addr = addr;
        this.size = size;
        this.cleaner = doFree ? PCleaner.cleaner(this, addr) : null;
    }

    @Override
    public RefCountedDirectMemory retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        if (this.cleaner != null)   {
            this.cleaner.clean();
        }
    }
}
