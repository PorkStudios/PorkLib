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
 * An immutable implementation of a {@link List} that contains two values.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableListTwo<T> extends ImmutableList<T> {
    private static ImmutableListTwo NULL;

    @SuppressWarnings("unchecked")
    public static <T> List<T> of(T v0, T v1) {
        return v0 != null || v1 != null
                ? new ImmutableListTwo<>(v0, v1)
                : NULL != null ? NULL : (NULL = new ImmutableListTwo(null, null));
    }

    protected final T v0;
    protected final T v1;

    @Override
    public T get(int index) {
        PValidation.checkIndex(index, 0, 2);
        return index == 0 ? this.v0 : this.v1;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public int indexOf(Object o) {
        return Objects.equals(o, this.v0) ? 0 : Objects.equals(o, this.v1) ? 1 : -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return Objects.equals(o, this.v1) ? 1 : Objects.equals(o, this.v0) ? 0 : -1;
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(this.v0, this.v1);
    }

    @Override
    public void forEach(@NonNull Consumer<? super T> action) {
        action.accept(this.v0);
        action.accept(this.v1);
    }
}
