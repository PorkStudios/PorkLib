/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.collections.list.immutable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PValidation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An immutable implementation of a {@link List} backed by an array of values.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public final class ImmutableArrayList<T> extends ImmutableList<T> {
    public static <T> List<T> of(@NonNull T[] values) {
        return of(values, true);
    }

    public static <T> List<T> of(@NonNull T[] values, boolean copy) {
        if (values.length == 0) {
            //don't bother doing anything complex for an empty array
            return Collections.emptyList();
        }

        if (copy) {
            if (values.length == 1) {
                return ImmutableListOne.of(values[0]);
            } else if (values.length == 2) {
                return ImmutableListTwo.of(values[0], values[1]);
            }

            CHECK_ALL_NULL:
            {
                for (T value : values) {
                    if (value != null) {
                        break CHECK_ALL_NULL;
                    }
                }

                //all values are null, don't copy (or even keep a reference to) the array
                return new ImmutableArrayList<>(null, values.length);
            }
            values = values.clone();
        }

        return new ImmutableArrayList<>(values, values.length);
    }

    private final T[] values;

    @Getter
    private final int size;

    @Override
    public T get(int index) {
        if (this.values != null) {
            return this.values[PValidation.checkIndex(this.values.length, index)];
        } else {
            PValidation.checkIndex(this.size, index);
            return null;
        }
    }

    @Override
    public int indexOf(Object o) {
        if (this.values != null) {
            if (o != null) {
                for (int i = 0, length = this.values.length; i < length; i++) {
                    if (Objects.equals(o, this.values[i])) {
                        return i;
                    }
                }
                return -1;
            } else {
                return PArrays.linearSearch(this.values, null);
            }
        } else {
            return o == null ? 0 : -1;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        if (this.values != null) {
            for (int i = this.values.length - 1; i >= 0; i--) {
                if (Objects.equals(o, this.values[i])) {
                    return i;
                }
            }
            return -1;
        } else {
            return o == null ? this.size - 1 : -1;
        }
    }

    @Override
    public Stream<T> stream() {
        return this.values != null
                ? Arrays.stream(this.values)
                : IntStream.range(0, this.size).mapToObj(i -> null);
    }

    @Override
    public Stream<T> parallelStream() {
        return this.values != null
                ? StreamSupport.stream(Arrays.spliterator(this.values), true)
                : IntStream.range(0, this.size).parallel().mapToObj(i -> null);
    }

    @Override
    public void forEach(@NonNull Consumer<? super T> action) {
        if (this.values != null) {
            for (T value : this.values) {
                action.accept(value);
            }
        } else {
            for (int i = this.size - 1; i >= 0; i--) {
                action.accept(null);
            }
        }
    }
}
