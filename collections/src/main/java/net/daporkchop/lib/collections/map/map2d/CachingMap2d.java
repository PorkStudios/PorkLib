/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.collections.map.map2d;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.primitive.BinMath;
import net.daporkchop.lib.primitive.lambda.consumer.IntIntConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntIntObjConsumer;
import net.daporkchop.lib.primitive.lambda.function.IntIntObjFunction;

import java.util.Arrays;
import java.util.function.Consumer;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.lib.math.primitive.BinMath.*;

/**
 * A simple implementation of {@link Map2d} that operates as a simple hash-based cache.
 * <p>
 * This is backed by a single, fixed-size table. Positions are hashed, hashes are used as indexes in the table. In the event
 * of a hash collision, the old value is simply evicted from the map (the method {@link #onEvicted(int, int, Object)} may be overriden
 * to handle any additional logic required after eviction).
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class CachingMap2d<V> implements Map2d<V> {
    protected final long[]   keys;
    protected final Object[] values;
    protected final int      mask;
    @Getter
    protected       int      size;

    public CachingMap2d(int maxSize) {
        if (!BinMath.isPow2(maxSize)) {
            throw new IllegalArgumentException(String.valueOf(maxSize));
        }
        this.keys = new long[maxSize];
        this.values = new Object[maxSize];
        this.mask = maxSize - 1;
    }

    @Override
    public V put(int x, int y, @NonNull V value) {
        long key = packXY(x, y);
        int offset = this.mix(key) & this.mask;
        long oldKey = this.keys[offset];
        this.keys[offset] = key;
        V old = uncheckedCast(this.values[offset]);
        this.values[offset] = value;
        if (old == null) {
            this.size++;
        }
        if (oldKey == key) {
            return old;
        } else if (old != null) {
            this.onEvicted(unpackX(oldKey), unpackY(oldKey), old);
        }
        return null;
    }

    @Override
    public V putIfAbsent(int x, int y, @NonNull V value) {
        long key = packXY(x, y);
        int offset = this.mix(key) & this.mask;
        long oldKey = this.keys[offset];
        this.keys[offset] = key;
        V old = uncheckedCast(this.values[offset]);
        if (old == null) {
            this.values[offset] = value;
            this.size++;
        } else if (oldKey != key) {
            this.values[offset] = value;
            this.onEvicted(unpackX(oldKey), unpackY(oldKey), old);
        }
        return oldKey == key ? old : null;
    }

    @Override
    public V get(int x, int y) {
        long key = packXY(x, y);
        int offset = this.mix(key) & this.mask;
        return this.keys[offset] == key ? uncheckedCast(this.values[offset]) : null;
    }

    @Override
    public V computeIfAbsent(int x, int y, @NonNull IntIntObjFunction<V> mappingFunction) {
        long key = packXY(x, y);
        int offset = this.mix(key) & this.mask;
        long oldKey = this.keys[offset];
        this.keys[offset] = key;
        V old = uncheckedCast(this.values[offset]);
        if (old == null) {
            this.values[offset] = mappingFunction.apply(x, y);
            this.size++;
        } else if (oldKey != key) {
            this.values[offset] = mappingFunction.apply(x, y);
            this.onEvicted(unpackX(oldKey), unpackY(oldKey), old);
        }
        return oldKey == key ? old : null;
    }

    @Override
    public V remove(int x, int y) {
        long key = packXY(x, y);
        int offset = this.mix(key) & this.mask;
        if (this.keys[offset] == key) {
            V old = uncheckedCast(this.values[offset]);
            if (old != null) {
                this.values[offset] = null;
                this.size--;
            }
            return old;
        } else {
            return null;
        }
    }

    @Override
    public boolean contains(int x, int y) {
        long key = packXY(x, y);
        int offset = this.mix(key) & this.mask;
        return this.values[offset] != null && this.keys[offset] == key;
    }

    @Override
    public void forEach(@NonNull IntIntObjConsumer<V> consumer) {
        for (int i = 0; i < this.values.length; i++) {
            V value = uncheckedCast(this.values[i]);
            if (value != null) {
                long key = this.keys[i];
                consumer.accept(unpackX(key), unpackY(key), value);
            }
        }
    }

    @Override
    public void forEachKey(@NonNull IntIntConsumer consumer) {
        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] != null) {
                long key = this.keys[i];
                consumer.accept(unpackX(key), unpackY(key));
            }
        }
    }

    @Override
    public void forEachValue(@NonNull Consumer<V> consumer) {
        for (Object obj : this.values) {
            if (obj != null) {
                consumer.accept(uncheckedCast(obj));
            }
        }
    }

    @Override
    public void clear() {
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    protected int mix(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        return (int) (((z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L) >>> 32);
    }

    protected void onEvicted(int x, int y, @NonNull V value) {
    }
}
