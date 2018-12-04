/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container.atomiclong;

import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.PorkDB;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public interface DBAtomicLong<C extends DBAtomicLong<? extends DBAtomicLong, B, DB>, B extends Container.Builder<AtomicLong, C, DB>, DB extends PorkDB<DB, ? extends Container>> extends Container<AtomicLong, B, DB> {
    default long get() {
        return this.getValue().get();
    }

    default long getAndSet(long val) {
        this.markDirty();
        return this.getValue().getAndSet(val);
    }

    default long addAndGet(long val) {
        this.markDirty();
        return this.getValue().addAndGet(val);
    }

    default long getAndAdd(long val) {
        this.markDirty();
        return this.getValue().getAndAdd(val);
    }

    default long getAndIncrement() {
        this.markDirty();
        return this.getValue().getAndIncrement();
    }

    default long incrementAndGet() {
        this.markDirty();
        return this.getValue().incrementAndGet();
    }

    default long getAndDecrement() {
        this.markDirty();
        return this.getValue().getAndDecrement();
    }

    default long decrementAndGet() {
        this.markDirty();
        return this.getValue().decrementAndGet();
    }
}
