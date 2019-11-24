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

package net.daporkchop.lib.minecraft.util.world;

import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * Abstract implementation of {@link Dirtiable}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDirtiable implements Dirtiable {
    protected static final long DIRTY_OFFSET = PUnsafe.pork_getOffset(AbstractDirtiable.class, "dirty");

    private volatile int dirty = 0;

    @Override
    public boolean dirty() {
        return this.dirty != 0;
    }

    @Override
    public boolean markDirty() {
        return PUnsafe.compareAndSwapInt(this, DIRTY_OFFSET, 0, 1);
    }

    /**
     * Clears this instance's dirty flag.
     */
    protected void resetDirty() {
        this.dirty = 0;
    }

    /**
     * Clears this instance's dirty flag.
     *
     * @return whether this instance was dirty prior to this method invocation
     */
    protected boolean checkAndResetDirty() {
        return PUnsafe.compareAndSwapInt(this, DIRTY_OFFSET, 1, 0);
    }
}
