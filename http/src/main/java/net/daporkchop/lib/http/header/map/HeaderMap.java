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

package net.daporkchop.lib.http.header.map;

import lombok.NonNull;
import net.daporkchop.lib.http.header.Header;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A specialized map for storing HTTP headers.
 * <p>
 * Note that as specified in RFC2616§4.2, keys are case-insensitive. Implementations may choose to remember the capitalization of keys passed to any
 * methods in this interface, or may modify the capitalization in any way they desire. In other words: don't depend on the capitalization of keys to
 * be preserved!
 * <p>
 * Unless otherwise explicitly stated, implementations make no guarantees as to thread-safety or concurrency.
 *
 * @author DaPorkchop_
 */
public interface HeaderMap {
    /**
     * @return the number of headers in this map
     */
    int size();

    /**
     * @return whether or not this map is empty
     */
    default boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Gets the {@link Header} at the given index.
     *
     * @param index the index of the header to get
     * @return the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    Header get(int index) throws IndexOutOfBoundsException;

    /**
     * Gets the key of the header at the given index.
     *
     * @param index the index of the header to get the key of
     * @return the key of the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    default String getKey(int index) throws IndexOutOfBoundsException {
        return this.get(index).key();
    }

    /**
     * Gets the value of the header at the given index.
     *
     * @param index the index of the header to get the value of
     * @return the value of the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    default String getValue(int index) throws IndexOutOfBoundsException {
        return this.get(index).value();
    }

    /**
     * Gets the values of the header at the given index.
     *
     * @param index the index of the header to get the values of
     * @return the values of the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    default List<String> getValues(int index) throws IndexOutOfBoundsException {
        return this.get(index).values();
    }

    /**
     * Gets the {@link Header} with the given key.
     *
     * @param key the key of the header to get
     * @return the header with the given key, or {@code null} if none was found
     */
    Header get(@NonNull String key);

    /**
     * Gets the value of the header with the given key.
     *
     * @param key the key of the header to get the value of
     * @return the value of the header with the given key, or {@code null} if none was found
     */
    default String getValue(@NonNull String key) {
        Header header = this.get(key);
        return header == null ? null : header.value();
    }

    /**
     * Gets the values of the header with the given key.
     *
     * @param key the key of the header to get the values of
     * @return the values of the header with the given key, or {@code null} if none was found
     */
    default List<String> getValues(@NonNull String key) {
        Header header = this.get(key);
        return header == null ? null : header.values();
    }

    /**
     * Checks if this map contains a header with the given key.
     *
     * @param key the key to check for the existence of
     * @return whether or not a header with a matching key was found
     */
    default boolean hasKey(@NonNull String key) {
        return this.get(key) != null;
    }

    /**
     * Gets an immutable copy of this {@link HeaderMap}.
     * <p>
     * No restrictions are placed on implementations of this method beyond the fact that the returned {@link HeaderMap} must have exactly identical
     * contents to this one at the time of invocation, and must be immutable.
     *
     * @return an immutable copy of this {@link HeaderMap}
     */
    default HeaderMap snapshot() {
        return this;
    }

    /**
     * Gets a mutable copy of this {@link HeaderMap}.
     * <p>
     * The returned map will be completely distinct from this map, and have exactly identical contents.
     *
     * @return a mutable copy of this {@link HeaderMap}
     */
    default MutableHeaderMap mutableCopy() {
        return new MutableHeaderMapImpl(this);
    }

    /**
     * Iterates over all headers in this map, passing each of them to the given callback function.
     *
     * @param callback the callback function to run
     */
    default void forEach(@NonNull Consumer<Header> callback) {
        for (int i = 0, c = this.size(); i < c; i++) {
            callback.accept(this.get(i));
        }
    }

    /**
     * Iterates over all headers in this map, passing each of them to the given callback function.
     *
     * @param callback the callback function to run
     */
    default void forEach(@NonNull BiConsumer<String, String> callback) {
        this.forEach(header -> {
            if (header.singleton()) {
                callback.accept(header.key(), header.value());
            } else {
                for (String value : header.values())    {
                    callback.accept(header.key(), value);
                }
            }
        });
    }
}
