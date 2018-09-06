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

import net.daporkchop.lib.primitive.lambda.consumer.FloatConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.ShortConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.FloatShortConsumer;
import net.daporkchop.lib.primitive.map.FloatShortMap;
import net.daporkchop.lib.primitive.iterator.FloatIterator;
import net.daporkchop.lib.primitive.iterator.ShortIterator;
import net.daporkchop.lib.primitive.iterator.bi.FloatShortIterator;

import lombok.*;

/**
 * A synchronized map, using a key type of float and a value type of short.
 * This doesn't do anything by itself, it just serves as a wrapper for another instance of FloatShortMap.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class FloatShortSynchronizedMap implements FloatShortMap    {
    private final Object sync;
    private final FloatShortMap map;

    public FloatShortSynchronizedMap(FloatShortMap map)    {
        this(map, map);
    }

    public FloatShortSynchronizedMap(@NonNull FloatShortMap map, @NonNull Object sync)    {
        this.sync = sync;
        this.map = map;
    }

    @Override
    public short get(float key)   {
        synchronized (sync) {
            return map.get(key);
        }
    }

    @Override
    public short put(float key, short value)   {
        synchronized (sync) {
            return map.put(key, value);
        }
    }

    @Override
    public short remove(float key)    {
        synchronized (sync) {
            return map.remove(key);
        }
    }

    @Override
    public boolean containsKey(float key)    {
        synchronized (sync) {
            return map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(short value)    {
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
    public void forEachKey(@NonNull FloatConsumer consumer)  {
        synchronized (sync){
            map.forEachKey(consumer);
        }
    }

    @Override
    public void forEachValue(@NonNull ShortConsumer consumer) {
        synchronized (sync){
            map.forEachValue(consumer);
        }
    }

    @Override
    public void forEachEntry(@NonNull FloatShortConsumer consumer) {
        synchronized (sync){
            map.forEachEntry(consumer);
        }
    }

    @Override
    public FloatIterator keyIterator()   {
        synchronized (sync){
            return map.keyIterator();
        }
    }

    @Override
    public ShortIterator valueIterator()   {
        synchronized (sync){
            return map.valueIterator();
        }
    }

    @Override
    public FloatShortIterator entryIterator()   {
        synchronized (sync){
            return map.entryIterator();
        }
    }
}
