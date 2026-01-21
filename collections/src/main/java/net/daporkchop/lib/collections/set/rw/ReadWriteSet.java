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

import net.daporkchop.lib.collections.collection.rw.ReadWriteCollection;
import net.daporkchop.lib.collections.set.lock.LockedSet;

import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

/**
 * A {@link Set} that uses a read-write lock to manage access to it.
 * <p>
 * Unlike a {@link Set} returned by {@link java.util.Collections#synchronizedSet(Set)}, of this interface are NOT expected to automatically lock and
 * unlock for every method invocation. This allows locking to be done manually by the user, providing performance in exchange for possible lack of
 * safety when used incorrectly.
 *
 * @author DaPorkchop_
 */
public interface ReadWriteSet<V> extends Set<V>, ReadWriteCollection<V> {
    /**
     * Gets a view of this {@link ReadWriteSet} as a {@link LockedSet} which locks using this collection's read lock.
     * <p>
     * Note that the returned {@link LockedSet} will only be able to obtain a read lock, which can make this unsafe to use in the event that a method
     * on the returned {@link LockedSet} is called that would modify the contents.
     *
     * @return a view of this {@link ReadWriteSet} as a {@link LockedSet} which locks using this collection's read lock
     */
    LockedSet<V> readLocked();

    /**
     * Gets a view of this {@link ReadWriteCollection} as a {@link LockedSet} which locks using this collection's write lock.
     * <p>
     * Note that the returned {@link LockedSet} will only be able to obtain a write lock, which can lead to suboptimal performance if the returned
     * {@link LockedSet} is used for read-only operations.
     *
     * @return a view of this {@link ReadWriteCollection} as a {@link LockedSet} which locks using this collection's write lock
     */
    LockedSet<V> writeLocked();

    @Override
    Lock readLock();

    @Override
    Lock writeLock();

    /**
     * Gets an {@link Iterator} over the contents of this set.
     * <p>
     * Synchronization over the returned iterator must be handled manually by the user.
     *
     * @see Set#iterator()
     */
    @Override
    Iterator<V> iterator();

    /**
     * Gets a {@link Spliterator} over the contents of this set.
     * <p>
     * Synchronization over the returned spliterator must be handled manually by the user.
     *
     * @see Set#spliterator()
     */
    @Override
    Spliterator<V> spliterator();

    /**
     * Gets a {@link Stream} over the contents of this set.
     * <p>
     * Synchronization over the returned stream must be handled manually by the user.
     *
     * @see Set#stream()
     */
    @Override
    Stream<V> stream();

    /**
     * Gets a {@link Stream} over the contents of this set.
     * <p>
     * Synchronization over the returned stream must be handled manually by the user.
     *
     * @see Set#parallelStream()
     */
    @Override
    Stream<V> parallelStream();
}
