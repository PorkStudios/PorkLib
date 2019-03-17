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
@Getter
public abstract class AbstractArrayStream<V> implements PStream<V> {
    protected final Object[] values;

    public AbstractArrayStream(@NonNull Object[] values)    {
        this.values = PArrays.toObjects(values);
    }

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
        return this;
    }

    @Override
    public PStream<V> concurrent() {
        return this.isConcurrent() ? this : new ConcurrentArrayStream<>(this.values);
    }

    @Override
    public PStream<V> singleThreaded() {
        return this.isConcurrent() ? new UncheckedArrayStream<>(this.values) : this;
    }

    @Override
    public V[] toArray(@NonNull IntFunction<V[]> arrayCreator) {
        V[] values = arrayCreator.apply(this.values.length);
        /*PUnsafe.copyMemory(
                this.values,
                PUnsafe.ARRAY_OBJECT_BASE_OFFSET,
                values,
                PUnsafe.ARRAY_OBJECT_BASE_OFFSET,
                (long) values.length * (long) PUnsafe.ARRAY_OBJECT_INDEX_SCALE
        );*/
        System.arraycopy(this.values, 0, values, 0, values.length);
        //TODO: i want to benchmark the performance difference between these two
        return values;
    }
}
