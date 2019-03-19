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
import net.daporkchop.lib.collections.stream.impl.list.ConcurrentListStream;
import net.daporkchop.lib.collections.stream.impl.list.UncheckedListStream;

/**
 * A simplification of {@link java.util.Collection}. A PCollection supports many of the same simple getters and setters
 * as a normal Java Collection, as well as some basic iteration methods and a few variations on the standard accessors
 * for performance.
 * <p>
 * Implementations of this class are expected to be thread-safe at a minimum, optionally supporting full concurrency.
 *
 * @author DaPorkchop_
 */
public interface PList<V> extends PCollection<V> {
    /**
     * Adds a value to this list at a specific position.
     *
     * @param pos   the position at which to add the element. Elements following this position will be displaced to make room
     *              for the new element.
     * @param value the value to add
     */
    void add(long pos, @NonNull V value);

    /**
     * Sets a value at a specific position in this list.
     *
     * @param pos   the position of the element to set. The old value at this position will be replaced silently.
     * @param value the new value to set at the given position
     */
    void set(long pos, @NonNull V value);

    /**
     * Sets a value at a specific position in this list.
     *
     * @param pos   the position of the element to set. The old value at this position will be replaced silently.
     * @param value the new value to set at the given position
     */
    V replace(long pos, @NonNull V value);

    /**
     * Gets a value at a specific position in this list.
     *
     * @param pos the position of the element to get.
     * @return the value at that position
     */
    V get(long pos);

    /**
     * Removes the value at a given position.
     *
     * @param pos the position of the element to remove. Elements following this position will be displaced to fill the gap.
     */
    void remove(long pos);

    /**
     * Removes and returns the value at a given position.
     *
     * @param pos the position of the element to remove. Elements following this position will be displaced to fill the gap.
     * @return the removed value
     */
    V getAndRemove(long pos);

    /**
     * Gets the position of a given element in this list. If the element is not contained in this list, returns {@code -1}.
     *
     * @param value the value whose position should be obtained
     * @return the value's position in the list, or {@code -1} if not present
     */
    long indexOf(@NonNull V value);

    @Override
    default boolean contains(@NonNull V value) {
        return this.indexOf(value) != -1L;
    }

    @Override
    default PStream<V> stream() {
        return new UncheckedListStream<>(this, false);
    }

    @Override
    default PStream<V> concurrentStream() {
        return new ConcurrentListStream<>(this, false);
    }
}
