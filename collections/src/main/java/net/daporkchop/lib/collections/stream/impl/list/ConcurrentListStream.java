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

import lombok.NonNull;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.impl.list.JavaListWrapper;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.util.ConcurrencyHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class ConcurrentListStream<V> extends AbstractListStream<V> {
    public ConcurrentListStream(PList<V> list, boolean mutable) {
        super(list, mutable);
    }

    @Override
    public boolean isConcurrent() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEach(@NonNull Consumer<V> consumer) {
        ConcurrencyHelper.runConcurrent(this.list.iterator(), consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        if (this.mutable) {
            ConcurrencyHelper.runConcurrent(this.list.size(), (long l) -> ((PList<T>) this.list).set(l, mappingFunction.apply(this.list.get(l))));
            return (PStream<T>) this;
        } else {
            PList<T> dst = this.newList();
            ConcurrencyHelper.runConcurrent(this.list.size(), (long l) -> dst.set(l, mappingFunction.apply(this.list.get(l))));
            return new ConcurrentListStream<>(dst, true);
        }
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        if (this.mutable) {
            this.list.removeIf(condition.negate()); //TODO: this obviously isn't concurrent
            return this;
        } else {
            PList<V> dst = this.newList();
            this.list.forEach(value -> {
                if (condition.test(value))  {
                    dst.add(value);
                }
            });
            return new ConcurrentListStream<>(dst, true);
        }
    }

    @Override
    public PStream<V> distinct(@NonNull BiPredicate<V, V> comparator) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        ConcurrencyHelper.runConcurrent(this.list.iterator(), value -> map.put(keyExtractor.apply(value), valueExtractor.apply(value)));
        return map;
    }

    @Override
    protected <T> PList<T> newList() {
        return new JavaListWrapper<>(Collections.synchronizedList(new ArrayList<>())); //TODO: custom implementation
    }
}
