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

package net.daporkchop.lib.http.header;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A specialized map for storing HTTP headers.
 * <p>
 * Note that as specified in RFC2616§4.2, keys are case-insensitive. Implementations may choose to remember the capitalization of keys passed to any
 * methods in this interface, or may modify the capitalization in any way they desire. In other words: don't depend on the capitalization of keys to
 * be preserved!
 * <p>
 * All methods in all implementations of this interface are expected to be thread-safe at the least (not necessarily concurrent).
 *
 * @author DaPorkchop_
 */
public interface HeaderMap {
    /**
     * An empty {@link HeaderMap} instance.
     */
    HeaderMap EMPTY = new HeaderMap() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Header get(int index) throws IndexOutOfBoundsException {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override
        public Header get(String key) {
            return null;
        }
    };

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
        for (int i = 0, c = this.size(); i < c; i++) {
            Header header = this.get(i);
            callback.accept(header.key(), header.value());
        }
    }
}
