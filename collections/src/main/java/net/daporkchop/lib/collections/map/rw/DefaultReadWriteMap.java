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
 * Wrapper implementation of {@link ReadWriteMap} around a normal {@link Map} and a {@link ReadWriteLock}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class DefaultReadWriteMap<K, V> implements ReadWriteMap<K, V> {
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

    public DefaultReadWriteMap(@NonNull Map<K, V> delegate) {
        this(delegate, new ReentrantReadWriteLock());
    }

    public DefaultReadWriteMap(@NonNull Map<K, V> delegate, @NonNull ReadWriteLock lock) {
        this(delegate, lock.readLock(), lock.writeLock());
    }

    @Override
    public LockedMap<K, V> readLocked() {
        LockedMap<K, V> readLocked = this.readLocked;
        if (readLocked == null) {
            synchronized (this.delegate) {
                if ((readLocked = this.readLocked) == null) {
                    this.readLocked = readLocked = this.readLocked0();
                }
            }
        }
        return readLocked;
    }

    protected LockedMap<K, V> readLocked0() {
        return PMaps.locked(this.delegate, this.readLock);
    }

    @Override
    public LockedMap<K, V> writeLocked() {
        LockedMap<K, V> writeLocked = this.writeLocked;
        if (writeLocked == null) {
            synchronized (this.delegate) {
                if ((writeLocked = this.writeLocked) == null) {
                    this.writeLocked = writeLocked = this.writeLocked0();
                }
            }
        }
        return writeLocked;
    }

    protected LockedMap<K, V> writeLocked0() {
        return PMaps.locked(this.delegate, this.writeLock);
    }

    //
    //
    // map methods
    //
    //

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.delegate.putAll(m);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public ReadWriteSet<K> keySet() {
        ReadWriteSet<K> keySet = this.keySet;
        if (keySet == null) {
            synchronized (this.delegate) {
                if ((keySet = this.keySet) == null) {
                    this.keySet = keySet = this.keySet0();
                }
            }
        }
        return keySet;
    }

    protected ReadWriteSet<K> keySet0()  {
        return PSets.readWrite(this.delegate.keySet(), this.readLock, this.writeLock);
    }

    @Override
    public ReadWriteCollection<V> values() {
        ReadWriteCollection<V> values = this.values;
        if (values == null) {
            synchronized (this.delegate) {
                if ((values = this.values) == null) {
                    this.values = values = this.values0();
                }
            }
        }
        return values;
    }

    protected ReadWriteCollection<V> values0()  {
        return PCollections.readWrite(this.delegate.values(), this.readLock, this.writeLock);
    }

    @Override
    public ReadWriteSet<Entry<K, V>> entrySet() {
        ReadWriteSet<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            synchronized (this.delegate) {
                if ((entrySet = this.entrySet) == null) {
                    this.entrySet = entrySet = this.entrySet0();
                }
            }
        }
        return entrySet;
    }

    protected ReadWriteSet<Entry<K, V>> entrySet0() {
        return PSets.readWrite(this.delegate.entrySet(), this.readLock, this.writeLock);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return this.delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.delegate.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.delegate.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return this.delegate.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return this.delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.delegate.merge(key, value, remappingFunction);
    }
}
