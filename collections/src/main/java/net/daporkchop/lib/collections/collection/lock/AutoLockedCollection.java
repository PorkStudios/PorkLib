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

package net.daporkchop.lib.collections.collection.lock;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Wrapper implementation of {@link LockedCollection} around a normal {@link Collection} and a {@link Lock} that automatically locks and unlocks
 * the resource for every method call.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class AutoLockedCollection<V> implements LockedCollection<V> {
    @NonNull
    protected final Collection<V> delegate;
    @NonNull
    protected final Lock          lock;

    public AutoLockedCollection(@NonNull Collection<V> delegate) {
        this(delegate, new ReentrantLock());
    }

    //
    //
    // lock methods
    //
    //

    @Override
    public AutoLockedCollection<V> lockAndGet() {
        this.lock.lock();
        return this;
    }

    @Override
    public AutoLockedCollection<V> lockAndGetInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
        return this;
    }

    @Override
    public void lock() {
        this.lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return this.lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return this.lock.newCondition();
    }

    //
    //
    // collection methods
    //
    //

    @Override
    public int size() {
        this.lock.lock();
        try {
            return this.delegate.size();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.delegate.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.lock.lock();
        try {
            return this.delegate.contains(o);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public Iterator<V> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        this.lock.lock();
        try {
            return this.delegate.toArray();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        this.lock.lock();
        try {
            return this.delegate.toArray(a);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean add(V v) {
        this.lock.lock();
        try {
            return this.delegate.add(v);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        this.lock.lock();
        try {
            return this.delegate.remove(o);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        this.lock.lock();
        try {
            return this.delegate.containsAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        this.lock.lock();
        try {
            return this.delegate.addAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.lock.lock();
        try {
            return this.delegate.removeAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.lock.lock();
        try {
            return this.delegate.retainAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.delegate.clear();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        this.lock.lock();
        try {
            return this.delegate.removeIf(filter);
        } finally {
            this.lock.unlock();
        }
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

    @Override
    public void forEach(Consumer<? super V> action) {
        this.lock.lock();
        try {
            this.delegate.forEach(action);
        } finally {
            this.lock.unlock();
        }
    }
}
