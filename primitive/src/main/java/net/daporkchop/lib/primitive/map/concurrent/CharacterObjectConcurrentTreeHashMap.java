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

import net.daporkchop.lib.primitive.lambda.consumer.CharacterConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.CharacterObjectConsumer;
import net.daporkchop.lib.primitive.lambda.function.CharacterToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.CharacterToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.CharacterConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.ObjectConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.CharacterObjectConcurrentIterator;
import net.daporkchop.lib.primitive.map.CharacterObjectMap;
import net.daporkchop.lib.primitive.tuple.CharacterObjectTuple;
import net.daporkchop.lib.primitive.tuple.CharacterObjectImmutableTuple;
import net.daporkchop.lib.primitive.tuple.CharacterObjectMutableTuple;
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
 * A tree hash map, using a key type of char and a value type of V.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class CharacterObjectConcurrentTreeHashMap<V extends Object> implements CharacterObjectMap<V>    {
    final AtomicInteger size = new AtomicInteger(0);

    private final CharacterObjectSubEntry[] entries = new CharacterObjectSubEntry[256];
    private final CharacterToIntegerFunction keyHash;
    final CharacterToLongFunction subKeyHash;
    final int subHashLength;

    public CharacterObjectConcurrentTreeHashMap()    {
        this(null, null);
    }

    public CharacterObjectConcurrentTreeHashMap(CharacterToIntegerFunction keyHash, CharacterToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 1);
    }

    public CharacterObjectConcurrentTreeHashMap(CharacterToIntegerFunction keyHash, CharacterToLongFunction subKeyHash, int subHashLength)    {
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
    public V get(char key)   {
        CharacterObjectSubEntry<V> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return null;
        } else {
            return (V) entry.get(key);
        }
    }

    @Override
    public V put(char key, V value)   {
        CharacterObjectSubEntry<V> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return (V) entry.put(key, value);
    }

    @Override
    public V remove(char key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        CharacterObjectSubEntry<V> entry = this.getSubentry(hash, false);
        if (entry == null)  {
            return null;
        } else {
            V val = (V) entry.remove(key);
            if (entry.size.get() == 0)  {
                this.entries[hash] = null;
            }
            return val;
        }
    }

    @Override
    public boolean containsKey(char key)    {
        CharacterObjectSubEntry<V> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return false;
        } else {
            return entry.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(V value)    {
        throw new UnsupportedOperationException("containsValue on ConcurrentTreeHashMap!");
    }

    @Override
    public void clear() {
        synchronized (this.entries) {
            for (int i = 0; i < 256; i++) {
                CharacterObjectSubEntry<V> entry = this.entries[i];
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
    public void forEachKey(CharacterConsumer consumer)   {
        for (int i = 0; i < 256; i++) {
            CharacterObjectSubEntry<V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(ObjectConsumer<V> consumer) {
        for (int i = 0; i < 256; i++) {
            CharacterObjectSubEntry<V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(CharacterObjectConsumer<V> consumer) {
        for (int i = 0; i < 256; i++) {
            CharacterObjectSubEntry<V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(CharacterObjectConsumer<V> consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            CharacterObjectSubEntry<V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public CharacterConcurrentIterator keyIterator()   {
        return new CharacterConcurrentIterator()  {
            private final CharacterObjectConcurrentTreeHashMap this_ = CharacterObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<CharacterObjectSubEntry<V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile char next;
            private volatile CharacterObjectSubEntry<V> entry;

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
                    return state.key;
                }
            }

            @Override
            public synchronized char advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                char current = state.key = this.next;
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
                    CharacterObjectSubEntry<V> entry = getSubentry(this.subEntryIndex++, false);
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
                public char key;
                public CharacterObjectSubEntry entry;
                private final CharacterConcurrentIterator this_;
                private final CharacterObjectConcurrentTreeHashMap this__;

                public ThreadState(CharacterConcurrentIterator this_, CharacterObjectConcurrentTreeHashMap this__)  {
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
    public ObjectConcurrentIterator<V> valueIterator() {
        return new ObjectConcurrentIterator<V>()  {
            private final CharacterObjectConcurrentTreeHashMap this_ = CharacterObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<CharacterObjectSubEntry<V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final CharacterObjectMutableTuple<V> next = new CharacterObjectMutableTuple<V>();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile CharacterObjectSubEntry<V> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public V get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.val;
                }
            }

            @Override
            public synchronized V advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                state.key = this.next.k;
                V current = state.val = this.next.v;
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
                    CharacterObjectSubEntry<V> entry = getSubentry(this.subEntryIndex++, false);
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
                public char key;
                public V val;
                public CharacterObjectSubEntry entry;
                private final ObjectConcurrentIterator<V> this_;
                private final CharacterObjectConcurrentTreeHashMap this__;

                public ThreadState(ObjectConcurrentIterator<V> this_, CharacterObjectConcurrentTreeHashMap this__)  {
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
    public CharacterObjectConcurrentIterator<V> entryIterator() {
        return new CharacterObjectConcurrentIterator<V>()  {
            private final CharacterObjectConcurrentTreeHashMap this_ = CharacterObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<CharacterObjectSubEntry<V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final CharacterObjectMutableTuple<V> next = new CharacterObjectMutableTuple<V>();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile CharacterObjectSubEntry<V> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public CharacterObjectTuple<V> get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized CharacterObjectTuple<V> advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                CharacterObjectTuple<V> current = state.tuple = new CharacterObjectMutableTuple<V>(this.next.k, this.next.v);
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
                    CharacterObjectSubEntry<V> entry = getSubentry(this.subEntryIndex++, false);
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
                public CharacterObjectTuple<V> tuple;
                public CharacterObjectSubEntry entry;
                private final CharacterObjectConcurrentIterator<V> this_;
                private final CharacterObjectConcurrentTreeHashMap this__;

                public ThreadState(CharacterObjectConcurrentIterator<V> this_, CharacterObjectConcurrentTreeHashMap this__)  {
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
    public CharacterObjectConcurrentIterator<V> fastEntryIterator() {
        return new CharacterObjectConcurrentIterator<V>()  {
            private final CharacterObjectConcurrentTreeHashMap this_ = CharacterObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<CharacterObjectSubEntry<V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile CharacterObjectMutableTuple<V> next = new CharacterObjectMutableTuple<V>((char) 0, null);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile CharacterObjectSubEntry<V> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public CharacterObjectMutableTuple<V> get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized CharacterObjectMutableTuple<V> advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                CharacterObjectMutableTuple<V> a = state.tuple;
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
                    CharacterObjectSubEntry<V> entry = getSubentry(this.subEntryIndex++, false);
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
                public CharacterObjectMutableTuple<V> tuple = new CharacterObjectMutableTuple<V>((char) 0, null);
                public CharacterObjectSubEntry entry;
                private final CharacterObjectConcurrentIterator<V> this_;
                private final CharacterObjectConcurrentTreeHashMap this__;

                public ThreadState(CharacterObjectConcurrentIterator<V> this_, CharacterObjectConcurrentTreeHashMap this__)  {
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

    private CharacterObjectSubEntry<V> getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            CharacterObjectSubEntry<V> entry = (CharacterObjectSubEntry<V>) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new CharacterObjectSubEntry<V>(this);
            }
            return entry;
        }
    }
}
