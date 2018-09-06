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

import net.daporkchop.lib.primitive.lambda.consumer.ObjectConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.BooleanConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ObjectBooleanConsumer;
import net.daporkchop.lib.primitive.map.ObjectBooleanMap;
import net.daporkchop.lib.primitive.iterator.ObjectIterator;
import net.daporkchop.lib.primitive.iterator.BooleanIterator;
import net.daporkchop.lib.primitive.iterator.bi.ObjectBooleanIterator;

import lombok.*;

/**
 * A synchronized map, using a key type of K and a value type of boolean.
 * This doesn't do anything by itself, it just serves as a wrapper for another instance of ObjectBooleanMap.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ObjectBooleanSynchronizedMap<K extends Object> implements ObjectBooleanMap<K>    {
    private final Object sync;
    private final ObjectBooleanMap<K> map;

    public ObjectBooleanSynchronizedMap(ObjectBooleanMap<K> map)    {
        this(map, map);
    }

    public ObjectBooleanSynchronizedMap(@NonNull ObjectBooleanMap<K> map, @NonNull Object sync)    {
        this.sync = sync;
        this.map = map;
    }

    @Override
    public boolean get(K key)   {
        synchronized (sync) {
            return map.get(key);
        }
    }

    @Override
    public boolean put(K key, boolean value)   {
        synchronized (sync) {
            return map.put(key, value);
        }
    }

    @Override
    public boolean remove(K key)    {
        synchronized (sync) {
            return map.remove(key);
        }
    }

    @Override
    public boolean containsKey(K key)    {
        synchronized (sync) {
            return map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(boolean value)    {
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
    public void forEachKey(@NonNull ObjectConsumer<K> consumer)  {
        synchronized (sync){
            map.forEachKey(consumer);
        }
    }

    @Override
    public void forEachValue(@NonNull BooleanConsumer consumer) {
        synchronized (sync){
            map.forEachValue(consumer);
        }
    }

    @Override
    public void forEachEntry(@NonNull ObjectBooleanConsumer<K> consumer) {
        synchronized (sync){
            map.forEachEntry(consumer);
        }
    }

    @Override
    public ObjectIterator<K> keyIterator()   {
        synchronized (sync){
            return map.keyIterator();
        }
    }

    @Override
    public BooleanIterator valueIterator()   {
        synchronized (sync){
            return map.valueIterator();
        }
    }

    @Override
    public ObjectBooleanIterator<K> entryIterator()   {
        synchronized (sync){
            return map.entryIterator();
        }
    }
}
