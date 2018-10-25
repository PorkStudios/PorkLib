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

package net.daporkchop.lib.primitive.map.concurrent.treehash;

import net.daporkchop.lib.primitive.lambda.consumer.ShortConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.DoubleConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ShortDoubleConsumer;
import net.daporkchop.lib.primitive.lambda.function.ShortToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.ShortToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.ShortConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.DoubleConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.ShortDoubleConcurrentIterator;
import net.daporkchop.lib.primitive.map.ShortDoubleMap;
import net.daporkchop.lib.primitive.tuple.ShortDoubleTuple;
import net.daporkchop.lib.primitive.tuple.ShortDoubleMutableTuple;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;
import net.daporkchop.lib.common.VoidFunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A tree hash map, using a key type of short and a value type of double.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ShortDoubleConcurrentTreeHashMap implements ShortDoubleMap    {
    final AtomicInteger size = new AtomicInteger(0);

    private final ShortDoubleSubEntry[] entries = new ShortDoubleSubEntry[256];
    private final ShortToIntegerFunction keyHash;
    final ShortToLongFunction subKeyHash;
    final int subHashLength;

    public ShortDoubleConcurrentTreeHashMap()    {
        this(null, null);
    }

    public ShortDoubleConcurrentTreeHashMap(ShortToIntegerFunction keyHash, ShortToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 1);
    }

    public ShortDoubleConcurrentTreeHashMap(ShortToIntegerFunction keyHash, ShortToLongFunction subKeyHash, int subHashLength)    {
        if (subHashLength < 0 || subHashLength > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + subHashLength + ")");
        }

        if (keyHash == null)    {
            this.keyHash = in -> {
                return in & 0xFFFF;
            };
        } else {
            this.keyHash = keyHash;
        }
        if (subKeyHash == null)    {
            this.subKeyHash = in -> {
                return in & 0xFFFF;
            };
            this.subHashLength = 1;
        } else {
            this.subKeyHash = subKeyHash;
            this.subHashLength = subHashLength;
        }
    }

    @Override
    public double get(short key)   {
        ShortDoubleSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return 0D;
        } else {
            return entry.get(key);
        }
    }

    @Override
    public double put(short key, double value)   {
        ShortDoubleSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return entry.put(key, value);
    }

    @Override
    public double remove(short key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        ShortDoubleSubEntry entry = this.getSubentry(hash, false);
        if (entry == null)  {
            return 0D;
        } else {
            double val = entry.remove(key);
            if (entry.size.get() == 0)  {
                this.entries[hash] = null;
            }
            return val;
        }
    }

    @Override
    public boolean containsKey(short key)    {
        ShortDoubleSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return false;
        } else {
            return entry.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(double value)    {
        throw new UnsupportedOperationException("containsValue on ConcurrentTreeHashMap!");
    }

    @Override
    public void clear() {
        synchronized (this.entries) {
            for (int i = 0; i < 256; i++) {
                ShortDoubleSubEntry entry = this.entries[i];
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
    public void forEachKey(ShortConsumer consumer)   {
        for (int i = 0; i < 256; i++) {
            ShortDoubleSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(DoubleConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            ShortDoubleSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(ShortDoubleConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            ShortDoubleSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(ShortDoubleConsumer consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            ShortDoubleSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public ShortConcurrentIterator keyIterator()   {
        return new ShortConcurrentIterator()  {
            private final ShortDoubleConcurrentTreeHashMap this_ = ShortDoubleConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ShortDoubleSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile short next;
            private volatile ShortDoubleSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public short get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.key;
                }
            }

            @Override
            public synchronized short advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                short current = state.key = this.next;
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
                    ShortDoubleSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public short key;
                public ShortDoubleSubEntry entry;
                private final ShortConcurrentIterator this_;
                private final ShortDoubleConcurrentTreeHashMap this__;

                public ThreadState(ShortConcurrentIterator this_, ShortDoubleConcurrentTreeHashMap this__)  {
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
    public DoubleConcurrentIterator valueIterator() {
        return new DoubleConcurrentIterator()  {
            private final ShortDoubleConcurrentTreeHashMap this_ = ShortDoubleConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ShortDoubleSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final ShortDoubleMutableTuple next = new ShortDoubleMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ShortDoubleSubEntry entry;

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
                    return state.val;
                }
            }

            @Override
            public synchronized double advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                state.key = this.next.k;
                double current = state.val = this.next.v;
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
                    ShortDoubleSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public short key;
                public double val;
                public ShortDoubleSubEntry entry;
                private final DoubleConcurrentIterator this_;
                private final ShortDoubleConcurrentTreeHashMap this__;

                public ThreadState(DoubleConcurrentIterator this_, ShortDoubleConcurrentTreeHashMap this__)  {
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
    public ShortDoubleConcurrentIterator entryIterator() {
        return new ShortDoubleConcurrentIterator()  {
            private final ShortDoubleConcurrentTreeHashMap this_ = ShortDoubleConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ShortDoubleSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final ShortDoubleMutableTuple next = new ShortDoubleMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ShortDoubleSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public ShortDoubleTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized ShortDoubleTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                ShortDoubleTuple current = state.tuple = new ShortDoubleMutableTuple(this.next.k, this.next.v);
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
                    ShortDoubleSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public ShortDoubleTuple tuple;
                public ShortDoubleSubEntry entry;
                private final ShortDoubleConcurrentIterator this_;
                private final ShortDoubleConcurrentTreeHashMap this__;

                public ThreadState(ShortDoubleConcurrentIterator this_, ShortDoubleConcurrentTreeHashMap this__)  {
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
    public ShortDoubleConcurrentIterator fastEntryIterator() {
        return new ShortDoubleConcurrentIterator()  {
            private final ShortDoubleConcurrentTreeHashMap this_ = ShortDoubleConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ShortDoubleSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile ShortDoubleMutableTuple next = new ShortDoubleMutableTuple((short) 0, 0D);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ShortDoubleSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public ShortDoubleMutableTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized ShortDoubleMutableTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                ShortDoubleMutableTuple a = state.tuple;
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
                    ShortDoubleSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public ShortDoubleMutableTuple tuple = new ShortDoubleMutableTuple((short) 0, 0D);
                public ShortDoubleSubEntry entry;
                private final ShortDoubleConcurrentIterator this_;
                private final ShortDoubleConcurrentTreeHashMap this__;

                public ThreadState(ShortDoubleConcurrentIterator this_, ShortDoubleConcurrentTreeHashMap this__)  {
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

    private ShortDoubleSubEntry getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            ShortDoubleSubEntry entry = (ShortDoubleSubEntry) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new ShortDoubleSubEntry(this);
            }
            return entry;
        }
    }
}
