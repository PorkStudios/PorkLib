/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container.map;

import lombok.NonNull;

import java.io.IOException;

/**
 * A simplification of {@link java.util.Map}. A DBMap supports many of the same simple getters and setters as a normal
 * Java Map, as well as some basic iteration methods and a few variations on the standard accessors for performance.
 * <p>
 * Implementations of this class are expected to be thread-safe at a minimum, optionally supporting full concurrency.
 *
 * @param <K> the type to be used as a key
 * @param <V> the type to be used as a value
 * @author DaPorkchop_
 */
public interface DBMap<K, V> extends AutoCloseable {
    /**
     * Gets this map's size (i.e. the number of key => value pairs in the map).
     *
     * @return the size of this map, or -1 if the map is closed
     */
    long size();

    /**
     * Checks whether or not this map is empty (i.e. contains no key => value pairs).
     *
     * @return whether or not this map is empty
     */
    default boolean isEmpty() {
        return this.size() == 0L;
    }

    /**
     * Removes all key => value pairs from this map.
     */
    void clear();

    /**
     * Gets the value corresponding to the given key.
     *
     * @param key the key
     * @return the value corresponding to the given key, or {@code null} if no such mapping exists
     */
    V get(@NonNull K key);

    /**
     * Submits a key => value pair to the map, replacing existing mappings if present.
     *
     * @param key   the key
     * @param value the value corresponding to the key
     * @return {@code true} if a key => value pair with the same key already existed and was replaced, {@code false} if no such pair existed and a new one was created.
     */
    boolean put(@NonNull K key, @NonNull V value);

    /**
     * Submits a key => value pair to the map, replacing existing mappings if present and returning the replaced value.
     *
     * @param key   the key
     * @param value the value corresponding to the key
     * @return the replaced value if a key => value pair with the same key already existed and was replaced, {@code null} if no such pair existed and a new one was created.
     */
    V getAndPut(@NonNull K key, @NonNull V value);

    /**
     * Checks if a key => value pair exists with the given key.
     *
     * @param key the key
     * @return whether or not a key => value pair with the given key exists in the map
     */
    boolean contains(@NonNull K key);

    /**
     * Removes a key => value pair with a given key from the map.
     *
     * @param key the key
     * @return {@code true} if a key => value pair with the same key already existed and was replaced, {@code false} if no such pair existed and no action was taken.
     */
    boolean remove(@NonNull K key);

    /**
     * Removes a key => value pair with a given key from the map, returning the removed value if present.
     *
     * @param key the key
     * @return the removed value if a key => value pair with the same key already existed and was removed, {@code null} if no such pair existed and no action was taken.
     */
    V getAndRemove(@NonNull K key);

    @Override
    void close() throws IOException;

    /**
     * Checks if this map is closed.
     *
     * @return whether or not this map has been closed
     */
    default boolean isClosed() {
        return this.size() == -1L;
    }
}
