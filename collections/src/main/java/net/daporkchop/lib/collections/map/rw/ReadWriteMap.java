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

package net.daporkchop.lib.collections.map.rw;

import net.daporkchop.lib.collections.collection.lock.LockedCollection;
import net.daporkchop.lib.collections.collection.rw.ReadWriteCollection;
import net.daporkchop.lib.collections.map.lock.LockedMap;
import net.daporkchop.lib.collections.set.lock.LockedSet;
import net.daporkchop.lib.collections.set.rw.ReadWriteSet;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * A {@link Map} that uses a {@link ReadWriteLock} to manage access to it rather than synchronizing on a mutex object.
 * <p>
 * Unlike a {@link Map} returned by {@link java.util.Collections#synchronizedMap(Map)}, of this interface are NOT expected to automatically lock and
 * unlock for every method invocation. This allows locking to be done manually by the user, providing performance in exchange for possible lack of
 * safety when used incorrectly.
 *
 * @author DaPorkchop_
 */
public interface ReadWriteMap<K, V> extends Map<K, V>, ReadWriteLock {
    /**
     * Gets a view of this {@link ReadWriteMap} as a {@link LockedMap} which locks using this collection's read lock.
     * <p>
     * Note that the returned {@link LockedMap} will only be able to obtain a read lock, which can make this unsafe to use in the event that a method
     * on the returned {@link LockedMap} is called that would modify the contents.
     *
     * @return a view of this {@link ReadWriteMap} as a {@link LockedMap} which locks using this collection's read lock
     */
    LockedMap<K, V> readLocked();

    /**
     * Gets a view of this {@link ReadWriteMap} as a {@link LockedMap} which locks using this collection's write lock.
     * <p>
     * Note that the returned {@link LockedMap} will only be able to obtain a write lock, which can lead to suboptimal performance if the returned
     * {@link LockedMap} is used for read-only operations.
     *
     * @return a view of this {@link ReadWriteMap} as a {@link LockedMap} which locks using this collection's write lock
     */
    LockedMap<K, V> writeLocked();

    @Override
    Lock readLock();

    @Override
    Lock writeLock();

    @Override
    ReadWriteSet<K> keySet();

    @Override
    ReadWriteCollection<V> values();

    @Override
    ReadWriteSet<Entry<K, V>> entrySet();

    /**
     * @see #readLocked()
     * @see LockedMap#keySet()
     */
    default LockedSet<K> readLockedKeySet() {
        return this.readLocked().keySet();
    }

    /**
     * @see #writeLocked()
     * @see LockedMap#keySet()
     */
    default LockedSet<K> writeLockedKeySet() {
        return this.writeLocked().keySet();
    }

    /**
     * @see #readLocked()
     * @see LockedMap#values()
     */
    default LockedCollection<V> readLockedValues() {
        return this.readLocked().values();
    }

    /**
     * @see #writeLocked()
     * @see LockedMap#values()
     */
    default LockedCollection<V> writeLockedValues() {
        return this.writeLocked().values();
    }

    /**
     * @see #readLocked()
     * @see LockedMap#entrySet()
     */
    default LockedSet<Entry<K, V>> readLockedEntrySet() {
        return this.readLocked().entrySet();
    }

    /**
     * @see #writeLocked()
     * @see LockedMap#entrySet()
     */
    default LockedSet<Entry<K, V>> writeLockedEntrySet() {
        return this.writeLocked().entrySet();
    }
}
