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

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Wrapper implementation of {@link LockedCollection} around a normal {@link Collection} and a {@link Lock} that automatically locks and unlocks
 * the resource for every method call.
 *
 * @author DaPorkchop_
 */
public class AutoLockedCollection<V> extends DefaultLockedCollection<V> {
    public AutoLockedCollection(@NonNull Collection<V> delegate) {
        super(delegate);
    }

    public AutoLockedCollection(@NonNull Collection<V> delegate, @NonNull Lock lock) {
        super(delegate, lock);
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

    //
    //
    // collection methods
    //
    //

    @Override
    public int size() {
        this.lock.lock();
        try {
            return super.size();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.lock.lock();
        try {
            return super.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.lock.lock();
        try {
            return super.contains(o);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        this.lock.lock();
        try {
            return super.toArray();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        this.lock.lock();
        try {
            return super.toArray(a);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean add(V v) {
        this.lock.lock();
        try {
            return super.add(v);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        this.lock.lock();
        try {
            return super.remove(o);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        this.lock.lock();
        try {
            return super.containsAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        this.lock.lock();
        try {
            return super.addAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.lock.lock();
        try {
            return super.removeAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.lock.lock();
        try {
            return super.retainAll(c);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void clear() {
        this.lock.lock();
        try {
            super.clear();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<? super V> filter) {
        this.lock.lock();
        try {
            return super.removeIf(filter);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void forEach(Consumer<? super V> action) {
        this.lock.lock();
        try {
            super.forEach(action);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public Iterator<V> iterator() {
        return super.iterator();
    }

    @Override
    public Spliterator<V> spliterator() {
        return super.spliterator();
    }

    @Override
    public Stream<V> stream() {
        return super.stream();
    }

    @Override
    public Stream<V> parallelStream() {
        return super.parallelStream();
    }
}
