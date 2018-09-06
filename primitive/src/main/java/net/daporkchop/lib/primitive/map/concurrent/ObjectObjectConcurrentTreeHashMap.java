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

import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ObjectObjectConsumer;
import net.daporkchop.lib.primitive.lambda.function.ObjectToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.ObjectToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.ObjectConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.ObjectConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.ObjectObjectConcurrentIterator;
import net.daporkchop.lib.primitive.map.ObjectObjectMap;
import net.daporkchop.lib.primitive.tuple.ObjectObjectTuple;
import net.daporkchop.lib.primitive.tuple.ObjectObjectImmutableTuple;
import net.daporkchop.lib.primitive.tuple.ObjectObjectMutableTuple;
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
 * A tree hash map, using a key type of K and a value type of V.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ObjectObjectConcurrentTreeHashMap<K extends Object, V extends Object> implements ObjectObjectMap<K, V>    {
    final AtomicInteger size = new AtomicInteger(0);

    private final ObjectObjectSubEntry[] entries = new ObjectObjectSubEntry[256];
    private final ObjectToIntegerFunction keyHash;
    final ObjectToLongFunction subKeyHash;
    final int subHashLength;

    public ObjectObjectConcurrentTreeHashMap()    {
        this(null, null);
    }

    public ObjectObjectConcurrentTreeHashMap(ObjectToIntegerFunction keyHash, ObjectToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 3);
    }

    public ObjectObjectConcurrentTreeHashMap(ObjectToIntegerFunction keyHash, ObjectToLongFunction subKeyHash, int subHashLength)    {
        if (subHashLength < 0 || subHashLength > 8)  {
            throw new IllegalArgumentException("subHashLength must be in range 0-8 (given: " + subHashLength + ")");
        }

        if (keyHash == null)    {
            this.keyHash = in -> {
                return java.util.Objects.hashCode(in);
            };
        } else {
            this.keyHash = keyHash;
        }
        if (subKeyHash == null)    {
            this.subKeyHash = in -> {
                return (long) java.util.Objects.hashCode(in);
            };
            this.subHashLength = 3;
        } else {
            this.subKeyHash = subKeyHash;
            this.subHashLength = subHashLength;
        }
    }

    @Override
    public V get(K key)   {
        ObjectObjectSubEntry<K, V> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return null;
        } else {
            return (V) entry.get(key);
        }
    }

    @Override
    public V put(K key, V value)   {
        ObjectObjectSubEntry<K, V> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return (V) entry.put(key, value);
    }

    @Override
    public V remove(K key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        ObjectObjectSubEntry<K, V> entry = this.getSubentry(hash, false);
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
    public boolean containsKey(K key)    {
        ObjectObjectSubEntry<K, V> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
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
                ObjectObjectSubEntry<K, V> entry = this.entries[i];
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
    public void forEachKey(ObjectConsumer<K> consumer)   {
        for (int i = 0; i < 256; i++) {
            ObjectObjectSubEntry<K, V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(ObjectConsumer<V> consumer) {
        for (int i = 0; i < 256; i++) {
            ObjectObjectSubEntry<K, V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(ObjectObjectConsumer<K, V> consumer) {
        for (int i = 0; i < 256; i++) {
            ObjectObjectSubEntry<K, V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(ObjectObjectConsumer<K, V> consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            ObjectObjectSubEntry<K, V> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public ObjectConcurrentIterator<K> keyIterator()   {
        return new ObjectConcurrentIterator<K>()  {
            private final ObjectObjectConcurrentTreeHashMap this_ = ObjectObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectObjectSubEntry<K, V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile K next;
            private volatile ObjectObjectSubEntry<K, V> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public K get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.key;
                }
            }

            @Override
            public synchronized K advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                K current = state.key = this.next;
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
                    ObjectObjectSubEntry<K, V> entry = getSubentry(this.subEntryIndex++, false);
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
                public K key;
                public ObjectObjectSubEntry entry;
                private final ObjectConcurrentIterator<K> this_;
                private final ObjectObjectConcurrentTreeHashMap this__;

                public ThreadState(ObjectConcurrentIterator<K> this_, ObjectObjectConcurrentTreeHashMap this__)  {
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
            private final ObjectObjectConcurrentTreeHashMap this_ = ObjectObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectObjectSubEntry<K, V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final ObjectObjectMutableTuple<K, V> next = new ObjectObjectMutableTuple<K, V>();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ObjectObjectSubEntry<K, V> entry;

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
                    ObjectObjectSubEntry<K, V> entry = getSubentry(this.subEntryIndex++, false);
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
                public K key;
                public V val;
                public ObjectObjectSubEntry entry;
                private final ObjectConcurrentIterator<V> this_;
                private final ObjectObjectConcurrentTreeHashMap this__;

                public ThreadState(ObjectConcurrentIterator<V> this_, ObjectObjectConcurrentTreeHashMap this__)  {
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
    public ObjectObjectConcurrentIterator<K, V> entryIterator() {
        return new ObjectObjectConcurrentIterator<K, V>()  {
            private final ObjectObjectConcurrentTreeHashMap this_ = ObjectObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectObjectSubEntry<K, V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final ObjectObjectMutableTuple<K, V> next = new ObjectObjectMutableTuple<K, V>();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ObjectObjectSubEntry<K, V> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public ObjectObjectTuple<K, V> get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized ObjectObjectTuple<K, V> advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                ObjectObjectTuple<K, V> current = state.tuple = new ObjectObjectMutableTuple<K, V>(this.next.k, this.next.v);
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
                    ObjectObjectSubEntry<K, V> entry = getSubentry(this.subEntryIndex++, false);
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
                public ObjectObjectTuple<K, V> tuple;
                public ObjectObjectSubEntry entry;
                private final ObjectObjectConcurrentIterator<K, V> this_;
                private final ObjectObjectConcurrentTreeHashMap this__;

                public ThreadState(ObjectObjectConcurrentIterator<K, V> this_, ObjectObjectConcurrentTreeHashMap this__)  {
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
    public ObjectObjectConcurrentIterator<K, V> fastEntryIterator() {
        return new ObjectObjectConcurrentIterator<K, V>()  {
            private final ObjectObjectConcurrentTreeHashMap this_ = ObjectObjectConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectObjectSubEntry<K, V>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile ObjectObjectMutableTuple<K, V> next = new ObjectObjectMutableTuple<K, V>(null, null);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ObjectObjectSubEntry<K, V> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public ObjectObjectMutableTuple<K, V> get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized ObjectObjectMutableTuple<K, V> advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                ObjectObjectMutableTuple<K, V> a = state.tuple;
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
                    ObjectObjectSubEntry<K, V> entry = getSubentry(this.subEntryIndex++, false);
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
                public ObjectObjectMutableTuple<K, V> tuple = new ObjectObjectMutableTuple<K, V>(null, null);
                public ObjectObjectSubEntry entry;
                private final ObjectObjectConcurrentIterator<K, V> this_;
                private final ObjectObjectConcurrentTreeHashMap this__;

                public ThreadState(ObjectObjectConcurrentIterator<K, V> this_, ObjectObjectConcurrentTreeHashMap this__)  {
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

    private ObjectObjectSubEntry<K, V> getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            ObjectObjectSubEntry<K, V> entry = (ObjectObjectSubEntry<K, V>) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new ObjectObjectSubEntry<K, V>(this);
            }
            return entry;
        }
    }
}
