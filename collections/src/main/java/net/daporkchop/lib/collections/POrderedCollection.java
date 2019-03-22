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

package net.daporkchop.lib.collections;

import lombok.NonNull;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.stream.impl.collection.ConcurrentOrderedCollectionStream;
import net.daporkchop.lib.collections.stream.impl.collection.UncheckedOrderedCollectionStream;
import net.daporkchop.lib.collections.util.exception.AlreadyRemovedException;
import net.daporkchop.lib.collections.util.exception.IterationCompleteException;

/**
 * A subtype of a PCollection that allows replacing values whilst maintaining their order in the collection.
 *
 * @author DaPorkchop_
 */
public interface POrderedCollection<V> extends PCollection<V> {
    OrderedIterator<V> orderedIterator();

    @Override
    @Deprecated
    default PIterator<V> iterator() {
        return new PIterator<V>() {
            protected final OrderedIterator<V> delegate = POrderedCollection.this.orderedIterator();
            protected Entry<V> entry = null;
            protected Entry<V> next = this.delegate.next();

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public V next() {
                if (!this.hasNext()) {
                    throw new IterationCompleteException();
                }
                this.entry = this.next;
                this.next = this.delegate.next();
                return this.entry.get();
            }

            @Override
            public V peek() {
                return this.entry.get();
            }

            @Override
            public void remove() {
                if (this.entry == null) {
                    throw new IterationCompleteException();
                } else {
                    this.entry.remove();
                }
            }

            @Override
            public void set(@NonNull V value) {
                if (this.entry == null) {
                    throw new IterationCompleteException();
                } else {
                    this.entry.set(value);
                }
            }

            @Override
            public boolean setSupported() {
                return true;
            }
        };
    }

    /**
     * Replaces a given value with a new one.
     *
     * @param oldValue the value to be replaced
     * @param newValue the value to replace it with
     * @return whether or not the value was replaced (i.e. whether oldValue was found in this collection)
     */
    boolean replace(@NonNull V oldValue, @NonNull V newValue);

    @Override
    default PStream<V> stream() {
        return new UncheckedOrderedCollectionStream<>(this, false);
    }

    @Override
    default PStream<V> concurrentStream() {
        return new ConcurrentOrderedCollectionStream<>(this, false);
    }

    @Override
    default PStream<V> mutableStream() {
        return new UncheckedOrderedCollectionStream<>(this, true);
    }

    @Override
    default PStream<V> concurrentMutableStream() {
        return new ConcurrentOrderedCollectionStream<>(this, true);
    }

    /**
     * An iterator over an ordered collection.
     * <p>
     * Basically allows for making use of the main advantage of an ordered collection, which is that values can be replaced
     * without losing ordering.
     *
     * @author DaPorkchop_
     */
    interface OrderedIterator<V> {
        /**
         * Grabs the next entry in the iterator. If none remain, returns {@code null}.
         *
         * @return the next entry in the iterator
         */
        Entry<V> next();
    }

    /**
     * A wrapper around a value in a concurrent iterator, to allow for multiple threads to actually do iteration concurrently
     *
     * @param <V> the value type
     */
    interface Entry<V> {
        /**
         * Gets this entry's value.
         *
         * @return this entry's value, or {@code null} if this entry has been removed.
         */
        V get();

        /**
         * Sets this entry's value. This will modify the value in the backing collection.
         *
         * @param value the new value to set
         * @throws AlreadyRemovedException if this entry has already been removed
         */
        void set(@NonNull V value) throws AlreadyRemovedException;

        /**
         * Sets this entry's value. This will modify the value in the backing collection.
         * <p>
         * If this entry has already been removed, this method does nothing.
         *
         * @param value the new value to set
         * @return {@code true} if this entry is present and was set, {@code false} otherwise
         */
        boolean trySet(@NonNull V value);

        /**
         * Sets this entry's value. This will modify the value in the backing collection.
         *
         * @param value the new value to set
         * @return the old value
         * @throws AlreadyRemovedException if this entry has already been removed
         */
        V replace(@NonNull V value) throws AlreadyRemovedException;

        /**
         * Removes this entry from the backing collection.
         * <p>
         * May only be invoked once per instance of {@link Entry}.
         *
         * @throws AlreadyRemovedException if this entry has already been removed
         */
        void remove() throws AlreadyRemovedException;

        /**
         * Tries to remove this entry from the backing collection.
         * <p>
         * If this entry has already been removed, this method does nothing.
         *
         * @return {@code true} if this entry was present and was removed, {@code false} otherwise
         */
        boolean tryRemove();
    }
}
