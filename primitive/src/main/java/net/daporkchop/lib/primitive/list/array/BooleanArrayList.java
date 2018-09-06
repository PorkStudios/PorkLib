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

import net.daporkchop.lib.primitive.lambda.consumer.BooleanConsumer;
import net.daporkchop.lib.primitive.iterator.BooleanIterator;
import net.daporkchop.lib.primitive.list.BooleanList;

import java.util.Collection;

import lombok.*;

/**
 * An array list of type boolean.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class BooleanArrayList implements BooleanList   {
    private int size = 0, len;

    private boolean[] backing;

    public BooleanArrayList()  {
        this.clear();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.len = 64;
        this.backing = new boolean[len];
    }

    @Override
    public int getSize()    {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean get(int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        return this.backing[index];
    }

    @Override
    public int add(boolean value) {
        this.grow();
        this.backing[this.size] = value;
        return this.size++;
    }

    @Override
    public void add(boolean value, int index)  {
        if (!(index < this.size + 1) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.grow();
        this.size++;
        for (int i = index + 1; i < this.size; i++) {
            this.backing[i - 1] = this.backing[i];
        }
        this.backing[index] = value;
    }

    @Override
    public void set(boolean value, int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.backing[index] = value;
    }

    @Override
    public boolean remove(boolean value)  {
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
        this.backing[this.backing.length - 1] = false; //reset final element
        if (--this.size < this.len >> 1)    {
            //System.out.println("Shrinking from " + this.len + " to " + (this.len >> 1));
            //if size is less than half of the array length, then shrink them!
            this.len >>= 1;
            boolean[] newArr = new boolean[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
        return true;
    }

    @Override
    public int indexOf(boolean value)  {
        for (int i = 0; i < this.size; i++) {
            if (this.backing[i] == value) return i;
        }

        return -1;
    }

    @Override
    public boolean contains(boolean value)   {
        for (int i = 0; i < this.size; i++)  {
            if (this.backing[i] == value) return true;
        }

        return false;
    }

    @Override
    public void forEach(@NonNull BooleanConsumer consumer)  {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.backing[i]);
        }
    }

    @Override
    public BooleanIterator iterator()  {
        return new Iterator();
    }

    @Override
    public boolean[] toArray() {
        boolean[] arr = new boolean[this.size];
        System.arraycopy(this.backing, 0, arr, 0, arr.length);
        return arr;
    }

    private void grow()  {
        if (this.size + 1 == this.len)  {
            //System.out.println("Growing from " + this.len + " to " + (this.len << 1));
            this.len <<= 1;
            boolean[] newArr = new boolean[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
    }

    private class Iterator implements BooleanIterator  {
        private final BooleanArrayList this_ = BooleanArrayList.this;
        private int index;

        @Override
        public boolean hasNext()    {
            return index + 1 < this_.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean get()   {
            return this_.backing[index];
        }

        @Override
        public boolean advance()   {
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