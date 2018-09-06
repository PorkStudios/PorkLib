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

package net.daporkchop.lib.primitive.map.sync;

import net.daporkchop.lib.primitive.lambda.consumer.LongConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntegerConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.LongIntegerConsumer;
import net.daporkchop.lib.primitive.map.LongIntegerMap;
import net.daporkchop.lib.primitive.iterator.LongIterator;
import net.daporkchop.lib.primitive.iterator.IntegerIterator;
import net.daporkchop.lib.primitive.iterator.bi.LongIntegerIterator;

import lombok.*;

/**
 * A synchronized map, using a key type of long and a value type of int.
 * This doesn't do anything by itself, it just serves as a wrapper for another instance of LongIntegerMap.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class LongIntegerSynchronizedMap implements LongIntegerMap    {
    private final Object sync;
    private final LongIntegerMap map;

    public LongIntegerSynchronizedMap(LongIntegerMap map)    {
        this(map, map);
    }

    public LongIntegerSynchronizedMap(@NonNull LongIntegerMap map, @NonNull Object sync)    {
        this.sync = sync;
        this.map = map;
    }

    @Override
    public int get(long key)   {
        synchronized (sync) {
            return map.get(key);
        }
    }

    @Override
    public int put(long key, int value)   {
        synchronized (sync) {
            return map.put(key, value);
        }
    }

    @Override
    public int remove(long key)    {
        synchronized (sync) {
            return map.remove(key);
        }
    }

    @Override
    public boolean containsKey(long key)    {
        synchronized (sync) {
            return map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(int value)    {
        synchronized (sync) {
            return map.containsValue(value);
        }
    }

    @Override
    public void clear() {
        synchronized (sync) {
            map.clear();
        }
    }

    @Override
    public int getSize()    {
        synchronized (sync) {
            return map.getSize();
        }
    }

    @Override
    public void forEachKey(@NonNull LongConsumer consumer)  {
        synchronized (sync){
            map.forEachKey(consumer);
        }
    }

    @Override
    public void forEachValue(@NonNull IntegerConsumer consumer) {
        synchronized (sync){
            map.forEachValue(consumer);
        }
    }

    @Override
    public void forEachEntry(@NonNull LongIntegerConsumer consumer) {
        synchronized (sync){
            map.forEachEntry(consumer);
        }
    }

    @Override
    public LongIterator keyIterator()   {
        synchronized (sync){
            return map.keyIterator();
        }
    }

    @Override
    public IntegerIterator valueIterator()   {
        synchronized (sync){
            return map.valueIterator();
        }
    }

    @Override
    public LongIntegerIterator entryIterator()   {
        synchronized (sync){
            return map.entryIterator();
        }
    }
}
