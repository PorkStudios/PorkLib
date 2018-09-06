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

package net.daporkchop.lib.primitive.map.hash;

import net.daporkchop.lib.primitive.lambda.consumer.DoubleConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ByteConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.DoubleByteConsumer;
import net.daporkchop.lib.primitive.lambda.function.DoubleToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.IntegerToBooleanFunction;
import net.daporkchop.lib.primitive.iterator.DoubleIterator;
import net.daporkchop.lib.primitive.iterator.ByteIterator;
import net.daporkchop.lib.primitive.iterator.bi.DoubleByteIterator;
import net.daporkchop.lib.primitive.map.DoubleByteMap;
import net.daporkchop.lib.primitive.tuple.DoubleByteTuple;
import net.daporkchop.lib.primitive.tuple.DoubleByteImmutableTuple;
import net.daporkchop.lib.primitiveutil.IsPow2;

import java.util.BitSet;
import java.util.ConcurrentModificationException;

import lombok.*;

/**
 * A hash map, using a key type of double and a value type of byte.
 * This works™, but isn't particularly efficiently
 * At some point I might get around to fixing this, until then use tree maps as they work correctly
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class DoubleByteHashMap implements DoubleByteMap    {
    protected double[] keys;
    protected byte[] values;
    protected BitSet states;
    protected final int baseSize;
    protected int len;
    protected int shrinkThreshold;
    protected int growThreshold;
    protected int mask;
    protected int size;
    protected final DoubleToIntegerFunction keyHash;

    public DoubleByteHashMap()    {
        this(16384);
    }

    public DoubleByteHashMap(int baseSize)    {
        this(baseSize, null);
    }

    public DoubleByteHashMap(DoubleToIntegerFunction keyHash) {
        this(16384, keyHash);
    }

    public DoubleByteHashMap(int baseSize, DoubleToIntegerFunction keyHash)    {
        if (!IsPow2.checkInt(baseSize)) throw new IllegalArgumentException("baseSize must be a power of 2!");
        this.baseSize = baseSize;
        if (keyHash == null)    {
            this.keyHash = in -> {
                long l = Double.doubleToLongBits(in);
        return (int) ((((l >> 32) & 0x7fffffff)) ^ ((l & 0x7fffffff)) & 0x7fffffff);
            };
        } else {
            this.keyHash = keyHash;
        }

        //clear function sets up the arrays and such for us
        clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public byte get(double key)   {
        int i = getIndex(key);
        //check if key is present
        if (this.states.get(i)) {
            return this.values[i];
        } else {
            //if not, return empty value
            return (byte) 0;
        }
    }

    @Override
    public byte put(double key, byte value)   {
        int i = getIndex(key);
        //check if key is present
        if (this.states.get(i)) {
            //if the new key isn't equal to the stored key, we've got us a hash collision
            //if that happens, just expand the array over and over until there's no more collisions
            if (key != this.keys[i])    {
                grow(true);
                //fetch new index, as the arrays have been changed
                i = getIndex(key);
                //increment size index because this is a new entry
                this.size++;
            }
            //fetch and overwrite old value, then return it
            @SuppressWarnings("unchecked")
            byte old = this.values[i];
            this.values[i] = value;
            return old;
        } else {
            //mark key index as full
            this.states.set(i);
            //actually set data
            this.keys[i] = key;
            this.values[i] = value;
            //increment size and check if we need to grow the backing arrays
            this.size++;
            grow(false);
            //return empty value, as there was no old value to return
            return (byte) 0;
        }
    }

    @Override
    public byte remove(double key)    {
        return removeIndex(getIndex(key));
    }

    @Override
    public boolean containsKey(double key)    {
        return this.states.get(getIndex(key));
    }

    @Override
    public boolean containsValue(byte value)    {
        return this.forEachBreaking((i) -> {
            if (this.values[i] == value)    {
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void clear() {
        this.keys = new double[baseSize];
        this.values = new byte[baseSize];
        this.states = new BitSet();

        this.len = baseSize;
        updateConstants();
        this.size = 0;
    }

    @Override
    public int getSize()    {
        return this.size;
    }

    @Override
    public void forEachKey(@NonNull DoubleConsumer consumer)   {
        this.forEach(i -> consumer.accept( this.keys[i]));
    }

    @Override
    public void forEachValue(@NonNull ByteConsumer consumer)   {
        this.forEach(i -> consumer.accept( this.values[i]));
    }

    @Override
    public void forEachEntry(@NonNull DoubleByteConsumer consumer)   {
        this.forEach(i -> consumer.accept( this.keys[i],  this.values[i]));
    }

    public DoubleIterator keyIterator()   {
        return new KeyIterator();
    }

    public ByteIterator valueIterator() {
        return new ValueIterator();
    }

    public DoubleByteIterator entryIterator() {
        return new EntryIterator();
    }

    protected boolean forEachBreaking(IntegerToBooleanFunction function)    {
        for (int i = this.states.nextSetBit(0); i != -1; i = this.states.nextSetBit(i + 1)) {
            if (function.apply(i)) {
                return true;
            }
        }
        return false;
    }

    protected void forEach(IntegerConsumer consumer)    {
        for (int i = this.states.nextSetBit(0); i != -1; i = this.states.nextSetBit(i + 1)) {
            consumer.accept(i);
        }
    }

    protected void grow(boolean force)  {
        if (force || this.size >= this.growThreshold)    {
            //System.out.println("Growing from current size: " + len);
            byte[] values;
            double[] keys;
            BitSet states;
            //repeat until there's no hash collisions
            do {
                //multiply length by 2 and update local values accordingly
                this.len <<= 1;
                updateConstants();
                //init new arrays
                values = new byte[this.len];
                keys = new double[this.len];
                states = new BitSet();
            } while (reHash(values, keys, states));
            this.values = values;
            this.keys = keys;
            this.states = states;
            //System.out.println("Grew! New size: " + len);
        }
    }

    protected boolean reHash(byte[] values, double[] keys, BitSet states)    {
        return this.forEachBreaking(i -> {
            double key = this.keys[i];
            int j = getIndex(key);
            if (states.get(j))  {
                //there's already an element with this index!
                return true;
            }
            states.set(j);
            values[j] = this.values[i];
            keys[j] = key;
            return false;
        });
    }

    protected int getIndex(double key)    {
        return this.keyHash.apply(key) & this.mask;
    }

    protected void updateConstants()    {
        this.shrinkThreshold = this.len >> 1;
        this.growThreshold = this.len << 1;
        this.mask = this.len - 1;
    }

    protected byte removeIndex(int i)   {
        //check if the key is present
        if (this.states.get(i)) {
            //mark the index as deleted
            //key and value arrays don't need to be reset, as they'll be overwritten if a key with the same index is added later
            this.states.clear(i);
            //decrement size
            this.size--;
            //the value is actually still in the array, just there's no indexes pointing to it anymore after this call
            return this.values[i];
            //TODO: some method of shrinking down arrays
            //that'll be a PITA in case there's a hash collision during shrinking because
            //it'll then proceed to keep trying to shrink down the array and having collisions
            //until one of the colliding elements is removed.
            //caching the indexes of the colliding elements is an option, but is probably useless overhead
        } else {
            //if not, return empty value
            return (byte) 0;
        }
    }

    private class KeyIterator implements DoubleIterator {
        private int size;
        private int index = -1;
        private int next;

        public KeyIterator()    {
            this.size = DoubleByteHashMap.this.size;
            this.next = DoubleByteHashMap.this.states.nextSetBit(0);
        }

        @Override
        public boolean hasNext()    {
            return this.next != -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public double get()   {
            return DoubleByteHashMap.this.keys[this.index];
        }

        @Override
        public double advance()   {
            findNext();
            if (this.index == -1) throw new IllegalStateException("Reached end of array");
            return get();
        }

        protected void findNext()   {
            this.next = DoubleByteHashMap.this.states.nextSetBit((this.index = this.next) + 1);
        }

        @Override
        public void remove()    {
            DoubleByteHashMap.this.removeIndex(this.index);
            this.size--;
        }
    }

    private class ValueIterator implements ByteIterator {
        private int size;
        private int index = -1;
        private int next;

        public ValueIterator()    {
            this.size = DoubleByteHashMap.this.size;
            this.next = DoubleByteHashMap.this.states.nextSetBit(0);
        }

        @Override
        public boolean hasNext()    {
            return this.next != -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public byte get()   {
            return DoubleByteHashMap.this.values[this.index];
        }

        @Override
        public byte advance()   {
            findNext();
            if (this.index == -1) throw new IllegalStateException("Reached end of array");
            return get();
        }

        protected void findNext()   {
            this.next = DoubleByteHashMap.this.states.nextSetBit((this.index = this.next) + 1);
        }

        @Override
        public void remove()    {
            DoubleByteHashMap.this.removeIndex(this.index);
            this.size--;
        }
    }

    private class EntryIterator implements DoubleByteIterator {
        private int size;
        private int index = -1;
        private int next;
        private DoubleByteTuple tuple;

        public EntryIterator()    {
            this.size = DoubleByteHashMap.this.size;
            this.next = DoubleByteHashMap.this.states.nextSetBit(0);
        }

        @Override
        public boolean hasNext()    {
            return this.next != -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public DoubleByteTuple get()   {
            return this.tuple;
        }

        @Override
        public DoubleByteTuple advance()   {
            findNext();
            if (this.index == -1) throw new IllegalStateException("Reached end of array");
            return this.tuple = new DoubleByteImmutableTuple( DoubleByteHashMap.this.keys[this.index],  DoubleByteHashMap.this.values[this.index]);
        }

        protected void findNext()   {
            this.next = DoubleByteHashMap.this.states.nextSetBit((this.index = this.next) + 1);
        }

        @Override
        public void remove()    {
            DoubleByteHashMap.this.removeIndex(this.index);
            this.size--;
        }
    }
}
