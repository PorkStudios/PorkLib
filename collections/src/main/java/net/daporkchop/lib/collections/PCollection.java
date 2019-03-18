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
import net.daporkchop.lib.collections.util.BaseCollection;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A simplification of {@link java.util.Collection}. A PCollection supports many of the same simple getters and setters
 * as a normal Java Collection, as well as some basic iteration methods and a few variations on the standard accessors
 * for performance.
 * <p>
 * Implementations of this class are expected to be thread-safe at a minimum, optionally supporting full concurrency.
 *
 * @param <V> the type to be used as a value
 * @author DaPorkchop_
 */
public interface PCollection<V> extends BaseCollection {
    /**
     * Adds a value to this collection.
     *
     * @param value the value to add
     */
    void add(V value);

    /**
     * Checks whether or not this collection contains the given value.
     *
     * @param value the value to check for
     * @return whether or not this collection contains the given value
     */
    boolean contains(V value);

    /**
     * Removes a value from this collection.
     *
     * @param value the value to be removed
     * @return whether or not the value was found in this collection and removed
     */
    boolean remove(V value);

    /**
     * Removes all occurrences of a given value from this collection.
     *
     * @param value the value to be removed
     * @return whether or not at least one occurrence of the value was found in this collection and removed
     */
    default boolean removeAll(V value) {
        boolean removed = false;
        while (this.remove(value)) {
            removed = true;
        }
        return removed;
    }

    /**
     * Iterates over all elements in the collection, invoking a given function on each
     *
     * @param consumer the function to run
     */
    void forEach(@NonNull Consumer<V> consumer);

    /**
     * Gets a stream over the contents of this collection.
     *
     * @return a stream over the contents of this collection
     */
    PStream<V> stream();

    /**
     * Gets a stream over the contents of this collection that supports concurrency.
     *
     * @return a stream over the contents of this collection that supports concurrency
     */
    default PStream<V> concurrentStream() {
        return this.stream().concurrent();
    }

    /**
     * Gets an iterator over the contents of this collection.
     *
     * @return an iterator over the contents of this collection
     */
    PIterator<V> iterator();

    /**
     * Removes all values in this collection that match a certain condition.
     *
     * @param condition the condition that must be met for a value to be removed
     */
    default void removeIf(@NonNull Predicate<V> condition) {
        PIterator<V> iterator = this.iterator();
        while (iterator.hasNext()) {
            if (condition.test(iterator.next())) {
                iterator.remove();
            }
        }
    }

    /**
     * Removes all values in this collection that do not match a certain condition.
     *
     * @param condition the condition that must be met for a value to be retained
     */
    default void retainIf(@NonNull Predicate<V> condition) {
        PIterator<V> iterator = this.iterator();
        while (iterator.hasNext()) {
            if (!condition.test(iterator.next())) {
                iterator.remove();
            }
        }
    }
}
