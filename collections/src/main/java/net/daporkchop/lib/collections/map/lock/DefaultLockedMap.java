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
 * Wrapper implementation of {@link LockedMap} around a normal {@link Map} and a {@link Lock}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class DefaultLockedMap<K, V> implements LockedMap<K, V> {
    @NonNull
    protected final Map<K, V> delegate;
    @NonNull
    protected final Lock      lock;

    protected LockedSet<K>           keySet;
    protected LockedCollection<V>    values;
    protected LockedSet<Entry<K, V>> entrySet;

    public DefaultLockedMap(@NonNull Map<K, V> delegate)    {
        this(delegate, new ReentrantLock());
    }

    //
    //
    // lock methods
    //
    //

    @Override
    public DefaultLockedMap<K, V> lockAndGet() {
        this.lock.lock();
        return this;
    }

    @Override
    public DefaultLockedMap<K, V> lockAndGetInterruptibly() throws InterruptedException {
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
    public LockedSet<K> keySet() {
        LockedSet<K> keySet = this.keySet;
        if (keySet == null) {
            synchronized (this) {
                if ((keySet = this.keySet) == null) {
                    this.keySet = keySet = PSets.lockedSet(this.delegate.keySet(), this.lock);
                }
            }
        }
        return keySet;
    }

    @Override
    public LockedCollection<V> values() {
        LockedCollection<V> values = this.values;
        if (values == null) {
            synchronized (this) {
                if ((values = this.values) == null) {
                    this.values = values = PCollections.lockedCollection(this.delegate.values(), this.lock);
                }
            }
        }
        return values;
    }

    @Override
    public LockedSet<Entry<K, V>> entrySet() {
        LockedSet<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            synchronized (this) {
                if ((entrySet = this.entrySet) == null) {
                    this.entrySet = entrySet = PSets.lockedSet(this.delegate.entrySet(), this.lock);
                }
            }
        }
        return entrySet;
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
