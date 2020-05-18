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
public class AutoReadWriteCollection<V> extends DefaultReadWriteCollection<V> {
    public AutoReadWriteCollection(@NonNull Collection<V> delegate) {
        super(delegate);
    }

    public AutoReadWriteCollection(@NonNull Collection<V> delegate, @NonNull ReadWriteLock lock) {
        super(delegate, lock);
    }

    public AutoReadWriteCollection(@NonNull Collection<V> delegate, @NonNull Lock readLock, @NonNull Lock writeLock) {
        super(delegate, readLock, writeLock);
    }

    @Override
    protected LockedCollection<V> readLocked0() {
        return PCollections.lockedAuto(this.delegate, this.readLock);
    }

    @Override
    protected LockedCollection<V> writeLocked0() {
        return PCollections.lockedAuto(this.delegate, this.writeLock);
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
            return super.size();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.readLock.lock();
        try {
            return super.isEmpty();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.readLock.lock();
        try {
            return super.contains(o);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        this.readLock.lock();
        try {
            return super.toArray();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        this.readLock.lock();
        try {
            return super.toArray(a);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean add(V v) {
        this.writeLock.lock();
        try {
            return super.add(v);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        this.writeLock.lock();
        try {
            return super.remove(o);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        this.readLock.lock();
        try {
            return super.containsAll(c);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        this.writeLock.lock();
        try {
            return super.addAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.writeLock.lock();
        try {
            return super.removeAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.writeLock.lock();
        try {
            return super.retainAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        this.writeLock.lock();
        try {
            super.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        this.writeLock.lock();
        try {
            return super.removeIf(filter);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void forEach(Consumer<? super V> action) {
        this.readLock.lock();
        try {
            super.forEach(action);
        } finally {
            this.readLock.unlock();
        }
    }
}
