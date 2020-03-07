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

package net.daporkchop.lib.collections.set.rw;

import lombok.NonNull;
import net.daporkchop.lib.collections.collection.rw.DefaultReadWriteCollection;
import net.daporkchop.lib.collections.set.PSets;
import net.daporkchop.lib.collections.set.lock.LockedSet;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Wrapper implementation of {@link ReadWriteSet} around a normal {@link Set} and a {@link ReadWriteLock}.
 *
 * @author DaPorkchop_
 */
public class DefaultReadWriteSet<V> extends DefaultReadWriteCollection<V> implements ReadWriteSet<V> {
    public DefaultReadWriteSet(@NonNull Set<V> delegate) {
        super(delegate);
    }

    public DefaultReadWriteSet(@NonNull Set<V> delegate, @NonNull ReadWriteLock lock) {
        super(delegate, lock);
    }

    public DefaultReadWriteSet(@NonNull Collection<V> delegate, @NonNull Lock readLock, @NonNull Lock writeLock) {
        super(delegate, readLock, writeLock);
    }

    @Override
    public LockedSet<V> readLocked() {
        LockedSet<V> readLocked = (LockedSet<V>) this.readLocked;
        if (readLocked == null) {
            synchronized (this.delegate) {
                if ((readLocked = (LockedSet<V>) this.readLocked) == null)  {
                    this.readLocked = readLocked = PSets.locked((Set<V>) this.delegate, this.readLock);
                }
            }
        }
        return readLocked;
    }

    @Override
    public LockedSet<V> writeLocked() {
        LockedSet<V> writeLocked = (LockedSet<V>) this.writeLocked;
        if (writeLocked == null) {
            synchronized (this.delegate) {
                if ((writeLocked = (LockedSet<V>) this.writeLocked) == null)  {
                    this.writeLocked = writeLocked = PSets.locked((Set<V>) this.delegate, this.writeLock);
                }
            }
        }
        return writeLocked;
    }
}
