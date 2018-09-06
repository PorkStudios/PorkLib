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

import net.daporkchop.lib.primitive.iterator.bi.CharacterFloatIterator;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.CharacterFloatConsumer;
import net.daporkchop.lib.primitive.lambda.function.ObjectToBooleanFunction;
import net.daporkchop.lib.primitive.lambda.function.bi.CharacterFloatToBooleanFunction;
import net.daporkchop.lib.primitive.tuple.CharacterFloatTuple;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;

import lombok.*;

/**
 * Allows parallel iteration over an iterator of char => float mappings. Implementing classes should be
 * thread safe and concurrent (calling advance() should throw {@link IteratorCompleteException} instead of (char) 0,
 * remove() should remove the last value given to this thread).
 * <p>
 * This also replaces the normal forEachRemaining() functions with concurrent methods.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public interface CharacterFloatConcurrentIterator extends CharacterFloatIterator   {
    /**
     * Iterate over all remaining entries in a thread-safe manner. If you intend to have multiple threads iterating
     * over this at the same time, simply have all of them call this method. Iteration is done in a
     * first-come-first-served manner.
     *
     * @param function the function to execute on values. the function should return true if the given entry is to be removed,
     *                 false otherwise.
     */
    default void concurrentIterate(@NonNull CharacterFloatToBooleanFunction function)    {
        try {
            while (this.hasNext())  {
                CharacterFloatTuple tuple = this.advance();
                if (function.apply(tuple.getK(), tuple.getV()))    {
                    this.remove();
                }
            }
        } catch (IteratorCompleteException e)   {
            //exit safely if another thread advanced between this thread's calling hasNext()
            //and advance()
        }
    }

    /**
     * Iterate over this all remaining entries in a thread-safe manner. If you intend to have multiple threads iterating
     * over this at the same time, simply have all of them call this method. Iteration is done in a
     * first-come-first-served manner.
     *
     * @param function the function to execute on values. the function should return true if the given entry is to be removed,
     *                 false otherwise.
     */
    default void concurrentIterate(@NonNull ObjectToBooleanFunction<CharacterFloatTuple> function)    {
        try {
            while (this.hasNext())  {
                if (function.apply(this.advance()))    {
                    this.remove();
                }
            }
        } catch (IteratorCompleteException e)   {
            //exit safely if another thread advanced between this thread's calling hasNext()
            //and advance()
        }
    }

    @Override
    default void forEachRemaining(@NonNull ObjectConsumer<CharacterFloatTuple> consumer)    {
        this.concurrentIterate(e -> {
            consumer.accept(e);
            return false;
        });
    }

    @Override
    default void forEachRemaining(@NonNull CharacterFloatConsumer consumer)    {
        this.concurrentIterate((k, v) -> {
            consumer.accept(k, v);
            return false;
        });
    }

    @Override
    default void forEachRemaining(@NonNull ObjectToBooleanFunction<CharacterFloatTuple> function)    {
        this.concurrentIterate(function);
    }

    @Override
    default void forEachRemaining(@NonNull CharacterFloatToBooleanFunction function)    {
        this.concurrentIterate(function);
    }
}
