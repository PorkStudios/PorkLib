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

package net.daporkchop.lib.primitive.map.concurrent;

import net.daporkchop.lib.primitive.lambda.consumer.BooleanConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.CharacterConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.BooleanCharacterConsumer;
import net.daporkchop.lib.primitive.tuple.BooleanCharacterTuple;
import net.daporkchop.lib.primitive.tuple.BooleanCharacterMutableTuple;
import net.daporkchop.lib.primitiveutil.UnlockableLock;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A single subentry for a {@link BooleanCharacterConcurrentTreeHashMap}.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
class BooleanCharacterSubEntry    {
    public final AtomicInteger size = new AtomicInteger(0);
    public final BooleanCharacterConcurrentTreeHashMap parent;
    public final UnlockableLock lock = new UnlockableLock();
    public volatile Sector rootPointer;
    public volatile ObjectConsumer<Sector> recursiveHack;

    public BooleanCharacterSubEntry(BooleanCharacterConcurrentTreeHashMap parent)   {
        this.parent = parent;
        this.rootPointer = this.parent.subHashLength == 0 ? new BooleanCharacterSubEntry.ValueSector(null, -1) : new BooleanCharacterSubEntry.PointerSector(null, -1);
    }

    public char get(boolean key)   {
        this.lock.lock();
        try {
            long hash = this.hashKey(key);
            Sector sector = this.getSectorFor(hash, false);
            if (sector == null) {
                return (char) 0;
            } else {
                return sector.getValue((int) hash & 0xFF);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public char put(boolean key, char value)   {
        this.lock.lock();
        try {
            long hash = this.hashKey(key);
            Sector sector = this.getSectorFor(hash, true);
            return sector.setEntry((int) hash & 0xFF, key, value);
        } finally {
            this.lock.unlock();
        }
    }

    public char remove(boolean key)   {
        this.lock.lock();
        try {
            long hash = this.hashKey(key);
            Sector sector = this.getSectorFor(hash, false);
            if (sector == null) {
                return (char) 0;
            } else {
                return sector.removeEntry((int) hash & 0xFF);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void removeNonBlocking_BeReallyCarefulWithThis(boolean key)   {
        long hash = this.hashKey(key);
        Sector sector = this.getSectorFor(hash, false);
        if (sector != null) {
            sector.removeEntry((int) hash & 0xFF);
        }
    }

    public boolean containsKey(boolean key)   {
        this.lock.lock();
        try {
            long hash = this.hashKey(key);
            Sector sector = this.getSectorFor(hash, false);
            return sector != null && sector.containsKey((int) hash & 0xFF);
        } finally {
            this.lock.unlock();
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            this.rootPointer = this.parent.subHashLength == 0 ? new BooleanCharacterSubEntry.ValueSector(null, -1) : new BooleanCharacterSubEntry.PointerSector(null, -1);
            this.parent.size.addAndGet(-this.size.getAndSet(0));
        } finally {
            this.lock.unlock();
        }
    }

    public void forEachKey(BooleanConsumer consumer)   {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof BooleanCharacterSubEntry.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachKey(k -> consumer.accept( k));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    public void forEachValue(CharacterConsumer consumer) {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof BooleanCharacterSubEntry.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachValue(v -> consumer.accept( v));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    public void forEachEntry(BooleanCharacterConsumer consumer) {
        ObjectConsumer<Sector> c = sector -> {
                    if (sector instanceof BooleanCharacterSubEntry.PointerSector)    {
                        sector.forEachSector(recursiveHack);
                    } else {
                        sector.forEachEntry((k, v) -> consumer.accept( k,  v));
                    }
                };
        recursiveHack = c;
        this.rootPointer.forEachSector(c);
    }

    public void beginConcurrentIterate()    {
        this.lock.lock();
    }

    public boolean isLocked()   {
        return this.lock.isHeld();
    }

    public boolean advanceKey(AtomicReference<Sector> sectorRef, AtomicInteger curr, AtomicBoolean hasNext)   {
        Sector sector = sectorRef.get();
        if (sector == null) {
            sectorRef.set(sector = this.rootPointer);
        }
        //search current sector
        for (int i = sector.states.nextSetBit(curr.get() + 1); i != -1; i = sector.states.nextSetBit(i + 1)) {
            curr.set(i);
            if (sector instanceof BooleanCharacterSubEntry.ValueSector)  {
                return sector.getKey(i);
            } else {
                //enter subsector at the beginning
                sectorRef.set(sector.getSector(i));
                curr.set(-1);
                return this.advanceKey(sectorRef, curr, hasNext);
            }
        }
        if (sector.parent == null)  {
            //no remaining elements in the root pointer
            hasNext.set(false);
            return false;
        }
        //no remaining sectors to be found, go up one node to search for next
        sectorRef.set(sector.parent);
        curr.set(sector.parentOffset);
        return this.advanceKey(sectorRef, curr, hasNext);
    }

    public void advanceEntry(AtomicReference<Sector> sectorRef, AtomicInteger curr, AtomicBoolean hasNext, BooleanCharacterMutableTuple tuple)   {
        Sector sector = sectorRef.get();
        if (sector == null) {
            sectorRef.set(sector = this.rootPointer);
        }
        //search current sector
        for (int i = sector.states.nextSetBit(curr.get() + 1); i != -1; i = sector.states.nextSetBit(i + 1)) {
            curr.set(i);
            if (sector instanceof BooleanCharacterSubEntry.ValueSector)  {
                tuple.k = sector.getKey(i);
                tuple.v = sector.getValue(i);
                return;
            } else {
                //enter subsector at the beginning
                sectorRef.set(sector.getSector(i));
                curr.set(-1);
                this.advanceEntry(sectorRef, curr, hasNext, tuple);
                return;
            }
        }
        if (sector.parent == null)  {
            //no remaining elements in the root pointer
            hasNext.set(false);
            return;
        }
        //no remaining sectors to be found, go up one node to search for next
        sectorRef.set(sector.parent);
        curr.set(sector.parentOffset);
        this.advanceEntry(sectorRef, curr, hasNext, tuple);
    }

    public void endConcurrentIterate()    {
        this.lock.unlock();
    }

    public Sector getSectorFor(long hash, boolean create)    {
        Sector curr = this.rootPointer;
        for (int i = 1; i < this.parent.subHashLength; i++) {
            int off = (int) (hash >> (i << 3L)) & 0xFF;
            Sector next = curr.getSector(off);
            if (next == null)    {
                if (create) {
                    next = i + 1 == this.parent.subHashLength ? new BooleanCharacterSubEntry.ValueSector(curr, off) : new BooleanCharacterSubEntry.PointerSector(curr, off);
                    curr.setSector(off, next);
                } else {
                    return null;
                }
            }
            curr = next;
        }
        return curr;
    }

    private long hashKey(boolean key)   {
        return this.parent.subKeyHash.apply(key);
    }

    public abstract class Sector  {
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

        public boolean getKey(int index)   {
            throw new UnsupportedOperationException();
        }

        public char getValue(int index)   {
            throw new UnsupportedOperationException();
        }

        public char setEntry(int index, boolean key, char value)   {
            throw new UnsupportedOperationException();
        }

        public char removeEntry(int index)  {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(int index)    {
            throw new UnsupportedOperationException();
        }

        public void forEachKey(BooleanConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        public void forEachValue(CharacterConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        public void forEachEntry(BooleanCharacterConsumer consumer)   {
            throw new UnsupportedOperationException();
        }

        protected void forEach(IntegerConsumer consumer)   {
            for (int i = this.states.nextSetBit(0); i != -1; i = this.states.nextSetBit(i + 1)) {
                consumer.accept(i);
            }
        }
    }

    public class PointerSector extends Sector   {
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

    public class ValueSector extends Sector   {
        private final boolean[] keys = new boolean[256];
        private final char[] values = new char[256];

        public ValueSector(Sector parent, int parentOffset)   {
            super(parent, parentOffset);
        }

        @Override
        public boolean getKey(int index)  {
            if (this.states.get(index)) {
                return this.keys[index];
            } else {
                return false;
            }
        }

        @Override
        public char getValue(int index) {
            if (this.states.get(index)) {
                return this.values[index];
            } else {
                return (char) 0;
            }
        }

        @Override
        public char setEntry(int index, boolean key, char value)   {
            this.keys[index] = key;
            if (this.states.get(index))    {
                char v = this.values[index];
                this.values[index] = value;
                return v;
            } else {
                this.values[index] = value;
                this.states.set(index);
                this.refs++;
                BooleanCharacterSubEntry.this.size.incrementAndGet();
                BooleanCharacterSubEntry.this.parent.size.incrementAndGet();
                return (char) 0;
            }
        }

        @Override
        public char removeEntry(int index)  {
            if (this.states.get(index)) {
                this.states.clear(index);
                BooleanCharacterSubEntry.this.size.decrementAndGet();
                BooleanCharacterSubEntry.this.parent.size.decrementAndGet();
                if (--this.refs == 0 && this.parent != null)   {
                    this.parent.removeSector(this.parentOffset);
                }
                return this.values[index];
            } else {
                return (char) 0;
            }
        }

        @Override
        public boolean containsKey(int index)    {
            return this.states.get(index);
        }

        @Override
        public void forEachKey(BooleanConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.keys[i]));
        }

        @Override
        public void forEachValue(CharacterConsumer consumer)   {
            this.forEach(i -> consumer.accept(this.values[i]));
        }

        @Override
        public void forEachEntry(BooleanCharacterConsumer consumer)   {
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