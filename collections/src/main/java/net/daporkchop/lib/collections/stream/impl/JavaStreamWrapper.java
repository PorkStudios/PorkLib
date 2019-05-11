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

package net.daporkchop.lib.collections.stream.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class JavaStreamWrapper<V> implements PStream<V> {
    @NonNull
    protected Stream<V> stream;
    protected boolean parallel = false;

    @Override
    public long size() {
        return this.stream.count();
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public PStream<V> ordered() {
        return this;
    }

    @Override
    public PStream<V> unordered() {
        return this;
    }

    @Override
    public boolean isConcurrent() {
        return this.parallel;
    }

    @Override
    public PStream<V> concurrent() {
        this.stream = this.stream.parallel();
        return this;
    }

    @Override
    public PStream<V> singleThreaded() {
        this.stream = this.stream.sequential();
        return this;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        this.stream.forEach(consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        ((JavaStreamWrapper<T>) this).stream = this.stream.map(mappingFunction);
        return (JavaStreamWrapper<T>) this;
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        this.stream = this.stream.filter(condition);
        return this;
    }

    @Override
    public PStream<V> distinct() {
        this.stream = this.stream.distinct();
        return this;
    }

    @Override
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        return this.stream.collect(
                mapCreator,
                (map, val) -> map.put(keyExtractor.apply(val), valueExtractor.apply(val)),
                PMap::putAll
        );
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        return this.stream.toArray(arrayCreator);
    }
}
