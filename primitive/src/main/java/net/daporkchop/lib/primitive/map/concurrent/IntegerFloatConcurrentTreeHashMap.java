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

import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.FloatConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.IntegerFloatConsumer;
import net.daporkchop.lib.primitive.lambda.function.IntegerToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.IntegerToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.IntegerConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.FloatConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.IntegerFloatConcurrentIterator;
import net.daporkchop.lib.primitive.map.IntegerFloatMap;
import net.daporkchop.lib.primitive.tuple.IntegerFloatTuple;
import net.daporkchop.lib.primitive.tuple.IntegerFloatImmutableTuple;
import net.daporkchop.lib.primitive.tuple.IntegerFloatMutableTuple;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;
import net.daporkchop.lib.primitiveutil.VoidFunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.*;

/**
 * A tree hash map, using a key type of int and a value type of float.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class IntegerFloatConcurrentTreeHashMap implements IntegerFloatMap    {
    final AtomicInteger size = new AtomicInteger(0);

    private final IntegerFloatSubEntry[] entries = new IntegerFloatSubEntry[256];
    private final IntegerToIntegerFunction keyHash;
    final IntegerToLongFunction subKeyHash;
    final int subHashLength;

    public IntegerFloatConcurrentTreeHashMap()    {
        this(null, null);
    }

    public IntegerFloatConcurrentTreeHashMap(IntegerToIntegerFunction keyHash, IntegerToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 3);
    }

    public IntegerFloatConcurrentTreeHashMap(IntegerToIntegerFunction keyHash, IntegerToLongFunction subKeyHash, int subHashLength)    {
        if (subHashLength < 0 || subHashLength > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + subHashLength + ")");
        }

        if (keyHash == null)    {
            this.keyHash = in -> {
                return in & 0x7fffffff;
            };
        } else {
            this.keyHash = keyHash;
        }
        if (subKeyHash == null)    {
            this.subKeyHash = in -> {
                return in & 0x7fffffff;
            };
            this.subHashLength = 3;
        } else {
            this.subKeyHash = subKeyHash;
            this.subHashLength = subHashLength;
        }
    }

    @Override
    public float get(int key)   {
        IntegerFloatSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return 0F;
        } else {
            return entry.get(key);
        }
    }

    @Override
    public float put(int key, float value)   {
        IntegerFloatSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return entry.put(key, value);
    }

    @Override
    public float remove(int key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        IntegerFloatSubEntry entry = this.getSubentry(hash, false);
        if (entry == null)  {
            return 0F;
        } else {
            float val = entry.remove(key);
            if (entry.size.get() == 0)  {
                this.entries[hash] = null;
            }
            return val;
        }
    }

    @Override
    public boolean containsKey(int key)    {
        IntegerFloatSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return false;
        } else {
            return entry.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(float value)    {
        throw new UnsupportedOperationException("containsValue on ConcurrentTreeHashMap!");
    }

    @Override
    public void clear() {
        synchronized (this.entries) {
            for (int i = 0; i < 256; i++) {
                IntegerFloatSubEntry entry = this.entries[i];
                if (entry != null)  {
                    entry.clear();
                    this.entries[i] = null;
                }
            }
        }
    }

    @Override
    public int getSize()    {
        return this.size.get();
    }

    @Override
    public void forEachKey(IntegerConsumer consumer)   {
        for (int i = 0; i < 256; i++) {
            IntegerFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(FloatConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            IntegerFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(IntegerFloatConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            IntegerFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(IntegerFloatConsumer consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            IntegerFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public IntegerConcurrentIterator keyIterator()   {
        return new IntegerConcurrentIterator()  {
            private final IntegerFloatConcurrentTreeHashMap this_ = IntegerFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<IntegerFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile int next;
            private volatile IntegerFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public int get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.key;
                }
            }

            @Override
            public synchronized int advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                int current = state.key = this.next;
                state.entry = this.entry;
                this.findNextValue();
                return current;
            }

            @Override
            public void remove(){
                this.tl.get().remove();
            }

            private void nextSub()   {
                if (this.entry != null) {
                    this.entry.endConcurrentIterate();
                }
                while (this.subEntryIndex < 256)    {
                    IntegerFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
                    if (entry != null)  {
                        this.entry = entry;
                        this.hasNextSub.set(true);
                        this.currSector.set(null);
                        this.sectorOffset.set(-1);
                        this.entry.beginConcurrentIterate();
                        return;
                    }
                }
                this.hasNext = false;
            }

            private void findNextValue()    {
                while (this.hasNext) {
                    this.next = this.entry.advanceKey(this.currSector, this.sectorOffset, this.hasNextSub);
                    if (this.hasNextSub.get()) {
                        //new value was gotten
                        return;
                    } else {
                        //move to next subentry
                        this.nextSub();
                    }
                }
                if (this.entry.isLocked())  {
                    this.entry.endConcurrentIterate();
                }
            }

            class ThreadState   {
                public int key;
                public IntegerFloatSubEntry entry;
                private final IntegerConcurrentIterator this_;
                private final IntegerFloatConcurrentTreeHashMap this__;

                public ThreadState(IntegerConcurrentIterator this_, IntegerFloatConcurrentTreeHashMap this__)  {
                    this.this_ = this_;
                    this.this__ = this__;
                }

                public void remove()    {
                    if (this.this_.hasNext())    {
                        this.entry.removeNonBlocking_BeReallyCarefulWithThis(this.key);
                    } else {
                        this.this__.remove(this.key);
                    }
                }
            }
        };
    }

    @Override
    public FloatConcurrentIterator valueIterator() {
        return new FloatConcurrentIterator()  {
            private final IntegerFloatConcurrentTreeHashMap this_ = IntegerFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<IntegerFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final IntegerFloatMutableTuple next = new IntegerFloatMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile IntegerFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public float get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.val;
                }
            }

            @Override
            public synchronized float advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                state.key = this.next.k;
                float current = state.val = this.next.v;
                state.entry = this.entry;
                this.findNextValue();
                return current;
            }

            @Override
            public void remove(){
                this.tl.get().remove();
            }

            private void nextSub()   {
                if (this.entry != null) {
                    this.entry.endConcurrentIterate();
                }
                while (this.subEntryIndex < 256)    {
                    IntegerFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
                    if (entry != null)  {
                        this.entry = entry;
                        this.hasNextSub.set(true);
                        this.currSector.set(null);
                        this.sectorOffset.set(-1);
                        this.entry.beginConcurrentIterate();
                        return;
                    }
                }
                this.hasNext = false;
            }

            private void findNextValue()    {
                while (this.hasNext) {
                    this.entry.advanceEntry(this.currSector, this.sectorOffset, this.hasNextSub, this.next);
                    if (this.hasNextSub.get()) {
                        //new value was gotten
                        return;
                    } else {
                        //move to next subentry
                        this.nextSub();
                    }
                }
                if (this.entry.isLocked())  {
                    this.entry.endConcurrentIterate();
                }
            }

            class ThreadState   {
                public int key;
                public float val;
                public IntegerFloatSubEntry entry;
                private final FloatConcurrentIterator this_;
                private final IntegerFloatConcurrentTreeHashMap this__;

                public ThreadState(FloatConcurrentIterator this_, IntegerFloatConcurrentTreeHashMap this__)  {
                    this.this_ = this_;
                    this.this__ = this__;
                }

                public void remove()    {
                    if (this.entry != null)    {
                        if (this.this_.hasNext())    {
                            this.entry.removeNonBlocking_BeReallyCarefulWithThis(this.key);
                        } else {
                            this.this__.remove(this.key);
                        }
                    }
                }
            }
        };
    }

    @Override
    public IntegerFloatConcurrentIterator entryIterator() {
        return new IntegerFloatConcurrentIterator()  {
            private final IntegerFloatConcurrentTreeHashMap this_ = IntegerFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<IntegerFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final IntegerFloatMutableTuple next = new IntegerFloatMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile IntegerFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public IntegerFloatTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized IntegerFloatTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                IntegerFloatTuple current = state.tuple = new IntegerFloatMutableTuple(this.next.k, this.next.v);
                state.entry = this.entry;
                this.findNextValue();
                return current;
            }

            @Override
            public void remove(){
                this.tl.get().remove();
            }

            private void nextSub()   {
                if (this.entry != null) {
                    this.entry.endConcurrentIterate();
                }
                while (this.subEntryIndex < 256)    {
                    IntegerFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
                    if (entry != null)  {
                        this.entry = entry;
                        this.hasNextSub.set(true);
                        this.currSector.set(null);
                        this.sectorOffset.set(-1);
                        this.entry.beginConcurrentIterate();
                        return;
                    }
                }
                this.hasNext = false;
            }

            private void findNextValue()    {
                while (this.hasNext) {
                    this.entry.advanceEntry(this.currSector, this.sectorOffset, this.hasNextSub, this.next);
                    if (this.hasNextSub.get()) {
                        //new value was gotten
                        return;
                    } else {
                        //move to next subentry
                        this.nextSub();
                    }
                }
                if (this.entry.isLocked())  {
                    this.entry.endConcurrentIterate();
                }
            }

            class ThreadState   {
                public IntegerFloatTuple tuple;
                public IntegerFloatSubEntry entry;
                private final IntegerFloatConcurrentIterator this_;
                private final IntegerFloatConcurrentTreeHashMap this__;

                public ThreadState(IntegerFloatConcurrentIterator this_, IntegerFloatConcurrentTreeHashMap this__)  {
                    this.this_ = this_;
                    this.this__ = this__;
                }

                public void remove()    {
                    if (this.tuple != null)    {
                        if (this.this_.hasNext())    {
                            this.entry.removeNonBlocking_BeReallyCarefulWithThis(this.tuple.getK());
                        } else {
                            this.this__.remove(this.tuple.getK());
                        }
                    }
                }
            }
        };
    }

    /**
     * A fast iterator over entries.
     * The tuple returned by this iterator is reused, so don't cache it!
     */
    public IntegerFloatConcurrentIterator fastEntryIterator() {
        return new IntegerFloatConcurrentIterator()  {
            private final IntegerFloatConcurrentTreeHashMap this_ = IntegerFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<IntegerFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile IntegerFloatMutableTuple next = new IntegerFloatMutableTuple(0, 0F);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile IntegerFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public IntegerFloatMutableTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized IntegerFloatMutableTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                IntegerFloatMutableTuple a = state.tuple;
                state.tuple = this.next;
                this.next = a;
                state.entry = this.entry;
                this.findNextValue();
                return state.tuple;
            }

            @Override
            public void remove(){
                this.tl.get().remove();
            }

            private void nextSub()   {
                if (this.entry != null) {
                    this.entry.endConcurrentIterate();
                }
                while (this.subEntryIndex < 256)    {
                    IntegerFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
                    if (entry != null)  {
                        this.entry = entry;
                        this.hasNextSub.set(true);
                        this.currSector.set(null);
                        this.sectorOffset.set(-1);
                        this.entry.beginConcurrentIterate();
                        return;
                    }
                }
                this.hasNext = false;
            }

            private void findNextValue()    {
                while (this.hasNext) {
                    this.entry.advanceEntry(this.currSector, this.sectorOffset, this.hasNextSub, this.next);
                    if (this.hasNextSub.get()) {
                        //new value was gotten
                        return;
                    } else {
                        //move to next subentry
                        this.nextSub();
                    }
                }
                if (this.entry.isLocked())  {
                    this.entry.endConcurrentIterate();
                }
            }

            class ThreadState   {
                public IntegerFloatMutableTuple tuple = new IntegerFloatMutableTuple(0, 0F);
                public IntegerFloatSubEntry entry;
                private final IntegerFloatConcurrentIterator this_;
                private final IntegerFloatConcurrentTreeHashMap this__;

                public ThreadState(IntegerFloatConcurrentIterator this_, IntegerFloatConcurrentTreeHashMap this__)  {
                    this.this_ = this_;
                    this.this__ = this__;
                }

                public void remove()    {
                    if (this.tuple != null)    {
                        if (this.this_.hasNext())    {
                            this.entry.removeNonBlocking_BeReallyCarefulWithThis(this.tuple.k);
                        } else {
                            this.this__.remove(this.tuple.k);
                        }
                    }
                }
            }
        };
    }

    private IntegerFloatSubEntry getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            IntegerFloatSubEntry entry = (IntegerFloatSubEntry) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new IntegerFloatSubEntry(this);
            }
            return entry;
        }
    }
}