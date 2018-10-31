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

import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.FloatConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.IntegerFloatConsumer;
import net.daporkchop.lib.primitive.lambda.function.IntegerToLongFunction;
import net.daporkchop.lib.primitive.map.IntegerFloatMap;
import net.daporkchop.lib.primitive.iterator.IntegerIterator;
import net.daporkchop.lib.primitive.iterator.FloatIterator;
import net.daporkchop.lib.primitive.iterator.bi.IntegerFloatIterator;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.*;

/**
 * A tree hash map, using a key type of int and a value type of float.
 * Tree hash maps are a creation of my own, using a tree of sectors, each with 256 child nodes. To
 * get a value for a given key, one simply uses the first byte of the hash as the index for the first node,
 * the second byte for the second node, and so on. This can be used to make a relatively map with very little
 * overhead, and can even be made concurrent with little extra work (see {@link net.daporkchop.lib.primitive.map.concurrent.IntegerFloatConcurrentTreeHashMap})
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class IntegerFloatTreeHashMap implements IntegerFloatMap    {
    private final AtomicInteger size = new AtomicInteger(0);
    private final IntegerToLongFunction hashFunc;
    private final int hashLen;
    private volatile Sector rootPointer;
    private volatile ObjectConsumer<Sector> recursiveHack;

    public IntegerFloatTreeHashMap()    {
        this(null, 0);
    }

    public IntegerFloatTreeHashMap(IntegerToLongFunction hashFunc, int hashLen)    {
        if (hashLen < 0 || hashLen > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + hashLen + ")");
        }

        if (hashFunc == null)    {
            this.hashFunc = in -> {
                return in & 0x7fffffff;
            };
            this.hashLen = 3;
        } else {
            this.hashFunc = hashFunc;
            this.hashLen = hashLen;
        }
        this.rootPointer = this.hashLen == 0 ? new IntegerFloatTreeHashMap.ValueSector(null, -1) : new IntegerFloatTreeHashMap.PointerSector(null, -1);
    }

    @Override
    public float get(int key)   {
        long hash = this.hashFunc.apply(key);
        Sector sector = this.getSectorFor(hash, false);
        if (sector == null) {
            return 0F;
        } else {
            return sector.getValue((int) hash & 0xFF);
        }
    }

    @Override
    public float put(int key, float value)   {
        long hash = this.hashFunc.apply(key);
        Sector sector = this.getSectorFor(hash, true);
        return sector.setEntry((int) hash & 0xFF, key, value);
    }

    @Override
    public float remove(int key)    {
        long hash = this.hashFunc.apply(key);
        Sector sector = this.getSectorFor(hash, false);
        if (sector == null) {
            return 0F;
        } else {
            return sector.removeEntry((int) hash & 0xFF);
        }
    }

    @Override
    public boolean containsKey(int key)    {
        long hash = this.hashKey(key);
        Sector sector = this.getSectorFor(hash, false);
        return sector != null && sector.containsKey((int) hash & 0xFF);
    }

    @Override
    public boolean containsValue(float value)    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.rootPointer = this.hashLen == 0 ? new IntegerFloatTreeHashMap.ValueSector(null, -1) : new IntegerFloatTreeHashMap.PointerSector(null, -1);
    }

    @Override
    public int getSize()    {
        return this.size.get();
    }

    @Override
    public void forEachKey(@NonNull IntegerConsumer consumer)  {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof IntegerFloatTreeHashMap.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachKey(k -> consumer.accept( k));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    @Override
    public void forEachValue(@NonNull FloatConsumer consumer) {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof IntegerFloatTreeHashMap.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachValue(v -> consumer.accept( v));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    @Override
    public void forEachEntry(@NonNull IntegerFloatConsumer consumer) {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof IntegerFloatTreeHashMap.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachEntry((k, v) -> consumer.accept( k,  v));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    @Override
    public IntegerIterator keyIterator()   {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatIterator valueIterator()   {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntegerFloatIterator entryIterator()   {
        throw new UnsupportedOperationException();
    }

    private Sector getSectorFor(long hash, boolean create)    {
        Sector curr = this.rootPointer;
        for (int i = 1; i < this.hashLen; i++) {
            int off = (int) (hash >> (i << 3L)) & 0xFF;
            Sector next = curr.getSector(off);
            if (next == null)    {
                if (create) {
                    next = i + 1 == this.hashLen ? new IntegerFloatTreeHashMap.ValueSector(curr, off) : new IntegerFloatTreeHashMap.PointerSector(curr, off);
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

        public int getKey(int index)   {
            throw new UnsupportedOperationException();
        }

        public float getValue(int index)   {
            throw new UnsupportedOperationException();
        }

        public float setEntry(int index, int key, float value)   {
            throw new UnsupportedOperationException();
        }

        public float removeEntry(int index)  {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(int index)    {
            throw new UnsupportedOperationException();
        }

        public void forEachKey(IntegerConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        public void forEachValue(FloatConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        public void forEachEntry(IntegerFloatConsumer consumer)   {
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
        private final int[] keys = new int[256];
        private final float[] values = new float[256];

        public ValueSector(Sector parent, int parentOffset)   {
            super(parent, parentOffset);
        }

        @Override
        public int getKey(int index)  {
            if (this.states.get(index)) {
                return this.keys[index];
            } else {
                return 0;
            }
        }

        @Override
        public float getValue(int index) {
            if (this.states.get(index)) {
                return this.values[index];
            } else {
                return 0F;
            }
        }

        @Override
        public float setEntry(int index, int key, float value)   {
            this.keys[index] = key;
            if (this.states.get(index))    {
                float v = this.values[index];
                this.values[index] = value;
                return v;
            } else {
                this.values[index] = value;
                this.states.set(index);
                this.refs++;
                IntegerFloatTreeHashMap.this.size.incrementAndGet();
                return 0F;
            }
        }

        @Override
        public float removeEntry(int index)  {
            if (this.states.get(index)) {
                this.states.clear(index);
                IntegerFloatTreeHashMap.this.size.decrementAndGet();
                if (--this.refs == 0 && this.parent != null)   {
                    this.parent.removeSector(this.parentOffset);
                }
                return this.values[index];
            } else {
                return 0F;
            }
        }

        @Override
        public boolean containsKey(int index)    {
            return this.states.get(index);
        }

        @Override
        public void forEachKey(IntegerConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.keys[i]));
        }

        @Override
        public void forEachValue(FloatConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.values[i]));
        }

        @Override
        public void forEachEntry(IntegerFloatConsumer consumer)   {
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