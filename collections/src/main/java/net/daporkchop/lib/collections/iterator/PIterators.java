/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
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
 */

package net.daporkchop.lib.collections.iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Helper methods for {@link Iterator}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PIterators {
    /**
     * Returns an empty {@link Iterator} instance.
     *
     * @return an {@link Iterator}
     */
    public static <E> Iterator<E> empty() {
        return Collections.emptyIterator();
    }

    /**
     * Returns an {@link Iterable} which always returns an empty {@link Iterator} instance.
     *
     * @return an {@link Iterable}
     */
    public static <E> Iterable<E> emptyIterable() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an {@link Iterator} which provides read-only access to the given array.
     *
     * @param array the array
     * @return an {@link Iterator}
     */
    public static <E> Iterator<E> array(E @NonNull [] array) {
        return new ArrayIterator<>(array);
    }

    /**
     * Returns an {@link Iterable} which provides read-only access to the given array.
     *
     * @param array the array
     * @return an {@link Iterable}
     */
    public static <E> Iterable<E> arrayIterable(E @NonNull [] array) {
        return new ArrayIterable<>(array);
    }

    @RequiredArgsConstructor
    private static final class ArrayIterator<E> implements Iterator<E> {
        private final E @NonNull [] array;
        private int nextIndex;

        @Override
        public boolean hasNext() {
            return this.nextIndex < this.array.length;
        }

        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            return this.array[this.nextIndex++];
        }

        @Override
        public void forEachRemaining(@NonNull Consumer<? super E> action) {
            E[] array = this.array;
            int nextIndex = this.nextIndex;

            while (nextIndex < array.length) {
                action.accept(array[nextIndex++]);
            }
        }
    }

    @RequiredArgsConstructor
    private static final class ArrayIterable<E> implements Iterable<E> {
        private final E @NonNull [] array;

        @Override
        public Iterator<E> iterator() {
            return new ArrayIterator<>(this.array);
        }

        @Override
        public void forEach(@NonNull Consumer<? super E> action) {
            for (E element : this.array) {
                action.accept(element);
            }
        }

        @Override
        public Spliterator<E> spliterator() {
            return Spliterators.spliterator(this.array, Spliterator.ORDERED);
        }
    }
}
