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

package net.daporkchop.lib.primitive.iterator.bi;

import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.LongFloatConsumer;
import net.daporkchop.lib.primitive.lambda.function.ObjectToBooleanFunction;
import net.daporkchop.lib.primitive.lambda.function.bi.LongFloatToBooleanFunction;
import net.daporkchop.lib.primitive.tuple.LongFloatTuple;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;

import lombok.*;

/**
 * An iterator over a collection of long => float mappings
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public interface LongFloatIterator   {
    /**
     * Checks if there are remaining entries in the iterator.
     *
     * @return whether or not there are still entries remaining (iteration should continue)
     */
    boolean hasNext();

    /**
     * Get the current entry.
     *
     * @return the entry at the iterator's current position
     */
    LongFloatTuple get();

    /**
     * Moves the iterator forwards by one.
     *
     * @return the entry at the new position.
     * @throws IteratorCompleteException if the end of the iterator has already been reached.
     */
    LongFloatTuple advance();

    /**
     * Removes the last returned entry from the backing set.
     */
    void remove();

    /**
     * Iterates through every remaining entry in the iterator, executing a given function on said entries.
     *
     * @param consumer the function to run on the entries.
     */
    default void forEachRemaining(@NonNull ObjectConsumer<LongFloatTuple> consumer)    {
        while (this.hasNext())  {
            consumer.accept(this.advance());
        }
    }

    /**
     * Iterates through every remaining entry in the iterator, executing a given function on said entries.
     *
     * @param consumer the function to run on the entries.
     */
    default void forEachRemaining(@NonNull LongFloatConsumer consumer)    {
        while (this.hasNext())  {
            LongFloatTuple tuple = this.advance();
            consumer.accept(tuple.getK(), tuple.getV());
        }
    }

    /**
     * Iterates through every remaining entry in the iterator, executing a given function on said entries.
     *
     * @param function the function to run on the entries. if it returns true, the entry will be
     *                 removed after execution.
     */
    default void forEachRemaining(@NonNull ObjectToBooleanFunction<LongFloatTuple> function)    {
        while (this.hasNext())  {
            if (function.apply(this.advance()))    {
                this.remove();
            }
        }
    }

    /**
     * Iterates through every remaining entry in the iterator, executing a given function on said entries.
     *
     * @param function the function to run on the entries. if it returns true, the entry will be
     *                 removed after execution.
     */
    default void forEachRemaining(@NonNull LongFloatToBooleanFunction function)    {
        while (this.hasNext())  {
            LongFloatTuple tuple = this.advance();
            if (function.apply(tuple.getK(), tuple.getV()))    {
                this.remove();
            }
        }
    }
}
