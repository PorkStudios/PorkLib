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

package net.daporkchop.lib.primitive.iterator;

import net.daporkchop.lib.primitive.lambda.consumer.LongConsumer;
import net.daporkchop.lib.primitive.lambda.function.LongToBooleanFunction;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;

import lombok.*;

/**
 * An iterator over a collection of long
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public interface LongIterator   {
    /**
     * Checks if there are remaining values in the iterator.
     *
     * @return whether or not there are still values remaining (iteration should continue)
     */
    boolean hasNext();

    /**
     * Get the current value.
     *
     * @return the value at the iterator's current position
     */
    long get();

    /**
     * Moves the iterator forwards by one.
     *
     * @return the value at the new position.
     * @throws IteratorCompleteException if the end of the iterator has already been reached.
     */
    long advance();

    /**
     * Removes the current element from the backing collection.
     * Depending on the implementation, this can either cause the iterator to jump back one element or
     * simply keep working on an invalid index.
     * Either way, get() should not be called on this iterater after calling remove() until advance() is called.
     */
    void remove();

    /**
     * Iterates through every remaining element in the iterator, executing a given function on said entries.
     *
     * @param consumer the function to run on the values.
     */
    default void forEachRemaining(@NonNull LongConsumer consumer)    {
        while (this.hasNext())  {
            consumer.accept(this.advance());
        }
    }

    /**
     * Iterates through every remaining element in the iterator, executing a given function on said entries.
     *
     * @param function the function to run on the values. if it returns true, the entry will be
     *                 removed after execution.
     */
    default void forEachRemaining(@NonNull LongToBooleanFunction function)   {
        while (this.hasNext())  {
            if (function.apply(this.advance()))    {
                this.remove();
            }
        }
    }
}
