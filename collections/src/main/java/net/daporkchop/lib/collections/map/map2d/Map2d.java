/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.collections.map.map2d;

import lombok.NonNull;
import net.daporkchop.lib.primitive.lambda.consumer.IntIntConsumer;
import net.daporkchop.lib.primitive.lambda.consumer.IntIntObjConsumer;
import net.daporkchop.lib.primitive.lambda.function.IntIntObjFunction;

import java.util.function.Consumer;

/**
 * A map using a pair of integer X,Y coordinates as a key rather than having to use a wrapper class.
 * <p>
 * Unless otherwise specified by the implementation, assume that {@code null} values are not allowed.
 *
 * @param <V> the type of value stored in the map
 * @author DaPorkchop_
 */
public interface Map2d<V> {
    /**
     * Puts a value into this map at the given position, replacing any existing value.
     *
     * @param x     the position's X coordinate
     * @param y     the position's Y coordinate
     * @param value the value to put
     * @return the replaced value, or {@code null} if there was no previous value
     */
    V put(int x, int y, V value);

    /**
     * Puts a value into this map at the given position, doing nothing if a value already exists.
     *
     * @param x     the position's X coordinate
     * @param y     the position's Y coordinate
     * @param value the value to put
     * @return the existing value, or {@code null} if there was no previous value
     */
    V putIfAbsent(int x, int y, V value);

    /**
     * Gets the value mapped to the given position.
     *
     * @param x the position's X coordinate
     * @param y the position's Y coordinate
     * @return the value at the given position, or {@code null} if none exists
     */
    V get(int x, int y);

    /**
     * Gets the value mapped to the given position. If no value exists at the given position, it will be computed using the given
     * function and put into this map.
     *
     * @param x               the position's X coordinate
     * @param y               the position's Y coordinate
     * @param mappingFunction the function to compute the value if none exists
     * @return the existing value, or {@code null} if there was no previous value
     */
    V computeIfAbsent(int x, int y, @NonNull IntIntObjFunction<V> mappingFunction);

    /**
     * Removes the value at the given position from this map.
     *
     * @param x the position's X coordinate
     * @param y the position's Y coordinate
     * @return the removed value, or {@code null} if there was no previous value
     */
    V remove(int x, int y);

    /**
     * Checks whether or not this map contains a value at the given position.
     *
     * @param x the position's X coordinate
     * @param y the position's Y coordinate
     * @return whether or not a value at the given position exists
     */
    boolean contains(int x, int y);

    /**
     * Iterates over every entry in this map and passes it to the given function.
     *
     * @param consumer the function to run
     */
    void forEach(@NonNull IntIntObjConsumer<V> consumer);

    /**
     * Iterates over every position in this map and passes it to the given function.
     *
     * @param consumer the function to run
     */
    void forEachKey(@NonNull IntIntConsumer consumer);

    /**
     * Iterates over every value in this map and passes it to the given function.
     *
     * @param consumer the function to run
     */
    void forEachValue(@NonNull Consumer<V> consumer);

    /**
     * @return the number of entries in this map
     */
    int size();

    /**
     * Removes all entries from this map.
     */
    void clear();
}
