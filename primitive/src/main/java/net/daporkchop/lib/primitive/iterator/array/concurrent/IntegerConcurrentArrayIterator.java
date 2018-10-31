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

package net.daporkchop.lib.primitive.iterator.array.concurrent;

import net.daporkchop.lib.primitive.iterator.concurrent.IntegerConcurrentIterator;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.*;

/**
 * A concurrent iterator over an array of int.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class IntegerConcurrentArrayIterator implements IntegerConcurrentIterator  {
    private final int[] arr;
    private final AtomicInteger index = new AtomicInteger(0);
    private final ThreadLocal<AtomicInteger> tlIndex = ThreadLocal.withInitial(() -> new AtomicInteger(-1));

    public IntegerConcurrentArrayIterator(@NonNull int[] arr)    {
        this.arr = arr;
    }

    @Override
    public boolean hasNext()    {
        return this.index.get() < arr.length;
    }

    @Override
    public int get()   {
        return this.arr[this.tlIndex.get().get()];
    }

    @Override
    public synchronized int advance()   {
        if (!this.hasNext())    {
            throw new IteratorCompleteException();
        }
        int i = this.index.getAndIncrement();
        this.tlIndex.get().set(i);
        return this.arr[i];
    }

    @Override
    public void remove()    {
        throw new UnsupportedOperationException();
    }
}