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

import lombok.NonNull;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.POrderedCollection;
import net.daporkchop.lib.collections.impl.ordered.BigLinkedCollection;
import net.daporkchop.lib.collections.impl.ordered.concurrent.ConcurrentBigLinkedCollection;
import net.daporkchop.lib.collections.stream.PStream;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class UncheckedOrderedCollectionStream<V> extends AbstractOrderedCollectionStream<V> {
    public UncheckedOrderedCollectionStream(POrderedCollection<V> collection, boolean mutable) {
        super(collection, mutable);
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    public void forEach(@NonNull Consumer<V> consumer) {
        this.collection.forEach(consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        POrderedCollection.Entry<V> entry;
        for (POrderedCollection.OrderedIterator<V> iterator = this.collection.orderedIterator(); (entry = iterator.next()) != null;)    {
            ((POrderedCollection.Entry<T>) entry).set(mappingFunction.apply(entry.get()));
        }
        return (PStream<T>) this;
    }

    @Override
    public PStream<V> filter(@NonNull Predicate<V> condition) {
        POrderedCollection.Entry<V> entry;
        for (POrderedCollection.OrderedIterator<V> iterator = this.collection.orderedIterator(); (entry = iterator.next()) != null;)    {
            if (!condition.test(entry.get()))   {
                entry.remove();
            }
        }
        return this;
    }

    @Override
    public PStream<V> distinct() {
        return null;
    }

    @Override
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        POrderedCollection.Entry<V> entry;
        for (POrderedCollection.OrderedIterator<V> iterator = this.collection.orderedIterator(); (entry = iterator.next()) != null;)    {
            map.put(keyExtractor.apply(entry.get()), valueExtractor.apply(entry.get()));
        }
        return map;
    }

    @Override
    protected <T> POrderedCollection<T> newCollection() {
        return new BigLinkedCollection<>();
    }
}
