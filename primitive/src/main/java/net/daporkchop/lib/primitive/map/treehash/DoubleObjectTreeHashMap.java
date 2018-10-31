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

package net.daporkchop.lib.primitive.map.treehash;

import net.daporkchop.lib.primitive.lambda.consumer.DoubleConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.DoubleObjectConsumer;
import net.daporkchop.lib.primitive.lambda.function.DoubleToLongFunction;
import net.daporkchop.lib.primitive.map.DoubleObjectMap;
import net.daporkchop.lib.primitive.iterator.DoubleIterator;
import net.daporkchop.lib.primitive.iterator.ObjectIterator;
import net.daporkchop.lib.primitive.iterator.bi.DoubleObjectIterator;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.*;

/**
 * A tree hash map, using a key type of double and a value type of V.
 * Tree hash maps are a creation of my own, using a tree of sectors, each with 256 child nodes. To
 * get a value for a given key, one simply uses the first byte of the hash as the index for the first node,
 * the second byte for the second node, and so on. This can be used to make a relatively map with very little
 * overhead, and can even be made concurrent with little extra work (see {@link net.daporkchop.lib.primitive.map.concurrent.DoubleObjectConcurrentTreeHashMap})
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class DoubleObjectTreeHashMap<V extends Object> implements DoubleObjectMap<V>    {
    private final AtomicInteger size = new AtomicInteger(0);
    private final DoubleToLongFunction hashFunc;
    private final int hashLen;
    private volatile Sector rootPointer;
    private volatile ObjectConsumer<Sector> recursiveHack;

    public DoubleObjectTreeHashMap()    {
        this(null, 0);
    }

    public DoubleObjectTreeHashMap(DoubleToLongFunction hashFunc, int hashLen)    {
        if (hashLen < 0 || hashLen > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + hashLen + ")");
        }

        if (hashFunc == null)    {
            this.hashFunc = in -> {
                return Double.doubleToLongBits(in);
            };
            this.hashLen = 7;
        } else {
            this.hashFunc = hashFunc;
            this.hashLen = hashLen;
        }
        this.rootPointer = this.hashLen == 0 ? new DoubleObjectTreeHashMap<V>.ValueSector(null, -1) : new DoubleObjectTreeHashMap<V>.PointerSector(null, -1);
    }

    @Override
    public V get(double key)   {
        long hash = this.hashFunc.apply(key);
        Sector sector = this.getSectorFor(hash, false);
        if (sector == null) {
            return null;
        } else {
            return (V) sector.getValue((int) hash & 0xFF);
        }
    }

    @Override
    public V put(double key, V value)   {
        long hash = this.hashFunc.apply(key);
        Sector sector = this.getSectorFor(hash, true);
        return (V) sector.setEntry((int) hash & 0xFF, key, value);
    }

    @Override
    public V remove(double key)    {
        long hash = this.hashFunc.apply(key);
        Sector sector = this.getSectorFor(hash, false);
        if (sector == null) {
            return null;
        } else {
            return (V) sector.removeEntry((int) hash & 0xFF);
        }
    }

    @Override
    public boolean containsKey(double key)    {
        long hash = this.hashKey(key);
        Sector sector = this.getSectorFor(hash, false);
        return sector != null && sector.containsKey((int) hash & 0xFF);
    }

    @Override
    public boolean containsValue(V value)    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.rootPointer = this.hashLen == 0 ? new DoubleObjectTreeHashMap.ValueSector(null, -1) : new DoubleObjectTreeHashMap.PointerSector(null, -1);
    }

    @Override
    public int getSize()    {
        return this.size.get();
    }

    @Override
    public void forEachKey(@NonNull DoubleConsumer consumer)  {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof DoubleObjectTreeHashMap.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachKey(k -> consumer.accept( k));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    @Override
    public void forEachValue(@NonNull ObjectConsumer<V> consumer) {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof DoubleObjectTreeHashMap.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachValue(v -> consumer.accept( (V) v));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    @Override
    public void forEachEntry(@NonNull DoubleObjectConsumer<V> consumer) {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof DoubleObjectTreeHashMap.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachEntry((k, v) -> consumer.accept( k,  (V) v));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    @Override
    public DoubleIterator keyIterator()   {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectIterator<V> valueIterator()   {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoubleObjectIterator<V> entryIterator()   {
        throw new UnsupportedOperationException();
    }

    private Sector getSectorFor(long hash, boolean create)    {
        Sector curr = this.rootPointer;
        for (int i = 1; i < this.hashLen; i++) {
            int off = (int) (hash >> (i << 3L)) & 0xFF;
            Sector next = curr.getSector(off);
            if (next == null)    {
                if (create) {
                    next = i + 1 == this.hashLen ? new DoubleObjectTreeHashMap<V>.ValueSector(curr, off) : new DoubleObjectTreeHashMap<V>.PointerSector(curr, off);
                    curr.setSector(off, next);
                } else {
                    return null;
                }
            }
            curr = next;
        }
        return curr;
    }

    private abstract class Sector  {
        protected volatile int refs = 0;
        public final BitSet states = new BitSet(256);
        public final Sector parent;
        public final int parentOffset;

        public Sector(Sector parent, int parentOffset)  {
            this.parent = parent;
            this.parentOffset = parentOffset;
        }

        public boolean canCollect() {
            return this.refs == 0;
        }

        public Sector getSector(int index)  {
            throw new UnsupportedOperationException();
        }

        public void setSector(int index, Sector val) {
            throw new UnsupportedOperationException();
        }

        public void removeSector(int index)  {
            throw new UnsupportedOperationException();
        }

        public void forEachSector(ObjectConsumer<Sector> consumer)  {
            throw new UnsupportedOperationException();
        }

        public double getKey(int index)   {
            throw new UnsupportedOperationException();
        }

        public Object getValue(int index)   {
            throw new UnsupportedOperationException();
        }

        public Object setEntry(int index, double key, Object value)   {
            throw new UnsupportedOperationException();
        }

        public Object removeEntry(int index)  {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(int index)    {
            throw new UnsupportedOperationException();
        }

        public void forEachKey(DoubleConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        public void forEachValue(ObjectConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        public void forEachEntry(DoubleObjectConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        protected void forEach(IntegerConsumer consumer)   {
            for (int i = this.states.nextSetBit(0); i != -1; i = this.states.nextSetBit(i + 1)) {
                consumer.accept(i);
            }
        }
    }

    private class PointerSector extends Sector   {
        private final Sector[] pointers = (Sector[]) Array.newInstance(Sector.class, 256);

        public PointerSector(Sector parent, int parentOffset)   {
            super(parent, parentOffset);
        }

        @Override
        public Sector getSector(int index)   {
            if (this.states.get(index)) {
                return this.pointers[index];
            } else {
                return null;
            }
        }

        @Override
        public void setSector(int index, Sector val) {
            this.pointers[index] = val;
            if (!this.states.get(index)) {
                this.states.set(index);
                this.refs++;
            }
        }

        @Override
        public void removeSector(int index)   {
            if (this.states.get(index)) {
                this.states.clear(index);
                this.pointers[index] = null;
                if (--this.refs == 0 && this.parent != null)   {
                    this.parent.removeSector(this.parentOffset);
                }
            }
        }

        @Override
        public void forEachSector(ObjectConsumer<Sector> consumer)  {
            this.forEach(i -> consumer.accept(this.pointers[i]));
        }
    }

    private class ValueSector extends Sector   {
        private final double[] keys = new double[256];
        private final Object[] values = new Object[256];

        public ValueSector(Sector parent, int parentOffset)   {
            super(parent, parentOffset);
        }

        @Override
        public double getKey(int index)  {
            if (this.states.get(index)) {
                return this.keys[index];
            } else {
                return 0D;
            }
        }

        @Override
        public Object getValue(int index) {
            if (this.states.get(index)) {
                return this.values[index];
            } else {
                return null;
            }
        }

        @Override
        public Object setEntry(int index, double key, Object value)   {
            this.keys[index] = key;
            if (this.states.get(index))    {
                Object v = this.values[index];
                this.values[index] = value;
                return v;
            } else {
                this.values[index] = value;
                this.states.set(index);
                this.refs++;
                DoubleObjectTreeHashMap.this.size.incrementAndGet();
                return null;
            }
        }

        @Override
        public Object removeEntry(int index)  {
            if (this.states.get(index)) {
                this.states.clear(index);
                DoubleObjectTreeHashMap.this.size.decrementAndGet();
                if (--this.refs == 0 && this.parent != null)   {
                    this.parent.removeSector(this.parentOffset);
                }
                return this.values[index];
            } else {
                return null;
            }
        }

        @Override
        public boolean containsKey(int index)    {
            return this.states.get(index);
        }

        @Override
        public void forEachKey(DoubleConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.keys[i]));
        }

        @Override
        public void forEachValue(ObjectConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.values[i]));
        }

        @Override
        public void forEachEntry(DoubleObjectConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.keys[i], this.values[i]));
        }

        @Override
        public void forEachSector(ObjectConsumer<Sector> consumer)  {
            //this allows foreach on maps with a key length of 0 to be a bit cleaner
            if (this.parent == null)    {
                consumer.accept(this);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}