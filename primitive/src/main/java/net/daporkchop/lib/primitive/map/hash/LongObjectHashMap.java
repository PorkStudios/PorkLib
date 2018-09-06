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

import net.daporkchop.lib.primitive.lambda.consumer.LongConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.LongObjectConsumer;
import net.daporkchop.lib.primitive.lambda.function.LongToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.IntegerToBooleanFunction;
import net.daporkchop.lib.primitive.iterator.LongIterator;
import net.daporkchop.lib.primitive.iterator.ObjectIterator;
import net.daporkchop.lib.primitive.iterator.bi.LongObjectIterator;
import net.daporkchop.lib.primitive.map.LongObjectMap;
import net.daporkchop.lib.primitive.tuple.LongObjectTuple;
import net.daporkchop.lib.primitive.tuple.LongObjectImmutableTuple;
import net.daporkchop.lib.primitiveutil.IsPow2;

import java.util.BitSet;
import java.util.ConcurrentModificationException;

import lombok.*;

/**
 * A hash map, using a key type of long and a value type of V.
 * This works™, but isn't particularly efficiently
 * At some point I might get around to fixing this, until then use tree maps as they work correctly
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class LongObjectHashMap<V extends Object> implements LongObjectMap<V>    {
    protected long[] keys;
    protected Object[] values;
    protected BitSet states;
    protected final int baseSize;
    protected int len;
    protected int shrinkThreshold;
    protected int growThreshold;
    protected int mask;
    protected int size;
    protected final LongToIntegerFunction keyHash;

    public LongObjectHashMap()    {
        this(16384);
    }

    public LongObjectHashMap(int baseSize)    {
        this(baseSize, null);
    }

    public LongObjectHashMap(LongToIntegerFunction keyHash) {
        this(16384, keyHash);
    }

    public LongObjectHashMap(int baseSize, LongToIntegerFunction keyHash)    {
        if (!IsPow2.checkInt(baseSize)) throw new IllegalArgumentException("baseSize must be a power of 2!");
        this.baseSize = baseSize;
        if (keyHash == null)    {
            this.keyHash = in -> {
                return ((int) ((in >> 32) & 0x7fffffff)) ^ ((int) (in & 0x7fffffff)) & 0x7fffffff;
            };
        } else {
            this.keyHash = keyHash;
        }

        //clear function sets up the arrays and such for us
        clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(long key)   {
        int i = getIndex(key);
        //check if key is present
        if (this.states.get(i)) {
            return (V) this.values[i];
        } else {
            //if not, return empty value
            return null;
        }
    }

    @Override
    public V put(long key, V value)   {
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
            V old = (V) this.values[i];
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
            return null;
        }
    }

    @Override
    public V remove(long key)    {
        return removeIndex(getIndex(key));
    }

    @Override
    public boolean containsKey(long key)    {
        return this.states.get(getIndex(key));
    }

    @Override
    public boolean containsValue(V value)    {
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
        this.keys = new long[baseSize];
        this.values = new Object[baseSize];
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
    public void forEachKey(@NonNull LongConsumer consumer)   {
        this.forEach(i -> consumer.accept( this.keys[i]));
    }

    @Override
    public void forEachValue(@NonNull ObjectConsumer<V> consumer)   {
        this.forEach(i -> consumer.accept( (V) this.values[i]));
    }

    @Override
    public void forEachEntry(@NonNull LongObjectConsumer<V> consumer)   {
        this.forEach(i -> consumer.accept( this.keys[i],  (V) this.values[i]));
    }

    public LongIterator keyIterator()   {
        return new KeyIterator();
    }

    public ObjectIterator<V> valueIterator() {
        return new ValueIterator();
    }

    public LongObjectIterator<V> entryIterator() {
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
            Object[] values;
            long[] keys;
            BitSet states;
            //repeat until there's no hash collisions
            do {
                //multiply length by 2 and update local values accordingly
                this.len <<= 1;
                updateConstants();
                //init new arrays
                values = new Object[this.len];
                keys = new long[this.len];
                states = new BitSet();
            } while (reHash(values, keys, states));
            this.values = values;
            this.keys = keys;
            this.states = states;
            //System.out.println("Grew! New size: " + len);
        }
    }

    protected boolean reHash(Object[] values, long[] keys, BitSet states)    {
        return this.forEachBreaking(i -> {
            long key = this.keys[i];
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

    protected int getIndex(long key)    {
        return this.keyHash.apply(key) & this.mask;
    }

    protected void updateConstants()    {
        this.shrinkThreshold = this.len >> 1;
        this.growThreshold = this.len << 1;
        this.mask = this.len - 1;
    }

    protected V removeIndex(int i)   {
        //check if the key is present
        if (this.states.get(i)) {
            //mark the index as deleted
            //key and value arrays don't need to be reset, as they'll be overwritten if a key with the same index is added later
            this.states.clear(i);
            //decrement size
            this.size--;
            //the value is actually still in the array, just there's no indexes pointing to it anymore after this call
            return (V) this.values[i];
            //TODO: some method of shrinking down arrays
            //that'll be a PITA in case there's a hash collision during shrinking because
            //it'll then proceed to keep trying to shrink down the array and having collisions
            //until one of the colliding elements is removed.
            //caching the indexes of the colliding elements is an option, but is probably useless overhead
        } else {
            //if not, return empty value
            return null;
        }
    }

    private class KeyIterator implements LongIterator {
        private int size;
        private int index = -1;
        private int next;

        public KeyIterator()    {
            this.size = LongObjectHashMap.this.size;
            this.next = LongObjectHashMap.this.states.nextSetBit(0);
        }

        @Override
        public boolean hasNext()    {
            return this.next != -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public long get()   {
            return LongObjectHashMap.this.keys[this.index];
        }

        @Override
        public long advance()   {
            findNext();
            if (this.index == -1) throw new IllegalStateException("Reached end of array");
            return get();
        }

        protected void findNext()   {
            this.next = LongObjectHashMap.this.states.nextSetBit((this.index = this.next) + 1);
        }

        @Override
        public void remove()    {
            LongObjectHashMap.this.removeIndex(this.index);
            this.size--;
        }
    }

    private class ValueIterator implements ObjectIterator<V> {
        private int size;
        private int index = -1;
        private int next;

        public ValueIterator()    {
            this.size = LongObjectHashMap.this.size;
            this.next = LongObjectHashMap.this.states.nextSetBit(0);
        }

        @Override
        public boolean hasNext()    {
            return this.next != -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V get()   {
            return (V) LongObjectHashMap.this.values[this.index];
        }

        @Override
        public V advance()   {
            findNext();
            if (this.index == -1) throw new IllegalStateException("Reached end of array");
            return get();
        }

        protected void findNext()   {
            this.next = LongObjectHashMap.this.states.nextSetBit((this.index = this.next) + 1);
        }

        @Override
        public void remove()    {
            LongObjectHashMap.this.removeIndex(this.index);
            this.size--;
        }
    }

    private class EntryIterator implements LongObjectIterator<V> {
        private int size;
        private int index = -1;
        private int next;
        private LongObjectTuple<V> tuple;

        public EntryIterator()    {
            this.size = LongObjectHashMap.this.size;
            this.next = LongObjectHashMap.this.states.nextSetBit(0);
        }

        @Override
        public boolean hasNext()    {
            return this.next != -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public LongObjectTuple<V> get()   {
            return this.tuple;
        }

        @Override
        public LongObjectTuple<V> advance()   {
            findNext();
            if (this.index == -1) throw new IllegalStateException("Reached end of array");
            return this.tuple = new LongObjectImmutableTuple<V>( LongObjectHashMap.this.keys[this.index],  (V) LongObjectHashMap.this.values[this.index]);
        }

        protected void findNext()   {
            this.next = LongObjectHashMap.this.states.nextSetBit((this.index = this.next) + 1);
        }

        @Override
        public void remove()    {
            LongObjectHashMap.this.removeIndex(this.index);
            this.size--;
        }
    }
}
