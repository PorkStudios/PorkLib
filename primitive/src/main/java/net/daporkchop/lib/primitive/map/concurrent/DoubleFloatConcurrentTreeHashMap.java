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

import net.daporkchop.lib.primitive.lambda.consumer.DoubleConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.FloatConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.DoubleFloatConsumer;
import net.daporkchop.lib.primitive.lambda.function.DoubleToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.DoubleToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.DoubleConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.FloatConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.DoubleFloatConcurrentIterator;
import net.daporkchop.lib.primitive.map.DoubleFloatMap;
import net.daporkchop.lib.primitive.tuple.DoubleFloatTuple;
import net.daporkchop.lib.primitive.tuple.DoubleFloatImmutableTuple;
import net.daporkchop.lib.primitive.tuple.DoubleFloatMutableTuple;
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
 * A tree hash map, using a key type of double and a value type of float.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class DoubleFloatConcurrentTreeHashMap implements DoubleFloatMap    {
    final AtomicInteger size = new AtomicInteger(0);

    private final DoubleFloatSubEntry[] entries = new DoubleFloatSubEntry[256];
    private final DoubleToIntegerFunction keyHash;
    final DoubleToLongFunction subKeyHash;
    final int subHashLength;

    public DoubleFloatConcurrentTreeHashMap()    {
        this(null, null);
    }

    public DoubleFloatConcurrentTreeHashMap(DoubleToIntegerFunction keyHash, DoubleToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 7);
    }

    public DoubleFloatConcurrentTreeHashMap(DoubleToIntegerFunction keyHash, DoubleToLongFunction subKeyHash, int subHashLength)    {
        if (subHashLength < 0 || subHashLength > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + subHashLength + ")");
        }

        if (keyHash == null)    {
            this.keyHash = in -> {
                long l = Double.doubleToLongBits(in);
        return (int) ((((l >> 32) & 0x7fffffff)) ^ ((l & 0x7fffffff)) & 0x7fffffff);
            };
        } else {
            this.keyHash = keyHash;
        }
        if (subKeyHash == null)    {
            this.subKeyHash = in -> {
                return Double.doubleToLongBits(in);
            };
            this.subHashLength = 7;
        } else {
            this.subKeyHash = subKeyHash;
            this.subHashLength = subHashLength;
        }
    }

    @Override
    public float get(double key)   {
        DoubleFloatSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return 0F;
        } else {
            return entry.get(key);
        }
    }

    @Override
    public float put(double key, float value)   {
        DoubleFloatSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return entry.put(key, value);
    }

    @Override
    public float remove(double key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        DoubleFloatSubEntry entry = this.getSubentry(hash, false);
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
    public boolean containsKey(double key)    {
        DoubleFloatSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
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
                DoubleFloatSubEntry entry = this.entries[i];
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
    public void forEachKey(DoubleConsumer consumer)   {
        for (int i = 0; i < 256; i++) {
            DoubleFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(FloatConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            DoubleFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(DoubleFloatConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            DoubleFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(DoubleFloatConsumer consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            DoubleFloatSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public DoubleConcurrentIterator keyIterator()   {
        return new DoubleConcurrentIterator()  {
            private final DoubleFloatConcurrentTreeHashMap this_ = DoubleFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<DoubleFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile double next;
            private volatile DoubleFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public double get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.key;
                }
            }

            @Override
            public synchronized double advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                double current = state.key = this.next;
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
                    DoubleFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public double key;
                public DoubleFloatSubEntry entry;
                private final DoubleConcurrentIterator this_;
                private final DoubleFloatConcurrentTreeHashMap this__;

                public ThreadState(DoubleConcurrentIterator this_, DoubleFloatConcurrentTreeHashMap this__)  {
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
            private final DoubleFloatConcurrentTreeHashMap this_ = DoubleFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<DoubleFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final DoubleFloatMutableTuple next = new DoubleFloatMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile DoubleFloatSubEntry entry;

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
                    DoubleFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public double key;
                public float val;
                public DoubleFloatSubEntry entry;
                private final FloatConcurrentIterator this_;
                private final DoubleFloatConcurrentTreeHashMap this__;

                public ThreadState(FloatConcurrentIterator this_, DoubleFloatConcurrentTreeHashMap this__)  {
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
    public DoubleFloatConcurrentIterator entryIterator() {
        return new DoubleFloatConcurrentIterator()  {
            private final DoubleFloatConcurrentTreeHashMap this_ = DoubleFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<DoubleFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final DoubleFloatMutableTuple next = new DoubleFloatMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile DoubleFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public DoubleFloatTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized DoubleFloatTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                DoubleFloatTuple current = state.tuple = new DoubleFloatMutableTuple(this.next.k, this.next.v);
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
                    DoubleFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public DoubleFloatTuple tuple;
                public DoubleFloatSubEntry entry;
                private final DoubleFloatConcurrentIterator this_;
                private final DoubleFloatConcurrentTreeHashMap this__;

                public ThreadState(DoubleFloatConcurrentIterator this_, DoubleFloatConcurrentTreeHashMap this__)  {
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
    public DoubleFloatConcurrentIterator fastEntryIterator() {
        return new DoubleFloatConcurrentIterator()  {
            private final DoubleFloatConcurrentTreeHashMap this_ = DoubleFloatConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<DoubleFloatSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile DoubleFloatMutableTuple next = new DoubleFloatMutableTuple(0D, 0F);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile DoubleFloatSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public DoubleFloatMutableTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized DoubleFloatMutableTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                DoubleFloatMutableTuple a = state.tuple;
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
                    DoubleFloatSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public DoubleFloatMutableTuple tuple = new DoubleFloatMutableTuple(0D, 0F);
                public DoubleFloatSubEntry entry;
                private final DoubleFloatConcurrentIterator this_;
                private final DoubleFloatConcurrentTreeHashMap this__;

                public ThreadState(DoubleFloatConcurrentIterator this_, DoubleFloatConcurrentTreeHashMap this__)  {
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

    private DoubleFloatSubEntry getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            DoubleFloatSubEntry entry = (DoubleFloatSubEntry) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new DoubleFloatSubEntry(this);
            }
            return entry;
        }
    }
}
