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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PIterator;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.PSet;
import net.daporkchop.lib.collections.impl.list.JavaListWrapper;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.stream.impl.list.ConcurrentListStream;
import net.daporkchop.lib.collections.stream.impl.list.UncheckedListStream;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractSetStream<V> implements PStream<V> {
    @NonNull
    protected final PSet<V> set;

    @Override
    public long size() {
        return this.set.size();
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public PStream<V> ordered() {
        PList<V> list = new JavaListWrapper<>(new ArrayList<>()); //TODO: custom implementation here too
        this.set.forEach(list::add);
        return this.isConcurrent() ? new ConcurrentListStream<>(list) : new UncheckedListStream<>(list);
    }

    @Override
    public PStream<V> unordered() {
        return this;
    }

    @Override
    public PStream<V> concurrent() {
        return this.isConcurrent() ? this : new ConcurrentSetStream<>(this.set);
    }

    @Override
    public PStream<V> singleThreaded() {
        return this.isConcurrent() ? new UncheckedSetStream<>(this.set) : this;
    }

    @Override
    public PStream<V> distinct() {
        return this;
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        if (this.set.size() > Integer.MAX_VALUE)    {
            throw new IllegalStateException("Set too large to convert to array!");
        }
        V[] arr = arrayCreator.apply((int) this.set.size());
        int i = 0;
        for (PIterator<V> iterator = this.set.iterator(); iterator.hasNext();)  {
            arr[i++] = iterator.next();
        }
        return arr;
    }
}
