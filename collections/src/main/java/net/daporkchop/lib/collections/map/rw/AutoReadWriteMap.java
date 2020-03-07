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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.collections.collection.PCollections;
import net.daporkchop.lib.collections.collection.rw.ReadWriteCollection;
import net.daporkchop.lib.collections.map.PMaps;
import net.daporkchop.lib.collections.map.lock.LockedMap;
import net.daporkchop.lib.collections.set.PSets;
import net.daporkchop.lib.collections.set.rw.ReadWriteSet;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Wrapper implementation of {@link ReadWriteMap} around a normal {@link Map} and a {@link ReadWriteLock} that automatically locks and unlocks the
 * resource for every method call.
 *
 * @author DaPorkchop_
 */
public class AutoReadWriteMap<K, V> extends DefaultReadWriteMap<K, V> {
    public AutoReadWriteMap(@NonNull Map<K, V> delegate) {
        super(delegate);
    }

    public AutoReadWriteMap(@NonNull Map<K, V> delegate, @NonNull ReadWriteLock lock) {
        super(delegate, lock);
    }

    public AutoReadWriteMap(@NonNull Map<K, V> delegate, @NonNull Lock readLock, @NonNull Lock writeLock) {
        super(delegate, readLock, writeLock);
    }

    @Override
    protected LockedMap<K, V> readLocked0() {
        return PMaps.lockedAuto(this.delegate, this.readLock);
    }

    @Override
    protected LockedMap<K, V> writeLocked0() {
        return PMaps.lockedAuto(this.delegate, this.writeLock);
    }

    //
    //
    // map methods
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
    public boolean containsKey(Object key) {
        this.readLock.lock();
        try {
            return super.containsKey(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.readLock.lock();
        try {
            return super.containsValue(value);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        this.readLock.lock();
        try {
            return super.get(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        this.writeLock.lock();
        try {
            return super.put(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        this.writeLock.lock();
        try {
            return super.remove(key);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.writeLock.lock();
        try {
            super.putAll(m);
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
    protected ReadWriteSet<K> keySet0() {
        return PSets.readWriteAuto(this.delegate.keySet(), this.readLock, this.writeLock);
    }

    @Override
    protected ReadWriteCollection<V> values0() {
        return PCollections.readWriteAuto(this.delegate.values(), this.readLock, this.writeLock);
    }

    @Override
    protected ReadWriteSet<Entry<K, V>> entrySet0() {
        return PSets.readWriteAuto(this.delegate.entrySet(), this.readLock, this.writeLock);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        this.readLock.lock();
        try {
            return super.getOrDefault(key, defaultValue);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.readLock.lock();
        try {
            super.forEach(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.writeLock.lock();
        try {
            super.replaceAll(function);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        this.writeLock.lock();
        try {
            return super.putIfAbsent(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.writeLock.lock();
        try {
            return super.remove(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.writeLock.lock();
        try {
            return super.replace(key, oldValue, newValue);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        this.writeLock.lock();
        try {
            return super.replace(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        //try a normal get() first, so that we don't have to obtain a write lock if the mapping is already present
        V value = this.get(key);
        if (value == null)  {
            this.writeLock.lock();
            try {
                value = super.computeIfAbsent(key, mappingFunction);
            } finally {
                this.writeLock.unlock();
            }
        }
        return value;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        //check if the key contained in the map first, so that we don't have to obtain a write lock if the mapping is absent
        if (this.containsKey(key)) {
            this.writeLock.lock();
            try {
                return super.computeIfPresent(key, remappingFunction);
            } finally {
                this.writeLock.unlock();
            }
        } else {
            return null;
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.writeLock.lock();
        try {
            return super.compute(key, remappingFunction);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        this.writeLock.lock();
        try {
            return super.merge(key, value, remappingFunction);
        } finally {
            this.writeLock.unlock();
        }
    }
}
