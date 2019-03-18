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

package net.daporkchop.lib.collections.stream.impl.list;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.impl.list.JavaListWrapper;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.ArrayList;
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
public class ListStream<V> implements PStream<V> {
    @NonNull
    protected final PList<V> list;

    @Override
    public long size() {
        return this.list.size();
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
        return null;
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    public PStream<V> concurrent() {
        return new ConcurrentListStream<>(this.list);
    }

    @Override
    public PStream<V> singleThreaded() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEach(@NonNull Consumer<V> consumer) {
        this.list.forEach(consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        long length = this.list.size();
        for (long l = 0L; l < length; l++)    {
            ((PList<T>) this.list).set(l, mappingFunction.apply(this.list.get(l)));
        }
        return (PStream<T>) this;
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        this.list.removeIf(condition.negate());
        return this;
    }

    @Override
    public PStream<V> distinct(@NonNull BiPredicate<V, V> comparator) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        long length = this.list.size();
        for (long l = 0L; l < length; l++)    {
            V value = this.list.get(l);
            map.put(keyExtractor.apply(value), valueExtractor.apply(value));
        }
        return map;
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        if (this.list.size() > Integer.MAX_VALUE)   {
            throw new IllegalStateException("Backing PList is too large to convert to array!");
        }
        V[] values = arrayCreator.apply((int) this.list.size());
        for (int i = values.length - 1; i >= 0; i--)    {
            values[i] = this.list.get(i);
        }
        return values;
    }
}
