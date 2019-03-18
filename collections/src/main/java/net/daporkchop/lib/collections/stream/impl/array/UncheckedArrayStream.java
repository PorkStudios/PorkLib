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
import net.daporkchop.lib.common.util.PUnsafe;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The same as {@link ArrayStream}, however this is backed by an Object[] instead of a generic type array, allowing it to
 * actually create new array instances. Most instances of {@link ArrayStream} will end up being converted into an
 * {@link UncheckedArrayStream} at some point in their lifetime.
 *
 * @author DaPorkchop_
 */
public class UncheckedArrayStream<V> extends AbstractArrayStream<V> {
    public UncheckedArrayStream(Object[] values) {
        super(values);
    }

    @Override
    public boolean isConcurrent() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEach(@NonNull Consumer<V> consumer) {
        int length = this.values.length; //this lets the length be inlined into a register by JIT
        for (int i = 0; i < length; i++)    {
            consumer.accept((V) this.values[i]);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PStream<T> map(@NonNull Function<V, T> mappingFunction) {
        int length = this.values.length;
        for (int i = 0; i < length; i++)    {
            this.values[i] = mappingFunction.apply((V) this.values[i]);
        }
        return (PStream<T>) this;
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
    @SuppressWarnings("unchecked")
    public <Key, Value, T extends PMap<Key, Value>> T toMap(@NonNull Function<V, Key> keyExtractor, @NonNull Function<V, Value> valueExtractor, @NonNull Supplier<T> mapCreator) {
        T map = mapCreator.get();
        int length = this.values.length;
        for (int i = 0; i < length; i++)    {
            V value = (V) this.values[i];
            map.put(keyExtractor.apply(value), valueExtractor.apply(value));
        }
        return map;
    }
}
