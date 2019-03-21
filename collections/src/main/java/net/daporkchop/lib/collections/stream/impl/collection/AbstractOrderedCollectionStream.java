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
import net.daporkchop.lib.collections.concurrent.ConcurrentOrderedCollection;
import net.daporkchop.lib.collections.concurrent.ConcurrentPIterator;
import net.daporkchop.lib.collections.stream.PStream;

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
public abstract class AbstractOrderedCollectionStream<V> implements PStream<V> {
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
        return null;
    }

    @Override
    public PStream<V> concurrent() {
        return this.isConcurrent() ? this : new ConcurrentOrderedCollectionStream<>(this.collection, this.mutable);
    }

    @Override
    public PStream<V> singleThreaded() {
        return this.isConcurrent() ? new UncheckedOrderedCollectionStream<>(this.collection, this.mutable) : this;
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

    protected abstract  <T> ConcurrentOrderedCollection<T> newCollection();
}
