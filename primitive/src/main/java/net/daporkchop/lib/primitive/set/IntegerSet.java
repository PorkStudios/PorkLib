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

package net.daporkchop.lib.primitive.set;

import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.iterator.IntegerIterator;

import java.util.Collection;

import lombok.*;

/**
 * A set of type int.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public interface IntegerSet   {
    /**
     * Add a element to the set
     *
     * @param value The value to add
     */
    void add(int value);

    /**
     * Add a number of elements to the set
     *
     * @param values The values to add
     */
    default void addAll(@NonNull int[] values)    {
        for (int p : values)   {
            this.add(p);
        }
    }

    /**
     * Copies the values from another set into this one
     *
     * @param set The set whose elements should be copied
     */
    default void addAll(@NonNull IntegerSet set)    {
        if (set.getSize() == 0) return;

        set.forEach(this::add);
    }

    /**
     * Remove a value from the set
     *
     * @param value The value to remove
     * @return whether or not something was removed
     */
    boolean remove(int value);

    /**
     * Remove a number of values from the set
     *
     * @param values The values to remove
     */
    default void removeAll(@NonNull Collection<Integer> values) {
        values.forEach(this::remove);
    }

    /**
     * Checks if the set contains a value
     *
     * @param value The value to check for
     * @return true if such a value exists, false otherwise
     */
    boolean contains(int value);

    /**
     * Empties the set completely
     */
    void clear();

    /**
     * Get the total number of elements in this set
     *
     * @return the total number of values
     */
    int getSize();

    /**
     * Runs a given function on every value in the set
     *
     * @param consumer the function to run
     */
    void forEach(IntegerConsumer consumer);

    /**
     * Gets an iterator over all values in this set
     *
     * @return an iterator over all values in this set
     */
    IntegerIterator iterator();

    default int hash(int in)  {
        return in & 0x7fffffff;
    }
}
