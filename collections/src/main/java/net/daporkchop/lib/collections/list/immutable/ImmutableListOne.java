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

package net.daporkchop.lib.collections.list.immutable;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PValidation;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An immutable implementation of a {@link List} that contains a single value.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableListOne<T> extends ImmutableList<T> {
    private static ImmutableListOne NULL;

    @SuppressWarnings("unchecked")
    public static <T> List<T> of(T value) {
        return value != null
                ? new ImmutableListOne<>(value)
                : NULL != null ? NULL : (NULL = new ImmutableListOne(null));
    }

    protected final T value;

    @Override
    public T get(int index) {
        PValidation.checkIndex(index, 0, 1);
        return this.value;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int indexOf(Object o) {
        return Objects.equals(o, this.value) ? 0 : -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.indexOf(o);
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(this.value);
    }

    @Override
    public void forEach(@NonNull Consumer<? super T> action) {
        action.accept(this.value);
    }
}
