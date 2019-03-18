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

package net.daporkchop.lib.collections.stream.impl.set;

import lombok.NonNull;
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.PSet;
import net.daporkchop.lib.collections.impl.set.JavaSetWrapper;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.HashSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class UncheckedSetStream<V> extends AbstractSetStream<V> {
    public UncheckedSetStream(PSet<V> set) {
        super(set);
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        this.set.forEach(consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        //because of the way stuff works, i can't use this:
        /*for (PIterator<V> iterator = this.set.iterator(); iterator.hasNext();)  {
            ((PSet<T>) this.set).add(mappingFunction.apply(iterator.next()));
            iterator.remove();
        }
        return (PStream<T>) this;*/
        PSet<T> dst = new JavaSetWrapper<>(new HashSet<>()); //TODO: custom implementation here
        for (PIterator<V> iterator = this.set.iterator(); iterator.hasNext();)  {
            dst.add(mappingFunction.apply(iterator.next()));
        }
        return new UncheckedSetStream<>(dst);
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        this.set.removeIf(condition.negate());
        return this;
    }

    @Override
    public PStream<V> distinct(@NonNull BiPredicate<V, V> comparator) {
        return this;
    }

    @Override
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        for (PIterator<V> iterator = this.set.iterator(); iterator.hasNext();)  {
            V value = iterator.next();
            map.put(keyExtractor.apply(value), valueExtractor.apply(value));
        }
        return map;
    }
}
