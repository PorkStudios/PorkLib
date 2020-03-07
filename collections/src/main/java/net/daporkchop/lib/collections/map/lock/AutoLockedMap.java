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
import net.daporkchop.lib.collections.collection.PCollections;
import net.daporkchop.lib.collections.collection.lock.LockedCollection;
import net.daporkchop.lib.collections.set.PSets;
import net.daporkchop.lib.collections.set.lock.LockedSet;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Wrapper implementation of {@link LockedMap} around a normal {@link Map} and a {@link Lock} that automatically locks and unlocks the resource
 * for every method call.
 *
 * @author DaPorkchop_
 */
public class AutoLockedMap<K, V> extends DefaultLockedMap<K, V> {
    public AutoLockedMap(@NonNull Map<K, V> delegate) {
        super(delegate);
    }

    public AutoLockedMap(@NonNull Map<K, V> delegate, @NonNull Lock lock) {
        super(delegate, lock);
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

    //
    //
    // map methods
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
    public boolean containsKey(Object key) {
        this.lock.lock();
        try {
            return super.containsKey(key);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.lock.lock();
        try {
            return super.containsValue(value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        this.lock.lock();
        try {
            return super.get(key);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        this.lock.lock();
        try {
            return super.put(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        this.lock.lock();
        try {
            return super.remove(key);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.lock.lock();
        try {
            super.putAll(m);
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
    protected LockedSet<K> keySet0() {
        return PSets.lockedAuto(this.delegate.keySet(), this.lock);
    }

    @Override
    protected LockedCollection<V> values0() {
        return PCollections.lockedAuto(this.delegate.values(), this.lock);
    }

    @Override
    protected LockedSet<Entry<K, V>> entrySet0() {
        return PSets.lockedAuto(this.delegate.entrySet(), this.lock);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        this.lock.lock();
        try {
            return super.getOrDefault(key, defaultValue);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.lock.lock();
        try {
            super.forEach(action);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.lock.lock();
        try {
            super.replaceAll(function);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        this.lock.lock();
        try {
            return super.putIfAbsent(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.lock.lock();
        try {
            return super.remove(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.lock.lock();
        try {
            return super.replace(key, oldValue, newValue);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        this.lock.lock();
        try {
            return super.replace(key, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        this.lock.lock();
        try {
            return super.computeIfAbsent(key, mappingFunction);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.lock.lock();
        try {
            return super.computeIfPresent(key, remappingFunction);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.lock.lock();
        try {
            return super.compute(key, remappingFunction);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        this.lock.lock();
        try {
            return super.merge(key, value, remappingFunction);
        } finally {
            this.lock.unlock();
        }
    }
}
