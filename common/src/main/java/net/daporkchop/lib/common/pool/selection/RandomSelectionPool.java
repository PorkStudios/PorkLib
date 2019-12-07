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

package net.daporkchop.lib.common.pool.selection;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation of {@link SelectionPool} which selects randomly from a fixed number of values given at construction time.
 *
 * @author DaPorkchop_
 */
public final class RandomSelectionPool<V> implements SelectionPool<V> {
    protected final Object[] values;
    protected final Random   random;

    public RandomSelectionPool(@NonNull Object[] values) {
        this(values, null);
    }

    public RandomSelectionPool(@NonNull Object[] values, Random random) {
        this.values = values.clone();
        this.random = random;
    }

    public RandomSelectionPool(@NonNull Collection<V> values)    {
        this(values, null);
    }

    public RandomSelectionPool(@NonNull Collection<V> values, Random random)    {
        this.values = values.toArray();
        this.random = random;
    }

    public RandomSelectionPool(@NonNull Stream<V> values)    {
        this(values, null);
    }

    public RandomSelectionPool(@NonNull Stream<V> values, Random random)    {
        this.values = values.toArray();
        this.random = random;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V any() {
        return (V) this.values[this.random().nextInt(this.values.length)];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<V> matching(@NonNull Predicate<V> condition) {
        return Arrays.stream((V[]) this.values).filter(condition).collect(Collectors.toList());
    }

    @Override
    public V anyMatching(@NonNull Predicate<V> condition) {
        List<V> list = this.matching(condition);
        return list.isEmpty() ? null : list.get(this.random().nextInt(list.size()));
    }

    protected Random random() {
        return this.random == null ? ThreadLocalRandom.current() : this.random;
    }
}
