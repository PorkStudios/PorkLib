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

import net.daporkchop.lib.primitive.lambda.consumer.BooleanConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.CharacterConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.BooleanCharacterConsumer;
import net.daporkchop.lib.primitive.lambda.function.BooleanToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.BooleanToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.BooleanConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.CharacterConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.BooleanCharacterConcurrentIterator;
import net.daporkchop.lib.primitive.map.BooleanCharacterMap;
import net.daporkchop.lib.primitive.tuple.BooleanCharacterTuple;
import net.daporkchop.lib.primitive.tuple.BooleanCharacterMutableTuple;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;
import net.daporkchop.lib.common.VoidFunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A tree hash map, using a key type of boolean and a value type of char.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class BooleanCharacterConcurrentTreeHashMap implements BooleanCharacterMap    {
    final AtomicInteger size = new AtomicInteger(0);

    private final BooleanCharacterSubEntry[] entries = new BooleanCharacterSubEntry[256];
    private final BooleanToIntegerFunction keyHash;
    final BooleanToLongFunction subKeyHash;
    final int subHashLength;

    public BooleanCharacterConcurrentTreeHashMap()    {
        this(null, null);
    }

    public BooleanCharacterConcurrentTreeHashMap(BooleanToIntegerFunction keyHash, BooleanToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 0);
    }

    public BooleanCharacterConcurrentTreeHashMap(BooleanToIntegerFunction keyHash, BooleanToLongFunction subKeyHash, int subHashLength)    {
        if (subHashLength < 0 || subHashLength > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + subHashLength + ")");
        }

        if (keyHash == null)    {
            this.keyHash = in -> {
                return in ? 1 : 0;
            };
        } else {
            this.keyHash = keyHash;
        }
        if (subKeyHash == null)    {
            this.subKeyHash = in -> {
                return in ? 1 : 0;
            };
            this.subHashLength = 0;
        } else {
            this.subKeyHash = subKeyHash;
            this.subHashLength = subHashLength;
        }
    }

    @Override
    public char get(boolean key)   {
        BooleanCharacterSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return (char) 0;
        } else {
            return entry.get(key);
        }
    }

    @Override
    public char put(boolean key, char value)   {
        BooleanCharacterSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return entry.put(key, value);
    }

    @Override
    public char remove(boolean key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        BooleanCharacterSubEntry entry = this.getSubentry(hash, false);
        if (entry == null)  {
            return (char) 0;
        } else {
            char val = entry.remove(key);
            if (entry.size.get() == 0)  {
                this.entries[hash] = null;
            }
            return val;
        }
    }

    @Override
    public boolean containsKey(boolean key)    {
        BooleanCharacterSubEntry entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return false;
        } else {
            return entry.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(char value)    {
        throw new UnsupportedOperationException("containsValue on ConcurrentTreeHashMap!");
    }

    @Override
    public void clear() {
        synchronized (this.entries) {
            for (int i = 0; i < 256; i++) {
                BooleanCharacterSubEntry entry = this.entries[i];
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
    public void forEachKey(BooleanConsumer consumer)   {
        for (int i = 0; i < 256; i++) {
            BooleanCharacterSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(CharacterConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            BooleanCharacterSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(BooleanCharacterConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            BooleanCharacterSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(BooleanCharacterConsumer consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            BooleanCharacterSubEntry entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public BooleanConcurrentIterator keyIterator()   {
        return new BooleanConcurrentIterator()  {
            private final BooleanCharacterConcurrentTreeHashMap this_ = BooleanCharacterConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<BooleanCharacterSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile boolean next;
            private volatile BooleanCharacterSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public boolean get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.key;
                }
            }

            @Override
            public synchronized boolean advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                boolean current = state.key = this.next;
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
                    BooleanCharacterSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public boolean key;
                public BooleanCharacterSubEntry entry;
                private final BooleanConcurrentIterator this_;
                private final BooleanCharacterConcurrentTreeHashMap this__;

                public ThreadState(BooleanConcurrentIterator this_, BooleanCharacterConcurrentTreeHashMap this__)  {
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
    public CharacterConcurrentIterator valueIterator() {
        return new CharacterConcurrentIterator()  {
            private final BooleanCharacterConcurrentTreeHashMap this_ = BooleanCharacterConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<BooleanCharacterSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final BooleanCharacterMutableTuple next = new BooleanCharacterMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile BooleanCharacterSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public char get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.val;
                }
            }

            @Override
            public synchronized char advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                state.key = this.next.k;
                char current = state.val = this.next.v;
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
                    BooleanCharacterSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public boolean key;
                public char val;
                public BooleanCharacterSubEntry entry;
                private final CharacterConcurrentIterator this_;
                private final BooleanCharacterConcurrentTreeHashMap this__;

                public ThreadState(CharacterConcurrentIterator this_, BooleanCharacterConcurrentTreeHashMap this__)  {
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
    public BooleanCharacterConcurrentIterator entryIterator() {
        return new BooleanCharacterConcurrentIterator()  {
            private final BooleanCharacterConcurrentTreeHashMap this_ = BooleanCharacterConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<BooleanCharacterSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final BooleanCharacterMutableTuple next = new BooleanCharacterMutableTuple();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile BooleanCharacterSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public BooleanCharacterTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized BooleanCharacterTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                BooleanCharacterTuple current = state.tuple = new BooleanCharacterMutableTuple(this.next.k, this.next.v);
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
                    BooleanCharacterSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public BooleanCharacterTuple tuple;
                public BooleanCharacterSubEntry entry;
                private final BooleanCharacterConcurrentIterator this_;
                private final BooleanCharacterConcurrentTreeHashMap this__;

                public ThreadState(BooleanCharacterConcurrentIterator this_, BooleanCharacterConcurrentTreeHashMap this__)  {
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
    public BooleanCharacterConcurrentIterator fastEntryIterator() {
        return new BooleanCharacterConcurrentIterator()  {
            private final BooleanCharacterConcurrentTreeHashMap this_ = BooleanCharacterConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<BooleanCharacterSubEntry.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile BooleanCharacterMutableTuple next = new BooleanCharacterMutableTuple(false, (char) 0);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile BooleanCharacterSubEntry entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public BooleanCharacterMutableTuple get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized BooleanCharacterMutableTuple advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                BooleanCharacterMutableTuple a = state.tuple;
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
                    BooleanCharacterSubEntry entry = getSubentry(this.subEntryIndex++, false);
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
                public BooleanCharacterMutableTuple tuple = new BooleanCharacterMutableTuple(false, (char) 0);
                public BooleanCharacterSubEntry entry;
                private final BooleanCharacterConcurrentIterator this_;
                private final BooleanCharacterConcurrentTreeHashMap this__;

                public ThreadState(BooleanCharacterConcurrentIterator this_, BooleanCharacterConcurrentTreeHashMap this__)  {
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

    private BooleanCharacterSubEntry getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            BooleanCharacterSubEntry entry = (BooleanCharacterSubEntry) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new BooleanCharacterSubEntry(this);
            }
            return entry;
        }
    }
}
