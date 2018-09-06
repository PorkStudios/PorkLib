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
import net.daporkchop.lib.primitive.lambda.consumer.CharacterConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.bi.ObjectCharacterConsumer;
import net.daporkchop.lib.primitive.map.ObjectCharacterMap;
import net.daporkchop.lib.primitive.iterator.ObjectIterator;
import net.daporkchop.lib.primitive.iterator.CharacterIterator;
import net.daporkchop.lib.primitive.iterator.bi.ObjectCharacterIterator;

import lombok.*;

/**
 * A synchronized map, using a key type of K and a value type of char.
 * This doesn't do anything by itself, it just serves as a wrapper for another instance of ObjectCharacterMap.
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class ObjectCharacterSynchronizedMap<K extends Object> implements ObjectCharacterMap<K>    {
    private final Object sync;
    private final ObjectCharacterMap<K> map;

    public ObjectCharacterSynchronizedMap(ObjectCharacterMap<K> map)    {
        this(map, map);
    }

    public ObjectCharacterSynchronizedMap(@NonNull ObjectCharacterMap<K> map, @NonNull Object sync)    {
        this.sync = sync;
        this.map = map;
    }

    @Override
    public char get(K key)   {
        synchronized (sync) {
            return map.get(key);
        }
    }

    @Override
    public char put(K key, char value)   {
        synchronized (sync) {
            return map.put(key, value);
        }
    }

    @Override
    public char remove(K key)    {
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
    public boolean containsValue(char value)    {
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
    public void forEachValue(@NonNull CharacterConsumer consumer) {
        synchronized (sync){
            map.forEachValue(consumer);
        }
    }

    @Override
    public void forEachEntry(@NonNull ObjectCharacterConsumer<K> consumer) {
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
    public CharacterIterator valueIterator()   {
        synchronized (sync){
            return map.valueIterator();
        }
    }

    @Override
    public ObjectCharacterIterator<K> entryIterator()   {
        synchronized (sync){
            return map.entryIterator();
        }
    }
}
