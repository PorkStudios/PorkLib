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
@RequiredArgsConstructor
@Accessors(fluent = true)
public class AutoReadWriteMap<K, V> implements ReadWriteMap<K, V> {
    @NonNull
    protected final Map<K, V> delegate;

    @Getter
    @NonNull
    protected final Lock readLock;
    @Getter
    @NonNull
    protected final Lock writeLock;

    protected volatile LockedMap<K, V>           readLocked;
    protected volatile LockedMap<K, V>           writeLocked;
    protected volatile ReadWriteSet<K>           keySet;
    protected volatile ReadWriteCollection<V>    values;
    protected volatile ReadWriteSet<Entry<K, V>> entrySet;

    public AutoReadWriteMap(@NonNull Map<K, V> delegate) {
        this(delegate, new ReentrantReadWriteLock());
    }

    public AutoReadWriteMap(@NonNull Map<K, V> delegate, @NonNull ReadWriteLock lock) {
        this(delegate, lock.readLock(), lock.writeLock());
    }

    @Override
    public LockedMap<K, V> readLocked() {
        LockedMap<K, V> readLocked = this.readLocked;
        if (readLocked == null) {
            synchronized (this.delegate) {
                if ((readLocked = this.readLocked) == null) {
                    this.readLocked = readLocked = PMaps.locked(this.delegate, this.readLock);
                }
            }
        }
        return readLocked;
    }

    @Override
    public LockedMap<K, V> writeLocked() {
        LockedMap<K, V> writeLocked = this.writeLocked;
        if (writeLocked == null) {
            synchronized (this.delegate) {
                if ((writeLocked = this.writeLocked) == null) {
                    this.writeLocked = writeLocked = PMaps.locked(this.delegate, this.writeLock);
                }
            }
        }
        return writeLocked;
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
    public boolean containsKey(Object key) {
        this.readLock.lock();
        try {
            return this.delegate.containsKey(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.readLock.lock();
        try {
            return this.delegate.containsValue(value);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        this.readLock.lock();
        try {
            return this.delegate.get(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        this.writeLock.lock();
        try {
            return this.delegate.put(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        this.writeLock.lock();
        try {
            return this.delegate.remove(key);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.writeLock.lock();
        try {
            this.delegate.putAll(m);
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
    public ReadWriteSet<K> keySet() {
        ReadWriteSet<K> keySet = this.keySet;
        if (keySet == null) {
            synchronized (this.delegate) {
                if ((keySet = this.keySet) == null) {
                    this.keySet = keySet = PSets.readWrite(this.delegate.keySet(), this.readLock, this.writeLock);
                }
            }
        }
        return keySet;
    }

    @Override
    public ReadWriteCollection<V> values() {
        ReadWriteCollection<V> values = this.values;
        if (values == null) {
            synchronized (this.delegate) {
                if ((values = this.values) == null) {
                    this.values = values = PCollections.readWrite(this.delegate.values(), this.readLock, this.writeLock);
                }
            }
        }
        return values;
    }

    @Override
    public ReadWriteSet<Entry<K, V>> entrySet() {
        ReadWriteSet<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            synchronized (this.delegate) {
                if ((entrySet = this.entrySet) == null) {
                    this.entrySet = entrySet = PSets.readWrite(this.delegate.entrySet(), this.readLock, this.writeLock);
                }
            }
        }
        return entrySet;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        this.readLock.lock();
        try {
            return this.delegate.getOrDefault(key, defaultValue);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.readLock.lock();
        try {
            this.delegate.forEach(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.writeLock.lock();
        try {
            this.delegate.replaceAll(function);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        this.writeLock.lock();
        try {
            return this.delegate.putIfAbsent(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.writeLock.lock();
        try {
            return this.delegate.remove(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.writeLock.lock();
        try {
            return this.delegate.replace(key, oldValue, newValue);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        this.writeLock.lock();
        try {
            return this.delegate.replace(key, value);
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
                value = this.delegate.computeIfAbsent(key, mappingFunction);
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
                return this.delegate.computeIfPresent(key, remappingFunction);
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
            return this.delegate.compute(key, remappingFunction);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        this.writeLock.lock();
        try {
            return this.delegate.merge(key, value, remappingFunction);
        } finally {
            this.writeLock.unlock();
        }
    }
}
