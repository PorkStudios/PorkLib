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

package net.daporkchop.lib.primitive.iterator.concurrent;

import net.daporkchop.lib.primitive.iterator.LongIterator;
import net.daporkchop.lib.primitive.lambda.consumer.LongConsumer;
import net.daporkchop.lib.primitive.lambda.function.LongToBooleanFunction;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;

import lombok.*;

/**
 * Allows parallel iteration over an iterator of long values. Implementing classes should be thread safe
 * and concurrent (calling advance() should throw {@link IteratorCompleteException} instead of 0L,
 * remove() should remove the last value given to this thread)
 * <p>
 * This also replaces the normal forEachRemaining() functions with concurrent methods.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public interface LongConcurrentIterator extends LongIterator   {
    /**
     * Iterate over this iterator in a thread-safe manner. If you intend to have multiple threads iterating
     * over this at the same time, simply have all of them call this method. Iteration is done in a
     * first-come-first-served manner.
     *
     * @param function the function to execute on values. the function should return true if the given entry is to be removed,
     *                 false otherwise.
     */
    default void concurrentIterate(@NonNull LongToBooleanFunction function)    {
        try {
            while (this.hasNext())  {
                long val = this.advance();
                if (function.apply(val))    {
                    this.remove();
                }
            }
        } catch (IteratorCompleteException e)   {
            //exit safely if another thread advanced between this thread's calling hasNext()
            //and advance()
        }
    }

    @Override
    default void forEachRemaining(@NonNull LongConsumer consumer)    {
        this.concurrentIterate(k -> {
            consumer.accept(k);
            return false;
        });
    }

    @Override
    default void forEachRemaining(@NonNull LongToBooleanFunction function)   {
        this.concurrentIterate(function);
    }
}
