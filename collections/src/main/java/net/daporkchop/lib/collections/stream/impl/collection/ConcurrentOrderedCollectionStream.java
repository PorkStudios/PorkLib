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

package net.daporkchop.lib.collections.stream.impl.collection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.PSet;
import net.daporkchop.lib.collections.concurrent.ConcurrentOrderedCollection;
import net.daporkchop.lib.collections.concurrent.ConcurrentPIterator;
import net.daporkchop.lib.collections.impl.collection.concurrent.ConcurrentBigLinkedCollection;
import net.daporkchop.lib.collections.impl.set.JavaSetWrapper;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.stream.impl.set.ConcurrentSetStream;
import net.daporkchop.lib.collections.util.ConcurrencyHelper;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ConcurrentOrderedCollectionStream<V> implements PStream<V> {
    @NonNull
    protected final ConcurrentOrderedCollection<V> collection;
    protected final boolean mutable;

    @Override
    public long size() {
        return this.collection.size();
    }

    @Override
    public boolean isOrdered() {
        return true;
    }

    @Override
    public PStream<V> ordered() {
        return this;
    }

    @Override
    public PStream<V> unordered() {
        return null; //TODO
    }

    @Override
    public boolean isConcurrent() {
        return true;
    }

    @Override
    public PStream<V> concurrent() {
        return this;
    }

    @Override
    public PStream<V> singleThreaded() {
        return null; //TODO
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        ConcurrencyHelper.runConcurrent(this.collection.concurrentIterator(), entry -> consumer.accept(entry.get()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        if (this.mutable) {
            ConcurrencyHelper.runConcurrent(this.collection.concurrentIterator(), entry -> ((ConcurrentPIterator.Entry<T>) entry).set(mappingFunction.apply(entry.get())));
            return (PStream<T>) this;
        } else {
            ConcurrentOrderedCollection<T> dst = this.newCollection();
            ConcurrencyHelper.runConcurrent(this.collection.concurrentIterator(), entry -> dst.add(mappingFunction.apply(entry.get())));
            return new ConcurrentOrderedCollectionStream<>(dst, true);
        }
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        ConcurrencyHelper.runConcurrent(this.collection.concurrentIterator(), entry -> {
            if (!condition.test(entry.get()))   {
                entry.tryRemove();
            }
        });
        return this;
    }

    @Override
    public PStream<V> distinct(@NonNull BiPredicate<V, V> comparator) {
        PSet<V> dst = new JavaSetWrapper<>(Collections.newSetFromMap(new ConcurrentHashMap<>()));
        ConcurrencyHelper.runConcurrent(this.collection.concurrentIterator(), entry -> dst.add(entry.get())); //probably isn't worth making this concurrent, but whatever
        return new ConcurrentSetStream<>(dst, true);
    }

    @Override
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        ConcurrencyHelper.runConcurrent(this.collection.concurrentIterator(), entry -> {
            V value = entry.get();
            map.put(keyExtractor.apply(value), valueExtractor.apply(value));
        });
        return map;
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        if (this.collection.size() > Integer.MAX_VALUE)   {
            throw new IllegalStateException("Backing ConcurrentOrderedCollection is too large to convert to array!");
        }
        V[] arr = arrayCreator.apply((int) this.collection.size());
        ConcurrentPIterator.Entry<V> entry;
        int i = 0;
        for (ConcurrentPIterator<V> iterator = this.collection.concurrentIterator(); (entry = iterator.next()) != null; i++)    {
            arr[i] = entry.get();
        }
        return arr;
    }

    protected <T> ConcurrentOrderedCollection<T> newCollection()    {
        return new ConcurrentBigLinkedCollection<>();
    }
}
