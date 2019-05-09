/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.collections.impl.map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class JavaMapWrapper<K, V> implements PMap<K, V> {
    @NonNull
    protected final Map<K, V> delegate;

    @Override
    public long size() {
        return this.delegate.size();
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public V get(K key) {
        return this.delegate.get(key);
    }

    @Override
    public void put(K key, V value) {
        this.delegate.put(key, value);
    }

    @Override
    public boolean checkAndPut(K key, V value) {
        return this.delegate.put(key, value) != null;
    }

    @Override
    public V getAndPut(K key, V value) {
        return this.delegate.put(key, value);
    }

    @Override
    public boolean contains(K key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public void remove(K key) {
        this.delegate.remove(key);
    }

    @Override
    public boolean checkAndRemove(K key) {
        return this.delegate.remove(key) != null;
    }

    @Override
    public V getAndRemove(K key) {
        return this.delegate.remove(key);
    }

    @Override
    public void forEach(BiConsumer<K, V> consumer) {
        this.delegate.forEach(consumer);
    }

    @Override
    public PStream<K> keyStream() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public PStream<V> valueStream() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public PStream<Entry<K, V>> entryStream() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }
}
