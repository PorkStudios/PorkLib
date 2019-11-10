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

package net.daporkchop.lib.http.util.header;

import lombok.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * A map for storing HTTP headers.
 *
 * @author DaPorkchop_
 */
public interface HeaderMap {
    /**
     * @return the number of headers in this map
     */
    int count();

    /**
     * Gets the {@link Header} at the given index.
     *
     * @param index the index of the header to get
     * @return the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    Header get(int index);

    /**
     * Gets the key of the header at the given index.
     *
     * @param index the index of the header to get
     * @return the key of the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    default String getKey(int index) {
        return this.get(index).key();
    }

    /**
     * Gets the value of the header at the given index.
     *
     * @param index the index of the header to get
     * @return the value of the header at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    default String getValue(int index) {
        return this.get(index).value();
    }

    /**
     * Gets the {@link Header} with the given key.
     *
     * @param key the key of the header to get
     * @return the header with the given key, or {@code null} if none could be found
     */
    Header get(@NonNull String key);

    /**
     * Gets the value of the header with the given key.
     *
     * @param key the key of the header to get
     * @return the value of the header with the given key, or {@code null} if none could be found
     */
    default String getValue(@NonNull String key) {
        Header header = this.get(key);
        return header == null ? null : header.value();
    }

    /**
     * Iterates over all the headers in this map and runs a given callback function on them.
     *
     * @param callback the callback function to run
     */
    default void forEach(@NonNull Consumer<Header> callback) {
        for (int i = 0, c = this.count(); i < c; i++) {
            callback.accept(this.get(i));
        }
    }

    /**
     * Iterates over all the headers in this map and runs a given callback function on them.
     *
     * @param callback the callback function to run
     */
    default void forEach(@NonNull ObjIntConsumer<Header> callback) {
        for (int i = 0, c = this.count(); i < c; i++) {
            callback.accept(this.get(i), i);
        }
    }

    /**
     * Iterates over all the headers in this map and runs a given callback function on them.
     *
     * @param callback the callback function to run
     */
    default void forEach(@NonNull BiConsumer<String, String> callback) {
        for (int i = 0, c = this.count(); i < c; i++) {
            Header header = this.get(i);
            callback.accept(header.key(), header.value());
        }
    }
}
