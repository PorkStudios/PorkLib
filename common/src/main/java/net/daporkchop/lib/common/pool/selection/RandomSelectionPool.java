/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.common.pool.selection;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An implementation of {@link SelectionPool} which selects randomly from a fixed number of values given at construction time.
 * <p>
 * Should not be created directly, instead use the helper methods in {@link SelectionPool}.
 *
 * @author DaPorkchop_
 */
public final class RandomSelectionPool<V> implements SelectionPool<V> {
    @NonNull
    protected final Object[] values;
    protected final Random   random;
    protected final int      valueCount;

    protected RandomSelectionPool(@NonNull Object[] values, Random random) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Must have at least 1 value!");
        }
        this.values = values;
        this.random = random;
        this.valueCount = values.length;
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

    protected Random random() {
        return this.random == null ? ThreadLocalRandom.current() : this.random;
    }
}
