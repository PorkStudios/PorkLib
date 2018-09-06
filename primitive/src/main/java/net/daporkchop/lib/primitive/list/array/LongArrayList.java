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

package net.daporkchop.lib.primitive.list.array;

import net.daporkchop.lib.primitive.lambda.consumer.LongConsumer;
import net.daporkchop.lib.primitive.iterator.LongIterator;
import net.daporkchop.lib.primitive.list.LongList;

import java.util.Collection;

import lombok.*;

/**
 * An array list of type long.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class LongArrayList implements LongList   {
    private int size = 0, len;

    private long[] backing;

    public LongArrayList()  {
        this.clear();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.len = 64;
        this.backing = new long[len];
    }

    @Override
    public int getSize()    {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public long get(int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        return this.backing[index];
    }

    @Override
    public int add(long value) {
        this.grow();
        this.backing[this.size] = value;
        return this.size++;
    }

    @Override
    public void add(long value, int index)  {
        if (!(index < this.size + 1) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.grow();
        this.size++;
        for (int i = index + 1; i < this.size; i++) {
            this.backing[i - 1] = this.backing[i];
        }
        this.backing[index] = value;
    }

    @Override
    public void set(long value, int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.backing[index] = value;
    }

    @Override
    public boolean remove(long value)  {
        int index = indexOf(value);
        if (index == -1)    {
            return false;
        } else {
            return removeAt(index);
        }
    }

    @Override
    public boolean removeAt(int index) {
        if (this.size == 0) return false;
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        //System.arraycopy(this.backing, index + 1, this.backing, index, this.size - index - 1);
        int j = this.backing.length - 1;
        for (int i = index; i < j; i++)   {
            //move every element back one
            this.backing[i] = this.backing[i + 1];
        }
        this.backing[this.backing.length - 1] = 0L; //reset final element
        if (--this.size < this.len >> 1)    {
            //System.out.println("Shrinking from " + this.len + " to " + (this.len >> 1));
            //if size is less than half of the array length, then shrink them!
            this.len >>= 1;
            long[] newArr = new long[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
        return true;
    }

    @Override
    public int indexOf(long value)  {
        for (int i = 0; i < this.size; i++) {
            if (this.backing[i] == value) return i;
        }

        return -1;
    }

    @Override
    public boolean contains(long value)   {
        for (int i = 0; i < this.size; i++)  {
            if (this.backing[i] == value) return true;
        }

        return false;
    }

    @Override
    public void forEach(@NonNull LongConsumer consumer)  {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.backing[i]);
        }
    }

    @Override
    public LongIterator iterator()  {
        return new Iterator();
    }

    @Override
    public long[] toArray() {
        long[] arr = new long[this.size];
        System.arraycopy(this.backing, 0, arr, 0, arr.length);
        return arr;
    }

    private void grow()  {
        if (this.size + 1 == this.len)  {
            //System.out.println("Growing from " + this.len + " to " + (this.len << 1));
            this.len <<= 1;
            long[] newArr = new long[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
    }

    private class Iterator implements LongIterator  {
        private final LongArrayList this_ = LongArrayList.this;
        private int index;

        @Override
        public boolean hasNext()    {
            return index + 1 < this_.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public long get()   {
            return this_.backing[index];
        }

        @Override
        public long advance()   {
            if (hasNext())  {
                this.index++;
                return get();
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public void remove()    {
            this_.removeAt(this.index--);
        }
    }
}