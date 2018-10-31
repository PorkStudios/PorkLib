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

import net.daporkchop.lib.primitive.lambda.consumer.ByteConsumer;
import net.daporkchop.lib.primitive.iterator.ByteIterator;
import net.daporkchop.lib.primitive.list.ByteList;

import java.util.Collection;

import lombok.*;

/**
 * An array list of type byte.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ByteArrayList implements ByteList   {
    private int size = 0, len;

    private byte[] backing;

    public ByteArrayList()  {
        this.clear();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.len = 64;
        this.backing = new byte[len];
    }

    @Override
    public int getSize()    {
        return this.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public byte get(int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        return this.backing[index];
    }

    @Override
    public int add(byte value) {
        this.grow();
        this.backing[this.size] = value;
        return this.size++;
    }

    @Override
    public void add(byte value, int index)  {
        if (!(index < this.size + 1) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.grow();
        this.size++;
        for (int i = index + 1; i < this.size; i++) {
            this.backing[i - 1] = this.backing[i];
        }
        this.backing[index] = value;
    }

    @Override
    public void set(byte value, int index)  {
        if (!(index < this.size) || index < 0) throw new ArrayIndexOutOfBoundsException("Invalid index " + index);
        this.backing[index] = value;
    }

    @Override
    public boolean remove(byte value)  {
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
        this.backing[this.backing.length - 1] = (byte) 0; //reset final element
        if (--this.size < this.len >> 1)    {
            //System.out.println("Shrinking from " + this.len + " to " + (this.len >> 1));
            //if size is less than half of the array length, then shrink them!
            this.len >>= 1;
            byte[] newArr = new byte[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
        return true;
    }

    @Override
    public int indexOf(byte value)  {
        for (int i = 0; i < this.size; i++) {
            if (this.backing[i] == value) return i;
        }

        return -1;
    }

    @Override
    public boolean contains(byte value)   {
        for (int i = 0; i < this.size; i++)  {
            if (this.backing[i] == value) return true;
        }

        return false;
    }

    @Override
    public void forEach(@NonNull ByteConsumer consumer)  {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.backing[i]);
        }
    }

    @Override
    public ByteIterator iterator()  {
        return new Iterator();
    }

    @Override
    public byte[] toArray() {
        byte[] arr = new byte[this.size];
        System.arraycopy(this.backing, 0, arr, 0, arr.length);
        return arr;
    }

    private void grow()  {
        if (this.size + 1 == this.len)  {
            //System.out.println("Growing from " + this.len + " to " + (this.len << 1));
            this.len <<= 1;
            byte[] newArr = new byte[len];
            System.arraycopy(this.backing, 0, newArr, 0, this.size);
            this.backing = newArr;
        }
    }

    private class Iterator implements ByteIterator  {
        private final ByteArrayList this_ = ByteArrayList.this;
        private int index;

        @Override
        public boolean hasNext()    {
            return index + 1 < this_.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public byte get()   {
            return this_.backing[index];
        }

        @Override
        public byte advance()   {
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