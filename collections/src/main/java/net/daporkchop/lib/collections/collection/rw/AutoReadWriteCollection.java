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

package net.daporkchop.lib.collections.collection.rw;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.collections.collection.PCollections;
import net.daporkchop.lib.collections.collection.lock.LockedCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Wrapper implementation of {@link ReadWriteCollection} around a normal {@link Collection} and a {@link ReadWriteLock} that automatically locks
 * and unlocks the resource for every method call.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class AutoReadWriteCollection<V> implements ReadWriteCollection<V> {
    @NonNull
    protected final Collection<V> delegate;

    @Getter
    @NonNull
    protected final Lock readLock;
    @Getter
    @NonNull
    protected final Lock writeLock;

    protected volatile LockedCollection<V> readLocked;
    protected volatile LockedCollection<V> writeLocked;

    public AutoReadWriteCollection(@NonNull Collection<V> delegate) {
        this(delegate, new ReentrantReadWriteLock());
    }

    public AutoReadWriteCollection(@NonNull Collection<V> delegate, @NonNull ReadWriteLock lock) {
        this(delegate, lock.readLock(), lock.writeLock());
    }

    @Override
    public LockedCollection<V> readLocked() {
        LockedCollection<V> readLocked = this.readLocked;
        if (readLocked == null) {
            synchronized (this.delegate) {
                if ((readLocked = this.readLocked) == null) {
                    this.readLocked = readLocked = PCollections.lockedAuto(this.delegate, this.readLock);
                }
            }
        }
        return readLocked;
    }

    @Override
    public LockedCollection<V> writeLocked() {
        LockedCollection<V> writeLocked = this.writeLocked;
        if (writeLocked == null) {
            synchronized (this.delegate) {
                if ((writeLocked = this.writeLocked) == null) {
                    this.writeLocked = writeLocked = PCollections.lockedAuto(this.delegate, this.writeLock);
                }
            }
        }
        return writeLocked;
    }

    //
    //
    // collection methods
    //
    //

    @Override
    public int size() {
        this.readLock.lock();
        try {
            return this.delegate.size();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.readLock.lock();
        try {
            return this.delegate.isEmpty();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.readLock.lock();
        try {
            return this.delegate.contains(o);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        this.readLock.lock();
        try {
            return this.delegate.toArray();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        this.readLock.lock();
        try {
            return this.delegate.toArray(a);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean add(V v) {
        this.writeLock.lock();
        try {
            return this.delegate.add(v);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        this.writeLock.lock();
        try {
            return this.delegate.remove(o);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        this.readLock.lock();
        try {
            return this.delegate.containsAll(c);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        this.writeLock.lock();
        try {
            return this.delegate.addAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.writeLock.lock();
        try {
            return this.delegate.removeAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.writeLock.lock();
        try {
            return this.delegate.retainAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        this.writeLock.lock();
        try {
            this.delegate.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        this.writeLock.lock();
        try {
            return this.delegate.removeIf(filter);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void forEach(Consumer<? super V> action) {
        this.readLock.lock();
        try {
            this.delegate.forEach(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Iterator<V> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Spliterator<V> spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public Stream<V> stream() {
        return this.delegate.stream();
    }

    @Override
    public Stream<V> parallelStream() {
        return this.delegate.parallelStream();
    }
}
