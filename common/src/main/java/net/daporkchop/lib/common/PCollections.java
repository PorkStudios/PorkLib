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

package net.daporkchop.lib.common;

import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Some random utilities for working with the Java collections API
 *
 * @author DaPorkchop_
 */
public abstract class PCollections {
    public static final Deque EMPTY_DEQUE = new EmptyDeque();

    /**
     * Obtains an instance of an empty {@link Deque}, because for some reason {@link java.util.Collections} doesn't have that
     * @param <T> the value type
     * @return an empty {@link Deque}
     */
    @SuppressWarnings("unchecked")
    public static <T> Deque<T> emptyDeque()    {
        return (Deque<T>) EMPTY_DEQUE;
    }

    private PCollections() {
        throw new IllegalStateException();
    }

    protected static final class EmptyDeque<V> implements Deque<V> {
        @Override
        public void addFirst(V v) {
            throw new IllegalStateException();
        }

        @Override
        public void addLast(V v) {
            throw new IllegalStateException();
        }

        @Override
        public boolean offerFirst(V v) {
            return false;
        }

        @Override
        public boolean offerLast(V v) {
            return false;
        }

        @Override
        public V removeFirst() {
            throw new NoSuchElementException();
        }

        @Override
        public V removeLast() {
            throw new NoSuchElementException();
        }

        @Override
        public V pollFirst() {
            return null;
        }

        @Override
        public V pollLast() {
            return null;
        }

        @Override
        public V getFirst() {
            throw new NoSuchElementException();
        }

        @Override
        public V getLast() {
            throw new NoSuchElementException();
        }

        @Override
        public V peekFirst() {
            return null;
        }

        @Override
        public V peekLast() {
            return null;
        }

        @Override
        public boolean removeFirstOccurrence(Object o) {
            return false;
        }

        @Override
        public boolean removeLastOccurrence(Object o) {
            return false;
        }

        @Override
        public boolean add(V v) {
            throw new IllegalStateException();
        }

        @Override
        public boolean offer(V v) {
            return false;
        }

        @Override
        public V remove() {
            throw new NoSuchElementException();
        }

        @Override
        public V poll() {
            return null;
        }

        @Override
        public V element() {
            throw new NoSuchElementException();
        }

        @Override
        public V peek() {
            return null;
        }

        @Override
        public void push(V v) {
            throw new IllegalStateException();
        }

        @Override
        public V pop() {
            return null;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public V next() {
                    return null;
                }
            };
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(@NonNull T[] a) {
            if (a.length == 0)  {
                return a;
            } else {
                return (T[]) Array.newInstance(a.getClass().getComponentType(), 0);
            }
        }

        @Override
        public Iterator<V> descendingIterator() {
            return this.iterator();
        }
    }
}
