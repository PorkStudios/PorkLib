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

import net.daporkchop.lib.primitive.lambda.consumer.FloatConsumer;
import net.daporkchop.lib.primitive.iterator.FloatIterator;
import net.daporkchop.lib.primitive.list.FloatList;

import java.util.Collection;

import lombok.*;

/**
 * An array list of type float.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class FloatArrayList implements FloatList   {
    private int size = 0, len;

    private float[] backing;

    public FloatArrayList()  {
        this.clear();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.len = 64;
        this.backing = new float[len];
    }

    @Override
    public int getSize()    {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public float get(int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        return this.backing[index];
    }

    @Override
    public int add(float value) {
        this.grow();
        this.backing[this.size] = value;
        return this.size++;
    }

    @Override
    public void add(float value, int index)  {
        if (!(index < this.size + 1) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.grow();
        this.size++;
        for (int i = index + 1; i < this.size; i++) {
            this.backing[i - 1] = this.backing[i];
        }
        this.backing[index] = value;
    }

    @Override
    public void set(float value, int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.backing[index] = value;
    }

    @Override
    public boolean remove(float value)  {
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
        this.backing[this.backing.length - 1] = 0F; //reset final element
        if (--this.size < this.len >> 1)    {
            //System.out.println("Shrinking from " + this.len + " to " + (this.len >> 1));
            //if size is less than half of the array length, then shrink them!
            this.len >>= 1;
            float[] newArr = new float[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
        return true;
    }

    @Override
    public int indexOf(float value)  {
        for (int i = 0; i < this.size; i++) {
            if (this.backing[i] == value) return i;
        }

        return -1;
    }

    @Override
    public boolean contains(float value)   {
        for (int i = 0; i < this.size; i++)  {
            if (this.backing[i] == value) return true;
        }

        return false;
    }

    @Override
    public void forEach(@NonNull FloatConsumer consumer)  {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.backing[i]);
        }
    }

    @Override
    public FloatIterator iterator()  {
        return new Iterator();
    }

    @Override
    public float[] toArray() {
        float[] arr = new float[this.size];
        System.arraycopy(this.backing, 0, arr, 0, arr.length);
        return arr;
    }

    private void grow()  {
        if (this.size + 1 == this.len)  {
            //System.out.println("Growing from " + this.len + " to " + (this.len << 1));
            this.len <<= 1;
            float[] newArr = new float[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
    }

    private class Iterator implements FloatIterator  {
        private final FloatArrayList this_ = FloatArrayList.this;
        private int index;

        @Override
        public boolean hasNext()    {
            return index + 1 < this_.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public float get()   {
            return this_.backing[index];
        }

        @Override
        public float advance()   {
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