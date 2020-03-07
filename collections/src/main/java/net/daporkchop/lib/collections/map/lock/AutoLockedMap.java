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

package net.daporkchop.lib.collections.map.lock;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.collection.PCollections;
import net.daporkchop.lib.collections.collection.lock.LockedCollection;
import net.daporkchop.lib.collections.set.PSets;
import net.daporkchop.lib.collections.set.lock.LockedSet;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Wrapper implementation of {@link LockedMap} around a normal {@link Map} and a {@link Lock} that automatically locks and unlocks the resource
 * for every method call.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class AutoLockedMap<K, V> implements LockedMap<K, V> {
    @NonNull
    protected final Map<K, V> delegate;
    @NonNull
    protected final Lock      lock;

    protected LockedSet<K>           keySet;
    protected LockedCollection<V>    values;
    protected LockedSet<Entry<K, V>> entrySet;

    public AutoLockedMap(@NonNull Map<K, V> delegate) {
        this(delegate, new ReentrantLock());
    }

    //
    //
    // lock methods
    //
    //

    @Override
    public AutoLockedMap<K, V> lockAndGet() {
        this.lock.lock();
        return this;
    }

    @Override
    public AutoLockedMap<K, V> lockAndGetInterruptibly() throws InterruptedException {
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
    // map methods
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
    public boolean containsKey(Object key) {
        this.lock.lock();
        try {
            return this.delegate.containsKey(key);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.lock.lock();
        try {
            return this.delegate.containsValue(value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        this.lock.lock();
        try {
            return this.delegate.get(key);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        this.lock.lock();
        try {
            return this.delegate.put(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        this.lock.lock();
        try {
            return this.delegate.remove(key);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.lock.lock();
        try {
            this.delegate.putAll(m);
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
    public LockedSet<K> keySet() {
        this.lock.lock();
        try {
            LockedSet<K> keySet = this.keySet;
            if (keySet == null) {
                this.keySet = keySet = PSets.autoLockedSet(this.delegate.keySet(), this.lock);
            }
            return keySet;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public LockedCollection<V> values() {
        this.lock.lock();
        try {
            LockedCollection<V> values = this.values;
            if (values == null) {
                this.values = values = PCollections.autoLockedCollection(this.delegate.values(), this.lock);
            }
            return values;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public LockedSet<Entry<K, V>> entrySet() {
        this.lock.lock();
        try {
            LockedSet<Entry<K, V>> entrySet = this.entrySet;
            if (entrySet == null) {
                this.entrySet = entrySet = PSets.autoLockedSet(this.delegate.entrySet(), this.lock);
            }
            return entrySet;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        this.lock.lock();
        try {
            return this.delegate.getOrDefault(key, defaultValue);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.lock.lock();
        try {
            this.delegate.forEach(action);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.lock.lock();
        try {
            this.delegate.replaceAll(function);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        this.lock.lock();
        try {
            return this.delegate.putIfAbsent(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.lock.lock();
        try {
            return this.delegate.remove(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.lock.lock();
        try {
            return this.delegate.replace(key, oldValue, newValue);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        this.lock.lock();
        try {
            return this.delegate.replace(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        this.lock.lock();
        try {
            return this.delegate.computeIfAbsent(key, mappingFunction);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.lock.lock();
        try {
            return this.delegate.computeIfPresent(key, remappingFunction);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.lock.lock();
        try {
            return this.delegate.compute(key, remappingFunction);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        this.lock.lock();
        try {
            return this.delegate.merge(key, value, remappingFunction);
        } finally {
            this.lock.unlock();
        }
    }
}
