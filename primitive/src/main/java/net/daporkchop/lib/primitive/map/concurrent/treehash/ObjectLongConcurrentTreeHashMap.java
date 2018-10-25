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

import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.LongConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ObjectLongConsumer;
import net.daporkchop.lib.primitive.lambda.function.ObjectToIntegerFunction;
import net.daporkchop.lib.primitive.lambda.function.ObjectToLongFunction;
import net.daporkchop.lib.primitive.iterator.concurrent.ObjectConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.LongConcurrentIterator;
import net.daporkchop.lib.primitive.iterator.concurrent.ObjectLongConcurrentIterator;
import net.daporkchop.lib.primitive.map.ObjectLongMap;
import net.daporkchop.lib.primitive.tuple.ObjectLongTuple;
import net.daporkchop.lib.primitive.tuple.ObjectLongMutableTuple;
import net.daporkchop.lib.primitiveutil.IteratorCompleteException;
import net.daporkchop.lib.common.VoidFunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A tree hash map, using a key type of K and a value type of long.
 * Designed to be highly concurrent, it should operate well with as many as 256
 * threads at the same time.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ObjectLongConcurrentTreeHashMap<K extends Object> implements ObjectLongMap<K>    {
    final AtomicInteger size = new AtomicInteger(0);

    private final ObjectLongSubEntry[] entries = new ObjectLongSubEntry[256];
    private final ObjectToIntegerFunction keyHash;
    final ObjectToLongFunction subKeyHash;
    final int subHashLength;

    public ObjectLongConcurrentTreeHashMap()    {
        this(null, null);
    }

    public ObjectLongConcurrentTreeHashMap(ObjectToIntegerFunction keyHash, ObjectToLongFunction subKeyHash)    {
        this(keyHash, subKeyHash, 3);
    }

    public ObjectLongConcurrentTreeHashMap(ObjectToIntegerFunction keyHash, ObjectToLongFunction subKeyHash, int subHashLength)    {
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
    public long get(K key)   {
        ObjectLongSubEntry<K> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return 0L;
        } else {
            return entry.get(key);
        }
    }

    @Override
    public long put(K key, long value)   {
        ObjectLongSubEntry<K> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, true);
        return entry.put(key, value);
    }

    @Override
    public long remove(K key)    {
        int hash = this.keyHash.apply(key) & 0xFF;
        ObjectLongSubEntry<K> entry = this.getSubentry(hash, false);
        if (entry == null)  {
            return 0L;
        } else {
            long val = entry.remove(key);
            if (entry.size.get() == 0)  {
                this.entries[hash] = null;
            }
            return val;
        }
    }

    @Override
    public boolean containsKey(K key)    {
        ObjectLongSubEntry<K> entry = this.getSubentry(this.keyHash.apply(key) & 0xFF, false);
        if (entry == null)  {
            return false;
        } else {
            return entry.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(long value)    {
        throw new UnsupportedOperationException("containsValue on ConcurrentTreeHashMap!");
    }

    @Override
    public void clear() {
        synchronized (this.entries) {
            for (int i = 0; i < 256; i++) {
                ObjectLongSubEntry<K> entry = this.entries[i];
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
            ObjectLongSubEntry<K> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachKey(consumer);
            }
        }
    }

    @Override
    public void forEachValue(LongConsumer consumer) {
        for (int i = 0; i < 256; i++) {
            ObjectLongSubEntry<K> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachValue(consumer);
            }
        }
    }

    @Override
    public void forEachEntry(ObjectLongConsumer<K> consumer) {
        for (int i = 0; i < 256; i++) {
            ObjectLongSubEntry<K> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
    }

    public void forEachEntry(ObjectLongConsumer<K> consumer, VoidFunction complete) {
        for (int i = 0; i < 256; i++) {
            ObjectLongSubEntry<K> entry = this.getSubentry(i, false);
            if (entry != null)  {
                entry.forEachEntry(consumer);
            }
        }
        complete.run();
    }

    @Override
    public ObjectConcurrentIterator<K> keyIterator()   {
        return new ObjectConcurrentIterator<K>()  {
            private final ObjectLongConcurrentTreeHashMap this_ = ObjectLongConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectLongSubEntry<K>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile K next;
            private volatile ObjectLongSubEntry<K> entry;

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
                    ObjectLongSubEntry<K> entry = getSubentry(this.subEntryIndex++, false);
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
                public ObjectLongSubEntry entry;
                private final ObjectConcurrentIterator<K> this_;
                private final ObjectLongConcurrentTreeHashMap this__;

                public ThreadState(ObjectConcurrentIterator<K> this_, ObjectLongConcurrentTreeHashMap this__)  {
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
    public LongConcurrentIterator valueIterator() {
        return new LongConcurrentIterator()  {
            private final ObjectLongConcurrentTreeHashMap this_ = ObjectLongConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectLongSubEntry<K>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final ObjectLongMutableTuple<K> next = new ObjectLongMutableTuple<K>();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ObjectLongSubEntry<K> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public long get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.val;
                }
            }

            @Override
            public synchronized long advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                state.key = this.next.k;
                long current = state.val = this.next.v;
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
                    ObjectLongSubEntry<K> entry = getSubentry(this.subEntryIndex++, false);
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
                public long val;
                public ObjectLongSubEntry entry;
                private final LongConcurrentIterator this_;
                private final ObjectLongConcurrentTreeHashMap this__;

                public ThreadState(LongConcurrentIterator this_, ObjectLongConcurrentTreeHashMap this__)  {
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
    public ObjectLongConcurrentIterator<K> entryIterator() {
        return new ObjectLongConcurrentIterator<K>()  {
            private final ObjectLongConcurrentTreeHashMap this_ = ObjectLongConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectLongSubEntry<K>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private final ObjectLongMutableTuple<K> next = new ObjectLongMutableTuple<K>();
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ObjectLongSubEntry<K> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public ObjectLongTuple<K> get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized ObjectLongTuple<K> advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                ObjectLongTuple<K> current = state.tuple = new ObjectLongMutableTuple<K>(this.next.k, this.next.v);
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
                    ObjectLongSubEntry<K> entry = getSubentry(this.subEntryIndex++, false);
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
                public ObjectLongTuple<K> tuple;
                public ObjectLongSubEntry entry;
                private final ObjectLongConcurrentIterator<K> this_;
                private final ObjectLongConcurrentTreeHashMap this__;

                public ThreadState(ObjectLongConcurrentIterator<K> this_, ObjectLongConcurrentTreeHashMap this__)  {
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
    public ObjectLongConcurrentIterator<K> fastEntryIterator() {
        return new ObjectLongConcurrentIterator<K>()  {
            private final ObjectLongConcurrentTreeHashMap this_ = ObjectLongConcurrentTreeHashMap.this;
            //private final ReentrantLock lock = new ReentrantLock();
            private final ThreadLocal<ThreadState> tl = ThreadLocal.withInitial(() -> new ThreadState(this, this.this_));
            private final AtomicInteger sectorOffset = new AtomicInteger(0);
            private final AtomicReference<ObjectLongSubEntry<K>.Sector> currSector = new AtomicReference(null);
            private final AtomicBoolean hasNextSub = new AtomicBoolean(true);
            private volatile ObjectLongMutableTuple<K> next = new ObjectLongMutableTuple<K>(null, 0L);
            private volatile boolean hasNext = true;
            private volatile int subEntryIndex = 0;
            private volatile ObjectLongSubEntry<K> entry;

            {
                this.nextSub();
                this.findNextValue();
            }

            @Override
            public boolean hasNext()    {
                return this.hasNext;
            }

            @Override
            public ObjectLongMutableTuple<K> get()   {
                ThreadState state = this.tl.get();
                if (state.entry == null)    {
                    throw new IllegalStateException();
                } else {
                    return state.tuple;
                }
            }

            @Override
            public synchronized ObjectLongMutableTuple<K> advance()   {
                if (!this.hasNext())    {
                    throw new IteratorCompleteException();
                }
                ThreadState state = this.tl.get();
                ObjectLongMutableTuple<K> a = state.tuple;
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
                    ObjectLongSubEntry<K> entry = getSubentry(this.subEntryIndex++, false);
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
                public ObjectLongMutableTuple<K> tuple = new ObjectLongMutableTuple<K>(null, 0L);
                public ObjectLongSubEntry entry;
                private final ObjectLongConcurrentIterator<K> this_;
                private final ObjectLongConcurrentTreeHashMap this__;

                public ThreadState(ObjectLongConcurrentIterator<K> this_, ObjectLongConcurrentTreeHashMap this__)  {
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

    private ObjectLongSubEntry<K> getSubentry(int index, boolean create)    {
        assert index == (index & 0xFF);

        synchronized (this.entries) {
            ObjectLongSubEntry<K> entry = (ObjectLongSubEntry<K>) this.entries[index];
            if (entry == null && create)    {
                this.entries[index] = entry = new ObjectLongSubEntry<K>(this);
            }
            return entry;
        }
    }
}
