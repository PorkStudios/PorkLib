/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.primitive.list;

import net.daporkchop.lib.primitive.lambda.consumer.ShortConsumer;
import net.daporkchop.lib.primitive.iterator.ShortIterator;

import java.util.Collection;

import lombok.*;

/**
 * A list of type short.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public interface ShortList   {
    /**
     * Get an element at the specified index
     *
     * @param index The index of the element to get
     * @return the element at to the given index, or (short) 0 if no such mapping exists
     */
    short get(int index);

    /**
     * Add a element to the list
     *
     * @param value The value to add
     * @return the index at which the element was added
     */
    int add(short value);

    /**
     * Add an element to the list
     *
     * @param value The value to add
     * @param index the index to add it at
     */
    void add(short value, int index);

    /**
     * Sets the element at the given index to the given value, replacing any value
     * that may already be there.
     *
     * @param value the value to add
     * @param index the index to set
     */
    void set(short value, int index);

    /**
     * Add a number of elements to the list
     *
     * @param values The values to add
     */
    default void addAll(@NonNull short[] values)    {
        for (short p : values)   {
            this.add(p);
        }
    }

    /**
     * Copies the values from another list into this one
     *
     * @param list The list whose elements should be copied
     */
    default void addAll(@NonNull ShortList list)    {
        if (list.getSize() == 0) return;

        list.forEach(this::add);
    }

    /**
     * Remove a value from the list
     *
     * @param value The value to remove
     * @return whether or not something was removed
     */
    boolean remove(short value);

    /**
     * Remove a value at the specified index
     *
     * @param index the index to remove at
     */
    boolean removeAt(int index);

    /**
     * Remove a number of values from the list
     *
     * @param values The values to remove
     */
    default void removeAll(@NonNull Collection<Short> values) {
        values.forEach(this::remove);
    }

    /**
     * Gets the index of a given value
     *
     * @param value the value to get the index of
     * @return the value's index, or -1 if not found
     */
    int indexOf(short value);

    /**
     * Checks if the list contains a value
     *
     * @param value The value to check for
     * @return true if such a value exists, false otherwise
     */
    boolean contains(short value);

    /**
     * Empties the list completely
     */
    void clear();

    /**
     * Get the total number of elements in this list
     *
     * @return the total number of values
     */
    int getSize();

    /**
     * Runs a given function on every value in the list
     *
     * @param consumer the function to run
     */
    void forEach(ShortConsumer consumer);

    /**
     * Gets an iterator over all values in this list
     *
     * @return an iterator over all values in this list
     */
    ShortIterator iterator();

    /**
     * Gets an array containing all values in the list
     *
     * @return an array containing all values in the list
     */
    short[] toArray();

    /**
     * Checks if this list is empty
     *
     * @return whether or not this list is empty
     */
    default boolean isEmpty()   {
        return this.getSize() == 0;
    }
}
