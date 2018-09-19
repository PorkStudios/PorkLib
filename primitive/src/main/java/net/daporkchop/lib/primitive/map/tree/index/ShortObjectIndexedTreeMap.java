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

package net.daporkchop.lib.primitive.map.tree.index;

import net.daporkchop.lib.primitive.lambda.consumer.ShortConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ShortObjectConsumer;
import net.daporkchop.lib.primitive.iterator.ShortIterator;
import net.daporkchop.lib.primitive.iterator.ObjectIterator;
import net.daporkchop.lib.primitive.iterator.bi.ShortObjectIterator;
import net.daporkchop.lib.primitive.map.tree.ShortObjectTreeMap;
import net.daporkchop.lib.primitive.tuple.ShortObjectTuple;
import net.daporkchop.lib.primitive.tuple.ShortObjectImmutableTuple;

import java.util.ConcurrentModificationException;

import lombok.*;

/**
 * A tree map, using a key type of short and a value type of V.
 * This map works just like its parent TreeMap, but additionally maintains an index of mappings that are valid
 * Doing so makes iteration really fast, but in exchange for that it has slightly more overhead
 * It also contains many optimizations:
 * - Compacting doesn't allocate new arrays
 * - Empty (all-zero) state arrays are automagically deleted
 * However, it doesn't shrink after deleting unused entries. This can cause unwanted high memory usage.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ShortObjectIndexedTreeMap<V extends Object> extends ShortObjectTreeMap<V>    {
    protected boolean[] index;

    public ShortObjectIndexedTreeMap()    {
        super();
    }

    @Override
    public boolean containsValue(V value)    {
        for (int i = 0; i < index.length; i++)  {
            if (index[i] && values[i] == value) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        this.index = new boolean[len];
    }

    @Override
    public void forEachKey(@NonNull ShortConsumer consumer)   {
        if (consumer == null) throw new IllegalArgumentException("Consumer may not be null!");
        //scan through the index until -1 is reached
        for (int i = 0; i < index.length; i++)  {
            if (index[i]) consumer.accept( keys[i]);
        }
    }

    @Override
    public void forEachValue(@NonNull ObjectConsumer<V> consumer)   {
        if (consumer == null) throw new IllegalArgumentException("Consumer may not be null!");
        //scan through the index until -1 is reached
        for (int i = 0; i < index.length; i++)  {
            if (index[i]) consumer.accept( (V) values[i]);
        }
    }

    @Override
    public void forEachEntry(@NonNull ShortObjectConsumer<V> consumer)   {
        if (consumer == null) throw new IllegalArgumentException("Consumer may not be null!");
        //scan through the index until -1 is reached
        for (int i = 0; i < index.length; i++)  {
            if (index[i]) consumer.accept( keys[i], (V) values[i]);
        }
    }

    @Override
    public ShortIterator keyIterator()   {
        return new KeyIterator();
    }

    @Override
    public ObjectIterator<V> valueIterator()   {
        return new ValueIterator();
    }

    @Override
    public ShortObjectIterator<V> entryIterator()   {
        return new EntryIterator();
    }

    @Override
    protected boolean containsKeyHash(int hash) {
        int state = getState(hash);
        return state < 0 ? index[state * -1 - 1] : false;
    }

    @Override
    protected boolean containsState(int state) {
        return state < 0 ? index[state * -1 - 1] : false;
    }

    /**
     * Since indexes are maintained in their own array here, we can fairly safely compact the map without creating new arrays
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doCompact() {
        System.out.println("Compacting! " + len);
        indexCounter = 0;
        removedCount = 0;
        for (int i = 0; i < index.length; i++)  {
            if (index[i])   {
                short key = keys[i];
                int index = indexCounter++;
                keys[index] = key;
                values[index] = values[i];
                this.index[index] = true;

                //finally, mark state
                int hash = hashKey(key);
                states[hash & 0xFF]
                    [(hash >> 8) & 0xFF]
                    [(hash >> 16) & 0xFF]
                    [(hash >> 24) & 0xFF] = (index + 1) * -1;

                if (indexCounter == size)   {
                    System.out.println("Filling remaining indexes with `false`...");
                    for (; i < this.index.length; i++)   {
                        this.index[i] = false;
                    }
                    break;
                }
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doResize(int oldSize, int newSize) {
        System.out.println((oldSize > newSize ? "Shrinking" : "Growing") + "! " + oldSize + " " + newSize);
        indexCounter = 0;
        removedCount = 0;
        len = newSize;
        //even if we can get by without resetting the states map, the other ones need to be re-created as we're changing the size
        short[] newKeys = new short[len];
        Object[] newValues = new Object[len];
        boolean[] newIndex = new boolean[len];
        forEachEntry((key, value) -> {
            int index = indexCounter++;
            newKeys[index] = key;
            newValues[index] = value;
            newIndex[index] = true;

            //finally, mark state
            int hash = hashKey(key);
            states[hash & 0xFF]
                [(hash >> 8) & 0xFF]
                [(hash >> 16) & 0xFF]
                [(hash >> 24) & 0xFF] = (index + 1) * -1;
        });
        keys = newKeys;
        values = newValues;
        index = newIndex;
    }

    @Override
    protected void mark(int hash, int state)  {
        int[][][] a = states[hash & 0xFF];
        if (a == null) a = states[hash & 0xFF] = new int[256][][];
        int[][] b = a[(hash >> 8) & 0xFF];
        if (b == null) b = a[(hash >> 8) & 0xFF] = new int[256][];
        int[] c = b[(hash >> 16) & 0xFF];
        if (c == null) c = b[(hash >> 16) & 0xFF] = new int[256];
        int j = (hash >> 24) & 0xFF;
        int old = c[j];
        c[j] = state;
        if (state < 0 && old >= 0)  { //changing from empty to occupied
            index[state * -1 - 1] = true;
        } else if (state >= 0 && old < 0)   { //changing from occupied to empty
            index[old * -1 - 1] = false;
        }
    }

    private class KeyIterator implements ShortIterator   {
        private int index = -1;
        private int nextIndex = -1;
        private final ShortObjectIndexedTreeMap this_ = ShortObjectIndexedTreeMap.this;

        public KeyIterator()    {
            this.findNext();
        }

        @Override
        public boolean hasNext()    {
            return this.nextIndex != -1;
        }

        @Override
        public short get()  {
            return this_.keys[this.index];
        }

        @Override
        public short advance()   {
            if (this.hasNext()) {
                this.findNext();
                return this.get();
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public void remove()    {
            this_.remove(this.get(), false);
        }

        private void findNext() {
            this.index = this.nextIndex++; //add one to prevent choosing the same index again
            for (; this.nextIndex < this_.len; this.nextIndex++)  {
                if (this_.index[this.nextIndex]) {
                    return;
                }
            }
            this.nextIndex = -1; //nothing was found
        }
    }

    private class ValueIterator implements ObjectIterator<V>   {
        private int index = -1;
        private int nextIndex = -1;
        private final ShortObjectIndexedTreeMap this_ = ShortObjectIndexedTreeMap.this;

        public ValueIterator()    {
            this.findNext();
        }

        @Override
        public boolean hasNext()    {
            return this.nextIndex != -1;
        }

        @Override
        public V get()  {
            return (V) this_.values[this.index];
        }

        @Override
        public V advance()   {
            if (this.hasNext()) {
                this.findNext();
                return this.get();
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public void remove()    {
            this_.remove(this_.keys[this.index], false);
        }

        private void findNext() {
            this.index = this.nextIndex++; //add one to prevent choosing the same index again
            for (; this.nextIndex < this_.len; this.nextIndex++)  {
                if (this_.index[this.nextIndex]) {
                    return;
                }
            }
            this.nextIndex = -1; //nothing was found
        }
    }

    private class EntryIterator implements ShortObjectIterator<V>   {
        private int index = -1;
        private int nextIndex = -1;
        private ShortObjectTuple<V> tuple;
        private final ShortObjectIndexedTreeMap this_ = ShortObjectIndexedTreeMap.this;

        public EntryIterator()    {
            this.findNext();
        }

        @Override
        public boolean hasNext()    {
            return this.nextIndex != -1;
        }

        @Override
        public ShortObjectTuple<V> get()  {
            return this.tuple;
        }

        @Override
        public ShortObjectTuple<V> advance()   {
            if (this.hasNext()) {
                this.findNext();
                return this.tuple = new ShortObjectImmutableTuple<V>( this_.keys[this.index],  (V) this_.values[this.index]);
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        @Override
        public void remove()    {
            this_.remove(this_.keys[this.index], false);
        }

        private void findNext() {
            this.index = this.nextIndex++; //add one to prevent choosing the same index again
            for (; this.nextIndex < this_.len; this.nextIndex++)  {
                if (this_.index[this.nextIndex]) {
                    return;
                }
            }
            this.nextIndex = -1; //nothing was found
        }
    }
}