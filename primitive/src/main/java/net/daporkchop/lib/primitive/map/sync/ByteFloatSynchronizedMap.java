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

import net.daporkchop.lib.primitive.lambda.consumer.ByteConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.FloatConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ByteFloatConsumer;
import net.daporkchop.lib.primitive.map.ByteFloatMap;
import net.daporkchop.lib.primitive.iterator.ByteIterator;
import net.daporkchop.lib.primitive.iterator.FloatIterator;
import net.daporkchop.lib.primitive.iterator.bi.ByteFloatIterator;

import lombok.*;

/**
 * A synchronized map, using a key type of byte and a value type of float.
 * This doesn't do anything by itself, it just serves as a wrapper for another instance of ByteFloatMap.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ByteFloatSynchronizedMap implements ByteFloatMap    {
    private final Object sync;
    private final ByteFloatMap map;

    public ByteFloatSynchronizedMap(ByteFloatMap map)    {
        this(map, map);
    }

    public ByteFloatSynchronizedMap(@NonNull ByteFloatMap map, @NonNull Object sync)    {
        this.sync = sync;
        this.map = map;
    }

    @Override
    public float get(byte key)   {
        synchronized (sync) {
            return map.get(key);
        }
    }

    @Override
    public float put(byte key, float value)   {
        synchronized (sync) {
            return map.put(key, value);
        }
    }

    @Override
    public float remove(byte key)    {
        synchronized (sync) {
            return map.remove(key);
        }
    }

    @Override
    public boolean containsKey(byte key)    {
        synchronized (sync) {
            return map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(float value)    {
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
    public void forEachKey(@NonNull ByteConsumer consumer)  {
        synchronized (sync){
            map.forEachKey(consumer);
        }
    }

    @Override
    public void forEachValue(@NonNull FloatConsumer consumer) {
        synchronized (sync){
            map.forEachValue(consumer);
        }
    }

    @Override
    public void forEachEntry(@NonNull ByteFloatConsumer consumer) {
        synchronized (sync){
            map.forEachEntry(consumer);
        }
    }

    @Override
    public ByteIterator keyIterator()   {
        synchronized (sync){
            return map.keyIterator();
        }
    }

    @Override
    public FloatIterator valueIterator()   {
        synchronized (sync){
            return map.valueIterator();
        }
    }

    @Override
    public ByteFloatIterator entryIterator()   {
        synchronized (sync){
            return map.entryIterator();
        }
    }
}
