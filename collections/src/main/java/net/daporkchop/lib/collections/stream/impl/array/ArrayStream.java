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

package net.daporkchop.lib.collections.stream.impl.array;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.common.util.PArrays;

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
public class ArrayStream<V> implements PStream<V> {
    @NonNull
    protected final V[] values;

    @Override
    public long size() {
        return this.values.length;
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
    public PStream<V> concurrent() {
        return new ConcurrentArrayStream<>(PArrays.toObjects(this.values));
    }

    @Override
    public PStream<V> singleThreaded() {
        return this;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        int length = this.values.length; //this lets the length be inlined into a register by JIT
        for (int i = 0; i < length; i++)    {
            consumer.accept(this.values[i]);
        }
    }

    @Override
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        int length = this.values.length;
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++)    {
            values[i] = mappingFunction.apply(this.values[i]);
        }
        return new UncheckedArrayStream<>(values);
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        return null; //TODO
    }

    @Override
    public PStream<V> distinct(@NonNull BiPredicate<V, V> comparator) {
        return null;
    }

    @Override
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        int length = this.values.length;
        for (int i = 0; i < length; i++)    {
            V value = this.values[i];
            map.put(keyExtractor.apply(value), valueExtractor.apply(value));
        }
        return map;
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        return this.values.clone();
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }
}
