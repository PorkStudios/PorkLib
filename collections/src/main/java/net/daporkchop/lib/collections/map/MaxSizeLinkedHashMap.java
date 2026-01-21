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

package net.daporkchop.lib.collections.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extension of {@link LinkedHashMap} which automatically removes entries after the map exceeds a given capacity.
 *
 * @author DaPorkchop_
 */
public class MaxSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    protected final int maxSize;

    public MaxSizeLinkedHashMap(int maxSize) {
        super(maxSize);

        this.maxSize = maxSize;
    }

    public MaxSizeLinkedHashMap(int maxSize, boolean accessOrder) {
        super(maxSize, 1.1f, accessOrder);

        this.maxSize = maxSize;
    }

    public MaxSizeLinkedHashMap(int maxSize, float loadFactor, boolean accessOrder) {
        super(maxSize, loadFactor, accessOrder);

        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (this.size() > this.maxSize) {
            this.onEvicted(eldest.getKey(), eldest.getValue());
            return true;
        } else {
            return false;
        }
    }

    protected void onEvicted(K key, V value) {
        //no-op
    }

    /**
     * Extension of {@link MaxSizeLinkedHashMap} which automatically closes values after eviction.
     *
     * @author DaPorkchop_
     */
    public static final class Closing<K, V extends AutoCloseable> extends MaxSizeLinkedHashMap<K, V> {
        public Closing(int maxSize) {
            super(maxSize);
        }

        public Closing(int maxSize, boolean accessOrder) {
            super(maxSize, accessOrder);
        }

        public Closing(int maxSize, float loadFactor, boolean accessOrder) {
            super(maxSize, loadFactor, accessOrder);
        }

        @Override
        protected void onEvicted(K key, V value) {
            try {
                value.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
