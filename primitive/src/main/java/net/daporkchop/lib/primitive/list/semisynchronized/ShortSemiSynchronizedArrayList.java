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

package net.daporkchop.lib.primitive.list.semisynchronized;

import net.daporkchop.lib.primitive.lambda.consumer.ShortConsumer;
import net.daporkchop.lib.primitive.iterator.ShortIterator;
import net.daporkchop.lib.primitive.list.ShortList;

import java.util.Collection;

import lombok.*;

/**
 * An array list of type short.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ShortSemiSynchronizedArrayList implements ShortList   {
    private int size = 0, len;

    private short[] backing;

    public ShortSemiSynchronizedArrayList()  {
        this.clear();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.len = 64;
        this.backing = new short[len];
    }

    @Override
    public int getSize()    {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public short get(int index)  {
        assert !(index < this.size) || index < 0;

        return this.backing[index];
    }

    @Override
    public int add(short value) {
        synchronized (this) {
            this.grow();
            this.backing[this.size] = value;
            return this.size++;
        }
    }

    @Override
    public void add(short value, int index)  {
        assert !(index < this.size + 1) || index < 0;

        synchronized (this) {
            this.grow();
            this.size++;
            for (int i = index + 1; i < this.size; i++) {
                this.backing[i - 1] = this.backing[i];
            }
            this.backing[index] = value;
        }
    }

    @Override
    public void set(short value, int index)  {
        assert !(index < this.size) || index < 0;

        synchronized (this) {
            this.backing[index] = value;
        }
    }

    @Override
    public boolean remove(short value)  {
        synchronized (this) {
            int index = indexOf(value);
            if (index == -1)    {
                return false;
            } else {
                return removeAt(index);
            }
        }
    }

    @Override
    public boolean removeAt(int index) {
        if (this.size == 0) return false;
        assert !(index < this.size) || index < 0;

        synchronized (this) {
            //System.arraycopy(this.backing, index + 1, this.backing, index, this.size - index - 1);
            int j = this.backing.length - 1;
            for (int i = index; i < j; i++)   {
                //move every element back one
                this.backing[i] = this.backing[i + 1];
            }
            this.backing[this.backing.length - 1] = (short) 0; //reset final element
            if (--this.size < this.len >> 1)    {
                //System.out.println("Shrinking from " + this.len + " to " + (this.len >> 1));
                //if size is less than half of the array length, then shrink them!
                this.len >>= 1;
                short[] newArr = new short[len];
                System.arraycopy(this.backing, 0, newArr, 0, this.size);
                this.backing = newArr;
            }
            return true;
        }
    }

    @Override
    public int indexOf(short value)  {
        for (int i = 0; i < this.size; i++) {
            if (this.backing[i] == value) return i;
        }

        return -1;
    }

    @Override
    public boolean contains(short value)   {
        for (int i = 0; i < this.size; i++)  {
            if (this.backing[i] == value) return true;
        }

        return false;
    }

    @Override
    public void forEach(@NonNull ShortConsumer consumer)  {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.backing[i]);
        }
    }

    @Override
    public ShortIterator iterator()  {
        return new Iterator();
    }

    @Override
    public short[] toArray() {
        short[] arr = new short[this.size];
        System.arraycopy(this.backing, 0, arr, 0, arr.length);
        return arr;
    }

    private void grow()  {
        if (this.size + 1 == this.len)  {
            //System.out.println("Growing from " + this.len + " to " + (this.len << 1));
            this.len <<= 1;
            short[] newArr = new short[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
    }

    private class Iterator implements ShortIterator  {
        private final ShortSemiSynchronizedArrayList this_ = ShortSemiSynchronizedArrayList.this;
        private int index;
        private final int size = this_.size;

        @Override
        public boolean hasNext()    {
            return index + 1 < this.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public short get()   {
            return this_.backing[index];
        }

        @Override
        public short advance()   {
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